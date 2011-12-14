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

import junit.framework.TestCase;

import com.palantir.ptoss.cinch.core.Bindings;
import com.palantir.ptoss.cinch.core.DefaultBindableModel;
import com.palantir.ptoss.cinch.core.NotBindable;

public class NotBindableTest extends TestCase {

    class Model extends DefaultBindableModel {
        //
    }

    class GoodView {
        final Model model = new Model();

        @NotBindable
        Model tempModel;

        final Bindings bindings = new Bindings();

        public GoodView() {
            bindings.bind(this);
        }
    }

    public void testGoodNotBindable() {
        new GoodView();
    }

    class BadView {
        final Model model = new Model();

        Model tempModel;

        final Bindings bindings = new Bindings();

        public BadView() {
            bindings.bind(this);
        }
    }

    public void testBadNotBindable() {
        try {
            new BadView();
            fail("should not allow non-final unannotated bindable model");
        } catch (Exception e) {
            // needs to throw exception to pass;
            e.printStackTrace();
        }
    }
}
