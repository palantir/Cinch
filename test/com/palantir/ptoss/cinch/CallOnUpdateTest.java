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
package com.palantir.ptoss.cinch;

import junit.framework.TestCase;

import com.palantir.ptoss.cinch.SimpleModel.UpdateTypes;
import com.palantir.ptoss.cinch.core.Bindings;
import com.palantir.ptoss.cinch.core.CallOnUpdate;
import com.palantir.ptoss.cinch.core.ModelUpdates;

public class CallOnUpdateTest extends TestCase {
	
	private final SimpleModel model;
	
	int privateCount = 0;
	int publicCount = 0;
	int specificCount = 0;
	int multiCount = 0;
	
	private final Bindings bindings = Bindings.standard();
	
	public CallOnUpdateTest() {
		model = new SimpleModel();
		bindings.bind(this);
    }
	
	@SuppressWarnings("unused")
    @CallOnUpdate
	private void privateMethod() {
		privateCount++;
	}
	
	@CallOnUpdate
	public void publicMethod() {
		publicCount++;
	}
	
	@CallOnUpdate(on = "SPECIFIC")
	public void specificMethod() {
		specificCount++;
	}
	
	@CallOnUpdate(on = {"MULTI_1", "MULTI_2"})
	public void multiMethod() {
		multiCount++;
	}
	
	public void testCallOnUpdate() {
		// 1 from initial setting
		assertEquals(1, privateCount);
		assertEquals(1, publicCount);
		assertEquals(1, specificCount);
		assertEquals(1, multiCount);
		model.setSimpleBoolean(true);
		assertEquals(2, privateCount);
		assertEquals(2, publicCount);
		assertEquals(1, specificCount);
		model.setSimpleBoolean(false);
		assertEquals(3, privateCount);
		assertEquals(3, publicCount);
		assertEquals(1, specificCount);
		model.modelUpdated(ModelUpdates.ALL);
		assertEquals(4, privateCount);
		assertEquals(4, publicCount);
		assertEquals(2, specificCount);
		model.modelUpdated(UpdateTypes.NO_TRIGGER);
		assertEquals(5, privateCount);
		assertEquals(5, publicCount);
		assertEquals(2, specificCount);
		model.modelUpdated(UpdateTypes.SPECIFIC);
		assertEquals(6, privateCount);
		assertEquals(6, publicCount);
		assertEquals(3, specificCount);
		assertEquals(2, multiCount);
		model.modelUpdated(UpdateTypes.MULTI_1);
		assertEquals(3, multiCount);
		model.modelUpdated(UpdateTypes.MULTI_2);
		assertEquals(4, multiCount);
	}
	
	@SuppressWarnings("unused")
	private class TwoModelsFail {
		final SimpleModel m1 = new SimpleModel();
		final SimpleModel m2 = new SimpleModel();
		
		final Bindings bindings = Bindings.standard();
		
		public TwoModelsFail() {
			bindings.bind(this);
		}
		
		@CallOnUpdate
		public void shouldFail() {
			throw new IllegalStateException();
		}
	}
	
	public void testTwoModelsFail() {
		try {
			new TwoModelsFail();
			fail("shouldn't be able to instantiate @CallOnUpdate with two models and no model parameter");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	static class TwoModelsSucceed {
		final SimpleModel m1 = new SimpleModel();
		final SimpleModel m2 = new SimpleModel();
		
		final Bindings bindings = Bindings.standard();
		
		public TwoModelsSucceed() {
			bindings.bind(this);
		}
		
		int m1Count = 0;
		@CallOnUpdate(model = "m1")
		public void m1Update() {
			m1Count++;
		}
		
		int m2Count = 0;
		@CallOnUpdate(model = "m2")
		public void m2Update() {
			m2Count++;
		}
	}
	
	public void testTwoModelsSucceed() {
		TwoModelsSucceed model = new TwoModelsSucceed();
		assertEquals(1, model.m1Count);
		assertEquals(1, model.m2Count);
		model.m1.modelUpdated(ModelUpdates.ALL);
		model.m1.update();
		assertEquals(3, model.m1Count);
		assertEquals(1, model.m2Count);
		model.m2.modelUpdated(ModelUpdates.ALL);
		model.m2.update();
		assertEquals(3, model.m1Count);
		assertEquals(3, model.m2Count);
	}
	
	static class FailNoModel {
		final SimpleModel m = new SimpleModel();
		final Bindings bindings = Bindings.standard();
		
		public FailNoModel() {
			bindings.bind(this);
		}
		
		@CallOnUpdate(model = "foo")
		public void shouldFail() {
			throw new IllegalStateException();
		}
	}
	
	public void testFailNoModel() {
		try {
			new FailNoModel();
			fail("shouldn't be able to instantiate @CallOnUpdate with missing model");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	static class FailNoOn {
		final SimpleModel m = new SimpleModel();
		final Bindings bindings = Bindings.standard();
		
		public FailNoOn() {
			bindings.bind(this);
		}
		
		@CallOnUpdate(on = "foo")
		public void shouldFail() {
			throw new IllegalStateException();
		}
	}
	
	public void testFailNoOn() {
		try {
			new FailNoOn();
			fail("shouldn't be able to instantiate @CallOnUpdate with missing on");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
