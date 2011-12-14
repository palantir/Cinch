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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.google.common.base.Strings;

public class IntroNoMVC {

	private final JPanel panel = new JPanel();

	private final JTextField toField = new JTextField();
	private final JTextField subjectField = new JTextField();
	private final JTextArea bodyArea = new JTextArea();
	private final JButton yellButton = new JButton("YELL!");
	private final JButton sendButton = new JButton("Send");
	private final JLabel messageLabel = new JLabel("");

	public IntroNoMVC() {
		initializeInterface();
		onUpdate();
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
    	
    	wireUi();
	}
	
	private void wireUi() {
		sendButton.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	String content = "[to=" + toField.getText() + ", subject=" + subjectField.getText() + ", body=" + bodyArea.getText() + "]";
	        	System.out.println("Send " + content);
	        }
        });
		
		yellButton.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
		        bodyArea.setText(bodyArea.getText().toUpperCase());
	        }
        });

		DocumentListener enableTracker = new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				onUpdate();			    
			}
			
			public void insertUpdate(DocumentEvent e) {
				onUpdate();			    
			}
			
			public void removeUpdate(DocumentEvent e) {
				onUpdate();
			}
		};
		toField.getDocument().addDocumentListener(enableTracker);
		subjectField.getDocument().addDocumentListener(enableTracker);
		bodyArea.getDocument().addDocumentListener(enableTracker);
	}
	
	private void onUpdate() {
		sendButton.setEnabled(isReady());
		messageLabel.setText(getCurrentMessage());
	}

	public String getCurrentMessage() {
		if (Strings.isNullOrEmpty(toField.getText())) {
			return "Fill out 'To' field.";
		} 
		if (Strings.isNullOrEmpty(subjectField.getText())) {
			return "Fill out 'Subject' field.";
		} 
		if (Strings.isNullOrEmpty(bodyArea.getText())) {
			return "Fill out 'Body'.";
		} 
		return "Ready to send.";
	}

	public boolean isReady() {
		return !Strings.isNullOrEmpty(toField.getText()) && !Strings.isNullOrEmpty(subjectField.getText()) && !Strings.isNullOrEmpty(bodyArea.getText());
	}
	
	public JComponent getDisplayComponent() {
		return panel;
	}

	public static void main(String[] args) throws InterruptedException, InvocationTargetException {
		EventQueue.invokeAndWait(new Runnable() {
			public void run() {
				IntroNoMVC example = new IntroNoMVC();
				JFrame frame = Examples.getFrameFor("No-MVC Email Example", example.panel);
				frame.pack();
				frame.setVisible(true);
			}
		});
	}
}
