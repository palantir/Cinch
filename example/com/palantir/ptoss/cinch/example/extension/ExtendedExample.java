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
package com.palantir.ptoss.cinch.example.extension;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.palantir.ptoss.cinch.core.Bindings;
import com.palantir.ptoss.cinch.core.DefaultBindableModel;
import com.palantir.ptoss.cinch.core.ModelUpdate;
import com.palantir.ptoss.cinch.example.Examples;
import com.palantir.ptoss.cinch.swing.Bound;

/**
 * An example class that uses extended bindings. Note the use of the {@link LoggedModel} 
 * annotation on the <code>model</code> field.
 * @see ExtendedBindings 
 * @see LoggedModel
 */
public class ExtendedExample {
	public static class Model extends DefaultBindableModel {
		boolean state;
		boolean specific;
		
		public enum UpdateType implements ModelUpdate {
			SPECIFIC;
		}
		
		public void setState(boolean state) {
	        this.state = state;
	        update();
        }
		
		public boolean isState() {
	        return state;
        }
		
		public void setSpecific(boolean specific) {
	        this.specific = specific;
	        modelUpdated(UpdateType.SPECIFIC);
        }
		
		public boolean isSpecific() {
	        return specific;
        }
	}
	
	/*
	 * By placing this annotation (LoggedModel) on this model, all model updates
	 * will be sent to the log4j logger 'cinch.debug' at INFO level. 
	 */
	@SuppressWarnings("unused")
	@LoggedModel
	private final Model model = new Model();
	
	@Bound(to = "state")
	private final JCheckBox box = new JCheckBox("State");
	
	@Bound(to = "specific")
	private final JCheckBox specificBox = new JCheckBox("Specific");
	
	private final JPanel panel = new JPanel();
	
	private final Bindings bindings = ExtendedBindings.extendedBindings();
	
	public ExtendedExample() {
		panel.setLayout(new BorderLayout());
		panel.add(box, BorderLayout.NORTH);
		panel.add(specificBox, BorderLayout.SOUTH);
		bindings.bind(this);
    }
	
	public static void main(String[] args) throws InterruptedException, InvocationTargetException {
		Examples.initializeLogging();
		EventQueue.invokeAndWait(new Runnable() {
	        public void run() {
	        	ExtendedExample example = new ExtendedExample();
	        	JFrame frame = Examples.getFrameFor("Cinch Extended Bindings Example", example.panel);
	        	frame.pack();
				frame.setVisible(true);
	        }
        });
    }
}
