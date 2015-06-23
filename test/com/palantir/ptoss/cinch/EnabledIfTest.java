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

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JCheckBox;

import com.palantir.ptoss.cinch.core.Bindings;
import com.palantir.ptoss.cinch.core.DefaultBindableModel;
import com.palantir.ptoss.cinch.swing.EnabledIf;

import junit.framework.TestCase;

public class EnabledIfTest extends TestCase {

    private static class SimpleModel extends DefaultBindableModel {
        boolean state = false;

        public boolean isState() {
            return state;
        }

        public void setState(boolean state) {
            this.state = state;
            update();
        }
    }

    private static class OtherModel extends DefaultBindableModel {
        boolean otherState = false;

        public boolean isOtherState() {
            return otherState;
        }

        public void setOtherState(boolean state) {
            this.otherState = state;
            update();
        }
    }

    final OtherModel otherModel = new OtherModel();
    final SimpleModel model1 = new SimpleModel();
    final SimpleModel model2 = new SimpleModel();

    @EnabledIf(to = "otherState")
    final JCheckBox ocb = new JCheckBox();
    @EnabledIf(to = "model1.state")
    final JCheckBox cb1 = new JCheckBox();
    @EnabledIf(to = "model2.state")
    final JCheckBox cb2 = new JCheckBox();

    final Bindings bindings = new Bindings();

    @Override
    protected void setUp() throws Exception {
        EventQueue.invokeAndWait(new Runnable() {
            public void run() {
                bindings.bind(EnabledIfTest.this);
            }
        });
    }

    public void testIt() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(new Runnable() {
            public void run() {
                assertFalse(ocb.isEnabled());
                assertFalse(cb1.isEnabled());
                assertFalse(cb2.isEnabled());
                model1.setState(true);
                assertFalse(ocb.isEnabled());
                assertTrue(cb1.isEnabled());
                assertFalse(cb2.isEnabled());
                model2.setState(true);
                assertFalse(ocb.isEnabled());
                assertTrue(cb1.isEnabled());
                assertTrue(cb2.isEnabled());
                model1.setState(false);
                assertFalse(ocb.isEnabled());
                assertFalse(cb1.isEnabled());
                assertTrue(cb2.isEnabled());
                model2.setState(false);
                assertFalse(ocb.isEnabled());
                assertFalse(cb1.isEnabled());
                assertFalse(cb2.isEnabled());
                otherModel.setOtherState(true);
                assertTrue(ocb.isEnabled());
                assertFalse(cb1.isEnabled());
                assertFalse(cb2.isEnabled());
                otherModel.setOtherState(false);
                assertFalse(ocb.isEnabled());
                assertFalse(cb1.isEnabled());
                assertFalse(cb2.isEnabled());
            }
        });
    }
}
