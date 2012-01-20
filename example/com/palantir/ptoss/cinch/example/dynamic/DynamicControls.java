// Copyright 2011 Palantir Technologies
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.palantir.ptoss.cinch.example.dynamic;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import com.google.common.collect.Lists;
import com.palantir.ptoss.cinch.core.Bindings;
import com.palantir.ptoss.cinch.core.CallOnUpdate;
import com.palantir.ptoss.cinch.core.DefaultBindableModel;
import com.palantir.ptoss.cinch.example.Examples;
import com.palantir.ptoss.cinch.swing.Bound;

/**
 * A contrived example of how to use Cinch to bind a dynamic set of controls. The UI shows a slider
 * between 1 and 10 that controls how many checkboxes should be shown.
 */
public class DynamicControls {

    public static class BooleanModel extends DefaultBindableModel {
        boolean state = false;

        public boolean isState() {
            return state;
        }

        public void setState(boolean state) {
            this.state = state;
            update();
        }
    }

    public static class DynamicModel extends DefaultBindableModel {
        // The number of dynamic controls
        int count;

        // A collection of BindableModels for our dynamic controls
        final List<BooleanModel> models = Lists.newArrayList();

        public DynamicModel() {
            setCount(5);
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            if (count < 1) {
                count = 1;
            }
            if (count > 10) {
                count = 10;
            }
            if (this.count == count) {
                return;
            }
            this.count = count;
            models.clear();
            for (int i = 0; i < count; i++) {
                models.add(new BooleanModel());
            }
            update();
        }

        public List<BooleanModel> getModels() {
            return models;
        }
    }

    // In this contrived example we're simply wrapping up a JCheckbox but this could be something
    // arbitrarily complex
    private static class BooleanComponent {
        final BooleanModel model;

        @Bound(to = "state")
        final JCheckBox box = new JCheckBox("State");

        final Bindings bindings = new Bindings();

        public BooleanComponent(BooleanModel model) {
            this.model = model;
            this.bindings.bind(this);
        }

        public JComponent getDisplayComponent() {
            return box;
        }

        public void dispose() {
            bindings.release(model);
        }
    }
    
    private final DynamicModel model = new DynamicModel();

    private final JPanel panel = new JPanel();
    
    @Bound(to = "count")
    private final JSlider slider = new JSlider(1, 10);
    
    private final List<BooleanComponent> checkboxComponents = Lists.newArrayList();
    private final JPanel checkboxPanel = new JPanel();

    private final Bindings bindings = Bindings.standard();;

    public DynamicControls() {
        initializeInterface();
        bindings.bind(this);
    }

    private void initializeInterface() {
        JPanel toPanel = new JPanel(new BorderLayout());
        toPanel.add(new JLabel("Count"), BorderLayout.WEST);
        slider.setPaintLabels(true);
        slider.setLabelTable(slider.createStandardLabels(1));
        slider.setSnapToTicks(true);
        toPanel.add(slider, BorderLayout.CENTER);

        panel.setLayout(new BorderLayout());
        panel.add(toPanel, BorderLayout.NORTH);
        checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS));
        checkboxPanel.setPreferredSize(new Dimension(200, 300));
        panel.add(checkboxPanel, BorderLayout.CENTER);

        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

	@SuppressWarnings("unused")
	@CallOnUpdate
    private void synchCheckboxes() {
        // It's important to remove transient listeners to long-lived models
        for (BooleanComponent component : checkboxComponents) {
            component.dispose();
        }
        
        checkboxPanel.removeAll();
        checkboxComponents.clear();
        
        for (BooleanModel booleanModel : model.getModels()) {
            BooleanComponent booleanComponent = new BooleanComponent(booleanModel);
            checkboxComponents.add(booleanComponent);
            checkboxPanel.add(booleanComponent.getDisplayComponent());
        }

        checkboxPanel.revalidate();
        checkboxPanel.repaint();
    }

    public JComponent getDisplayComponent() {
        return panel;
    }

    public static void main(String[] args) throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(new Runnable() {
            public void run() {
                Examples.initializeLogging();
                DynamicControls example = new DynamicControls();
                JFrame frame = Examples.getFrameFor("Cinch Dynamic Controls Example", example.panel);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }
}
