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

import javax.swing.JCheckBoxMenuItem;

import junit.framework.TestCase;

import com.palantir.ptoss.cinch.core.Bindings;
import com.palantir.ptoss.cinch.swing.Bound;

public class BoundJCheckBoxMenuItemTest extends TestCase {

    final BooleanModel model = new BooleanModel();

    @Bound(to = "state")
    final JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem();

    final Bindings bindings = Bindings.standard();

    @Override
    protected void setUp() throws Exception {
        bindings.bind(this);
    }

    public void testSimple() {
        assertFalse(model.isState());
        assertFalse(menuItem.isSelected());
        model.setState(true);
        assertTrue(menuItem.isSelected());
        assertTrue(model.isState());
        menuItem.doClick();
        assertFalse(model.isState());
        assertFalse(menuItem.isSelected());
    }
}
