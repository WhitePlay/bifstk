#!/bin/bash

cd $(dirname $0)
cd ..

# CP=$CP:$(pwd)/classes
CP=$CP:$(pwd)/lib/lwjgl/jar/lwjgl.jar
CP=$CP:$(pwd)/lib/lwjgl/jar/lwjgl_util.jar
CP=$CP:$(pwd)/lib/lwjgl/jar/jinput.jar

CP=$CP:$(pwd)/dist/bifstk.jar

config="config/bifstk.conf"

$JAVA_HOME/bin/java -cp $CP \
	-Djava.library.path=$(pwd)/lib/lwjgl/native/linux/ \
	$@ test.Test $config

