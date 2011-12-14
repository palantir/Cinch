package com.palantir.ptoss.cinch;

import com.palantir.ptoss.cinch.core.DefaultBindableModel;
import com.palantir.ptoss.cinch.core.ModelUpdate;

public class SimpleModel extends DefaultBindableModel {
	private boolean simpleBoolean;
	
	public enum UpdateTypes implements ModelUpdate {
		NO_TRIGGER, SPECIFIC, MULTI_1, MULTI_2;
	}
	
	public void setSimpleBoolean(boolean simpleBoolean) {
        this.simpleBoolean = simpleBoolean;
        update();
    }
	
	public boolean isSimpleBoolean() {
        return simpleBoolean;
    }
}