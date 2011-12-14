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

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.google.common.collect.ImmutableList;
import com.palantir.ptoss.cinch.core.*;
import com.palantir.ptoss.cinch.swing.Bound.Wiring;
import com.palantir.ptoss.util.Mutator;

/**
 * A {@link WiringHarness} for binding a {@link JSlider} to an {@link Integer} value in a 
 * {@link BindableModel}.
 */
public class JSliderWiringHarness implements WiringHarness<Bound, Field> {
	public Collection<Binding> wire(Bound bound, BindingContext context, Field field) throws IllegalAccessException, IntrospectionException {
		String target = bound.to();
		Mutator mutator = Mutator.create(context, target);
		JSlider slider = context.getFieldObject(field, JSlider.class); 
        return ImmutableList.of(bindJSlider(mutator, slider));
    }
	
	public static Binding bindJSlider(final Mutator mutator, final JSlider slider) { 
	    final ChangeListener changeListener = new ChangeListener() {
        	public void stateChanged(ChangeEvent e) {
        		try {
        			if (!slider.getValueIsAdjusting()) {
        				mutator.set(slider.getValue());
        			}
            	} catch (Exception ex) {
                	Wiring.logger.error("exception in JSlider binding", ex); 
                }	     
        	}
        };
		slider.addChangeListener(changeListener);
	    Binding binding = new Binding() {
	    	public <T extends Enum<?> & ModelUpdate> void update(T... changed) {
				try {
					Integer val = (Integer)mutator.get();
					if (val == null) {
						val = -1;
					}
					if (!val.equals(slider.getValue())) {
						slider.removeChangeListener(changeListener);
						slider.setValue(val);
						slider.addChangeListener(changeListener);
					}
				} catch (Exception ex) {
					Wiring.logger.error("exception in JSlider binding", ex); 
				}
	    	}
	    };
	    mutator.getModel().bind(binding);
	    return binding;
	}
}