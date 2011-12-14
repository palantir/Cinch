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

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import com.google.common.collect.Sets;

/**
 * A stand-alone implementation of {@link BindableModel}.  Uses {@link WeakReference}s to
 * attach bindings to models.
 */
public class WeakBindableModelSupport implements BindableModel {

    // It's possible for a binding to kick off a chain of events that culminates in the
    // addition of new bindings which can throw a CME when iterating in #modelUpdated.
    // We'll use a CopyOnWriteArrayList instead.  This seems like a good compromise since
    // the number of reads of this list is likely to far outweigh the number of writes.
    private final List<WeakReference<Binding>> bindings = new CopyOnWriteArrayList<WeakReference<Binding>>();

    /**
     * {@inheritDoc}
     */
    public void bind(final Binding binding) {
        bindings.add(new WeakReference<Binding>(binding));
    }

    /**
     * Shortcut call for a generic model update.
     */
    public void update() {
        this.modelUpdated(ModelUpdates.UNSPECIFIED);
    }

    /**
     * {@inheritDoc}
     */
    public <T extends Enum<T> & ModelUpdate> void modelUpdated(final T... changed) {
        final Set<WeakReference<Binding>> toRemove = Sets.newHashSet();
        for (final WeakReference<Binding> weakBinding : bindings) {
            final Binding binding = weakBinding.get();
            if (binding != null) {
                binding.update(changed);
            } else {
                toRemove.add(weakBinding);
            }
        }
        if (!toRemove.isEmpty()) {
            bindings.removeAll(toRemove);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void unbind(Binding toUnbind) {
        final Set<WeakReference<Binding>> toRemove = Sets.newHashSet();
        for (final WeakReference<Binding> weakBinding : bindings) {
            final Binding binding = weakBinding.get();
            if (binding != null) {
                if (toUnbind.equals(binding)) {
                    toRemove.add(weakBinding);
                }
            } else {
                toRemove.add(weakBinding);
            }
        }
        if (!toRemove.isEmpty()) {
            bindings.removeAll(toRemove);
        }
    }

    /**
     * Removes all bindings from this model.
     */
    public void unbindAll() {
        bindings.clear();
    }
}
