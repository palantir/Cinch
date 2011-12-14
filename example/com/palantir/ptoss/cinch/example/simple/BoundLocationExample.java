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
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.UIManager;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.palantir.ptoss.cinch.core.BindableModel;
import com.palantir.ptoss.cinch.core.Bindings;
import com.palantir.ptoss.cinch.core.DefaultBindableModel;
import com.palantir.ptoss.cinch.example.Examples;
import com.palantir.ptoss.cinch.swing.BoundLocation;

/**
 * An example of using the {@link BoundLocation} annotation to keep two components in sync with
 * other, synchronized via the model. In this example, the relative position of two 
 * {@link JInternalFrame}s is kept in sync.
 * 
 * @see <a href='http://docs.oracle.com/javase/tutorial/uiswing/components/internalframe.html'>Java Internal frame tutorial</a>
 *
 */
public class BoundLocationExample extends JFrame {

	static final Logger log = LogManager.getLogger(BoundLocationExample.class);
	
	private static final long serialVersionUID = 1L;
	
	// need a better orange to match UIllinois colors
	static Color ORANGE = new Color(255,127,0);
	

	/**
	 * Used to allow {@link JInternalFrame} to move inside of it.
	 */
	public final JDesktopPane desktopLeft = new JDesktopPane();
	/**
	 * Used to allow {@link JInternalFrame} to move inside of it.
	 */
	public final JDesktopPane desktopRight = new JDesktopPane();
	/**
	 * One of the containers to hold the floating frames
	 */
	public final JInternalFrame left = new JInternalFrame();
	/**
	 * One of the containers to hold the floating frames
	 */
	public final JInternalFrame right = new JInternalFrame();
	@BoundLocation(to = "location")
	public final JInternalFrame leftFloater = new JInternalFrame("Internal Frame");
	@BoundLocation(to = "location")
	public final JInternalFrame rightFloater = new JInternalFrame("Internal Frame");
	
	/**
	 * Bindings used by this exampel
	 */
	final Bindings bindings = new Bindings();
	/**
	 * {@link BindableModel} instance for this exampel
	 */
	public final Model model = new Model();
	
	public BoundLocationExample() {
		buildGUI();
		bindings.bind(this);
	}

	/**
	 * A simple {@link BindableModel} class that contains a single {@link Point}.
	 */
	public static class Model extends DefaultBindableModel {
		
		/** 
		 * Field used to hold the position of the {@link JInternalFrame}s relative 
		 * to their containers.
		 */
		Point location;
		
		
		public Point getLocation() {
			return location;
		}
		
		public void setLocation(Point location) {
			this.location = location;
			log.info("New location: " + location.toString());
			update();
		}
	}
	
	void buildGUI() {
		setSize(800, 800);
		setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.gridx = 0;
		add(left,gbc);
		gbc.gridx = 1;
		add(right,gbc);
		
		left.setContentPane(desktopLeft);
		left.setResizable(true);
		left.setSize(400, 800);
		left.setVisible(true);
		left.setOpaque(true);
		left.getContentPane().setBackground(ORANGE);
		left.setDoubleBuffered(true);
		
		right.setContentPane(desktopRight);
		right.setResizable(true);
		right.setSize(400, 800);
		right.setVisible(true);
		right.setOpaque(true);
		right.getContentPane().setBackground(Color.BLUE);
		right.setDoubleBuffered(true);
		
		desktopLeft.add(leftFloater);
		desktopRight.add(rightFloater);
		leftFloater.setSize(100, 100);
		leftFloater.setVisible(true);
		leftFloater.getContentPane().setBackground(Color.BLUE);
		rightFloater.setSize(100,100);
		rightFloater.setVisible(true);
		rightFloater.getContentPane().setBackground(ORANGE);

		setVisible(true);
	}
	
	public static void main(String[] args) throws InterruptedException, InvocationTargetException {
		Examples.initializeLogging();
		// set to Metal LaF
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			EventQueue.invokeAndWait(new Runnable() {
				public void run() {
					new BoundLocationExample();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
