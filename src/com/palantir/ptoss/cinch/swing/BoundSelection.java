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

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.IntrospectionException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.palantir.ptoss.cinch.core.Binding;
import com.palantir.ptoss.cinch.core.BindingContext;
import com.palantir.ptoss.cinch.core.BindingWiring;
import com.palantir.ptoss.cinch.core.ModelUpdate;
import com.palantir.ptoss.util.Mutator;
import com.palantir.ptoss.util.Throwables;

/**
 * A binding for an interface component that has a selection like a {@link JList} or
 * {@link JComboBox}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface BoundSelection {
    /**
     * The model property to bind to.
     */
    String to();

    /**
     * When this binding should occur.
     */
    String[] on() default "";

    /**
     * How to render the null value in a JComboBox.
     */
    String nullValue() default "";

    /**
     * Whether or not a JList should bind with multiselect.
     */
    boolean multi() default false;

    /**
     * Inner utility class that performs the runtime wiring of all {@link BoundSelection} bindings.
     */
    public static class Wiring implements BindingWiring {
        private static final Logger logger = LoggerFactory.getLogger(BoundSelection.class);

        public Collection<Binding> wire(BindingContext context) {
            List<Field> boundFields = context.getAnnotatedFields(BoundSelection.class);
            List<Binding> bindings = Lists.newArrayList();
            for (Field field : boundFields) {
                BoundSelection bound = field.getAnnotation(BoundSelection.class);
                try {
                    bindings.addAll(wire(bound, context, field));
                } catch (Exception e) {
                    throw Throwables.throwUncheckedException(e);
                }
            }
            return bindings;
        }

        private static Collection<Binding> wire(BoundSelection bound, BindingContext context, Field field) throws IntrospectionException {
            String target = bound.to();
            Mutator mutator = Mutator.create(context, target);
            if (JList.class.isAssignableFrom(field.getType())) {
                final JList list = context.getFieldObject(field, JList.class);
                return bindJList(bound, mutator, list);
            } else if (JComboBox.class.isAssignableFrom(field.getType())) {
                final JComboBox combo = context.getFieldObject(field, JComboBox.class);
                final String nullValue = (String)Bound.Utilities.getNullValue(context, bound.nullValue());
                return bindJComboBox(bound, mutator, combo, nullValue);
            } else {
                throw new IllegalArgumentException("don't know how to wire up @BoundSelection field: " + field.getName());
            }
        }

        private static Collection<Binding> bindJList(BoundSelection bound,
                final Mutator mutator,
                final JList list) {
            final boolean multi = bound.multi();
            final List<Object> ons = BindingContext.getOnObjects(bound.on(), mutator.getModel());

            list.addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    if (!e.getValueIsAdjusting()) {
                        try {
                            if (multi) {
                                mutator.set(ImmutableList.copyOf(list.getSelectedValues()));
                            } else {
                                mutator.set(list.getSelectedValue());
                            }
                        } catch (Exception ex) {
                            logger.error("could not invoke JList binding", ex);
                        }
                    }
                }
            });

            Binding binding = new Binding() {
                public <T extends Enum<?> & ModelUpdate> void update(T... changed) {
                    if (!BindingContext.isOn(ons, changed)) {
                        return;
                    }
                    try {
                        if (multi) {
                            Object[] selVals = list.getSelectedValues();
                            ImmutableList<Object> listCurrent = ImmutableList.copyOf(selVals);
                            Collection<?> current = (Collection<?>)mutator.get();
                            if (current == null) {
                                current = ImmutableList.of();
                            }
                            if (!Iterables.elementsEqual(listCurrent, current)) {
                                Set<?> currentSet = Sets.newHashSet(current);
                                List<Integer> selIndices = Lists.newArrayList();
                                for (int i = 0; i < list.getModel().getSize(); i++) {
                                    if (currentSet.contains(list.getModel().getElementAt(i))) {
                                        selIndices.add(i);
                                    }
                                }
                                int[] sel = new int[selIndices.size()];
                                int i = 0;
                                for (Integer index : selIndices) {
                                    sel[i++] = index;
                                }
                                list.setSelectedIndices(sel);
                            }
                        } else {
                            Object current = mutator.get();
                            Object listCurrent = list.getSelectedValue();
                            if (!Objects.equal(current, listCurrent)) {
                                if (current == null) {
                                    list.clearSelection();
                                } else {
                                    list.setSelectedValue(current, true);
                                }
                            }
                        }
                    } catch (Exception ex) {
                        logger.error("could not invoke JList binding", ex);
                    }
                }
            };
            mutator.getModel().bind(binding);
            return Collections.singleton(binding);
        }

        private static Collection<Binding> bindJComboBox(final BoundSelection bound,
                final Mutator mutator,
                final JComboBox combo,
                final String nullValue) {

            combo.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    try {
                        Object current = mutator.get();
                        Object newValue = combo.getSelectedItem();
                        if (newValue != null && newValue.equals(nullValue)) {
                            newValue = null;
                        }
                        if (!Objects.equal(current, newValue)) {
                            mutator.set(newValue);
                        }
                    } catch (Exception ex) {
                        logger.error("could not invoke JComboBox binding", ex);
                    }
                }
            });

            Binding binding = new Binding() {
                public <T extends Enum<?> & ModelUpdate> void update(T... changed) {
                    try {
                        Object current = mutator.get();
                        if (current == null) {
                            current = nullValue;
                        }
                        if (!Objects.equal(combo.getSelectedItem(), current)) {
                            combo.setSelectedItem(current);
                        }
                    } catch (Exception ex) {
                        logger.error("could not invoke JComboBox binding", ex);
                    }
                }
            };
            mutator.getModel().bind(binding);
            return Collections.singleton(binding);
        }
    }
}
