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

import javax.swing.JTextField;

import junit.framework.TestCase;

import com.palantir.ptoss.cinch.core.Bindings;
import com.palantir.ptoss.cinch.core.DefaultBindableModel;
import com.palantir.ptoss.cinch.swing.Bound;

public class BoundJTextComponentTest extends TestCase {

	public static class Model extends DefaultBindableModel {
		private String readOnly;
		private String writeOnly;
		private String readWrite;
		
		public void setReadOnly0(String readOnly) {
	        this.readOnly = readOnly;
	        update();
        }
		
		public String getReadOnly() {
	        return readOnly;
        }

		public String getWriteOnly0() {
			return writeOnly;
		}
		
		public void setWriteOnly(String writeOnly) {
	        this.writeOnly = writeOnly;
	        update();
        }
		
		public String getReadWrite() {
	        return readWrite;
        }
		
		public void setReadWrite(String readWrite) {
	        this.readWrite = readWrite;
	        update();
        }
	}
	
	private final Model model = new Model();
	
	private final Bindings bindings = new Bindings();

	@Bound(to = "readOnly")
	private final JTextField readField = new JTextField();
	@Bound(to = "writeOnly")
	private final JTextField writeField = new JTextField();
	@Bound(to = "readWrite")
	private final JTextField readWriteField = new JTextField();
	
	@Override
	protected void setUp() throws Exception {
		bindings.bind(this);
	}
	
	public void testReadWrite() {
		assertEquals(null, model.getReadWrite());
		assertEquals("", readWriteField.getText());
		
		model.setReadWrite("readWrite");
		assertEquals("readWrite", model.getReadWrite());
		assertEquals("readWrite", readWriteField.getText());
		
		readWriteField.setText("fromTextField");
		assertEquals("fromTextField", model.getReadWrite());
		assertEquals("fromTextField", readWriteField.getText());
	}
	
	public void testReadOnly() {
		assertEquals(null, model.getReadOnly());
		assertEquals("", readField.getText());
		
		model.setReadOnly0("readOnly");
		assertEquals("readOnly", model.getReadOnly());
		assertEquals("readOnly", readField.getText());
		
		readField.setText("shouldNotChange");
		assertEquals("readOnly", model.getReadOnly());
		assertEquals("shouldNotChange", readField.getText());
		
		model.setReadOnly0("synch");
		assertEquals("synch", model.getReadOnly());
		assertEquals("synch", readField.getText());
	}
	
	public void testWriteOnly() {
		assertEquals(null, model.getWriteOnly0());
		assertEquals("", writeField.getText());
		
		model.setWriteOnly("writeOnly");
		assertEquals("writeOnly", model.getWriteOnly0());
		assertEquals("", writeField.getText());
		
		writeField.setText("synch");
		assertEquals("synch", model.getWriteOnly0());
		assertEquals("synch", writeField.getText());
	}
}
