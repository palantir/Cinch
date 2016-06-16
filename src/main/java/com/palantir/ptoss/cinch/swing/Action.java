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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JTextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.palantir.ptoss.cinch.core.Bindable;
import com.palantir.ptoss.cinch.core.BindableModel;
import com.palantir.ptoss.cinch.core.Binding;
import com.palantir.ptoss.cinch.core.BindingContext;
import com.palantir.ptoss.cinch.core.BindingException;
import com.palantir.ptoss.cinch.core.BindingWiring;
import com.palantir.ptoss.cinch.core.Bindings;
import com.palantir.ptoss.cinch.core.ObjectFieldMethod;

/**
 * <p>
 * A component binding that will call a method when an action occurs.  This can be applied to
 * any object that has an "addActionListener" method that takes an {@link ActionListener}.
 * Normally used for any sort of {@link JButton} or for a {@link JTextField}.
 * </p>
 * Here's an example of using @Action annotations:
 * <pre>
 * <code>
 * public class BoundJCheckBoxExample {
 *
 *     public static class Model extends DefaultBindableModel {
 *         private boolean state;
 *
 *         public void setState(boolean state) {
 *             this.state = state;
 *             update();
 *         }
 *
 *         public boolean isState() {
 *             return state;
 *         }
*      }
 *
 *     public static class Controller {
 *         private final Model model;
 *
 *         public Controller(Model model) {
 *             this.model = model;
 *         }
 *
 *         public void setToTrue() {
 *             model.setState(true);
 *         }
 *
 *         public void setToFalse() {
 *             model.setState(false);
 *         }
 *     }
 *
 *     private final Model model = new Model();
 *     &#064;Bindable
 *     private final Controller controller = new Controller(model);
 *
 *     &#064;Bound(to = "state")
 *     private final JCheckBox box = new JCheckBox("State");
 *     &#064;Bound(to = "state")
 *     private final JLabel stateLabel = new JLabel("?");
 *
 *     &#064;Action(call = "setToTrue")
 *     private final JButton trueButton = new JButton("Set True");
 *     &#064;Action(call = "setToFalse")
 *     private final JButton falseButton = new JButton("Set False");
 *
 *     private final JPanel panel = new JPanel();
 *
 *     private final Bindings bindings = Bindings.standard();
 *
 *     public BoundJCheckBoxExample() {
 *            // ... layout panel ...
 *            bindings.bind(this);
 *     }
 *
 *     // main()
 * }
 * </pre></code>
 *
 * <h2>Methods available to &#064;Actions</h2>
 * <p>The following are callable from &#064;Actions:
 * <ul>
 * <li>Methods on the class passed into {@link Bindings#bind(Object)} if the class is marked as
 * {@link Bindable}.</li>
 * <li>Any methods in fields that are marked {@link Bindable} (as in the example).</li>
 * <li>Any methods in fields that implement {@link BindableModel}.</li>
 * </ul>
 * <h2>Disambiguation</h2>
 * <p>
 * Strings passed into {@link #call()} are disambiguated as follows:
 * </p>
 * <ol>
 * <li>If the string uniquely identifies a model property or bindable method then
 * there is no problem.</li>
 * <li>If there are two or more possibilities then you must specify the field name in the
 * string like "model1.state" or "fileController.close".</li>
 * <li>You can always use the long form in (2) even if the short form would suffice.</li>
 * </ol>
 *
 * @see Bound
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Action {
    /**
     * The name of the method to call when the action occurs. Must be accessible in the
     * {@link BindingContext}.
     */
    String call();

    /**
     * Inner utility class used to wire {@link Action} bindings.
     */
    static class Wiring implements BindingWiring {
        private static final Logger logger = LoggerFactory.getLogger(Action.class);

        /**
         * Wires all {@link Action} bindings in the passed context.
         * Called by {@link Bindings#createBindings(BindingContext)} as part of runtime wiring
         * process.
         *
         * @param context
         */
        public Collection<Binding> wire(BindingContext context) {
            List<Field> actions = context.getAnnotatedFields(Action.class);
            for (Field field : actions) {
                Action action = field.getAnnotation(Action.class);
                String call = action.call();
                try {
                    wire(call, field, context);
                } catch (Exception e) {
                    throw new BindingException("could not wire up @Action on " +
                            field.getName(), e);
                }
            }
            return ImmutableList.of();
        }

        /**
         * Wires up to any object with an addActionListener method.  Automatically called
         * by {@link #wire(BindingContext)}.
         *
         * @param call name of an {@link ObjectFieldMethod} in the passed {@link BindingContext}.
         * @param field field to bind the call to.
         * @param context the {@link BindingContext}
         */
        private static void wire(String call, Field field, BindingContext context)
                throws SecurityException, NoSuchMethodException, IllegalArgumentException,
                        IllegalAccessException, InvocationTargetException {
            Method aalMethod = field.getType().getMethod("addActionListener", ActionListener.class);
            Object actionObject = context.getFieldObject(field, Object.class);
            final ObjectFieldMethod ofm = context.getBindableMethod(call);
            if (ofm == null) {
                throw new BindingException("could not find bindable method: " + call);
            }
            ActionListener actionListener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        boolean accessible = ofm.getMethod().isAccessible();
                        ofm.getMethod().setAccessible(true);
                        ofm.getMethod().invoke(ofm.getObject());
                        ofm.getMethod().setAccessible(accessible);
                    } catch (InvocationTargetException itex) {
                        logger.error("exception during action firing", itex.getCause());
                    } catch (Exception ex) {
                        logger.error("exception during action firing", ex);
                    }
                }
            };
            aalMethod.invoke(actionObject, actionListener);
        }
    }
}
