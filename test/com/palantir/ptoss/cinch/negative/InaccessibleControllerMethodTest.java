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
package com.palantir.ptoss.cinch.negative;

import javax.swing.JButton;

import com.palantir.ptoss.cinch.core.Bindable;
import com.palantir.ptoss.cinch.core.Bindings;
import com.palantir.ptoss.cinch.swing.Action;

import junit.framework.TestCase;

public class InaccessibleControllerMethodTest extends TestCase {

//    final NegativeModel model = new NegativeModel();

    @Bindable
    final NegativeController controller = new NegativeController();

    @Action(call = "privateMethod")
    private final JButton button = new JButton("button");

    final Bindings bindings = new Bindings();

    public void testFailure() {
        try {
            bindings.bind(this);
            fail("should not allow binding to inaccessible controller methods");
        } catch (Exception e) {
            // passes
        }
    }
}
