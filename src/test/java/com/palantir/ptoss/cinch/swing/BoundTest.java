package com.palantir.ptoss.cinch.swing;

import junit.framework.TestCase;

public class BoundTest extends TestCase {

    public void testIsNullOrBlank() {
        assertTrue(Bound.Utilities.isNullOrBlank(null));
        assertTrue(Bound.Utilities.isNullOrBlank(""));
        assertTrue(Bound.Utilities.isNullOrBlank(" "));
        assertFalse(Bound.Utilities.isNullOrBlank("a"));
        assertFalse(Bound.Utilities.isNullOrBlank("   a  b c  "));
    }

}
