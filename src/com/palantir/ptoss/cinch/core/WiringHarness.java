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

import java.beans.IntrospectionException;
import java.lang.annotation.Annotation;
import java.util.Collection;

/**
 * Interface for classes that actually perform a specific wiring.
 * @author regs
 *
 * @param <T> the type of {@link Annotation} that this harness consumes.
 * @param <W> the type of component that this harness knows how to wire.
 */
public interface WiringHarness<T extends Annotation, W> {
	/**
	 * Performs the wiring of a specific component based on the passed binding annotation.
	 * @param bound annotation containing the data needed to wire this component to the context.
	 * @param context metadata about the context for wiring.
	 * @param toWire the component to be wired by this harness.
	 * @return one or more {@link Binding} objects represent the wirings performed by this harness.
	 * @throws IllegalAccessException
	 * @throws IntrospectionException
	 */
	Collection<? extends Binding> wire(T bound, BindingContext context, W toWire) 
		throws IllegalAccessException, IntrospectionException;
}
