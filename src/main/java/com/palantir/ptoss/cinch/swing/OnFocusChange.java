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

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.palantir.ptoss.cinch.core.Binding;
import com.palantir.ptoss.cinch.core.BindingContext;
import com.palantir.ptoss.cinch.core.BindingException;
import com.palantir.ptoss.cinch.core.BindingWiring;
import com.palantir.ptoss.cinch.core.ObjectFieldMethod;

/**
 * A binding that will call one method when the bound component loses focus, and another method when
 * focus is gained.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface OnFocusChange {
    String lost() default "";
    String gained() default "";

    /**
     *     Inner utility class that performs the runtime wiring of all {@link OnFocusChange} bindings.
     */
    static class Wiring implements BindingWiring {
        private static final Logger logger = LoggerFactory.getLogger(OnFocusChange.class);

        private static String normalizeString(String string) {
            if (Bound.Utilities.isNullOrBlank(string)) {
                return null;
            }
            return string;
        }

        public Collection<Binding> wire(BindingContext context) {
            List<Field> actions = context.getAnnotatedFields(OnFocusChange.class);
            for (Field field : actions) {
                OnFocusChange focusChange = field.getAnnotation(OnFocusChange.class);
                String lost = normalizeString(focusChange.lost());
                String gained = normalizeString(focusChange.gained());
                if (Bound.Utilities.isNullOrBlank(lost) && Bound.Utilities.isNullOrBlank(gained)) {
                    throw new BindingException("either lost or gained must be specified on @OnFocusChange on " + field.getName());
                }
                try {
                    wire(lost, gained, field, context);
                } catch (Exception e) {
                    throw new BindingException("could not wire up @OnFocusChange " + field.getName(), e);
                }
            }
            return ImmutableList.of();
        }

        private static void wire(String lost, String gained, Field field, BindingContext context)
            throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
            Method aflMethod = field.getType().getMethod("addFocusListener", FocusListener.class);
            if (aflMethod != null) {
                Object actionObject = context.getFieldObject(field, Object.class);
                final ObjectFieldMethod lostOFM;
                if (lost == null) {
                    lostOFM = null;
                } else {
                    lostOFM = context.getBindableMethod(lost);
                    if (lostOFM == null) {
                        throw new BindingException("could not find bindable method: " + lost);
                    }
                }
                final ObjectFieldMethod gainedOFM;
                if (gained == null) {
                    gainedOFM = null;
                } else {
                    gainedOFM = context.getBindableMethod(gained);
                    if (gainedOFM == null) {
                        throw new BindingException("could not find bindable method: " + gained);
                    }
                }
                FocusListener focusListener = new FocusListener() {
                    public void focusGained(FocusEvent e) {
                        try {
                            if (gainedOFM != null) {
                                boolean accessible = gainedOFM.getMethod().isAccessible();
                                gainedOFM.getMethod().setAccessible(true);
                                gainedOFM.getMethod().invoke(gainedOFM.getObject());
                                gainedOFM.getMethod().setAccessible(accessible);
                            }
                        } catch (Exception ex) {
                            logger.error("exception during focusGained firing", ex);
                        }
                    }

                    public void focusLost(FocusEvent e) {
                        try {
                            if (lostOFM != null) {
                                boolean accessible = lostOFM.getMethod().isAccessible();
                                lostOFM.getMethod().setAccessible(true);
                                lostOFM.getMethod().invoke(lostOFM.getObject());
                                lostOFM.getMethod().setAccessible(accessible);
                            }
                        } catch (Exception ex) {
                            logger.error("exception during focusLost firing", ex);
                        }
                    }
                };
                aflMethod.invoke(actionObject, focusListener);
            }
        }
    }
}
