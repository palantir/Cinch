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

import com.palantir.ptoss.cinch.swing.Bound;

/**
 * Pre-defined {@link ModelUpdate} types with special meanings to the framework.
 *
 * @see Bound
 * @see CallOnUpdate
 */
public enum ModelUpdates implements ModelUpdate {

    /**
     * <p>
     * This is used for unspecified update types in the default implementations of {@link BindableModel}.
     * Listeners that don't have a specified "on" parameter should react to these model updates.
     * </p>
     * <p>
     * <strong>Note:</strong> when bound to a specific {@link ModelUpdate}, a control will not
     * receive updates not sent specifically to any {@link ModelUpdate} - i.e. default updates. To
     * selectively answer a specific {@link ModelUpdate} and also still get default notications,
     * add {@link ModelUpdates#UNSPECIFIED} to the list of {@link ModelUpdate}s passed to {@link Bound#on()}.
     * </p>
     * @see Bound
     */
    UNSPECIFIED,

    /**
     * This is used to sync all listeners.  All listeners, regardless of whether they have an "on" parameter, should
     * react to these model updates.
     */
    ALL;
}
