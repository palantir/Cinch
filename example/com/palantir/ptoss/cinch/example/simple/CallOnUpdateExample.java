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
import java.lang.reflect.InvocationTargetException;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.palantir.ptoss.cinch.core.Bindings;
import com.palantir.ptoss.cinch.core.CallOnUpdate;
import com.palantir.ptoss.cinch.core.DefaultBindableModel;
import com.palantir.ptoss.cinch.example.Examples;
import com.palantir.ptoss.cinch.swing.Bound;

public class CallOnUpdateExample {

	static class CallOnUpdateModel extends DefaultBindableModel {
		boolean yellow;
		boolean blue;
		
		public boolean isYellow() {
        	return yellow;
        }
		
		public void setYellow(boolean yellow) {
        	this.yellow = yellow;
        	update();
        }
		
		public boolean isBlue() {
        	return blue;
        }
		
		public void setBlue(boolean blue) {
        	this.blue = blue;
        	update();
        }
	}
	
	private final CallOnUpdateModel model = new CallOnUpdateModel();

	@Bound(to = "yellow")
	private final JCheckBox yellow = new JCheckBox("yellow");
	@Bound(to = "blue")
	private final JCheckBox blue = new JCheckBox("blue");
	
	private final JPanel panel = new JPanel();
	
	private final Bindings bindings = Bindings.standard();

	public CallOnUpdateExample() {
		panel.add(yellow);
		yellow.setOpaque(false);
		panel.add(blue);
		blue.setOpaque(false);
		panel.setPreferredSize(new Dimension(200, 100));
		bindings.bind(this);
	}

	@CallOnUpdate(model = "model")
	public void synchBackground() {
		if (model.isYellow() && model.isBlue()) {
			panel.setBackground(Color.GREEN);
		} else if (model.isBlue()) {
			panel.setBackground(Color.BLUE);
		} else if (model.isYellow()) {
			panel.setBackground(Color.YELLOW);
		} else {
			panel.setBackground(Color.WHITE);
		}
	}
		
	public static void main(String[] args) throws InterruptedException, InvocationTargetException {
		EventQueue.invokeAndWait(new Runnable() {
			public void run() {
				CallOnUpdateExample example = new CallOnUpdateExample();
				JFrame frame = Examples.getFrameFor("Cinch CallOnUpdate Example", example.panel);
				frame.pack();
				frame.setVisible(true);
			}
		});
	}
}
