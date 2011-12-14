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

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.swing.*;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.palantir.ptoss.cinch.core.Bindable;
import com.palantir.ptoss.cinch.core.Bindings;
import com.palantir.ptoss.cinch.core.DefaultBindableModel;
import com.palantir.ptoss.cinch.example.Examples;
import com.palantir.ptoss.cinch.swing.Action;
import com.palantir.ptoss.cinch.swing.Bound;
import com.palantir.ptoss.cinch.swing.BoundSelection;

public class BoundJListExample {

    @SuppressWarnings("unused")
    private static class Model extends DefaultBindableModel {
        private List<String> items = ImmutableList.of("one", "two", "three", "four");
        private List<String> selectedItems = ImmutableList.of();

        public List<String> getItems() {
            return items;
        }

        public void setItems(List<String> items) {
            this.items = items;
            update();
        }

        public void setSelectedItems(List<String> selectedItems) {
            if (selectedItems == null) {
                selectedItems = ImmutableList.of();
            }
            this.selectedItems = ImmutableList.copyOf(selectedItems);
            update();
        }

        public List<String> getSelectedItems() {
            return selectedItems;
        }

        public String getSelectedString() {
            return "selected: " + Joiner.on(",").join(getSelectedItems());
        }
    }

    @SuppressWarnings("unused")
    private static class Controller {
        private final Model model;

        public Controller(Model model) {
            this.model = model;
        }

        public void changeItems() {
            if (model.getItems().contains("one")) {
                model.setItems(ImmutableList.of("two", "three", "four", "five", "six"));
            } else {
                model.setItems(ImmutableList.of("one", "two", "three", "four"));
            }
        }
    }

    private final Model model = new Model();

    @SuppressWarnings("unused")
    @Bindable
    private final Controller controller = new Controller(model);

    @Bound(to = "items")
    @BoundSelection(to = "selectedItems", multi = true)
    private final JList list = new JList();

    private final JPanel panel = new JPanel();

    @Action(call = "changeItems")
    private final JButton changeItems = new JButton("Change");

    @Bound(to = "selectedString")
    private final JLabel selectedLabel = new JLabel(" ");

    private final Bindings bindings = Bindings.standard();

    public BoundJListExample() {
        initializeInterface();
        bindings.bind(this);
    }

    private void initializeInterface() {
        panel.setLayout(new BorderLayout());
        panel.add(selectedLabel, BorderLayout.NORTH);
        panel.add(list, BorderLayout.CENTER);
        panel.add(changeItems, BorderLayout.SOUTH);

        JFrame frame = new JFrame("Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocation(100, 100);
        frame.setSize(400, 600);

        frame.setContentPane(panel);

        frame.setVisible(true);
    }

    public static void main(String[] args) throws InterruptedException, InvocationTargetException {
        Examples.initializeLogging();
        EventQueue.invokeAndWait(new Runnable() {
            public void run() {
                new BoundJListExample();
            }
        });
    }
}
