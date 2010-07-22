#!/bin/bash

cd $(dirname $0)
cd ..

CLASSPATH=$CLASSPATH:$(pwd)/classes
CLASSPATH=$CLASSPATH:$(pwd)/lib/lwjgl/jar/lwjgl.jar
CLASSPATH=$CLASSPATH:$(pwd)/lib/lwjgl/jar/lwjgl_util.jar
CLASSPATH=$CLASSPATH:$(pwd)/lib/lwjgl/jar/jinput.jar
CLASSPATH=$CLASSPATH:$(pwd)/lib/slick/slick.jar

config="config/bifstk.conf"

if [ $? -gt 1 ] ; then
	if [ -f "$1" ] ; then
		config=$1
	fi
fi

java -cp $CLASSPATH \
	-Djava.library.path=$(pwd)/lib/lwjgl/native/linux/ \
	bifstk.Test $config

