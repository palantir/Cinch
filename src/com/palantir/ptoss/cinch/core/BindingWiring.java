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

import java.util.Collection;
/**
 * Interface for classes that perform wiring of bindings.
 */
public interface BindingWiring {
    /**
     * Performs wiring that this instance performs given the passed {@link BindingContext}.
     * @param context the {@link BindingContext} contain all the data about components in this context.
     * @return a collection of {@link Binding} objects wired by this {@link BindingWiring} instance.
     */
    public Collection<Binding> wire(BindingContext context);
}