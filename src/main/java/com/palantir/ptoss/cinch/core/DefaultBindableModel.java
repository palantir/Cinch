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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Default implementation of {@link BindableModel} - should be subclassed by implementations.
 */
public class DefaultBindableModel implements BindableModel {

    // must be transient because Bindings aren't serializable, and
    // subclasses might be serialized.
    private transient List<Binding> bindings;

    public DefaultBindableModel() {
        bindings = new CopyOnWriteArrayList<Binding>();
    }

    /**
     * {@inheritDoc}
     */
    public void bind(Binding toBind) {
        if (!bindings.contains(toBind)) {
            bindings.add(toBind);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void unbind(Binding toUnbind) {
        bindings.remove(toUnbind);
    }

    /**
     * Removes all bindings from this model.
     */
    public void unbindAll() {
        bindings.clear();
    }
    /**
     * {@inheritDoc}
     */
    public <T extends Enum<T> & ModelUpdate> void modelUpdated(T... changed) {
        for (Binding binding : bindings) {
            binding.update(changed);
        }
    }

    /**
     * Performs a model update of type {@link ModelUpdates#UNSPECIFIED} - convenience method
     * for most models.
     */
    public void update() {
        this.modelUpdated(ModelUpdates.UNSPECIFIED);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        bindings = new CopyOnWriteArrayList<Binding>();
    }

}
