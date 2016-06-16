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

import java.beans.IntrospectionException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;

import javax.swing.JProgressBar;

import com.palantir.ptoss.cinch.core.BindableModel;
import com.palantir.ptoss.cinch.core.Binding;
import com.palantir.ptoss.cinch.core.BindingContext;
import com.palantir.ptoss.cinch.core.ModelUpdate;
import com.palantir.ptoss.cinch.core.ObjectFieldMethod;
import com.palantir.ptoss.cinch.core.WiringHarness;
import com.palantir.ptoss.cinch.swing.Bound.Wiring;

/**
 * A {@link WiringHarness} for binding a {@link JProgressBar} to an <code>int</code> value in a {@link BindableModel}.
 */
public class JProgressBarWiringHarness implements WiringHarness<Bound, Field> {
        public Collection<Binding> wire(Bound bound, BindingContext context, Field field) throws IllegalAccessException, IntrospectionException {
            String target = bound.to();
            JProgressBar bar = context.getFieldObject(field, JProgressBar.class);
//            ObjectFieldMethod setter = context.findSetter(target);
            ObjectFieldMethod getter = context.findGetter(target);
            if (getter == null) {
                throw new IllegalArgumentException("could not find getter/setter for " + target);
            }
            BindableModel model = context.getFieldObject(getter.getField(), BindableModel.class);
            // verify type parameters
            return bindJProgressBar(model, bar, getter.getMethod());
        }

        private static int getValueForObject(Object obj) {
            if (obj instanceof Double) {
                return (int)Math.round(((Double)obj).doubleValue() * 100);
            } else if (obj instanceof Integer) {
                return ((Integer)obj).intValue();
            } else if (obj instanceof Float) {
                return (int)Math.round(((Float)obj).doubleValue() * 100);
            }
            return -1;
        }

        public static Collection<Binding> bindJProgressBar(final BindableModel model, final JProgressBar bar,
                final Method getter) {
            Binding binding = new Binding() {
                public <T extends Enum<?> & ModelUpdate> void update(T... changed) {
                    try {
                        int val = getValueForObject(getter.invoke(model));
                        bar.setValue(val);
                        bar.setIndeterminate(val < 0);
                    } catch (Exception ex) {
                        Wiring.logger.error("exception in JTextField binding", ex);
                    }
                }
            };
            model.bind(binding);
            return Collections.singleton(binding);
        }
    }
