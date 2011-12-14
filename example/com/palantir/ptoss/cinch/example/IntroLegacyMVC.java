package com.palantir.ptoss.cinch.example;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
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

import com.google.common.base.Objects;
import com.google.common.base.Strings;

public class IntroLegacyMVC {
	public static class IntroModel {
		private static final String TO_PROPERTY = "IntroModel.to";
		private static final String SUBJECT_PROPERTY = "IntroModel.subject";
		private static final String BODY_PROPERTY = "IntroModel.body";
		private String to;
		private String subject;
		private String body;
		private final PropertyChangeSupport support = new PropertyChangeSupport(this);
		
		public String getBody() {
	        return body;
        }
		
		public void setBody(String body) {
			String old = this.body;
	        this.body = body;
	        support.firePropertyChange(BODY_PROPERTY, old, this.body);
        }
		
		public String getSubject() {
	        return subject;
        }
		
		public void setSubject(String subject) {
			String old = this.subject;
	        this.subject = subject;
	        support.firePropertyChange(SUBJECT_PROPERTY, old, this.subject);
        }
		
		public String getTo() {
	        return to;
        }
		
		public void setTo(String to) {
			String old = this.to;
	        this.to = to;
	        support.firePropertyChange(TO_PROPERTY, old, this.to);
        }
		
		public void addPropertyChangeListener(PropertyChangeListener listener) {
			support.addPropertyChangeListener(listener);
		}
		
		public void removePropertyChangeListener(PropertyChangeListener listener) {
			support.removePropertyChangeListener(listener);
		}
		
		public void addPropertyChangeListener(String property, PropertyChangeListener listener) {
			support.addPropertyChangeListener(property, listener); 
		}
		
		public void removePropertyChangeListener(String property, PropertyChangeListener listener) {
			support.removePropertyChangeListener(property, listener);
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

    private final IntroModel model = new IntroModel();
    
    private final IntroController controller = new IntroController(model);
    
    private final JTextField toField = new JTextField();
    
    private final JTextField subjectField = new JTextField();
    
    private final JTextArea bodyArea = new JTextArea();
    
    private final JButton yellButton = new JButton("YELL!");
    
    private final JButton sendButton = new JButton("Send");

    private final JLabel messageLabel = new JLabel("");
    
    public IntroLegacyMVC() {
	    initializeInterface();
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
    
    private abstract class SimpleDocumentListener implements DocumentListener {
    	public void changedUpdate(DocumentEvent e) {
			onUpdate();			    
		}
		
		public void insertUpdate(DocumentEvent e) {
			onUpdate();			    
		}
		
		public void removeUpdate(DocumentEvent e) {
			onUpdate();
		}
		
		public abstract void onUpdate();
    }

    private void wireUi() {
	    sendButton.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
		        controller.sendEmail();
	        }
        });
	    
	    yellButton.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
		        controller.yell();
	        }
        });
	    
	    model.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (IntroModel.TO_PROPERTY.equals(evt.getPropertyName())) {
					if (!Objects.equal(toField.getText(), model.getTo())) {
						toField.setText(model.getTo());
					}
				} else if (IntroModel.SUBJECT_PROPERTY.equals(evt.getPropertyName())) {
					if (!Objects.equal(subjectField.getText(), model.getSubject())) {
						subjectField.setText(model.getSubject());
					}
    			} else if (IntroModel.BODY_PROPERTY.equals(evt.getPropertyName())) {
    				if (!Objects.equal(bodyArea.getText(), model.getBody())) {
    					bodyArea.setText(model.getBody());
    				}
    			}
				sendButton.setEnabled(model.isReady());
				messageLabel.setText(model.getCurrentMessage());
			}
		});
	    toField.getDocument().addDocumentListener(new SimpleDocumentListener() {
			@Override
			public void onUpdate() {
				model.setTo(toField.getText());
			}
		});
	    subjectField.getDocument().addDocumentListener(new SimpleDocumentListener() {
	    	@Override
	    	public void onUpdate() {
	    		model.setSubject(subjectField.getText());
	    	}
	    });
	    bodyArea.getDocument().addDocumentListener(new SimpleDocumentListener() {
	    	@Override
	    	public void onUpdate() {
	    		model.setBody(bodyArea.getText());
	    	}
	    });
	    sendButton.setEnabled(model.isReady());
	    messageLabel.setText(model.getCurrentMessage());
    }

	public JComponent getDisplayComponent() {
	    return panel;
    }
    
    public static void main(String[] args) throws InterruptedException, InvocationTargetException {
    	EventQueue.invokeAndWait(new Runnable() {
			public void run() {
				IntroLegacyMVC example = new IntroLegacyMVC();
				JFrame frame = Examples.getFrameFor("Legacy MVC Email Example", example.panel);
				frame.pack();
				frame.setVisible(true);
			}
		});
    }

}
