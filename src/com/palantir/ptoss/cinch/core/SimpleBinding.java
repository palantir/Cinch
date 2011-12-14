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

import com.google.common.collect.ImmutableSet;

/**
 * An implementation of {@link Binding} that hides the complicated method signature required
 * of that interface. Instead, implementors can override the {@link #onUpdate()} function for
 * their listener's behavior. Any "on" parameters can be passed in at creation time.
 */
public abstract class SimpleBinding implements Binding {

    private final ImmutableSet<Object> on;

    private ImmutableSet<Object> changed = ImmutableSet.of();

    /**
     * Default constructor that will fire on any model update.
     */
    public <T extends Enum<?> & ModelUpdate> SimpleBinding() {
        this.on = ImmutableSet.of();
    }

    /**
     * Constructor that will make it so this {@link Binding} only fires on the given
     * "on" parameters.
     * @param <T>
     * @param on
     */
    public <T extends Enum<?> & ModelUpdate> SimpleBinding(T... on) {
        this.on = ImmutableSet.<Object>copyOf(on);
    }

    /**
     * {@inheritDoc}
     */
    public <T extends Enum<?> & ModelUpdate> void update(T... changes) {
        boolean fire = false;
        if (on.isEmpty()) {
            fire = true;
        } else {
            for (T t : changes) {
                if (t == ModelUpdates.ALL || on.contains(t)) {
                    fire = true;
                    break;
                }
            }
        }
        if (fire) {
            this.changed = ImmutableSet.<Object>copyOf(changes);
            onUpdate();
        }
    }

    /**
     * Gets the set of {@link ModelUpdate}s that occurred on the last update.
     */
    public ImmutableSet<Object> getLastChanged() {
        return changed;
    }

    /**
     * This will be called when the model is changed in a way compatible with the "on"
     * parameters specified at compile time.
     */
    public abstract void onUpdate();
}
