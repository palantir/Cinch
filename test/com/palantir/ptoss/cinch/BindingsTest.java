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

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import junit.framework.TestCase;

import com.google.common.collect.ImmutableList;
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

@Bindable
public class BindingsTest extends TestCase {
    @Bound(to = "demoBoolean")  // TODO: autodiscover bound variable name
    private final JCheckBox demoBooleanCheckBox = new JCheckBox("demoBoolean");

//    @Bound(to = "thisBoolean")  // TODO: autodiscover bound variable name
//    private final JCheckBox thisBooleanCheckBox = new JCheckBox("thisBoolean");

    @Action(call = "demoAction")
//    @EnabledIf(method = "isDemoBoolean")
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

    @Bound(to = "demoMultiList", on = "MULTILIST")
    @BoundSelection(to = "multiSelectedItems", multi = true)
    private final JList demoMultiselectList = new JList();

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

    @Override
    protected void setUp() throws Exception {
        bindings.bind(this);
    }

    public void testDuplicateMethods() {
        assertEquals(0, duplicateCount);
        assertEquals(0, controller.duplicateCount);
        dup1.doClick();
        assertEquals(0, duplicateCount);
        assertEquals(1, controller.duplicateCount);
        dup2.doClick();
        assertEquals(1, duplicateCount);
        assertEquals(1, controller.duplicateCount);
    }

    int duplicateCount = 0;
    public void duplicate() {
        duplicateCount++;
    }

    int testCount = 0;
    public void count() {
        testCount++;
    }

    boolean thisBoolean;
    public void setThisBoolean(boolean state) {
        thisBoolean = state;
    }

    public boolean isThisBoolean() {
        return thisBoolean;
    }

    public void testOne() {
        assertEquals(false, demoModel.isDemoBoolean());
        demoBooleanCheckBox.doClick();
        assertEquals(true, demoModel.isDemoBoolean());
    }

    public void testTwo() {
        assertEquals(false, demoBooleanCheckBox.isSelected());
        demoModel.setDemoBoolean(true);
        assertEquals(true, demoBooleanCheckBox.isSelected());
    }

    public void testDoActionFar() {
        assertEquals(0, controller.demo1ActionCount);
        demoButton1.doClick();
        assertEquals(1, controller.demo1ActionCount);
    }

    public void testTextField() {
        assertEquals("", demoModel.getDemoString());
        textField.setText("abc");
        assertEquals("abc", demoModel.getDemoString());
        demoModel.setDemoString("def");
        assertEquals("def", textField.getText());
    }

    public void testLabel() {
        assertEquals("", demoModel.getDemoString());
        demoModel.setDemoString("def");
        assertEquals("def", label.getText());
    }

//    public void testLocal() {
//        assertEquals(false, thisBoolean);
//        thisBooleanCheckBox.doClick();
//        assertEquals(true, thisBoolean);
//    }

    public void testDoActionLocal() {
        assertEquals(0, testCount);
        countButton.doClick();
        assertEquals(1, testCount);
    }

    public void testRadioButtons() {
        demoModel.setDemoEnum(DemoEnum.FOO);
        assertEquals(DemoEnum.FOO, demoModel.getDemoEnum());
        barButton.doClick();
        assertEquals(DemoEnum.BAR, demoModel.getDemoEnum());
        demoModel.setDemoEnum(DemoEnum.BAZ);
        assertEquals(true, bazButton.isSelected());
        setToFooButton.doClick();
        assertEquals(true, fooButton.isSelected());
        assertEquals(DemoEnum.FOO, demoModel.getDemoEnum());
    }

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

    public void testCallbacks() {
        assertEquals(1, count1);
        assertEquals(1, count2);
        demoModel.update();
        assertEquals(2, count1);
        assertEquals(1, count2);
        demoModel.modelUpdated(DemoModel.UpdateType.LIST);
        assertEquals(3, count1);
        assertEquals(2, count2);
        demoModel.modelUpdated(DemoModel.UpdateType.OTHER);
        assertEquals(4, count1);
        assertEquals(2, count2);
        demoModel.modelUpdated(ModelUpdates.ALL);
        assertEquals(5, count1);
        assertEquals(3, count2);
    }

    public void testList1() {
        assertEquals("Quick", demoList.getModel().getElementAt(0));
        assertEquals(5, demoList.getModel().getSize());
        changeList.doClick();
        assertEquals("The", demoList.getModel().getElementAt(0));
        assertEquals(7, demoList.getModel().getSize());
    }

    public void testList2() {
        demoList.setSelectedIndex(1);
        assertEquals("Quickly", demoModel.getSelectedItem());
        changeSelection.doClick();
        assertEquals("Fox", demoModel.getSelectedItem());
        demoModel.setSelectedItem("Quick");
        assertEquals("Quick", demoList.getSelectedValue());
    }

    public void testMultiList() {
        demoMultiselectList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        assertEquals(0, demoMultiselectList.getSelectedValues().length);
        demoMultiselectList.setSelectedIndices(new int[] { 1, 2, 4 });
        List<String> items = demoModel.getMultiSelectedItems();
        assertEquals(3, items.size());
        assertEquals("bravo", items.get(0));
        assertEquals("charlie", items.get(1));
        assertEquals("echo", items.get(2));
        demoModel.setMultiSelectedItems(ImmutableList.of("bravo", "delta"));
        Object[] selectedValues = demoMultiselectList.getSelectedValues();
        assertEquals("bravo", selectedValues[0]);
        assertEquals("delta", selectedValues[1]);
    }
}

