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

/**
 * Marks the annotated field as not bindable.  In Cinch, all bindable fields are required to be
 * marked <code>final</code>.  In fact, if a {@link BindableModel} has a field that is not marked
 * <code>final</code>, a runtime error will during the call to {@link Bindings#bind(Object)}.  To
 * allow {@link BindableModel}s to have non-final fields, mark those fields with this annotation.
 * <p>
 * It can be thought of as similar to {@link SuppressWarnings} as a way to force the coder
 * to explicitly specify intention when doing something dangerous.
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface NotBindable {
    // Empty annotation.
}
