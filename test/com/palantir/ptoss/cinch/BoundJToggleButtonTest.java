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

import javax.swing.JRadioButton;
import javax.swing.JToggleButton;

import com.palantir.ptoss.cinch.core.Bindings;
import com.palantir.ptoss.cinch.core.DefaultBindableModel;
import com.palantir.ptoss.cinch.swing.Bound;

import junit.framework.TestCase;

public class BoundJToggleButtonTest extends TestCase {

    private enum Value {
        ONE, TWO, THREE;
    }

    private static class EnumModel extends DefaultBindableModel {
        Value value;

        public Value getValue() {
            return value;
        }

        public void setValue(Value value) {
            this.value = value;
            update();
        }
    }

    private final EnumModel enumModel = new EnumModel();
    private final BooleanModel model = new BooleanModel();


    @Bound(to = "state", value = "true")
    private final JRadioButton on = new JRadioButton();
    @Bound(to = "state", value = "false")
    private final JRadioButton off = new JRadioButton();

    @Bound(to = "state")
    private final JToggleButton button = new JToggleButton();

    @Bound(to = "value", value = "ONE")
    private final JToggleButton oneButton = new JToggleButton();
    @Bound(to = "value", value = "TWO")
    private final JToggleButton twoButton = new JToggleButton();
    @Bound(to = "value", value = "THREE")
    private final JToggleButton threeButton = new JToggleButton();

    private final Bindings bindings = Bindings.standard();

    @Override
    protected void setUp() {
        bindings.bind(this);
    }

    public void testBoolean() {
        assertFalse(button.isSelected());
        assertFalse(model.isState());
        model.setState(true);
        assertTrue(model.isState());
        assertTrue(button.isSelected());
        button.doClick();
        assertFalse(button.isSelected());
        assertFalse(model.isState());
    }

    public void testTwoStateBoolean() {
        assertFalse(model.isState());
        assertFalse(on.isSelected());
        assertTrue(off.isSelected());
        model.setState(true);
        assertTrue(on.isSelected());
        assertFalse(off.isSelected());
        off.doClick();
        assertFalse(on.isSelected());
        assertTrue(off.isSelected());
    }

    public void testEnum() {
        assertNull(enumModel.getValue());
        assertFalse(oneButton.isSelected());
        assertFalse(twoButton.isSelected());
        assertFalse(threeButton.isSelected());

        enumModel.setValue(Value.ONE);
        assertEquals(Value.ONE, enumModel.getValue());
        assertTrue(oneButton.isSelected());
        assertFalse(twoButton.isSelected());
        assertFalse(threeButton.isSelected());

        enumModel.setValue(Value.TWO);
        assertEquals(Value.TWO, enumModel.getValue());
        assertFalse(oneButton.isSelected());
        assertTrue(twoButton.isSelected());
        assertFalse(threeButton.isSelected());

        threeButton.doClick();
        assertEquals(Value.THREE, enumModel.getValue());
        assertFalse(oneButton.isSelected());
        assertFalse(twoButton.isSelected());
        assertTrue(threeButton.isSelected());

        enumModel.setValue(null);
        assertEquals(null, enumModel.getValue());
        assertFalse(oneButton.isSelected());
        assertFalse(twoButton.isSelected());
        assertFalse(threeButton.isSelected());
    }
}
