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
package com.palantir.ptoss.cinch.example;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.google.common.base.Strings;
import com.palantir.ptoss.cinch.core.Bindable;
import com.palantir.ptoss.cinch.core.Bindings;
import com.palantir.ptoss.cinch.core.DefaultBindableModel;
import com.palantir.ptoss.cinch.swing.Action;
import com.palantir.ptoss.cinch.swing.Bound;
import com.palantir.ptoss.cinch.swing.EnabledIf;

public class IntroCinchMVC {

    public static class IntroModel extends DefaultBindableModel {
        private String to = "";
        private String subject = "";
        private String body = "";

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
            update();
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
            update();
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
            update();
        }

        public String getCurrentMessage() {
            if (Strings.isNullOrEmpty(to)) {
                return "Fill out 'To' field.";
            }
            if (Strings.isNullOrEmpty(subject)) {
                return "Fill out 'Subject' field.";
            }
            if (Strings.isNullOrEmpty(body)) {
                return "Fill out 'Body'.";
            }
            return "Ready to send.";
        }

        public boolean isReady() {
            return !Strings.isNullOrEmpty(to) && !Strings.isNullOrEmpty(subject) && !Strings.isNullOrEmpty(body);
        }

        @Override
        public String toString() {
            return "IntroModel [to=" + to + ", subject=" + subject + ", body=" + body + "]";
        }
    }

    public static class IntroController {
        private final IntroModel model;

        public IntroController(IntroModel model) {
            this.model = model;
        }

        public void sendEmail() {
            System.out.println("Send: " + model);
        }

        public void yell() {
            model.setBody(model.getBody().toUpperCase());
        }
    }

    private final JPanel panel = new JPanel();

    private final Bindings bindings = new Bindings();

    private final IntroModel model = new IntroModel();

    @SuppressWarnings("unused")
    @Bindable
    private final IntroController controller = new IntroController(model);

    @Bound(to = "to")
    private final JTextField toField = new JTextField();

    @Bound(to = "subject")
    private final JTextField subjectField = new JTextField();

    @Bound(to = "body")
    private final JTextArea bodyArea = new JTextArea();

    @Action(call = "yell")
    private final JButton yellButton = new JButton("YELL!");

    @Action(call = "sendEmail")
    @EnabledIf(to = "ready")
    private final JButton sendButton = new JButton("Send");


    @Bound(to = "currentMessage")
    private final JLabel messageLabel = new JLabel("");

    public IntroCinchMVC() {
        initializeInterface();
        bindings.bind(this);
    }

    private void initializeInterface() {
        JPanel toPanel = new JPanel(new BorderLayout());
        toPanel.add(new JLabel("To"), BorderLayout.NORTH);
        toPanel.add(toField, BorderLayout.CENTER);
        toPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        JPanel subjectPanel = new JPanel(new BorderLayout());
        subjectPanel.add(new JLabel("Subject"), BorderLayout.NORTH);
        subjectPanel.add(subjectField, BorderLayout.CENTER);
        subjectPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        JPanel bodyPanel = new JPanel(new BorderLayout());
        bodyPanel.add(new JLabel("Body"), BorderLayout.NORTH);
        bodyPanel.add(new JScrollPane(bodyArea), BorderLayout.CENTER);
        bodyPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(toPanel, BorderLayout.NORTH);
        topPanel.add(subjectPanel, BorderLayout.SOUTH);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(messageLabel, BorderLayout.WEST);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(yellButton);
        buttonPanel.add(sendButton);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        bodyArea.setPreferredSize(new Dimension(400, 200));

        panel.setLayout(new BorderLayout());
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(bodyPanel, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    public JComponent getDisplayComponent() {
        return panel;
    }

    public static void main(String[] args) throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(new Runnable() {
            public void run() {
                IntroCinchMVC example = new IntroCinchMVC();
                JFrame frame = Examples.getFrameFor("Cinch Email Example", example.panel);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }
}
