#!/bin/bash

cd $(dirname $0)
cd ..

CLASSPATH=$CLASSPATH:$(pwd)/classes
CLASSPATH=$CLASSPATH:$(pwd)/lib/lwjgl-2.5/lwjgl/jar/lwjgl.jar
CLASSPATH=$CLASSPATH:$(pwd)/lib/lwjgl-2.5/lwjgl/jar/lwjgl_util.jar
CLASSPATH=$CLASSPATH:$(pwd)/lib/lwjgl-2.5/lwjgl/jar/jinput.jar
CLASSPATH=$CLASSPATH:$(pwd)/lib/slick/lib/slick.jar

java -cp $CLASSPATH \
	-Djava.library.path=$(pwd)/lib/lwjgl-2.5/lwjgl/native/linux/ \
	bifstk.Test $@

