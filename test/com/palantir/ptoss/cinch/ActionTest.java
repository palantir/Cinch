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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import junit.framework.TestCase;

import com.palantir.ptoss.cinch.core.Bindable;
import com.palantir.ptoss.cinch.core.Bindings;
import com.palantir.ptoss.cinch.swing.Action;

@Bindable
public class ActionTest extends TestCase {

    public static class Actionable {
        private ActionListener actionListener;

        public void addActionListener(ActionListener actionListener) {
            if (this.actionListener != null) {
                throw new IllegalStateException("actionListener already set");
            }
            this.actionListener = actionListener;
        }

        public void doAction() {
            if (actionListener != null) {
                actionListener.actionPerformed(new ActionEvent(this, 0, null));
            }
        }
    }

    public static class ActionableSub extends Actionable {
        // Subclass.
    }

    @Action(call = "checkAction")
    Actionable actionable = new Actionable();
    int checkAction = 0;

    @Action(call = "privateCheckAction")
    ActionableSub privateActionable = new ActionableSub();
    int privateCheckAction = 0;

    final Bindings bindings = new Bindings();

    public ActionTest() {
        bindings.bind(this);
    }

    @SuppressWarnings("unused") // called by binding
    private void privateCheckAction() {
        ++privateCheckAction;
    }

    public void checkAction() {
        ++checkAction;
    }

    public void testAction1() {
        assertEquals(0, checkAction);
        actionable.doAction();
        assertEquals(1, checkAction);
        actionable.doAction();
        assertEquals(2, checkAction);
    }

    public void testAction2() {
        assertEquals(0, privateCheckAction);
        privateActionable.doAction();
        assertEquals(1, privateCheckAction);
        privateActionable.doAction();
        assertEquals(2, privateCheckAction);
    }
}
