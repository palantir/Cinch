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
package com.palantir.ptoss.cinch.example.superdemo;

import java.awt.EventQueue;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.palantir.ptoss.cinch.core.BindableModel;
import com.palantir.ptoss.cinch.core.Bindings;
import com.palantir.ptoss.cinch.swing.Bound;
import com.palantir.ptoss.cinch.swing.EnabledIf;

/**
 * This example shows off the following features:
 * <ol>
 * <li>Binding a control to a model field: <code>box</code> is bound to the <code>someBoolean</code>
 * field on <code>model</code>.</li>
 * <li>The {@link EnabledIf} annotation: <code>button</code> is only enabled when
 * <code>someBoolean</code> is set to <em>true</em>.</li>
 * <li>The implicit model binding: <code>model</code> is implicitly bound since it's an instance of
 * {@link BindableModel}.</li>
 * </ol>
 * @author regs
 *
 */
public class SuperDemo {

    protected final JPanel panel = new JPanel();
    @Bound(to = "someBoolean")
    private final JCheckBox box = new JCheckBox("Box");
    @EnabledIf(to = "someBoolean")
    private final JButton button = new JButton("Button");
    private final Bindings bindings = new Bindings();

    @SuppressWarnings("unused") // binding
    private final SuperDemoModel model = new SuperDemoModel();

    protected SuperDemo() {
        setupPanel();
        bindings.bind(this);
    }

    protected void setupPanel() {
//        panel.setLayout(new MigLayout());
        panel.add(box);
        panel.add(button, "wrap");
    }

    protected void showUi() {
        JFrame frame = new JFrame("Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocation(100, 100);
        frame.setSize(600, 600);

        frame.setContentPane(panel);
        frame.setVisible(true);
    }

    public static SuperDemo create() {
        SuperDemo demo = new SuperDemo();
        demo.showUi();
        return demo;
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                SuperDemo.create();
            }
        });
    }
}
