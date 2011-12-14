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

import java.util.List;

import com.google.common.collect.Lists;
import com.palantir.ptoss.cinch.core.BindingWiring;
import com.palantir.ptoss.cinch.core.Bindings;

/**
 * A sample class that returns extended bindings for including customizations to Cinch.
 * 
 * @see Bindings
 * @see ExtendedExample
 * @see LoggedModel
 * 
 */
public class ExtendedBindings {
	/**
	 * Call this instead of {@link Bindings#standard()} to inject the custom bindings.
	 */
	public static Bindings extendedBindings() {
		// start with standard set of bindings
		List<BindingWiring> wirings = Lists.newArrayList(Bindings.STANDARD_BINDINGS);
		// add in all additions
		wirings.add(new LoggedModel.Wiring());
		return new Bindings(wirings);
	}
}
