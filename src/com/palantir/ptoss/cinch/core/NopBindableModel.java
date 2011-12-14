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


/**
 * A {@link BindableModel} implementation which does nothing. Can be used as a superclass to
 * an immutable class that needs to implement {@link BindableModel}.
 */
public class NopBindableModel implements BindableModel {

    public void bind(Binding to) {
        // Do nothing
    }

    public void unbind(Binding binding) {
        // Do nothing
    }

    public <T extends Enum<T> & ModelUpdate> void modelUpdated(T... changed) {
        // Do nothing
    }

}
