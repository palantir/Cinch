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
import java.util.Collection;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import com.google.common.collect.ImmutableList;
import com.palantir.ptoss.cinch.core.*;
import com.palantir.ptoss.cinch.swing.Bound.Wiring;
import com.palantir.ptoss.util.Mutator;

/**
 * A {@link WiringHarness} for binding a {@link JTextComponent} to a {@link String} value in a
 * {@link BindableModel}.
 */
public class JTextComponentWiringHarness implements WiringHarness<Bound, Field> {
    public Collection<Binding> wire(Bound bound, BindingContext context, Field field) throws IllegalAccessException, IntrospectionException {
        JTextComponent textComponent = context.getFieldObject(field, JTextComponent.class);
        Mutator mutator = Mutator.create(context, bound.to());
        Binding binding = bindJTextComponent(mutator, textComponent);
        if (binding == null) {
            return ImmutableList.of();
        }
        return ImmutableList.of(binding);
    }

    public static Binding bindJTextComponent(final Mutator mutator, final JTextComponent textField) {
        if (mutator.getSetter() != null) {
            textField.getDocument().addDocumentListener(new DocumentListener() {
                public void removeUpdate(DocumentEvent e) {
                    updateModel();
                }

                public void insertUpdate(DocumentEvent e) {
                    updateModel();
                }

                public void changedUpdate(DocumentEvent e) {
                    updateModel();
                }

                private void updateModel() {
                    try {
                        mutator.set(textField.getText());
                    } catch (Exception ex) {
                        Wiring.logger.error("exception in JTextField binding", ex);
                    }
                }
            });
        }
        Binding binding = null;
        if (mutator.getGetter() != null) {
            binding = new Binding() {
                public <T extends Enum<?> & ModelUpdate> void update(T... changed) {
                    try {
                        String string = (String)mutator.get();
                        if (string == null) {
                            string = "";
                        }
                        if (!string.equals(textField.getText())) {
                            textField.setText(string);
                        }
                    } catch (Exception ex) {
                        Wiring.logger.error("exception in JTextField binding", ex);
                    }
                }
            };
            mutator.getModel().bind(binding);
        }
        return binding;
    }
}