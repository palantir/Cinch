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

import com.palantir.ptoss.cinch.core.Bindings;
import com.palantir.ptoss.cinch.core.CallOnUpdate;
import com.palantir.ptoss.cinch.core.DefaultBindableModel;

public class ViewSubclassModelNameCollisionTest extends TestCase {
	class Model1 extends DefaultBindableModel {
		// empty
	}
	
	class Model2 extends DefaultBindableModel {
		// empty
	}

	class BaseView {
		final Model1 model = new Model1();
	}

	class DerivedView extends BaseView {
		private final Bindings bindings = new Bindings();
		
		final Model2 model = new Model2();
		
		final Model2 otherModel = new Model2();
		
		int otherCount = 0;
		int thisCount = 0;
		int superCount = 0;
		
		public DerivedView() {
			bindings.bind(this);  // throws an exception
		}
		
		@CallOnUpdate(model = "DerivedView.model")
		public void thisCount() {
			thisCount++;
		}
		
		@CallOnUpdate(model = "BaseView.model")
		public void superCount() {
			superCount++;
		}
		
		@CallOnUpdate(model = "otherModel")
		public void otherCount() {
			otherCount++;
		}
		
		@CallOnUpdate(model = "DerivedView.otherModel")
		public void otherCount2() {
			otherCount++;
		}
	}
	
	public void testViewSubclass() {
		DerivedView v = new DerivedView();
		assertEquals(1, v.thisCount);
		assertEquals(1, v.superCount);
		assertEquals(2, v.otherCount);
		v.model.update();
		assertEquals(2, v.thisCount);
		assertEquals(1, v.superCount);
		assertEquals(2, v.otherCount);
		((BaseView)v).model.update();
		assertEquals(2, v.thisCount);
		assertEquals(2, v.superCount);
		assertEquals(2, v.otherCount);
		v.otherModel.update();
		assertEquals(2, v.thisCount);
		assertEquals(2, v.superCount);
		assertEquals(4, v.otherCount);
	}
}
