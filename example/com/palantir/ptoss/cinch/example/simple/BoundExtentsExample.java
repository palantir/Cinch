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

import java.awt.*;
import java.lang.reflect.InvocationTargetException;

import javax.swing.*;
import javax.swing.plaf.metal.MetalSliderUI;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.palantir.ptoss.cinch.core.Bindings;
import com.palantir.ptoss.cinch.core.DefaultBindableModel;
import com.palantir.ptoss.cinch.example.Examples;
import com.palantir.ptoss.cinch.swing.Bound;
import com.palantir.ptoss.cinch.swing.BoundExtent;

/**
 * An illustration of using the {@link BoundExtent} annotation to tie the extent of one control to
 * another. In this example, the top slider in the UI sets the extent for the bottom slider.
 * @see JSlider#setExtent(int)
 * @see BoundExtent
 */
public class BoundExtentsExample {

	static final Logger log = LogManager.getLogger(BoundExtentsExample.class);

	public static class Model extends DefaultBindableModel {
		private int sliderExtent;

		public int getSliderExtent() {
			return sliderExtent;
		}

		public void setSliderExtent(int sliderPosition) {
			this.sliderExtent = sliderPosition;
			log.debug("Slider Extent set: " + sliderPosition);
			update();
		}

	}

	@SuppressWarnings("unused")
	private final Model model = new Model();

	private final JPanel panel = new JPanel();

	@BoundExtent(to = "sliderExtent")
	private final JSlider slider = new JSlider(new DefaultBoundedRangeModel(0, 0, 0, 100));

	@Bound(to = "sliderExtent")
	private final JSlider controlSlider = new JSlider(new DefaultBoundedRangeModel(0, 0, 0, 100));

	@Bound(to = "sliderExtent")
	private final JLabel extent = new JLabel();
	private final Bindings bindings = Bindings.standard();

	public BoundExtentsExample() {
		initializeInterface();
		bindings.bind(this);
	}

	private JPanel buildTextPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(extent, BorderLayout.CENTER);
		panel.add(new JLabel("Extent: "), BorderLayout.WEST);
		return panel;
	}

	private void styleSlider(JSlider slider) {
		slider.setMajorTickSpacing(10);
		slider.setMinorTickSpacing(1);
		slider.setPaintLabels(true);
		slider.setPaintTicks(true);
		slider.setPaintTrack(true);
		slider.setSnapToTicks(true);
	}

	private void addExtentsHighlight(JSlider slider) {
		slider.setUI(new ExtentHighlightingSliderUI(slider));
	}

	private void initializeInterface() {
		panel.setLayout(new BorderLayout());
		styleSlider(slider);
		addExtentsHighlight(slider);
		panel.add(slider, BorderLayout.CENTER);
		styleSlider(controlSlider);
		panel.add(controlSlider, BorderLayout.NORTH);
		panel.add(buildTextPanel(), BorderLayout.SOUTH);

		JFrame frame = new JFrame(getClass().getSimpleName());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocation(100, 100);
		frame.setSize(400, 200);

		frame.setContentPane(panel);

		frame.setVisible(true);
	}

	public static void main(String[] args) throws InterruptedException, InvocationTargetException {
		Examples.initializeLogging();
		// set to Metal LaF
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			EventQueue.invokeAndWait(new Runnable() {
				public void run() {
					new BoundExtentsExample();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static class ExtentHighlightingSliderUI extends MetalSliderUI {

		final JSlider slider;
		Color originalLabelColor = null;
		static final Color RED = Color.RED;

		public ExtentHighlightingSliderUI(JSlider slider) {
			this.slider = slider;
		}

		public void setOriginalLabelColor(Color originalLabelColor) {
			if (originalLabelColor == null || !RED.equals(originalLabelColor)) {
				this.originalLabelColor = originalLabelColor;
			}
		}

		protected void paintHorizontalLabel(Graphics g, int value, Component label) {
			final int extentLowerBound = slider.getMaximum() - slider.getExtent();
			setOriginalLabelColor(label.getForeground());
			if (value > extentLowerBound) {
				label.setForeground(Color.RED);
			} else {
				label.setForeground(originalLabelColor);
			}
			super.paintHorizontalLabel(g, value, label);
		}

		protected void paintMinorTickForHorizSlider(Graphics g, Rectangle tickBounds, int x) {
			super.paintMinorTickForHorizSlider(g, tickBounds, x);
			Color original = g.getColor();
			if (valueForXPosition(x) > slider.getMaximum() - slider.getExtent()) {
				g.setColor(Color.RED);
			}
			g.drawLine(x, TICK_BUFFER, x, TICK_BUFFER + (tickLength / 2));
			g.setColor(original);
		}

		protected void paintMajorTickForHorizSlider(Graphics g, Rectangle tickBounds, int x) {
			super.paintMajorTickForHorizSlider(g, tickBounds, x);
			Color original = g.getColor();
			if (valueForXPosition(x) > slider.getMaximum() - slider.getExtent()) {
				g.setColor(Color.RED);
			}
			g.drawLine(x, TICK_BUFFER, x, TICK_BUFFER + (tickLength - 1));
			g.setColor(original);
		}
	}
}
