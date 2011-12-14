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
package com.palantir.ptoss.util;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;

import com.palantir.ptoss.cinch.core.BindableModel;
import com.palantir.ptoss.cinch.core.BindingContext;
import com.palantir.ptoss.cinch.core.ObjectFieldMethod;

/**
 * A class that performs the work of reading and setting values on a bound field.
 *
 */
public class Mutator {
    /**
     * Creates a new Mutator bound to the passed {@link BindingContext}.
     * @param context the context for this {@link Mutator}
     * @param target the model and field to bind this {@link Mutator} to.
     * @return the {@link Mutator}
     * @throws IntrospectionException
     */
    public static Mutator create(BindingContext context, String target) throws IntrospectionException {
        final ObjectFieldMethod getter = context.findGetter(target);
        final ObjectFieldMethod setter = context.findSetter(target);
        if (getter == null && setter == null) {
            throw new IllegalArgumentException("could not find either getter/setter for " + target);
        }
        BindableModel model = null;
        BindableModel getterModel = null;
        BindableModel setterModel = null;
        if (getter != null) {
            getterModel = context.getFieldObject(getter.getField(), BindableModel.class);
            model = getterModel;
        }
        if (setter != null) {
            setterModel = context.getFieldObject(setter.getField(), BindableModel.class);
            model = setterModel;
        }
        if (getterModel != null && setterModel != null && getterModel != setterModel) {
            throw new IllegalStateException("setter and getter must be on same BindableModel.");
        }
        return new Mutator(getter, setter, model);
    }

    private final ObjectFieldMethod getter;
    private final ObjectFieldMethod setter;
    private final BindableModel model;

    /**
     * @param getter method to use as the getter for this field
     * @param setter method to use as the setter for this field
     * @param model model object that this {@link Mutator} applies to.
     */
    private Mutator(ObjectFieldMethod getter, ObjectFieldMethod setter, BindableModel model) {
        this.getter = getter;
        this.setter = setter;
        this.model = model;
    }

    public Object get() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        if (getter == null) {
            throw new IllegalStateException("can not call get() with no getter.");
        }
        boolean accessible = getter.getMethod().isAccessible();
        getter.getMethod().setAccessible(true);
        Object value = getter.getMethod().invoke(model);
        getter.getMethod().setAccessible(accessible);
        return value;
    }

    public void set(Object value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        if (setter == null) {
            throw new IllegalStateException("can not call set() with no setter.");
        }
        boolean accessible = setter.getMethod().isAccessible();
        setter.getMethod().setAccessible(true);
        setter.getMethod().invoke(model, value);
        setter.getMethod().setAccessible(accessible);
    }

    public BindableModel getModel() {
        return model;
    }

    public ObjectFieldMethod getSetter() {
        return setter;
    }

    public ObjectFieldMethod getGetter() {
        return getter;
    }
}