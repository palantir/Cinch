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

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import junit.framework.TestCase;

import com.palantir.ptoss.cinch.core.Bindings;
import com.palantir.ptoss.cinch.core.DefaultBindableModel;
import com.palantir.ptoss.cinch.swing.Bound;

/**
 * This is meant to capture some feedback that was received about using Bindings with subclasses.
 */
public class SubclassEdgeCasesTest extends TestCase {

    class BaseModel extends DefaultBindableModel {
        private String displayText;

        public String getDisplayText() {
            return displayText;
        }

        public void setDisplayText(String value) {
            displayText = value;
            update();
        }
    }

    class CommentableModel extends BaseModel {
        private String comment;

        public String getComment() {
            return comment;
        }

        public void setComment(String value) {
            comment = value;
            update();
        }
    }

    class BaseView<T extends BaseModel> {
        protected final T model;
        protected Bindings bindings = new Bindings();

        protected JPanel panel = new JPanel(new BorderLayout());

        // "model." is required to prevent subclasses from clobbering
        @Bound(to = "model.displayText")
        private final JLabel label = new JLabel();

        public BaseView(T model) {
            this.model = model;

            panel.add(label, BorderLayout.CENTER);
        }
    }

    class CommentsView extends BaseView<CommentableModel> {
        @Bound(to = "comment")
        private JLabel countLabel = new JLabel();

        public CommentsView(CommentableModel model) {
            super(model);

            panel.add(countLabel, BorderLayout.EAST);

            bindings.bind(this);
        }
    }

    /**
     * Bindings are done against the declared type of models, not the runtime type.
     */
    public void testReflectionUponDeclaredType() {
        CommentableModel model = new CommentableModel();
        // should throw IllegalArgumentException: could not find getter/setter for comment
        // because it's performing introspection against the declared type of the model in BaseView,
        // which is BaseModel
        // even though the runtime type is CommentableModel
        try {
            CommentsView view = new CommentsView(model);
            fail("should have thrown exception");
        } catch (IllegalArgumentException ex) {
            assertEquals("could not find either getter/setter for comment", ex.getMessage());
        }
    }

    public class CommentsView2 extends BaseView<CommentableModel> {

        private final CommentableModel commentableModel;

        @Bound(to="comment")
        private JLabel countLabel = new JLabel();

        private Bindings bindings = new Bindings();

        public CommentsView2(CommentableModel model) {
            super(model);
            this.commentableModel = model;

            panel.add(countLabel, BorderLayout.EAST);

            bindings.bind(this);
        }
    }

    /**
     * Views that are intended to be subclassed should explicitly name their bound models:
     * Example:
     * <pre>
     * @Bound(to = "model.displayText")
     * </pre>
     * instead of
     * <pre>
     * @Bound(to = "displayText")
     * </pre>
     */
    public void testSuperclassExplicitBinding() {
        CommentableModel model = new CommentableModel();
        // should throw IllegalArgumentException: could not find getter/setter for displayText
        // actually caused by there being more than one displayText property available in the context
        CommentsView2 view = new CommentsView2(model);
    }
}
