#!/bin/bash
CP=".:`ls -1 libs/*.jar|xargs|sed "s/\ /:/g"`"
javac -cp $CP Main.java && java -Djava.util.logging.config.file=logging.properties -cp $CP Main $*
rm *.class



