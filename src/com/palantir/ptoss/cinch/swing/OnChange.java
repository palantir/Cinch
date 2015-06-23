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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.palantir.ptoss.cinch.core.Binding;
import com.palantir.ptoss.cinch.core.BindingContext;
import com.palantir.ptoss.cinch.core.BindingException;
import com.palantir.ptoss.cinch.core.BindingWiring;
import com.palantir.ptoss.cinch.core.ObjectFieldMethod;

/**
 * A component binding that will call a method when an action occurs.  This can be applied to
 * any object that has an "addChangeListener" method that takes an {@link ChangeListener}.
 * Normally used for any sort of {@link JButton} or for a {@link JTextField}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface OnChange {
    /**
     * The name of the method to call when the action occurs. Must be accessible in the
     * {@link BindingContext}.
     */
    String call();

    /**
     * A boolean indicating whether or not we want to call this when the values
     * is adjusting
     */
    boolean onAdjust() default false;

    /**
     *     Inner utility class that performs the runtime wiring of all {@link OnChange} bindings.
     */
    static class Wiring implements BindingWiring {
        private static final Logger logger = LoggerFactory.getLogger(OnChange.class);

        public Collection<Binding> wire(BindingContext context) {
            List<Field> actions = context.getAnnotatedFields(OnChange.class);
            for (Field field : actions) {
                OnChange change = field.getAnnotation(OnChange.class);
                try {
                    wire(change, field, context);
                } catch (Exception e) {
                    throw new BindingException("could not wire up @OnChange on " + field.getName(), e);
                }
            }
            return ImmutableList.of();
        }

        private static void wire(final OnChange change, Field field, BindingContext context)
            throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {

            String call = change.call();
            if (call == null) {
                throw new BindingException("call on @OnChange on " + field.getName() + " must be specified");
            }
            Method _adjustMethod = null;
            try {
                _adjustMethod = field.getType().getMethod("getValueIsAdjusting");
            } catch(NoSuchMethodException nsme) {
                // not a problem, leave value as null;
            }

            final Method adjustMethod = _adjustMethod;
            final Method addChangeMethod = field.getType().getMethod("addChangeListener", ChangeListener.class);
            if (addChangeMethod != null) {
                final Object changeObject = context.getFieldObject(field, Object.class);
                final ObjectFieldMethod ofm = context.getBindableMethod(call);
                if (ofm == null) {
                    throw new BindingException("could not find bindable method: " + call);
                }
                final ChangeListener changeListener = new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        try {
                            if (adjustMethod != null && !change.onAdjust()) {
                                if ((Boolean) adjustMethod.invoke(changeObject)) return;
                            }
                            ofm.getMethod().invoke(ofm.getObject());
                        } catch (InvocationTargetException itex) {
                            logger.error("exception during action firing", itex.getCause());
                        } catch (Exception ex) {
                            logger.error("exception during action firing", ex);
                        }
                    }
                };
                addChangeMethod.invoke(changeObject, changeListener);
            }
        }
    }
}
