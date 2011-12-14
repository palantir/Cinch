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
package com.palantir.ptoss.cinch.example.demo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTextField;

import com.palantir.ptoss.cinch.core.Bindable;
import com.palantir.ptoss.cinch.core.Bindings;
import com.palantir.ptoss.cinch.core.CallOnUpdate;
import com.palantir.ptoss.cinch.example.Examples;
import com.palantir.ptoss.cinch.example.demo.DemoModel.DemoEnum;
import com.palantir.ptoss.cinch.swing.Action;
import com.palantir.ptoss.cinch.swing.Bound;
import com.palantir.ptoss.cinch.swing.BoundSelection;
import com.palantir.ptoss.cinch.swing.EnabledIf;
import com.palantir.ptoss.cinch.swing.OnClick;
import com.palantir.ptoss.cinch.swing.OnClick.Button;
import com.palantir.ptoss.cinch.swing.VisibleIf;

@Bindable
public class DemoView {

    private final Bindings bindings = new Bindings();
    private final JPanel panel = new JPanel();

    private final DemoModel demoModel = new DemoModel();

    @SuppressWarnings("unused") // used by binding
    @Bindable
    private final DemoController controller = new DemoController(demoModel);

    @Bound(to = "demoBoolean")  // TODO: autodiscover bound variable name
    private final JCheckBox demoBooleanCheckBox = new JCheckBox("Enable Button 1/Show Button 2");

    @Action(call = "demoAction")
    @EnabledIf(to = "demoBoolean")
    private final JButton demoButton1 = new JButton("Button 1");
    @Action(call = "demoAction2")
    @VisibleIf(to = "demoBoolean")
    private final JButton demoButton2 = new JButton("Button 2");

    @Bound(to = "demoRadioBoolean", value = "true")
    private final JRadioButton trueButton = new JRadioButton("True");
    @Bound(to = "demoRadioBoolean", value = "false")
    private final JRadioButton falseButton = new JRadioButton("False");

    @Bound(to = "demoEnum", value = "FOO")
    private final JRadioButton fooButton = new JRadioButton("Foo");
    @Bound(to = "demoEnum", value = "BAR")
    private final JRadioButton barButton = new JRadioButton("Bar");
    @Bound(to = "demoEnum", value = "BAZ")
    private final JRadioButton bazButton = new JRadioButton("Baz (Red Background)");
    @Action(call = "setToFoo")
    private final JButton setToFooButton = new JButton("Foo");

    @Bound(to = "filterText")
    private final JTextField filterText = new JTextField(15);
    @Bound(to = "filteredList", on = "LIST")
    @BoundSelection(to = "selectedItem")
    private final JList demoList = new JList();
    @Action(call = "changeList")
    private final JButton changeList = new JButton("Change List");
    @Action(call = "selectFox")
    private final JButton changeSelection = new JButton("Select Fox");
    @OnClick(call = "listClicked", button = Button.RIGHT, count = 2)
    private final JLabel listChangedLabel = new JLabel();

    @Bound(to = "demoMultiList", on = "MULTILIST")
    @BoundSelection(to = "multiSelectedItems", multi = true)
    private final JList demoMultiselectList = new JList();
    @Bound(to = "multiSelectedItems")
    private final JLabel multiselectContents = new JLabel();
    @Action(call = "selectMulti")
    private final JButton selectMulti = new JButton("Select Bravo/Delta");

    public static final String NULL_VALUE = "<--none-->";

    @Bound(to = "comboList", nullValue = "NULL_VALUE", on = "COMBO")
    @BoundSelection(to = "selectedComboItem", nullValue = "NULL_VALUE")
    private final JComboBox comboBox = new JComboBox();

    @Bound(to = "selectedComboItemString")
    private final JLabel selItem = new JLabel();

    private final DrawingCanvasModel canvasModel = new DrawingCanvasModel();

    private final DrawingCanvas canvas = new DrawingCanvas(canvasModel);
    @Bound(to = "mode", value = "POINT")
    private final JRadioButton pointButton = new JRadioButton("Points");
    @Bound(to = "mode", value = "LINE")
    private final JRadioButton lineButton = new JRadioButton("Lines");
    @Bound(to = "allowDrag")
    private final JCheckBox allowDrag = new JCheckBox("Allow Drag");
    @Action(call = "clear")
    private final JButton clearButton = new JButton("Clear");

    @Bound(to = "sliderValue")
    private final JSlider slider = new JSlider(0, 100);
    @Bound(to = "sliderValue")
    private final JLabel sliderValue = new JLabel();
    @Action(call = "setSlider")
    private final JButton setSlider = new JButton("Set to 50");

    private DemoView() {
        // private
    }

    public static DemoView create() {
        DemoView demo = new DemoView();
        demo.bindings.bind(demo);
        demo.showUi();
        return demo;
    }

    private static JPanel panelOf(JComponent... comps) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        for (JComponent comp : comps) {
            p.add(comp);
        }
        return p;
    }

    private void showUi() {
        JFrame frame = new JFrame("Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocation(100, 100);
        frame.setSize(600, 800);

        JPanel controlsPanel = new JPanel();
        controlsPanel.setOpaque(false);
        BoxLayout box = new BoxLayout(controlsPanel, BoxLayout.Y_AXIS);
        controlsPanel.setLayout(box);
        controlsPanel.add(panelOf(
                new JLabel("Enabling Button:"),
                demoBooleanCheckBox,
                demoButton1,
                demoButton2));
        controlsPanel.add(panelOf(
                new JLabel("Bind Radio Buttons to boolean:"),
                trueButton,
                falseButton));
        controlsPanel.add(new JSeparator());
        controlsPanel.add(panelOf(
                new JLabel("Bind Radio Buttons to enum:"),
                fooButton,
                barButton,
                bazButton,
                setToFooButton));
        controlsPanel.add(new JSeparator());
        controlsPanel.add(panelOf(
                new JLabel("Filtering JList:"),
                filterText,
                demoList,
                changeList,
                changeSelection,
                listChangedLabel));
        controlsPanel.add(new JSeparator());
        controlsPanel.add(panelOf(
                new JLabel("Multiselect JList:"),
                demoMultiselectList,
                multiselectContents,
                selectMulti));
        controlsPanel.add(new JSeparator());
        controlsPanel.add(panelOf(
                new JLabel("Bind JComboBox:"),
                comboBox,
                selItem));
        controlsPanel.add(new JSeparator());
        controlsPanel.add(panelOf(
                new JLabel("Slider:"),
                slider,
                sliderValue,
                setSlider));
        controlsPanel.add(new JSeparator());

        String text = "test\nlabel\ttab";
        JLabel testLabel = new JLabel(text);
        JTextField testField = new JTextField(text);
        controlsPanel.add(panelOf(
                testLabel,
                testField));

        controlsPanel.add(panelOf(
                new JLabel("Custom component:"),
                pointButton,
                lineButton,
                allowDrag,
                clearButton));
        controlsPanel.add(canvas);

        panel.setLayout(new BorderLayout());
        panel.add(controlsPanel, BorderLayout.NORTH);
        panel.add(canvas, BorderLayout.CENTER);
        frame.setContentPane(panel);
        frame.setVisible(true);
    }

    public void listClicked() {
        System.out.println("listClicked");
    }

    @CallOnUpdate(model = "demoModel")
    public void updateBackgroundColor() {
        Color color = null;
        if (demoModel.getDemoEnum() == DemoEnum.BAZ) {
            color = Color.RED;
        }
        panel.setBackground(color);
        panel.repaint();
    }

    int listChangeCount = 0;
    @CallOnUpdate(model = "demoModel", on = "LIST")
    public void listChanged() {
        ++listChangeCount;
        listChangedLabel.setText("list changes: " + listChangeCount);
    }

    public void setSlider() {
        demoModel.setSliderValue(50);
    }

    public static void main(String[] args) {
        Examples.initializeLogging();
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                DemoView.create();
            }
        });
    }
}
