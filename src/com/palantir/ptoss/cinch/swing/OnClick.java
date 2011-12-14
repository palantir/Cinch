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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.common.collect.ImmutableList;
import com.palantir.ptoss.cinch.core.Binding;
import com.palantir.ptoss.cinch.core.BindingContext;
import com.palantir.ptoss.cinch.core.BindingException;
import com.palantir.ptoss.cinch.core.BindingWiring;
import com.palantir.ptoss.cinch.core.ObjectFieldMethod;

/**
 * A binding that will call a method when the on-click action occurs on this component.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface OnClick {
    String call();
    int count() default 1;
    Button button() default Button.LEFT;
    /**
     * Enum specifying which mouse button is being bound by this binding.
     */
    public enum Button {
        LEFT(MouseEvent.BUTTON1), CENTER(MouseEvent.BUTTON2), RIGHT(MouseEvent.BUTTON3);

        private final int constant;

        private Button(int constant) {
            this.constant = constant;
        }

        private int getConstant() {
            return constant;
        }
    }

    /**
     *     Inner utility class that performs the runtime wiring of all {@link OnClick} bindings.
     */
    static class Wiring implements BindingWiring {
        private static final Logger logger = Logger.getLogger(OnClick.class);

        public Collection<Binding> wire(BindingContext context) {
            List<Field> actions = context.getAnnotatedFields(OnClick.class);
            for (Field field : actions) {
                OnClick onClick = field.getAnnotation(OnClick.class);
                try {
                    wire(onClick, field, context);
                } catch (Exception e) {
                    throw new BindingException("could not wire up @OnClick on " + field.getName(), e);
                }
            }
            return ImmutableList.of();
        }

        private static void wire(final OnClick onClick, Field field, BindingContext context)
            throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
            String call = onClick.call();
            if (call == null) {
                throw new BindingException("call on @OnClick on " + field.getName() + " must be specified");
            }
            Method amlMethod = field.getType().getMethod("addMouseListener", MouseListener.class);
            if (amlMethod != null) {
                Object actionObject = context.getFieldObject(field, Object.class);
                final ObjectFieldMethod ofm = context.getBindableMethod(call);
                if (ofm == null) {
                    throw new BindingException("could not find bindable method: " + call);
                }
                MouseListener mouseListener = new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getButton() != onClick.button().getConstant() || e.getClickCount() != onClick.count()) {
                            return;
                        }
                        try {
                            ofm.getMethod().setAccessible(true);
                            ofm.getMethod().invoke(ofm.getObject());
                        } catch (Exception ex) {
                            logger.error("exception during action firing", ex);
                        }
                    }
                };
                amlMethod.invoke(actionObject, mouseListener);
            }
        }
    }
}
