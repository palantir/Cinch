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

import java.util.Arrays;
import java.util.List;

import javax.swing.JList;

import junit.framework.TestCase;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.palantir.ptoss.cinch.core.Bindings;
import com.palantir.ptoss.cinch.core.DefaultBindableModel;
import com.palantir.ptoss.cinch.core.ModelUpdate;
import com.palantir.ptoss.cinch.swing.Bound;
import com.palantir.ptoss.cinch.swing.BoundSelection;

public class BoundJListTest extends TestCase {

	public static class Model extends DefaultBindableModel {
		public enum Update implements ModelUpdate {
			NO_TRIGGER, SPECIFIC;
		}
		
		private List<String> values = Lists.newArrayList();
		private List<String> selectedValues = Lists.newArrayList();
		
		public List<String> getValues() {
	        return values;
        }
		
		public void setValues(List<String> values) {
	        this.values = values;
	        update();
        }
		
		public void setValuesSpecific(List<String> values) {
			this.values = values;
			modelUpdated(Update.SPECIFIC);
		}
		
		public void setValuesNoTrigger(List<String> values) {
			this.values = values;
			modelUpdated(Update.NO_TRIGGER);
		}
		
		public List<String> getSelectedValues() {
	        return selectedValues;
        }
		
		public void setSelectedValues(List<String> selectedValues) {
			if (selectedValues == null) {
				this.selectedValues = Lists.newArrayList();
			} else {
				this.selectedValues = Lists.newArrayList(selectedValues);
			}
	        update();
        }
		
		public String getSelectedValue() {
			if (selectedValues == null || selectedValues.isEmpty()) {
				return null;
			}
			return selectedValues.get(0);
		}
		
		public void setSelectedValue(String value) {
			selectedValues.clear();
			if (value != null) {
				selectedValues.add(value);
			}
			update();
		}
	}

	private final Model model = new Model();
	
	@Bound(to = "model.values")
	@BoundSelection(to = "model.selectedValue")
	private final JList list = new JList();
	
	private final Model multiModel = new Model();
	@Bound(to = "multiModel.values")
	@BoundSelection(to = "multiModel.selectedValues", multi = true)
	private final JList multiList = new JList();
	
	@Bound(to = "model.values", on = "SPECIFIC")
	private final JList specificList = new JList();
	
	private final Bindings bindings = Bindings.standard();
	
	@Override
	protected void setUp() throws Exception {
		bindings.bind(this);
		list.setName("standard");
		specificList.setName("specific");
	}
	
	public void testInitial() {
		assertEquals(0, list.getModel().getSize());
	}
	
	public void testSimple() {
		model.setValues(ImmutableList.of("one", "two", "three"));
		assertEquals("one", list.getModel().getElementAt(0));
		assertEquals("two", list.getModel().getElementAt(1));
		assertEquals("three", list.getModel().getElementAt(2));
	}
	
	public void testNullList() {
		model.setValues(ImmutableList.of("one", "two", "three"));
		assertEquals(3, list.getModel().getSize());		
		model.setValues(null);
		assertEquals(0, list.getModel().getSize());		
	}
	
	public void testMaintainSelection() {
		model.setValues(ImmutableList.of("one", "two", "three"));
		assertEquals(3, list.getModel().getSize());
		assertNull(list.getSelectedValue());
		list.setSelectedIndex(1);
		assertEquals(1, list.getSelectedIndex());
		assertEquals("two", list.getSelectedValue());
		model.setValues(ImmutableList.of("two", "three", "four"));
		assertEquals(0, list.getSelectedIndex());
		assertEquals("two", list.getSelectedValue());
		model.setValues(ImmutableList.of("three", "four", "five"));
		assertEquals(-1, list.getSelectedIndex());
		assertNull(list.getSelectedValue());
		
		list.setSelectedIndices(new int[] {0, 2});
		model.setValues(ImmutableList.of("one", "two", "three", "four", "five"));
		assertTrue(Arrays.equals(new int[] {2, 4}, list.getSelectedIndices()));
		model.setValues(ImmutableList.of("six", "five"));
		assertTrue(Arrays.equals(new int[] {1}, list.getSelectedIndices()));
	}
	
	public void testSpecific() {
		assertEquals(0, specificList.getModel().getSize());
		model.setValuesNoTrigger(ImmutableList.of("one", "two", "three"));
		assertEquals(0, specificList.getModel().getSize());
		model.setValuesSpecific(ImmutableList.of("one", "two", "three"));
		assertEquals(3, specificList.getModel().getSize());
	}
	
	public void testSimpleSelection() {
		simpleSelectionTest(model, list);
	}
	
	public void simpleSelectionTest(Model model, JList list) {
		model.setValues(ImmutableList.of("one", "two", "three"));
		assertEquals(null, model.getSelectedValue());
		model.setSelectedValue("one");
		assertEquals("one", model.getSelectedValue());
		assertEquals("one", list.getSelectedValue());
		list.setSelectedIndex(1);
		assertEquals("two", model.getSelectedValue());
		assertEquals("two", list.getSelectedValue());
		model.setSelectedValue(null);
		assertNull(model.getSelectedValue());
		assertNull(list.getSelectedValue());
		model.setSelectedValue("one");
		assertEquals("one", model.getSelectedValue());
		assertEquals("one", list.getSelectedValue());
		list.clearSelection();
		assertNull(model.getSelectedValue());
		assertNull(list.getSelectedValue());
	}
	
	public void testMultiSelection() {
		simpleSelectionTest(multiModel, multiList);
		
		multiModel.setSelectedValues(ImmutableList.of("one", "three"));
		assertEquals(ImmutableList.of("one", "three"), multiModel.getSelectedValues());
		assertTrue(Arrays.equals(new String[] {"one", "three"}, multiList.getSelectedValues()));
		
		multiList.setSelectedIndices(new int[] {1, 2});
		assertEquals(ImmutableList.of("two", "three"), multiModel.getSelectedValues());
		assertTrue(Arrays.equals(new String[] {"two", "three"}, multiList.getSelectedValues()));
		
		multiModel.setSelectedValue(null);
		assertTrue(multiModel.getSelectedValues().isEmpty());
		assertTrue(multiList.getSelectedValues().length == 0);
		
		multiModel.setSelectedValues(ImmutableList.of("one", "three"));
		assertEquals(ImmutableList.of("one", "three"), multiModel.getSelectedValues());
		assertTrue(Arrays.equals(new String[] {"one", "three"}, multiList.getSelectedValues()));
		
		multiList.clearSelection();
		assertTrue(multiModel.getSelectedValues().isEmpty());
		assertTrue(multiList.getSelectedValues().length == 0);
	}
}
