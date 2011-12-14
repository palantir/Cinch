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

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.palantir.ptoss.cinch.core.Bindable;
import com.palantir.ptoss.cinch.core.Bindings;
import com.palantir.ptoss.cinch.core.DefaultBindableModel;
import com.palantir.ptoss.cinch.example.Examples;
import com.palantir.ptoss.cinch.swing.Action;
import com.palantir.ptoss.cinch.swing.Bound;

/**
 * <p>
 * This example shows how controls can be bound to methods on a controller object, 
 * <code>controller</code>.
 * </p>
 * <p>
 * Note that <code>controller</code> must be marked as {@link Bindable} to enable this behavior.
 * </p>
 * @see Action
 * @see Bindable
 * @see Bound
 */
public class BoundJCheckBoxExample {
	public static class Model extends DefaultBindableModel {
		private boolean state;
		
		public void setState(boolean state) {
	        this.state = state;
	        update();
        }
		
		public boolean isState() {
	        return state;
        }
	}
	
	public static class Controller {
		private final Model model;

		public Controller(Model model) {
	        this.model = model;
        }
		
		public void setToTrue() {
			model.setState(true);
		}
		
		public void setToFalse() {
			model.setState(false);
		}
	}
	
	private final Model model = new Model();
	@SuppressWarnings("unused")
	@Bindable
	private final Controller controller = new Controller(model);
	
	@Bound(to = "state")
	private final JCheckBox box = new JCheckBox("State");
	@Bound(to = "state")
	private final JLabel stateLabel = new JLabel("?");
	
	@Action(call = "setToTrue")
	private final JButton trueButton = new JButton("Set True");
	@Action(call = "setToFalse")
	private final JButton falseButton = new JButton("Set False");
	
	private final JPanel panel = new JPanel();
	
	private final Bindings bindings = Bindings.standard();
	
	public BoundJCheckBoxExample() {
		panel.setLayout(new BorderLayout());
		panel.add(box, BorderLayout.CENTER);
		panel.add(stateLabel, BorderLayout.SOUTH);
		JPanel buttons = new JPanel();
		buttons.add(trueButton);
		buttons.add(falseButton);
		panel.add(buttons, BorderLayout.NORTH);
		bindings.bind(this);
    }
	
	public static void main(String[] args) throws InterruptedException, InvocationTargetException {
		EventQueue.invokeAndWait(new Runnable() {
	        public void run() {
	        	BoundJCheckBoxExample example = new BoundJCheckBoxExample();
	        	JFrame frame = Examples.getFrameFor("Cinch JCheckBox Example", example.panel);
	        	frame.pack();
				frame.setVisible(true);
	        }
        });
    }
}
