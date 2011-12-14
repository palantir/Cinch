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
package com.palantir.ptoss.cinch.swing;

import java.beans.IntrospectionException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import javax.swing.JPasswordField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.lang.Validate;

import com.palantir.ptoss.cinch.core.*;
import com.palantir.ptoss.cinch.swing.Bound.Wiring;

// The interface uses char[] but has to make a String under the covers in order to set the password.
// TODO (dcervelli): find a (hacky) way to set the text without throwing it into a String
 /**
 * A {@link WiringHarness} for binding a {@link JPasswordField} to a value in a
 * {@link BindableModel}.
 */
public class JPasswordFieldWiringHarness implements WiringHarness<Bound, Field> {
    public Collection<Binding> wire(Bound bound, BindingContext context, Field field) throws IllegalAccessException, IntrospectionException {
        String target = bound.to();
        JPasswordField pwdField = context.getFieldObject(field, JPasswordField.class);
        ObjectFieldMethod setter = context.findSetter(target);
        ObjectFieldMethod getter = context.findGetter(target);
        if (setter == null || getter == null) {
            throw new IllegalArgumentException("could not find getter/setter for " + target);
        }
        BindableModel model1 = context.getFieldObject(setter.getField(), BindableModel.class);
        BindableModel model2 = context.getFieldObject(getter.getField(), BindableModel.class);
        Validate.isTrue(model1 == model2);
        // verify type parameters
        return bindJPasswordField(model1, pwdField, getter.getMethod(), setter.getMethod());
    }

    public static Collection<Binding> bindJPasswordField(final BindableModel model, final JPasswordField pwdField,
            final Method getter, final Method setter) {
        pwdField.getDocument().addDocumentListener(new DocumentListener() {
            public void removeUpdate(DocumentEvent e) {
                updateModel();
            }

            public void insertUpdate(DocumentEvent e) {
                updateModel();
            }

            public void changedUpdate(DocumentEvent e) {
                updateModel();
            }

            private void updateModel() {
                try {
                    setter.invoke(model, pwdField.getPassword());
                } catch (Exception ex) {
                    Wiring.logger.error("exception in JPasswordField binding", ex);
                }
            }
        });
        Binding binding = new Binding() {
            public <T extends Enum<?> & ModelUpdate> void update(T... changed) {
                try {
                    char[] charArray = (char[])getter.invoke(model);
                    if (charArray == null) {
                        charArray = new char[0];
                    }
                    if (!Arrays.equals(charArray, pwdField.getPassword())) {
                        pwdField.setText(String.valueOf(charArray));
                    }
                } catch (Exception ex) {
                    Wiring.logger.error("exception in JPasswordField binding", ex);
                }
            }
        };
        model.bind(binding);
        return Collections.singleton(binding);
    }
}