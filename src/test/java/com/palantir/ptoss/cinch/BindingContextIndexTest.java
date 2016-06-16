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

import java.lang.reflect.InvocationTargetException;

import com.palantir.ptoss.cinch.core.BindingContext;
import com.palantir.ptoss.cinch.core.DefaultBindableModel;
import com.palantir.ptoss.cinch.core.ObjectFieldMethod;

import junit.framework.TestCase;

public class BindingContextIndexTest extends TestCase {

    public static class SimpleModel extends DefaultBindableModel {
        private String simpleString;

        public String getSimpleString() {
            return simpleString;
        }

        public void setSimpleString(String simpleString) {
            this.simpleString = simpleString;
            update();
        }
    }

    public static class SimpleModel2 extends DefaultBindableModel {
        private String simpleString;
        private String otherString;

        public String getSimpleString() {
            return simpleString;
        }

        public void setSimpleString(String simpleString) {
            this.simpleString = simpleString;
            update();
        }

        public String getOtherString() {
            return otherString;
        }

        public void setOtherString(String otherString) {
            this.otherString = otherString;
            update();
        }
    }

    final SimpleModel model1 = new SimpleModel();
    final SimpleModel model2 = new SimpleModel();
    final SimpleModel2 model = new SimpleModel2();

    public void testGetterIndex() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        model1.setSimpleString("string1");
        model2.setSimpleString("string2");
        model.setOtherString("other");
        BindingContext context = new BindingContext(this);
        assertNull(context.findGetter("simpleString"));
        ObjectFieldMethod ofm = context.findGetter("model1.simpleString");
        assertEquals("string1", ofm.getMethod().invoke(ofm.getObject(), (Object[])null));
        ofm = context.findGetter("model2.simpleString");
        assertEquals("string2", ofm.getMethod().invoke(ofm.getObject(), (Object[])null));
        ofm = context.findGetter("otherString");
        assertEquals("other", ofm.getMethod().invoke(ofm.getObject(), (Object[])null));
    }

    public void testSetterIndex() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        BindingContext context = new BindingContext(this);
        assertNull(context.findSetter("simpleString"));
        ObjectFieldMethod ofm = context.findSetter("model1.simpleString");
        ofm.getMethod().invoke(ofm.getObject(), new Object[] { "set1" });
        assertEquals("set1", model1.getSimpleString());

        ofm = context.findSetter("model2.simpleString");
        ofm.getMethod().invoke(ofm.getObject(), new Object[] { "set2" });
        assertEquals("set2", model2.getSimpleString());

        ofm = context.findSetter("otherString");
        ofm.getMethod().invoke(ofm.getObject(), new Object[] { "setOther" });
        assertEquals("setOther", model.getOtherString());
    }
}
