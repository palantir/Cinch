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
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.common.collect.Lists;
import com.palantir.ptoss.cinch.core.BindableModel;
import com.palantir.ptoss.cinch.core.Binding;
import com.palantir.ptoss.cinch.core.BindingContext;
import com.palantir.ptoss.cinch.core.BindingException;
import com.palantir.ptoss.cinch.core.BindingWiring;
import com.palantir.ptoss.cinch.core.ModelUpdate;
import com.palantir.ptoss.cinch.core.ObjectFieldMethod;

/**
 * A binding that will set the Visible state of the annotated component to the state of a model
 * boolean. The component must have a "setVisible" method that takes a boolean.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface VisibleIf {
	/**
	 * Enum to specify if normal or inverted comparisons should be used.
	 */
	public enum Type { NORMAL, INVERTED };
	/**
	 * The model boolean property to bind to.
	 */
	String to();

	/**
	 * Whether or not to invert the boolean.
	 */
	Type type() default Type.NORMAL;

	/**
	 * Inner utility class that performs the runtime wiring of all {@link VisibleIf} bindings. 
	 */
	public static class Wiring implements BindingWiring {
		private static final Logger logger = Logger.getLogger(VisibleIf.class);

		public Collection<Binding> wire(final BindingContext context) {
			final List<Field> actions = context.getAnnotatedFields(VisibleIf.class);
			final List<Binding> bindings = Lists.newArrayList();
			for (final Field field : actions) {
				final VisibleIf action = field.getAnnotation(VisibleIf.class);
				final String to = action.to();
				final boolean invert = (action.type() == Type.INVERTED);
				try {
					bindings.addAll(wire(to, field, context, invert));
				} catch (final Exception e) {
					throw new BindingException("could not wire up @VisibleIf on " + field.getName(), e); 
				}
			}
			return bindings;
		}

		private static Collection<Binding> wire(final String to, final Field field, final BindingContext context, final boolean invert)
			throws SecurityException, NoSuchMethodException, IllegalArgumentException, IntrospectionException {
			final Method setVisibleMethod = field.getType().getMethod("setVisible", boolean.class); 
			if (setVisibleMethod == null) {
				throw new BindingException("no setVisible call on VisibleIf field: " + field); 
			}
			final Object setVisibleObject = context.getFieldObject(field, Object.class);
			final ObjectFieldMethod getter = context.findGetter(to);
			if (getter == null) {
				throw new BindingException("could not find bindable property: " + to); 
			}
			if (getter.getMethod().getReturnType() != boolean.class) {
				throw new BindingException("VisibleIf binding must return boolean: " + to); 
			}
			final Binding binding = new Binding() {
				public <T extends Enum<?> & ModelUpdate> void update(final T... changed) {
					try {
						getter.getMethod().setAccessible(true);
						boolean visible = (Boolean)getter.getMethod().invoke(getter.getObject());
						if (invert) {
							visible = !visible;
						}
						setVisibleMethod.invoke(setVisibleObject, visible);
					} catch (final Exception e) {
						Wiring.logger.error("exception during VisibleIf binding", e); 
					}
				}
			};
			((BindableModel)getter.getObject()).bind(binding);
			return Collections.singleton(binding);
		}
	}
}
