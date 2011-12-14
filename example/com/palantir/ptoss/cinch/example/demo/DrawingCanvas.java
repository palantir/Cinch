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
package com.palantir.ptoss.cinch.example.demo;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import javax.swing.JComponent;

import com.palantir.ptoss.cinch.core.Bindings;
import com.palantir.ptoss.cinch.core.CallOnUpdate;
import com.palantir.ptoss.cinch.example.demo.DrawingCanvasModel.Mode;

public class DrawingCanvas extends JComponent {
    private static final long serialVersionUID = 1L;

    private final DrawingCanvasModel model;
    private final Bindings bindings = new Bindings();
    
    public DrawingCanvas(DrawingCanvasModel model) {
    	this.model = model;
    	initializeListeners();
    	bindings.bind(this);
    }
    
	private void initializeListeners() {
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				processPoint(e);
			}
		});
		
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				if (model.isAllowDrag()) {
					processPoint(e);
				}
			}
		});
    }

	private void processPoint(MouseEvent e) {
	    if (model.getMode() == Mode.POINT) {
	    	model.addPoint(e.getPoint());
	    } else if (model.getMode() == Mode.LINE) {
	    	if (model.getCursor() != null) {
	    		model.addLine(new Line2D.Double(
	    				model.getCursor().getX(), model.getCursor().getY(), e.getX(), e.getY()));
	    	}
	    	model.setCursor(e.getPoint());
	    }
    }
	
	@CallOnUpdate(model = "model")
	@Override
	public void repaint() {
		super.repaint();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, getWidth(), getHeight());
		g2.setColor(Color.BLACK);
		for (Line2D line : model.getLines()) {
			g2.draw(line);
		}
		for (Point2D point : model.getPoints()) {
			g2.fillOval((int)point.getX(), (int)point.getY(), 5, 5);
		}
	}
}
