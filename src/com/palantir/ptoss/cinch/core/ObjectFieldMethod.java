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
package com.palantir.ptoss.cinch.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.google.common.base.Function;

/**
 * A simple tuple of an object, a field on that object's class, and a method of that object's class.
 */
public class ObjectFieldMethod {

    /**
     * A function that returns the field's name.
     */
    public static final Function<ObjectFieldMethod, String> TO_FIELD_NAME = new Function<ObjectFieldMethod, String>() {
        public String apply(ObjectFieldMethod from) {
            return from.getField().getName();
        }
    };

    private final Object object;
    private final Field field;
    private final Method method;

    /**
     * Constructs a tuple of an object, a field on that object, and a method on that object.
     * @param object
     * @param field
     * @param method
     */
    public ObjectFieldMethod(Object object, Field field, Method method) {
        this.object = object;
        this.field = field;
        this.method = method;
    }

    public Object getObject() {
        return object;
    }

    public Field getField() {
        return field;
    }

    public Method getMethod() {
        return method;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder("(");

        // object value
        out.append("object=");
        if(object != null) {
            final String klass = object.getClass().getSimpleName();
            final int hashcode = System.identityHashCode(object);
            out.append(klass).append("[").append(hashcode).append("]");
        } else {
            out.append("null");
        }
        out.append(", ");

        // field value
        out.append("field=");
        if(field != null) {
            final String fieldName = field.getName();
            out.append(fieldName);
        } else {
            out.append("null");
        }
        out.append(", ");

        // method value
        out.append("method=");
        if(method != null) {
            final String methodName = method.getName();
            out.append(methodName);
        } else {
            out.append("null");
        }

        // finish up
        out.append(")");

        return out.toString();
    }
}