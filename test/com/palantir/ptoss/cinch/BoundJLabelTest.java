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

import javax.swing.JLabel;

import junit.framework.TestCase;

import com.palantir.ptoss.cinch.core.Bindings;
import com.palantir.ptoss.cinch.core.DefaultBindableModel;
import com.palantir.ptoss.cinch.swing.Bound;

public class BoundJLabelTest extends TestCase {
    public static class Model extends DefaultBindableModel {
        private String string;

        public String getString() {
            return string;
        }

        public void setString(String string) {
            this.string = string;
            update();
        }
    }

    private final Model model = new Model();

    @Bound(to = "string")
    private final JLabel label = new JLabel();

    private final Bindings bindings = Bindings.standard();

    @Override
    protected void setUp() throws Exception {
        bindings.bind(this);
    }

    public void testBinding() {
        assertNull(model.getString());
        assertEquals("", label.getText());
        model.setString("string");
        assertEquals("string", model.getString());
        assertEquals("string", label.getText());
        model.setString(null);
        assertNull(model.getString());
        assertEquals("", label.getText());
    }
}

