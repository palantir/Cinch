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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.IntrospectionException;
import java.lang.reflect.Field;
import java.util.Collection;

import javax.swing.AbstractButton;
import javax.swing.JToggleButton;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.palantir.ptoss.cinch.core.BindableModel;
import com.palantir.ptoss.cinch.core.Binding;
import com.palantir.ptoss.cinch.core.BindingContext;
import com.palantir.ptoss.cinch.core.BindingException;
import com.palantir.ptoss.cinch.core.ModelUpdate;
import com.palantir.ptoss.cinch.core.WiringHarness;
import com.palantir.ptoss.cinch.swing.Bound.Wiring;
import com.palantir.ptoss.util.Mutator;
import com.palantir.ptoss.util.Reflections;

/**
 * A {@link WiringHarness} for binding a {@link JToggleButton} to an {@link Enum} value in a
 * {@link BindableModel}.
 */
public class JToggleButtonWiringHarness implements WiringHarness<Bound, Field> {
    public Collection<Binding> wire(Bound bound, BindingContext context, Field field)
            throws IllegalAccessException, IntrospectionException {
        String target = bound.to();
        Mutator mutator = Mutator.create(context, target);
        JToggleButton toggle = context.getFieldObject(field, JToggleButton.class);

        Class<?>[] paramTypes = mutator.getSetter().getMethod().getParameterTypes();
        if (paramTypes.length == 1 && paramTypes[0].isEnum()) {
            Class<?> enumType = paramTypes[0];
            String value = bound.value();
            return ImmutableList.of(bindJToggleButtonToEnum(value, enumType, mutator, toggle));
        } else if (paramTypes.length == 1 && paramTypes[0] == boolean.class) {
            String value = bound.value();
            if (Strings.isNullOrEmpty(value)) {
                return ImmutableList.of(JCheckBoxWiringHarness.bindJCheckBox(mutator, toggle));
            } else {
                return ImmutableList.of(bindJToggleButtonToBoolean(bound.value(), mutator, toggle));
            }
        } else {
            throw new BindingException("can only bind JToggleButtons to enums or booleans"); //$NON-NLS-1$
        }
    }

    public static Binding bindJToggleButtonToBoolean(String value,
            final Mutator mutator, final AbstractButton button) {
        final boolean booleanValue = Boolean.valueOf(value);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    mutator.set(booleanValue);
                } catch (Exception ex) {
                    Wiring.logger.error("exception in JRadioButton binding", ex); //$NON-NLS-1$
                }
            }
        });

        Binding binding = new Binding() {
            public <T extends Enum<?> & ModelUpdate> void update(T... changed) {
                try {
                    button.setSelected(mutator.get().equals(Boolean.valueOf(booleanValue)));
                } catch (Exception ex) {
                    Wiring.logger.error("exception in JRadioButton binding", ex); //$NON-NLS-1$
                }
            }
        };
        mutator.getModel().bind(binding);
        return binding;
    }

    public static Binding bindJToggleButtonToEnum(final String value, final Class<?> enumType,
            final Mutator mutator, final AbstractButton button) {
        final Object enumValue = Reflections.evalEnum(enumType, value);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    mutator.set(enumValue);
                } catch (Exception ex) {
                    Wiring.logger.error("exception in JToggleButton binding", ex); //$NON-NLS-1$
                }
            }
        });

        Binding binding = new Binding() {
            public <T extends Enum<?> & ModelUpdate> void update(T... changed) {
                try {
                    button.setSelected(mutator.get() == enumValue);
                } catch (Exception ex) {
                    Wiring.logger.error("exception in JToggleButton binding", ex); //$NON-NLS-1$
                }
            }
        };
        mutator.getModel().bind(binding);
        return binding;
    }
}
