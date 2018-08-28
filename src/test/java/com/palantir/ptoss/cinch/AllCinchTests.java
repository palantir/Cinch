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

import com.palantir.ptoss.cinch.negative.CantFindGetterSetterTest;
import com.palantir.ptoss.cinch.negative.InaccessibleControllerMethodTest;
import com.palantir.ptoss.cinch.negative.NegativeActionTest;
import com.palantir.ptoss.cinch.negative.WrongTypeTest;

import com.palantir.ptoss.cinch.swing.BoundTest;
import junit.framework.Test;
import junit.framework.TestSuite;

public class AllCinchTests {
    public static Test suite() {
        TestSuite suite = new TestSuite("Palantir Cinch Tests");
        //$JUnit-BEGIN$
        suite.addTestSuite(BindingsTest.class);
        suite.addTestSuite(BindingsSubclassTest.class);
        suite.addTestSuite(CantFindGetterSetterTest.class);
        suite.addTestSuite(WrongTypeTest.class);
        suite.addTestSuite(InaccessibleControllerMethodTest.class);
        suite.addTestSuite(AbstractBoundMethodTest.class);
        suite.addTestSuite(BindingContextIndexTest.class);
        suite.addTestSuite(BindingContextTest.class);
        suite.addTestSuite(EnabledIfTest.class);
        suite.addTestSuite(ActionTest.class);
        suite.addTestSuite(ViewSubclassModelNameCollisionTest.class);
        suite.addTestSuite(NotBindableTest.class);
        suite.addTestSuite(SubclassEdgeCasesTest.class);
        suite.addTestSuite(NegativeActionTest.class);
        suite.addTestSuite(CallOnUpdateTest.class);

        suite.addTestSuite(BoundJLabelTest.class);
        suite.addTestSuite(BoundJComboBoxTest.class);
        suite.addTestSuite(BoundJProgressBarTest.class);
        suite.addTestSuite(BoundJCheckBoxTest.class);
        suite.addTestSuite(BoundJCheckBoxMenuItemTest.class);
        suite.addTestSuite(BoundJToggleButtonTest.class);
        suite.addTestSuite(BoundJSliderTest.class);
        suite.addTestSuite(BoundJTextComponentTest.class);
        suite.addTestSuite(BoundJListTest.class);
        suite.addTestSuite(BoundTest.class);
        //$JUnit-END$
        return suite;
    }
}
