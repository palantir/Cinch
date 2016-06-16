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
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import com.google.common.collect.ImmutableList;
import com.palantir.ptoss.cinch.core.BindableModel;
import com.palantir.ptoss.cinch.core.Binding;
import com.palantir.ptoss.cinch.core.BindingContext;
import com.palantir.ptoss.cinch.core.ModelUpdate;
import com.palantir.ptoss.cinch.core.WiringHarness;
import com.palantir.ptoss.cinch.swing.Bound.Utilities;
import com.palantir.ptoss.cinch.swing.Bound.Wiring;
import com.palantir.ptoss.util.Mutator;

/**
 * A {@link WiringHarness} for binding a {@link JComboBox} to a {@link List} value in a
 * {@link BindableModel}.
 */
public class JComboBoxWiringHarness implements WiringHarness<Bound, Field> {
    public Collection<Binding> wire(Bound bound, BindingContext context, Field field) throws IllegalAccessException, IntrospectionException {
        String target = bound.to();
        JComboBox combo = context.getFieldObject(field, JComboBox.class);
        Mutator mutator = Mutator.create(context, target);
        final String nullValue = (String)Utilities.getNullValue(context, bound.nullValue());
        return ImmutableList.of(bindJComboBox(bound, mutator, combo, nullValue));
    }

    private Binding bindJComboBox(final Bound bound, final Mutator mutator, final JComboBox combo, final String nullValue) {
        final List<Object> ons = BindingContext.getOnObjects(bound.on(), mutator.getModel());
        Binding binding = new Binding() {
            public <T extends Enum<?> & ModelUpdate> void update(T... changed) {
                if (!BindingContext.isOn(ons, changed)) {
                    return;
                }
                try {
                    updateComboModel(combo, (List<?>)mutator.get(), nullValue);
                } catch (Exception ex) {
                    Wiring.logger.error("exception in JList binding", ex);
                }
            }
        };
        mutator.getModel().bind(binding);
        return binding;
    }

    private void updateComboModel(JComboBox combo, List<?> newContents, String nullValue) {
        int selectedIndex = combo.getSelectedIndex();
        Object selected = combo.getSelectedItem();
        DefaultComboBoxModel comboModel = new DefaultComboBoxModel();
        if (nullValue != null) {
            comboModel.addElement(nullValue);
        }
        for (Object obj : newContents) {
            comboModel.addElement(obj);
        }
        if (comboModel.getIndexOf(selected) != -1) {
            comboModel.setSelectedItem(selected);
        } else {
            if (comboModel.getSize() > selectedIndex) {
                comboModel.setSelectedItem(combo.getItemAt(selectedIndex));
            } else if (comboModel.getSize() > 0) {
                comboModel.setSelectedItem(combo.getItemAt(comboModel.getSize() - 1));
            }
        }
        combo.setModel(comboModel);
    }
}
