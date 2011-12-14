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
 * A model that can have bindings applied to it.
 */
public interface BindableModel {
	/**
	 * Attach the binding to the model. Whenever the model is updated then the binding will be
	 * triggered.
	 * @param toBind
	 */
	void bind(Binding toBind);
	
	/**
	 * Removes the passed binding from this model.
	 * @param toUnbind
	 */
	void unbind(Binding toUnbind);
	
	/**
	 * Alert the bindings that the model has updated with the specified change types.
	 * @param changed list of change types to indicate
	 */
	<T extends Enum<T> & ModelUpdate> void modelUpdated(T... changed);
}
