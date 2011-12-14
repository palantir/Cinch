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

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import junit.framework.TestCase;

import com.palantir.ptoss.cinch.core.Bindable;
import com.palantir.ptoss.cinch.core.Bindings;
import com.palantir.ptoss.cinch.core.CallOnUpdate;
import com.palantir.ptoss.cinch.core.ModelUpdates;
import com.palantir.ptoss.cinch.example.demo.DemoController;
import com.palantir.ptoss.cinch.example.demo.DemoModel;
import com.palantir.ptoss.cinch.example.demo.DemoModel.DemoEnum;
import com.palantir.ptoss.cinch.swing.Action;
import com.palantir.ptoss.cinch.swing.Bound;
import com.palantir.ptoss.cinch.swing.BoundSelection;


public class BindingsSubclassTest extends TestCase {

	@Bindable
	public static class View {
		@Bound(to = "demoBoolean")  // TODO: autodiscover bound variable name
		private final JCheckBox demoBooleanCheckBox = new JCheckBox("demoBoolean");

		//	@Bound(to = "thisBoolean")  // TODO: autodiscover bound variable name
		//	private final JCheckBox thisBooleanCheckBox = new JCheckBox("thisBoolean");

		@Action(call = "demoAction")
		//	@EnabledIf(method = "isDemoBoolean")
		private final JButton demoButton1 = new JButton("demo1");

		@Action(call = "demoAction2")
		private final JButton demoButton2 = new JButton("demo2");

		@Action(call = "count")
		private final JButton countButton = new JButton("count");

		private final DemoModel demoModel = new DemoModel();

		@Bindable
		private final DemoController controller = new DemoController(demoModel);

		@Bound(to = "demoEnum", value = "FOO")
		private final JRadioButton fooButton = new JRadioButton("Foo");

		@Bound(to = "demoEnum", value = "BAR")
		private final JRadioButton barButton = new JRadioButton("Bar");

		@Bound(to = "demoEnum", value = "BAZ")
		private final JRadioButton bazButton = new JRadioButton("Baz (Red Background)");

		@Action(call = "setToFoo")
		private final JButton setToFooButton = new JButton("Foo");

		@Bound(to = "demoList", on = "LIST")
		@BoundSelection(to = "selectedItem")
		private final JList demoList = new JList();

		@Action(call = "changeList")
		private final JButton changeList = new JButton("Change List");

		@Action(call = "selectFox")
		private final JButton changeSelection = new JButton("Select Fox");

		@Action(call = "controller.duplicate")
		private final JButton dup1 = new JButton("dup1");
		@Action(call = "this.duplicate")
		private final JButton dup2 = new JButton("dup2");

		@Bound(to = "demoString")
		private final JTextField textField = new JTextField();

		@Bound(to = "demoString")
		private final JLabel label = new JLabel();

		private final Bindings bindings = new Bindings();
		
		int count1 = 0;
		@CallOnUpdate(model = "demoModel")
		public void callback1() {
			count1++;
		}
		
		int count2 = 0;
		@CallOnUpdate(model = "demoModel", on = "LIST")
		public void callback2() {
			count2++;
		}
		
		int duplicateCount = 0;
		public void duplicate() {
			duplicateCount++;
		}
		
		int testCount = 0;
		public void count() {
			testCount++;
		}
	}

	private static class SubView extends View {
		//do nothing
	}
	
	private final View view = new SubView();
	
	@Override
	protected void setUp() throws Exception {
		view.bindings.bind(view);
	}
	
	public void testDuplicateMethods() {
		assertEquals(0, view.duplicateCount);
		assertEquals(0, view.controller.duplicateCount);
		view.dup1.doClick();
		assertEquals(0, view.duplicateCount);
		assertEquals(1, view.controller.duplicateCount);
		view.dup2.doClick();
		assertEquals(1, view.duplicateCount);
		assertEquals(1, view.controller.duplicateCount);
	}
	
	boolean thisBoolean;
	public void setThisBoolean(boolean state) {
		thisBoolean = state;
	}
	
	public boolean isThisBoolean() {
	    return thisBoolean;
    }
	
	public void testOne() {
		assertEquals(false, view.demoModel.isDemoBoolean());
		view.demoBooleanCheckBox.doClick();
		assertEquals(true, view.demoModel.isDemoBoolean());
	}
	
	public void testTwo() {
		assertEquals(false, view.demoBooleanCheckBox.isSelected());
		view.demoModel.setDemoBoolean(true);
		assertEquals(true, view.demoBooleanCheckBox.isSelected());
	}
	
	public void testDoActionFar() {
		assertEquals(0, view.controller.demo1ActionCount);
		view.demoButton1.doClick();
		assertEquals(1, view.controller.demo1ActionCount);
	}
	
	public void testTextField() {
		assertEquals("", view.demoModel.getDemoString());
		view.textField.setText("abc");
		assertEquals("abc", view.demoModel.getDemoString());
		view.demoModel.setDemoString("def");
		assertEquals("def", view.textField.getText());
	}
	
	public void testLabel() {
		assertEquals("", view.demoModel.getDemoString());
		view.demoModel.setDemoString("def");
		assertEquals("def", view.label.getText());
	}
	
//	public void testLocal() {
//		assertEquals(false, thisBoolean);
//		thisBooleanCheckBox.doClick();
//		assertEquals(true, thisBoolean);
//	}
	
	public void testDoActionLocal() {
		assertEquals(0, view.testCount);
		view.countButton.doClick();
		assertEquals(1, view.testCount);
    }
	
	public void testRadioButtons() {
		view.demoModel.setDemoEnum(DemoEnum.FOO);
		assertEquals(DemoEnum.FOO, view.demoModel.getDemoEnum());
		view.barButton.doClick();
		assertEquals(DemoEnum.BAR, view.demoModel.getDemoEnum());
		view.demoModel.setDemoEnum(DemoEnum.BAZ);
		assertEquals(true, view.bazButton.isSelected());
		view.setToFooButton.doClick();
		assertEquals(true, view.fooButton.isSelected());
		assertEquals(DemoEnum.FOO, view.demoModel.getDemoEnum());
	}
	
	
	public void testCallbacks() {
		assertEquals(1, view.count1);
		assertEquals(1, view.count2);
		view.demoModel.update();
		assertEquals(2, view.count1);
		assertEquals(1, view.count2);
		view.demoModel.modelUpdated(DemoModel.UpdateType.LIST);
		assertEquals(3, view.count1);
		assertEquals(2, view.count2);
		view.demoModel.modelUpdated(DemoModel.UpdateType.OTHER);
		assertEquals(4, view.count1);
		assertEquals(2, view.count2);
		view.demoModel.modelUpdated(ModelUpdates.ALL);
		assertEquals(5, view.count1);
		assertEquals(3, view.count2);
	}
	
	public void testList1() {
		assertEquals("Quick", view.demoList.getModel().getElementAt(0));
		assertEquals(5, view.demoList.getModel().getSize());
		view.changeList.doClick();
		assertEquals("The", view.demoList.getModel().getElementAt(0));
		assertEquals(7, view.demoList.getModel().getSize());
	}
	
	public void testList2() {
		view.demoList.setSelectedIndex(1);
		assertEquals("Quickly", view.demoModel.getSelectedItem());
		view.changeSelection.doClick();
		assertEquals("Fox", view.demoModel.getSelectedItem());
		view.demoModel.setSelectedItem("Quick");
		assertEquals("Quick", view.demoList.getSelectedValue());
	}
}

