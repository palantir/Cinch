![Palantir Logo](https://github.com/palantir/Cinch/wiki/palantir-masthead.png)
# Cinch - annotations to make MVC easy #

[![CircleCI Build Status](https://circleci.com/gh/palantir/Cinch/tree/master.svg)](https://circleci.com/gh/palantir/Cinch)
![Maven Central](https://img.shields.io/maven-central/v/com.palantir.opensource/cinch)

---

### About #

Cinch makes MVC in Swing easy. Cinch is a Java library used by developers to simplify writing certain types of GUI code.

When developing Swing applications it's very easy to fall into the trap of not separating out Models and Controllers. It's all too easy to just store the state of that boolean in the checkbox itself, or that String in the JTextField. The design goal behind Cinch was to make it easier to apply MVC than to not by reducing much of the typical Swing friction and boilerplate.

Cinch uses Java annotations to reflectively wire up Models, Views, and Controllers. 

### Project Resources #

* The [Wiki](http://github.com/palantir/Cinch/wiki) has all the project documentation.
* API docs are available [here](http://palantir.github.com/Cinch/apidocs)
* Mailing lists are hosted on Google Groups:
    * [Announce](http://groups.google.com/group/ptoss-cinch-announce)
    * [General](http://groups.google.com/group/ptoss-cinch)
* Please file issues on the [Github Issue Tracker](https://github.com/palantir/Cinch/issues)
* Email project admin: [Ari Gesher](mailto:agesher@palantir.com)

## Example: [IntroCinchMVC.java](http://github.com/palantir/Cinch/blob/master/example/com/palantir/ptoss/cinch/example/IntroCinchMVC.java)

Believe it or not, this is a complete Swing program (note the lack of anonymous listener objects) - full source is available in [IntroCinchMVC.java](https://github.com/palantir/Cinch/blob/master/example/com/palantir/ptoss/cinch/example/IntroCinchMVC.java):

```java

public class IntroCinchMVC {

    public static class IntroModel extends DefaultBindableModel {
        private String to = "";
        private String subject = "";
        private String body = "";
        
        public String getBody() {
            return body;
        }
        
        public void setBody(String body) {
            this.body = body;
            update();
        }
        
        public String getSubject() {
            return subject;
        }
        
        public void setSubject(String subject) {
            this.subject = subject;
            update();
        }
        
        public String getTo() {
            return to;
        }
        
        public void setTo(String to) {
            this.to = to;
            update();
        }
        
        public String getCurrentMessage() {
            if (Strings.isNullOrEmpty(to)) {
                return "Fill out 'To' field.";
            } 
            if (Strings.isNullOrEmpty(subject)) {
                return "Fill out 'Subject' field.";
            } 
            if (Strings.isNullOrEmpty(body)) {
                return "Fill out 'Body'.";
            } 
            return "Ready to send.";
        }
        
        public boolean isReady() {
            return !Strings.isNullOrEmpty(to) && !Strings.isNullOrEmpty(subject) && !Strings.isNullOrEmpty(body);
        }
        
        @Override
        public String toString() {
            return "IntroModel [to=" + to + ", subject=" + subject + ", body=" + body + "]";
        }
    }
    
    public static class IntroController {
        private final IntroModel model;
        
        public IntroController(IntroModel model) {
            this.model = model;
        }

        public void sendEmail() {
            System.out.println("Send: " + model);
        }
        
        public void yell() {
            model.setBody(model.getBody().toUpperCase());
        }
    }
    
    private final JPanel panel = new JPanel();
    private final Bindings bindings = new Bindings();
    private final IntroModel model = new IntroModel();
    
    @SuppressWarnings("unused")
    @Bindable
    private final IntroController controller = new IntroController(model);
    @Bound(to = "to")
    private final JTextField toField = new JTextField();
    @Bound(to = "subject")
    private final JTextField subjectField = new JTextField();
    @Bound(to = "body")
    private final JTextArea bodyArea = new JTextArea();
    @Action(call = "yell")
    private final JButton yellButton = new JButton("YELL!");
    @Action(call = "sendEmail")
    @EnabledIf(to = "ready")
    private final JButton sendButton = new JButton("Send");

    @Bound(to = "currentMessage")
    private final JLabel messageLabel = new JLabel("");
    
    public IntroCinchMVC() {
        initializeInterface();
        bindings.bind(this);
    }

    private void initializeInterface() {
        // swing layout code cut for brevity
        // ...
    }

    public JComponent getDisplayComponent() {
        return panel;
    }
    
    // main removed
}

```

## Authors #

[Dan Cervelli](https://github.com/dcervelli)

## License #

Cinch is made available under the Apache 2.0 License.

>Copyright 2011 Palantir Technologies
>
>Licensed under the Apache License, Version 2.0 (the "License");
>you may not use this file except in compliance with the License.
>You may obtain a copy of the License at
>
><http://www.apache.org/licenses/LICENSE-2.0>
>
>Unless required by applicable law or agreed to in writing, software
>distributed under the License is distributed on an "AS IS" BASIS,
>WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
>See the License for the specific language governing permissions and
>limitations under the License.
