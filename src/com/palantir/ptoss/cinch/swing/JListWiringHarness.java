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
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.lang.ArrayUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.palantir.ptoss.cinch.core.BindableModel;
import com.palantir.ptoss.cinch.core.Binding;
import com.palantir.ptoss.cinch.core.BindingContext;
import com.palantir.ptoss.cinch.core.ModelUpdate;
import com.palantir.ptoss.cinch.core.WiringHarness;
import com.palantir.ptoss.cinch.swing.Bound.Wiring;
import com.palantir.ptoss.util.Mutator;

/**
 * <p>
 * Wires up a {@link JList} to a {@link List} in a {@link BindableModel}. Whenever the contents of
 * the bound List change (and the BindableModel's update is called) the JList's model will be
 * swapped out with a new model with the new contents. Selection of values is maintained between
 * model swaps.
 * <p>
 * This bindings is only one way. The JList will modify itself to reflect the contents of the List
 * in the BindableModel. However, changes to the JList's model after that will not be reflected
 * in the model. The idea here is that if the user takes some action to remove an item from the
 * list then that should be accomplished by removing an item from the underlying BindableModel,
 * not the JList.
 * <p>
 * Implementation notes: a {@link DefaultTableModel} is used to back the JList. Maintaining
 * selection between model swaps is simply done with {@link Object#equals(Object)} comparisons.
 */
public class JListWiringHarness implements WiringHarness<Bound, Field> {
    public Collection<Binding> wire(Bound bound, BindingContext context, Field field)
            throws IllegalAccessException, IntrospectionException {
        JList list = context.getFieldObject(field, JList.class);
        Mutator mutator = Mutator.create(context, bound.to());
        return ImmutableList.of(bindJList(bound, mutator, list));
    }

    private Binding bindJList(final Bound bound, final Mutator mutator, final JList list) {
        final List<Object> ons = BindingContext.getOnObjects(bound.on(), mutator.getModel());
        Binding binding = new Binding() {
            public <T extends Enum<?> & ModelUpdate> void update(T... changed) {
                if (!BindingContext.isOn(ons, changed)) {
                    return;
                }
                try {
                    updateListModel(list, (List<?>)mutator.get());
                } catch (Exception ex) {
                    Wiring.logger.error("exception in JList binding", ex);
                }
            }
        };
        mutator.getModel().bind(binding);
        return binding;
    }

    private static void updateListModel(JList list, List<?> newContents) {
        if (newContents == null) {
            newContents = ImmutableList.of();
        }
        ListModel oldModel = list.getModel();
        List<Object> old = Lists.newArrayListWithExpectedSize(oldModel.getSize());
        for (int i = 0; i < oldModel.getSize(); i++) {
            old.add(oldModel.getElementAt(i));
        }
        if (old.equals(newContents)) {
            return;
        }
        Object[] selected = list.getSelectedValues();
        DefaultListModel listModel = new DefaultListModel();
        for (Object obj : newContents) {
            listModel.addElement(obj);
        }
        list.setModel(listModel);
        List<Integer> newIndices = Lists.newArrayListWithCapacity(selected.length);
        Set<Object> selectedSet = Sets.newHashSet(selected);
        for (int i = 0; i < listModel.size(); i++) {
            if (selectedSet.contains(listModel.elementAt(i))) {
                newIndices.add(i);
            }
        }
        list.setSelectedIndices(ArrayUtils.toPrimitive(newIndices.toArray(new Integer[0])));
    }
}
