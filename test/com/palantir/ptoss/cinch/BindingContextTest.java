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

import com.palantir.ptoss.cinch.core.Bindable;
import com.palantir.ptoss.cinch.core.BindingContext;
import com.palantir.ptoss.cinch.core.Bindings;
import com.palantir.ptoss.cinch.core.DefaultBindableModel;
import com.palantir.ptoss.cinch.core.NotBindable;

public class BindingContextTest extends TestCase {

    public static class SimpleModel extends DefaultBindableModel {
        private boolean simpleBoolean;

        public void setSimpleBoolean(boolean simpleBoolean) {
            this.simpleBoolean = simpleBoolean;
            update();
        }

        public boolean isSimpleBoolean() {
            return simpleBoolean;
        }
    }

    public static class SimpleController {
        public void doSomething() {
            // Empty
        }
    }

    public static class NonfinalModel {
        private SimpleModel model = new SimpleModel();
        private final Bindings bindings = new Bindings();

        public NonfinalModel() {
            bindings.bind(this);
        }
    }

    public static class NonfinalNotBindableModel {
        @NotBindable
        private SimpleModel model = new SimpleModel();
        private final Bindings bindings = new Bindings();

        public NonfinalNotBindableModel() {
            bindings.bind(this);
        }
    }

    public static class FinalModel {
        private final SimpleModel model = new SimpleModel();
        private final Bindings bindings = new Bindings();

        public FinalModel() {
            bindings.bind(this);
        }
    }

    public void testModelFinality() {
        new FinalModel();
        new NonfinalNotBindableModel();
        try {
            new NonfinalModel();
            fail("shouldn't allow non-final models");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class NonfinalController {
        @Bindable
        private SimpleController controller;
        private final Bindings bindings = new Bindings();

        public NonfinalController() {
            bindings.bind(this);
        }
    }

    public void testControllerFinality() {
        try {
            new NonfinalController();
            fail("shouldn't allow non-final @Bindables");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final String STRING_1 = "string1";
    public static final Boolean BOOLEAN = Boolean.TRUE;
    private static final String PRIVATE_STRING = "privateString";
    private static final String FUNCTION_STRING = getString();
    static int i = 0;
    public static String getString() {
        return "#" + i++;
    }

    public void testConstants() {
        BindingContext context = new BindingContext(this);
        assertEquals("string1", context.getBindableConstant("STRING_1"));
        assertEquals(Boolean.TRUE, context.getBindableConstant("BOOLEAN"));
        assertEquals("privateString", context.getBindableConstant("PRIVATE_STRING"));
        assertEquals("#0", context.getBindableConstant("FUNCTION_STRING"));

        assertEquals(null, context.getBindableConstant("NOT_FOUND"));
    }

    private final SimpleModel model = new SimpleModel();

    public void testModel() {
        BindingContext context = new BindingContext(this);
        assertEquals(model, context.getBindableModel("model"));
    }
}
