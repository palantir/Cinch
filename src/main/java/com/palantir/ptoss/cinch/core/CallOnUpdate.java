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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * An annotation to mark that a method should be called when the bound model updates
 * in a specific way. The method can have any access modifiers, i.e. it can be private.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface CallOnUpdate {
    /**
     * The model field name to bind to. If this is left blank and the {@link BindingContext} has
     * only a single {@link BindableModel} then it will bind to that.
     */
    String model() default "";

    /**
     * What model update type to trigger on, or blank (default) for all updates.
     */
    String[] on() default ""; //$NON-NLS-1$

    /**
     * Utility class that performs the wiring for {@link CallOnUpdate} annotations.
     */
    static class Wiring implements BindingWiring {
        private static final Logger logger = LoggerFactory.getLogger(CallOnUpdate.class);

        public Collection<Binding> wire(final BindingContext context) {
            final List<ObjectFieldMethod> methods = context.getAnnotatedParameterlessMethods(CallOnUpdate.class);
            final List<Binding> bindings = Lists.newArrayList();
            for (final ObjectFieldMethod method : methods) {
                final CallOnUpdate callOnUpdate = method.getMethod().getAnnotation(CallOnUpdate.class);
                bindings.addAll(wire(callOnUpdate, context, method));
            }
            return bindings;
        }

        private static Collection<Binding> wire(final CallOnUpdate callOnUpdate, final BindingContext context, final ObjectFieldMethod method) {
            final String to = callOnUpdate.model();
            final BindableModel model;
            if (Strings.isNullOrEmpty(to)) {
                Set<BindableModel> models = context.getBindableModels();
                if (models.size() != 1) {
                    throw new BindingException("more than one bindable model for empty 'to'"); //$NON-NLS-1$
                }
                model = models.iterator().next();
            } else {
                model = context.getBindableModel(to);
                if (model == null) {
                    throw new BindingException("can't find method to bind to: " + to); //$NON-NLS-1$
                }
            }
            final String[] ons = callOnUpdate.on();
            List<Object> onObjects = BindingContext.getOnObjects(ons, model);
            Binding binding = makeBinding(method, onObjects);
            model.bind(binding);
            return ImmutableList.of(binding);
        }

        private static Binding makeBinding(final ObjectFieldMethod method, final List<Object> onObjects) {
            final Method actualMethod = method.getMethod();
            actualMethod.setAccessible(true);
            final Binding binding = new Binding() {
                public <T extends Enum<?> & ModelUpdate> void update(final T... changed) {
                    if (!BindingContext.isOn(onObjects, changed)) {
                        return;
                    }
                    try {
                        actualMethod.invoke(method.getObject());
                    } catch (final InvocationTargetException itex) {
                        logger.error("exception during CallOnUpdate firing", itex.getCause()); //$NON-NLS-1$
                    } catch (final Exception e) {
                        logger.error("exception in method binding", e); //$NON-NLS-1$
                    }
                }
            };
            return binding;
        }
    }
}
