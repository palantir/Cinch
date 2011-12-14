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
package com.palantir.ptoss.cinch;

import java.util.List;

import javax.swing.JComboBox;

import junit.framework.TestCase;

import com.google.common.collect.ImmutableList;
import com.palantir.ptoss.cinch.core.Bindings;
import com.palantir.ptoss.cinch.core.DefaultBindableModel;
import com.palantir.ptoss.cinch.swing.Bound;
import com.palantir.ptoss.cinch.swing.BoundSelection;

public class BoundJComboBoxTest extends TestCase {

    public static class JComboTestModel extends DefaultBindableModel {
        private String selectedComboItem;

        public List<String> getComboList() {
            return ImmutableList.of("Alpha", "Bravo", "Charlie");
        }

        public void setSelectedComboItem(String selectedComboItem) {
            this.selectedComboItem = selectedComboItem;
            update();
        }

        public String getSelectedComboItem() {
            return selectedComboItem;
        }
    }

    private final JComboTestModel model = new JComboTestModel();

    public static final String NULL_VALUE = "<--none-->";

    @Bound(to = "comboList", nullValue = "NULL_VALUE")
    @BoundSelection(to = "selectedComboItem", nullValue = "NULL_VALUE")
    private final JComboBox nullConstantBox = new JComboBox();

    @Bound(to = "comboList")
    @BoundSelection(to = "selectedComboItem")
    private final JComboBox noNullBox = new JComboBox();

    @Bound(to = "comboList", nullValue = "null value")
    @BoundSelection(to = "selectedComboItem", nullValue = "null value")
    private final JComboBox directNullBox = new JComboBox();

    private final Bindings bindings = new Bindings();

    @Override
    protected void setUp() {
        bindings.bind(BoundJComboBoxTest.this);
    }

    public void testNull() {
        assertEquals(4, nullConstantBox.getItemCount());
        assertEquals(NULL_VALUE, nullConstantBox.getSelectedItem());
        model.setSelectedComboItem("Alpha");
        assertEquals("Alpha", nullConstantBox.getSelectedItem());
        model.setSelectedComboItem(null);
        assertEquals(NULL_VALUE, nullConstantBox.getSelectedItem());
    }

    // TODO (dcervelli): enforce non-null model?
    public void testNoNull() {
        assertEquals(3, noNullBox.getItemCount());
    }

    public void testDirectNull() {
        assertEquals(4, directNullBox.getItemCount());
        assertEquals("null value", directNullBox.getSelectedItem());
        model.setSelectedComboItem("Alpha");
        assertEquals("Alpha", directNullBox.getSelectedItem());
        model.setSelectedComboItem(null);
        assertEquals("null value", directNullBox.getSelectedItem());
    }
}
