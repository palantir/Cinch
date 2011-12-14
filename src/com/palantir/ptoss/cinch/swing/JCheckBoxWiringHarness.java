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
import javax.swing.JCheckBox;

import com.google.common.collect.ImmutableList;
import com.palantir.ptoss.cinch.core.BindableModel;
import com.palantir.ptoss.cinch.core.Binding;
import com.palantir.ptoss.cinch.core.BindingContext;
import com.palantir.ptoss.cinch.core.ModelUpdate;
import com.palantir.ptoss.cinch.core.WiringHarness;
import com.palantir.ptoss.cinch.swing.Bound.Wiring;
import com.palantir.ptoss.util.Mutator;

/**
 * A {@link WiringHarness} for binding a {@link JCheckBox} to a boolean value in a
 * {@link BindableModel}.
 */
public class JCheckBoxWiringHarness implements WiringHarness<Bound, Field> {

    public Collection<Binding> wire(Bound bound, BindingContext context, Field field)
            throws IllegalAccessException, IntrospectionException {
        Mutator mutator = Mutator.create(context, bound.to());
        JCheckBox checkbox = context.getFieldObject(field, JCheckBox.class);
        return ImmutableList.of(bindJCheckBox(mutator, checkbox));
    }

    public static Binding bindJCheckBox(
            final Mutator mutator, final AbstractButton checkbox) {
        checkbox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    mutator.set(checkbox.isSelected());
                } catch (Exception ex) {
                    Wiring.logger.error("exception in JCheckBox binding", ex);
                }
            }
        });
        Binding binding = new Binding() {
            public <T extends Enum<?> & ModelUpdate> void update(T... changed) {
                try {
                    checkbox.setSelected((Boolean)mutator.get());
                } catch (Exception ex) {
                    Wiring.logger.error("exception in JCheckBox binding", ex);
                }
            }
        };
        mutator.getModel().bind(binding);
        return binding;
    }
}