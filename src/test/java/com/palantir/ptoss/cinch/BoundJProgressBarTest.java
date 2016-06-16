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

import javax.swing.JProgressBar;

import com.palantir.ptoss.cinch.core.Bindings;
import com.palantir.ptoss.cinch.core.DefaultBindableModel;
import com.palantir.ptoss.cinch.swing.Bound;

import junit.framework.TestCase;

public class BoundJProgressBarTest extends TestCase {
    public static class TestModel extends DefaultBindableModel {

        private int intVal = 0;
        private double doubleVal = 0d;
        private float floatVal = 0f;
        public int getIntVal() {
            return intVal;
        }
        public void setIntVal(int intVal) {
            this.intVal = intVal;
            update();
        }
        public double getDoubleVal() {
            return doubleVal;
        }
        public void setDoubleVal(double doubleVal) {
            this.doubleVal = doubleVal;
            update();
        }
        public float getFloatVal() {
            return floatVal;
        }
        public void setFloatVal(float floatVal) {
            this.floatVal = floatVal;
            update();
        }
    }

    private final TestModel model = new TestModel();


    @Bound(to = "intVal")
    private final JProgressBar intBar = new JProgressBar();
    @Bound(to = "floatVal")
    private final JProgressBar floatBar = new JProgressBar();
    @Bound(to = "doubleVal")
    private final JProgressBar doubleBar = new JProgressBar();

    private final Bindings bindings = new Bindings();

    @Override
    protected void setUp() {
        bindings.bind(this);
    }

    public void testInt() {
        assertEquals(0, intBar.getValue());
        assertFalse(intBar.isIndeterminate());
        model.setIntVal(50);
        assertEquals(50, intBar.getValue());
        assertFalse(intBar.isIndeterminate());
        model.setIntVal(100);
        assertEquals(100, intBar.getValue());
        assertFalse(intBar.isIndeterminate());
        model.setIntVal(-1);
        assertTrue(intBar.isIndeterminate());
        model.setIntVal(100);
        assertEquals(100, intBar.getValue());
        assertFalse(intBar.isIndeterminate());
        model.setIntVal(200);
        assertEquals(100, intBar.getValue());
        assertFalse(intBar.isIndeterminate());
    }

    public void testDouble() {
        assertEquals(0, doubleBar.getValue());
        assertFalse(doubleBar.isIndeterminate());
        model.setDoubleVal(0.50);
        assertEquals(50, doubleBar.getValue());
        assertFalse(doubleBar.isIndeterminate());
        model.setDoubleVal(1.00);
        assertEquals(100, doubleBar.getValue());
        assertFalse(doubleBar.isIndeterminate());
        model.setDoubleVal(-1);
        assertTrue(doubleBar.isIndeterminate());
        model.setDoubleVal(1.00);
        assertEquals(100, doubleBar.getValue());
        assertFalse(doubleBar.isIndeterminate());
        model.setDoubleVal(2.00);
        assertEquals(100, doubleBar.getValue());
        assertFalse(doubleBar.isIndeterminate());
    }

    public void testFloat() {
        assertEquals(0, floatBar.getValue());
        assertFalse(floatBar.isIndeterminate());
        model.setFloatVal(0.50f);
        assertEquals(50, floatBar.getValue());
        assertFalse(floatBar.isIndeterminate());
        model.setFloatVal(1.00f);
        assertEquals(100, floatBar.getValue());
        assertFalse(floatBar.isIndeterminate());
        model.setFloatVal(-1f);
        assertTrue(floatBar.isIndeterminate());
        model.setFloatVal(1.00f);
        assertEquals(100, floatBar.getValue());
        assertFalse(floatBar.isIndeterminate());
        model.setFloatVal(2.00f);
        assertEquals(100, floatBar.getValue());
        assertFalse(floatBar.isIndeterminate());
    }
}
