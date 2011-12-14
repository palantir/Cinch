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

import javax.swing.JButton;

import junit.framework.TestCase;

import com.palantir.ptoss.cinch.core.Bindable;
import com.palantir.ptoss.cinch.core.Bindings;
import com.palantir.ptoss.cinch.swing.Action;

// This test exists because ideally you shouldn't have to specify "this.refresh" as below.
public class AbstractBoundMethodTest extends TestCase {
	
	@Bindable
	private abstract static class Base {
		@Action(call = "this.refresh")
		JButton button = new JButton("test");
		
		abstract public void refresh();
	}
	
	@Bindable
	private static class Impl extends Base {
		int refreshCount = 0;
	
		private final Bindings bindings = new Bindings();
		public Impl() {
			bindings.bind(this);
        }
		
		@Override
        public void refresh() {
			refreshCount++;
        }
	}
	
	public void testIt() throws Exception {
		Impl impl = new Impl();
		impl.button.doClick();
		assertEquals(1, impl.refreshCount);
    }
}
