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
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.palantir.ptoss.cinch.swing.Action;
import com.palantir.ptoss.cinch.swing.Bound;
import com.palantir.ptoss.cinch.swing.BoundExtent;
import com.palantir.ptoss.cinch.swing.BoundLocation;
import com.palantir.ptoss.cinch.swing.BoundSelection;
import com.palantir.ptoss.cinch.swing.EnabledIf;
import com.palantir.ptoss.cinch.swing.OnChange;
import com.palantir.ptoss.cinch.swing.OnClick;
import com.palantir.ptoss.cinch.swing.OnFocusChange;
import com.palantir.ptoss.cinch.swing.VisibleIf;

/**
 * <p>
 * All {@link Binding}s are called when {@link ModelUpdates#ALL} is called.
 * <p>
 * If a {@link Bound} component has an "on" parameter then it will only be triggered if the
 * specific {@link ModelUpdate} type is triggered.
 * <p>
 * If a {@link Bound} component has no "on" parameter then it will be triggered by any update
 * type (including none) triggered by its {@link BindableModel}.
 */
public class Bindings {

    private final List<Binding> bindings = Lists.newArrayList();

    private final ImmutableList<BindingWiring> wirings;

    /**
     * The list of {@link BindingWiring} classes that are standard to this framework.
     */
    public static final ImmutableList<BindingWiring> STANDARD_BINDINGS = ImmutableList.<BindingWiring>builder()
            .add(new Bound.Wiring())
            .add(new BoundSelection.Wiring())
            .add(new BoundExtent.Wiring())
            .add(new BoundLocation.Wiring())
            .add(new CallOnUpdate.Wiring())
            .add(new EnabledIf.Wiring())
            .add(new Action.Wiring())
            .add(new VisibleIf.Wiring())
            .add(new OnClick.Wiring())
            .add(new OnChange.Wiring())
            .add(new OnFocusChange.Wiring())
            .build();

    public static Bindings standard() {
        return new Bindings(STANDARD_BINDINGS);
    }

    public Bindings() {
        this.wirings = STANDARD_BINDINGS;
    }

    public Bindings(Collection<? extends BindingWiring> wirings) {
        this.wirings = ImmutableList.copyOf(wirings);
    }

    public void bind(Object object) {
        bindWithoutUpdate(object);
        updateAll();
    }

    public void bindWithoutUpdate(Object object) {
        BindingContext context = new BindingContext(object);
        bindings.addAll(createBindings(context));
    }

    public void release(BindableModel model) {
        for (Binding binding : bindings) {
            model.unbind(binding);
        }
    }

    protected List<Binding> createBindings(BindingContext context) {
        final List<Binding> bindingList = Lists.newArrayList();
        for (BindingWiring wiring : wirings) {
            bindingList.addAll(wiring.wire(context));
        }
        return bindingList;
    }

    public void updateAll() {
        for (Binding binding : bindings) {
            binding.update(ModelUpdates.ALL);
        }
    }
}
