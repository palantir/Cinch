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

import java.util.List;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

/**
 * <p>
 * Superclass for {@link BindableModel} instances, this class handles the binding
 * tasks for subclassed models.
 * </p>
 * <p>
 * Basic theory of operation:
 * TODO
 */
public class WeakBindableModel implements BindableModel {
    /**
     * A delegate object that implements all of the logic behind {@link BindableModel}.
     */
    private final WeakBindableModelSupport support = new WeakBindableModelSupport();
    /**
     * List of strong bindings.  This {@link List} holds strong references to all
     * of the {@link Binding} objects passed to this model.
     */
    private final List<Binding> strongBindings = Lists.newArrayList();

    /**
     * Binds this model to some component with a {@link Binding} object.  This method is called by
     * the various {@link WiringHarness} objects to bind individual components to this model.
     *
     * @param binding
     */
    public void bind(Binding binding) {
        support.bind(binding);
    }

    /**
     * Binds this model to a {@link Binding} using strong references.
     *
     * @param binding
     */
    public void bindStrongly(Binding binding) {
        support.bind(binding);
        strongBindings.add(binding);
    }

    /**
     * {@inheritDoc}
     */
    public <T extends Enum<T> & ModelUpdate> void modelUpdated(T... changed) {
        support.modelUpdated(changed);
    }

    /**
     * Fires a model update if the old and new values are different.
     * @param oldValue original value to be compared
     * @param newValue new value to compare against
     * @param <T> A varargs list of enumerated {@link ModelUpdate} types.
     */
    public <T extends Enum<T> & ModelUpdate> void modelUpdated(
            Object oldValue, Object newValue, T... changed) {
        if (!Objects.equal(oldValue, newValue)) {
            modelUpdated(changed);
        }
    }

    /**
     * Called by the implementing <code>Model</code> to notify all listeners
     * that data in the <code>Model</code> has changed.
     */
    public void update() {
        support.update();
    }

    /**
     * {@inheritDoc}
     */
    public void unbind(Binding binding) {
        support.unbind(binding);
        strongBindings.remove(binding);
    }

    /**
     * Unbinds both weak and strong {@link Binding} from this Model.
     * @see BindableModel#unbind(Binding)
     */
    public void unbindAll() {
        support.unbindAll();
        strongBindings.clear();
    }

}
