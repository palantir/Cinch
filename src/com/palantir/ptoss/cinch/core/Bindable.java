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

import com.palantir.ptoss.cinch.swing.Action;

/**
 * <p>
 * Marks the annotated field or type as bindable.  For a type this makes all methods 
 * accessible to bindings that have a 'call' parameter like {@link Action}. 
 * </p><p>
 * Note that this
 * allows the methods to be called, but it does not enable the binding of controls to 
 * fields on the object being marked as bindable.
 * </p>
 * 
 * @see BindableModel Information on creating bindable models.
 * @see Action Information on using @Action annotations
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface Bindable {
	// Empty annotation
}
