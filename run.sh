#!/bin/bash

# this is EXTREMELY important. Otherwise running on clic will throw GLX13 error.
export LD_LIBRARY_PATH="$LD_LIBRARY_PATH:/usr/lib/jvm/java-7-openjdk-amd64/jre/lib/amd64"

echo Renew bin/ folder to store compiled classes
rm -rf bin/
mkdir bin
echo

echo Compiling ...

 # the last . must not forget
javac -d bin -cp "lib/jME3-core.jar:lib/jME3-effects.jar:lib/jME3-lwjgl.jar:lib/lwjgl.jar:lib/jME3-desktop.jar:lib/jME3-lwjgl-natives.jar:lib/jME3-plugins.jar:." src/chess/*.java src/utils/*.java src/control/*.java

echo DONE
echo

echo Running ...
cd bin

 # must include both the assets and ".", which contains main.class
java -cp "../lib/jME3-core.jar:../lib/jME3-effects.jar:../lib/jME3-lwjgl.jar:../lib/lwjgl.jar:../lib/jME3-desktop.jar:../lib/jME3-lwjgl-natives.jar:../lib/jME3-plugins.jar:../assets:." chess/ExcaliLux 2> /dev/null # 2>&1

echo
echo DONE
echo Testing Sucessful. 
echo
