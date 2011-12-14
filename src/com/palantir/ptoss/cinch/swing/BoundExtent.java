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

import java.awt.Component;
import java.beans.IntrospectionException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import com.google.common.collect.Lists;
import com.palantir.ptoss.cinch.core.*;
import com.palantir.ptoss.util.Throwables;

/**
 * A binding for an interface component that has an extent (setExtent/getExtent methods).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface BoundExtent {
	/**
	 * The model property to bind to.
	 */
	String to();
	
	/**
	 * When this binding should occur.
	 */
	String on() default "";
	
	/**
	 * Inner utility class that performs the runtime wiring of all {@link BoundExtent} bindings.
	 * 
	 * @see Bindings#STANDARD_BINDINGS
	 */
	public static class Wiring implements BindingWiring {
		private static final Logger logger = Logger.getLogger(BoundExtent.class);
		
		public Collection<Binding> wire(BindingContext context) {
			List<Field> boundFields = context.getAnnotatedFields(BoundExtent.class);
			List<Binding> bindings = Lists.newArrayList();
		    for (Field field : boundFields) {
		    	BoundExtent bound = field.getAnnotation(BoundExtent.class);
		    	try {
		    		bindings.addAll(wire(bound, context, field));
		    	} catch (Exception e) {
		    		throw Throwables.throwUncheckedException(e);
		    	}
		    }
		    return bindings;
		}
		
		private static Collection<Binding> wire(BoundExtent bound, BindingContext context, Field field) throws IntrospectionException {
			String target = bound.to();
            final ObjectFieldMethod setter = context.findSetter(target);
            final ObjectFieldMethod getter = context.findGetter(target);
            if (setter == null || getter == null) {
            	throw new IllegalArgumentException("could not find setter/getter for @BoundExtent: " + field); 
            }
            final BindableModel model1 = context.getFieldObject(setter.getField(), BindableModel.class);
            final BindableModel model2 = context.getFieldObject(getter.getField(), BindableModel.class);
            assert model1 == model2;
			if (Component.class.isAssignableFrom(field.getType())) {
				final JSlider comp = context.getFieldObject(field, JSlider.class);
				return bindSlider(bound, context, comp, setter, getter, model1);
			} else {
				throw new IllegalArgumentException("don't know how to wire up @BoundExtent field: " + field.getName()); 	
			}
		}

		private static Collection<Binding> bindSlider(BoundExtent bound,
				BindingContext context,
				final JSlider slider,
                final ObjectFieldMethod setter, final ObjectFieldMethod getter,
                final BindableModel model1) {
	        
			String on = bound.on();
			final Object onObject = context.evalOnObject(on, model1);
			
			final ChangeListener changeListener = new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					try {
						setter.getMethod().invoke(model1, slider.getExtent());
					} catch (Exception ex) {
						logger.error("could not invoke JSlider binding", ex); 
					}
				}
			};
			slider.addChangeListener(changeListener);
			
	        Binding binding = new Binding() {
	        	public <T extends Enum<?> & ModelUpdate> void update(T... changed) {
	        		if (!BindingContext.isOn(onObject, changed)) {
	        			return;
	        		}
	        		try {
	                    int extent = (Integer) getter.getMethod().invoke(model1);
	        			slider.removeChangeListener(changeListener);
	        			slider.setExtent(extent);
	        			slider.addChangeListener(changeListener);
	        		} catch (Exception ex) {
	                	logger.error("could not invoke JSlider binding", ex); 
	                }
	            }
	        };
	        model1.bind(binding);
	        return Collections.singleton(binding);
        }
	}
}
