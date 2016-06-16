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
package com.palantir.ptoss.util;

import java.io.InterruptedIOException;

/**
 * A class of utility methods to make exception handling cleaner.
 */
public final class Throwables {

    private Throwables() { /* Static utility methods only. */ }

    /**
     * If Throwable is a RuntimeException or Error, rethrow it. If not, throw a
     * new PalantirRuntimeException(ex)
     */
    public static RuntimeException throwUncheckedException(Throwable ex) {
        throwIfInstance(ex, RuntimeException.class);
        throwIfInstance(ex, Error.class);
        throw createRuntimeException(ex);
    }

    private static RuntimeException createRuntimeException(Throwable ex) {
        return createRuntimeException(ex.getMessage(), ex);
    }

    private static RuntimeException createRuntimeException(String newMessage, Throwable ex) {
        if (ex instanceof InterruptedException || ex instanceof InterruptedIOException) {
            Thread.currentThread().interrupt();
        }
        return new RuntimeException(newMessage, ex);
    }

    /**
     * if (t instanceof K) throw (K)t;
     * <p>
     * Note: The runtime type of the thrown exception will be the same as t even if
     * clazz is a supertype of t.
     */
    @SuppressWarnings("unchecked")
    public static <K extends Throwable> void throwIfInstance(Throwable t, Class<K> clazz) throws K {
        if ((t != null) && clazz.isAssignableFrom(t.getClass())) {
            K kt = (K) t;
            throw kt;
        }
    }
}
