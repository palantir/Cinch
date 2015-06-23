#!/usr/bin/env bash

cd -P -- "$(dirname -- "$0")"

function error_exit {
	echo $1
	exit $2
}

mvn  || error_exit "Maven build of Cinch failed" 1
mvn javadoc:javadoc || error_exit "Maven build of Cinch javadocs failed" 2

COMMITREF=`git log -n 1  --oneline | awk '{print $1}'`

cd target/dist
JAR=`ls ptoss-cinch-*-SNAPSHOT.jar`

VERSIONED_NAME=`echo $JAR | sed "s/SNAPSHOT/SNAPSHOT-${COMMITREF}/" | sed 's/.jar//'`

mv $JAR ${VERSIONED_NAME}.jar
cp -r ../site/apidocs/ ./
cd ..
cp -r dist ${VERSIONED_NAME}

cat > ${VERSIONED_NAME}/README.txt <<EOF
==========================================================================

Cinch - annotations to make MVC easy

==========================================================================

Cinch makes MVC in Swing easy. Cinch is a Java library used by developers
to simplify writing certain types of GUI code.

When developing Swing applications it's very easy to fall into the trap
of not separating out Models and Controllers. It's all too easy to just
store the state of that boolean in the checkbox itself, or that String
in the JTextField. The design goal behind Cinch was to make it easier
to apply MVC than to not by reducing much of the typical Swing friction
and boilerplate.

Cinch uses Java annotations to reflectively wire up Models, Views,
and Controllers. Property Change Listeners

Project homepage:

	http://github.com/palantir/Cinch
	
Full project documentation:

	http://github.com/palantir/Cinch/wiki

Javodoc API Documentation:

	http://palantir.github.com/Cinch/apidocs/

Palantir Technologies Open Source homepage:

	http://palantir.github.com/

Palantir Technologies:

	http://palantir.com/

==========================================================================

Copyright 2011 Palantir Technologies

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

==========================================================================
EOF

cat > ${VERSIONED_NAME}/LICENSE.txt <<EOF

==========================================================================

Copyright 2011 Palantir Technologies

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

==========================================================================

EOF

zip -r ${VERSIONED_NAME}.zip ${VERSIONED_NAME}
rm -rf ${VERSIONED_NAME}
echo
echo  ==========================================================================
echo
echo "Zip file ${VERSIONED_NAME}.zip built"
echo
