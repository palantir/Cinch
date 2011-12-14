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
package com.palantir.ptoss.cinch.swing;

import java.beans.IntrospectionException;
import java.lang.reflect.Field;
import java.util.Collection;

import javax.swing.JLabel;

import com.google.common.collect.ImmutableList;
import com.palantir.ptoss.cinch.core.*;
import com.palantir.ptoss.cinch.swing.Bound.Wiring;
import com.palantir.ptoss.util.Mutator;

/**
 * A {@link WiringHarness} for binding a {@link JLabel} to a value in a {@link BindableModel}.
 */
public class JLabelWiringHarness implements WiringHarness<Bound, Field> {
	public Collection<Binding> wire(Bound bound, BindingContext context, Field field) 
			throws IllegalAccessException, IntrospectionException {
		JLabel label = context.getFieldObject(field, JLabel.class);
		Mutator mutator = Mutator.create(context, bound.to());
		return ImmutableList.of(bindJLabel(mutator, label));
	}
	
	public static Binding bindJLabel(final Mutator mutator, final JLabel label) {
		Binding binding = new Binding() {
			public <T extends Enum<?> & ModelUpdate> void update(T... changed) {
				try {
					String text = ""; 
					Object obj = mutator.get();
					if (obj != null) {
						text = obj.toString();
					}
					label.setText(text);
				} catch (Exception ex) {
					Wiring.logger.error("exception in JLabel binding", ex); 
				}
			}
		};
		mutator.getModel().bind(binding);
		return binding;
	}
}