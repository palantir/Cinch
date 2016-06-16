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
package com.palantir.ptoss.cinch;

import javax.swing.JSlider;

import com.palantir.ptoss.cinch.core.Bindings;
import com.palantir.ptoss.cinch.core.DefaultBindableModel;
import com.palantir.ptoss.cinch.swing.Bound;

import junit.framework.TestCase;

public class BoundJSliderTest extends TestCase {

    public static class IntegerModel extends DefaultBindableModel {
        int value = 0;

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
            update();
        }
    }

    private final IntegerModel model = new IntegerModel();

    @Bound(to = "value")
    private final JSlider slider = new JSlider();

    private final Bindings bindings = Bindings.standard();

    @Override
    protected void setUp() {
        bindings.bind(this);
    }

    public void testSimple() {
        assertEquals(0, slider.getValue());
        assertEquals(0, model.getValue());
        model.setValue(50);
        assertEquals(50, slider.getValue());
        assertEquals(50, model.getValue());
        slider.setValue(75);
        assertEquals(75, slider.getValue());
        assertEquals(75, model.getValue());
    }
}
