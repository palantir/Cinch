//   Copyright 2011 Palantir Technologies
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.
package com.palantir.ptoss.cinch.swing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.text.JTextComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.palantir.ptoss.cinch.core.Binding;
import com.palantir.ptoss.cinch.core.BindingContext;
import com.palantir.ptoss.cinch.core.BindingException;
import com.palantir.ptoss.cinch.core.BindingWiring;
import com.palantir.ptoss.cinch.core.Bindings;
import com.palantir.ptoss.cinch.core.ModelUpdate;
import com.palantir.ptoss.cinch.core.ModelUpdates;
import com.palantir.ptoss.cinch.core.WiringHarness;
import com.palantir.ptoss.util.Reflections;
import com.palantir.ptoss.util.Throwables;
import com.palantir.ptoss.util.Visitor;

/**
 * Used to bind a UI control to a model property, where property is meant in a strict
 * Java sense.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface Bound {
    /**
     * The property to bind to.
     */
    String to();

    /**
     * <p>
     * Specifies the {@link ModelUpdate}s on which the binding will occur.
     * Defaults to empty, which indicates all updates.
     * </p>
     * <p>
     * <em>Note:</em> when bound to a specific {@link ModelUpdate}, a control will not
     * receive not sent specifically to any {@link ModelUpdate} - i.e. default updates. To
     * selectively answer a specific {@link ModelUpdate} and also still get default notications,
     * add {@link ModelUpdates#UNSPECIFIED} to the list of {@link ModelUpdate}s passed to {@link #on()}.
     * @see ModelUpdates
     */
    String[] on() default "";

    /**
     * Used for enum/boolean values for {@link JRadioButton}s and {@link JToggleButton}s. Valid
     * values for booleans are "true", "false", and unset. When "true" the button will indicate
     * a selected state, "false" indicates unselected state, and unset will indicate the state
     * of the backing boolean.
     */
    String value() default "";

    /**
     * Used to add a null value to a {@link JComboBox}.
     */
    String nullValue() default "";

    /**
     * Inner utility class to simplify figuring out how to display null values in controls.
     */
    public static class Utilities {
        /**
         * If the provided nullValue is null, empty, or just whitespace then the returned value
         * will be null. Otherwise, this function will look in the provided {@link BindingContext}
         * to find a constant with the name of the nullValue string and return that. If there is
         * no constant then the nullValue string itself will be returned.
         * @see JComboBoxWiringHarness
         * @see BoundSelection.Wiring
         */
        public static Object getNullValue(BindingContext context, String nullValue) {
            final String finalNullValue;
            if (isNullOrBlank(nullValue)) {
                finalNullValue = null;
            } else {
                Object constant = context.getBindableConstant(nullValue);
                if (constant != null) {
                    finalNullValue = constant.toString();
                } else {
                    finalNullValue = nullValue;
                }
            }
            return finalNullValue;
        }

        static boolean isNullOrBlank(String string) {
            if (string == null) {
                return true;
            }
            int length = string.length();
            if (length > 0) {
                for (int i = 0; i < length; i++) {
                    if (!Character.isWhitespace(string.charAt(i))) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    /**
     * Inner utility class that performs the runtime wiring of all bindings in this package.
     *
     * @see Bindings#STANDARD_BINDINGS
     */
    public static class Wiring implements BindingWiring {
        static final Logger logger = LoggerFactory.getLogger(Bound.class);

        private static Map<Class<?>, WiringHarness<Bound, Field>> wiringHarnesses =
            ImmutableMap.<Class<?>, WiringHarness<Bound, Field>>builder()
                .put(AbstractButton.class, new AbstractButtonWiringHarness())
                .put(JRadioButton.class, new JToggleButtonWiringHarness())
                .put(JToggleButton.class, new JToggleButtonWiringHarness())
                .put(JSlider.class, new JSliderWiringHarness())
                .put(JProgressBar.class, new JProgressBarWiringHarness())
                .put(JList.class, new JListWiringHarness())
                .put(JComboBox.class, new JComboBoxWiringHarness())
                .put(JTextComponent.class, new JTextComponentWiringHarness())
                .put(JPasswordField.class, new JPasswordFieldWiringHarness())
                .put(JLabel.class, new JLabelWiringHarness())
                .build();

        public Collection<Binding> wire(BindingContext context) throws IllegalArgumentException {
            List<Field> boundFields = context.getAnnotatedFields(Bound.class);
            List<Binding> bindings = Lists.newArrayList();
            for (Field field : boundFields) {
                Bound bound = field.getAnnotation(Bound.class);
                bindings.addAll(wire(bound, context, field));
            }
            return bindings;
        }

        private static Collection<Binding> wire(final Bound bound, final BindingContext context, final Field field) {
            final boolean[] didBind = new boolean[1];
            final List<Binding> bindings = Lists.newArrayList();
            Reflections.visitClassHierarchy(field.getType(), new Visitor<Class<?>>() {
                public void visit(Class<?> klass){
                    if (didBind[0]) {
                        return;
                    }
                    WiringHarness<Bound, Field> wiringHarness = wiringHarnesses.get(klass);
                    if (wiringHarness == null) {
                        return;
                    }
                    try {
                        bindings.addAll(wiringHarness.wire(bound, context, field));
                        didBind[0] = true;
                    } catch (Exception e) {
                        throw Throwables.throwUncheckedException(e);
                    }
                }
            });
            if (!didBind[0]) {
                throw new BindingException("don't know how to wire up @Bound field: " + field.getName());
            }
            return bindings;
        }
    }
}
