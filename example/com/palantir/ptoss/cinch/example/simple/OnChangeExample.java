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

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.palantir.ptoss.cinch.core.Bindings;
import com.palantir.ptoss.cinch.core.DefaultBindableModel;
import com.palantir.ptoss.cinch.example.Examples;
import com.palantir.ptoss.cinch.swing.Bound;
import com.palantir.ptoss.cinch.swing.OnChange;


/**
 * Simple example illustrating use of {@link OnChange}.  Fires a method call on an Change event. 
 * Note that for a component like {@link JToggleButton}, this includes every mouse event, such that
 * a toggle will generate six {@link ChangeEvent}s.
 * 
 * @see <a href='http://docs.oracle.com/javase/6/docs/api/javax/swing/event/ChangeListener.html'>ChangeListener</a> - contains a list of components that support ChangeListeners.
 */
public class OnChangeExample {

	static final Logger log = LogManager.getLogger(OnChangeExample.class);
	
	public final Model model = new Model();
	public final JLabel label = new JLabel("Toggle: ");
	@Bound(to = "toggle")
	@OnChange(call = "incrementStateChangeCount")
	public final JToggleButton button = new JToggleButton("Click Me!");
	public final JLabel countLabel = new JLabel("Button State Changes: ");
	@Bound(to = "stateChangeCount")
	public final JLabel count = new JLabel("0");
	
	final Bindings bindings = new Bindings();
	
	public OnChangeExample() {
		this.bindings.bind(this);
		this.buildGUI();
	}
	
	public static class Model extends DefaultBindableModel {
		
		boolean toggle;
		Integer stateChangeCount = new Integer(0);

		public boolean isToggle() {
			return toggle;
		}

		public void setToggle(boolean toggle) {
			this.toggle = toggle;
		}

		public Integer getStateChangeCount() {
			return stateChangeCount;
		}
		
		public synchronized void setStateChangeCount(Integer inverseVal) {
			this.stateChangeCount = inverseVal;
			update();
		}
		
		public synchronized void incrementStateChangeCount() {
			int incrementedCount = stateChangeCount + 1;
			log.info("New toggle count: " + incrementedCount);
			setStateChangeCount(incrementedCount);
		}
	}
		

	
	void buildGUI() {
		JFrame frame = new JFrame(OnChangeExample.class.getSimpleName());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 100);
		frame.setLayout(new GridBagLayout());
		

		

		frame.add(label,getLeftGBC(0));
		frame.add(button,getRightGBC(0));
		count.setHorizontalAlignment(SwingConstants.CENTER);
		frame.add(countLabel,getLeftGBC(1));
		frame.add(count,getRightGBC(1));
		frame.setVisible(true);
	}
	
	public static void main(String[] args) {
		Examples.initializeLogging();
		// set to Metal LaF
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			EventQueue.invokeAndWait(new Runnable() {
				public void run() {
					new OnChangeExample();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	GridBagConstraints getDefaultGBC() {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
		gbc.gridy = 0;
		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.ipadx = 5;
		gbc.ipady = 5;
		gbc.insets = new Insets(10, 0, 10, 0);
		return gbc;
	}
	
	GridBagConstraints getLeftGBC(int gridy) {
		GridBagConstraints gbc = getDefaultGBC();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.weightx = 0;
		return gbc;
	}
	
	GridBagConstraints getRightGBC(int gridy) {
		GridBagConstraints gbc = getDefaultGBC();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1;
		return gbc;
	}
}
