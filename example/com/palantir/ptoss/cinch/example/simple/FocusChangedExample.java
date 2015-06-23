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
package com.palantir.ptoss.cinch.example.simple;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.palantir.ptoss.cinch.core.Bindable;
import com.palantir.ptoss.cinch.core.Bindings;
import com.palantir.ptoss.cinch.example.Examples;
import com.palantir.ptoss.cinch.swing.OnFocusChange;

@SuppressWarnings("unused")
@Bindable
public class FocusChangedExample {

    static final Logger log = LoggerFactory.getLogger(FocusChangedExample.class);

    public FocusChangedExample() {
        initializeInterface();
        bindings.bind(this);
    }

    private final Bindings bindings = Bindings.standard();

    private static Color SELECTED = Color.YELLOW;
    private static Color UNSELECTED = new Color(128,128,255);

    private JLabel aLabel = new JLabel("A");
    private JLabel bLabel = new JLabel("B");
    private JLabel cLabel = new JLabel("C");

    @OnFocusChange(gained = "aGained", lost = "aLost")
    private JTextField aInput = new JTextField();
    @OnFocusChange(gained = "bGained", lost = "bLost")
    private JTextField bInput = new JTextField();
    @OnFocusChange(gained = "cGained", lost = "cLost")
    private JTextField cInput = new JTextField();

    private final JPanel panel = new JPanel();


    void styleLabel(JLabel c) {
        c.setOpaque(true);
        c.setBackground(UNSELECTED);
        c.setHorizontalAlignment(SwingConstants.CENTER);
    }

    void styleInput(JTextField c) {
        c.setPreferredSize(new Dimension(75, c.getPreferredSize().height));
    }
    void initializeInterface() {
        styleLabel(aLabel);
        styleLabel(bLabel);
        styleLabel(cLabel);

        styleInput(aInput);
        styleInput(bInput);
        styleInput(cInput);

        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints(); // insets
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0,3,1,3);
        panel.add(aLabel, gbc );
        gbc.gridx = 1;
        panel.add(bLabel, gbc);
        gbc.gridx = 2;
        panel.add(cLabel, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(aInput, gbc);
        gbc.gridx = 1;
        panel.add(bInput, gbc);
        gbc.gridx = 2;
        panel.add(cInput, gbc);

        JFrame frame = new JFrame(getClass().getSimpleName());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocation(100, 100);
        frame.setSize(275,100);

        frame.setContentPane(panel);

        frame.setVisible(true);

    }
    private void aGained() {
        gainedFocus(aLabel);
    }

    private void aLost() {
        lostFocus(aLabel);
    }

    private void bGained() {
        gainedFocus(bLabel);
    }

    private void bLost() {
        lostFocus(bLabel);
    }

    private void cGained() {
        gainedFocus(cLabel);
    }

    private void cLost() {
        lostFocus(cLabel);
    }

    private void lostFocus(JLabel c) {
        log.info("Focus lost by " + c.toString());
        c.setBackground(UNSELECTED);
    }

    private void gainedFocus(JLabel c) {
        log.info("Focus gained by " + c.toString());
        c.setBackground(SELECTED);
    }

    public static void main(String[] args) throws InterruptedException, InvocationTargetException {
        Examples.initializeLogging();
        // set to Metal LaF
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            EventQueue.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    new FocusChangedExample();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
