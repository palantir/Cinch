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
package com.palantir.ptoss.cinch.example.extension;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.palantir.ptoss.cinch.core.BindableModel;
import com.palantir.ptoss.cinch.core.Binding;
import com.palantir.ptoss.cinch.core.BindingContext;
import com.palantir.ptoss.cinch.core.BindingException;
import com.palantir.ptoss.cinch.core.BindingWiring;
import com.palantir.ptoss.cinch.core.ModelUpdate;

/**
 * A simple, example annotation that logs model updates via log4j.  This example is meant to be
 * a straightforward look at creating a new sort of annotation for use with Cinch.
 *
 *  @see ExtendedExample see ExtendedExample for usage in a program
 *  @see ExtendedBindings see ExentendBindings for how to wire this annotation into Cinch
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface LoggedModel {

    String logger() default "cinch.debug";

    static class Wiring implements BindingWiring {
        public Collection<Binding> wire(BindingContext context) {
            List<Field> loggedModels = context.getAnnotatedFields(LoggedModel.class);
            List<Binding> bindings = Lists.newArrayList();
            for (Field field : loggedModels) {
                final LoggedModel annotation = field.getAnnotation(LoggedModel.class);
                final String loggerParam = annotation.logger();
                final String fieldName = field.getName();
                if (BindableModel.class.isAssignableFrom(field.getType())) {
                    BindableModel model = context.getFieldObject(field, BindableModel.class);
                    Binding binding = new Binding() {
                        public <T extends Enum<?> & ModelUpdate> void update(T... changed) {
                            for (T t : changed){
                                Logger logger = LoggerFactory.getLogger(loggerParam);
                                if (logger.isInfoEnabled()) {
                                    logger.info(t.toString() + ": " + fieldName);
                                }
                            }
                        }
                    };
                    model.bind(binding);
                    bindings.add(binding);
                } else {
                    throw new BindingException("Can only log a BindableModel.");
                }
            }
            return bindings;
        }
    }
}
