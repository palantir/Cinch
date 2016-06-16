package com.palantir.ptoss.cinch.example.dynamic;

import com.palantir.ptoss.cinch.core.DefaultBindableModel;

/**
 * Simple model for a boolean field.
 */
public class BooleanModel extends DefaultBindableModel {
    boolean state = false;

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
        update();
    }
}

