#!/usr/bin/env bash

java -Dconfig.file=/home/ipapp/workspace/finastra/codebase/sandbox/obfuscator/script/application.conf -Dconfig.trace=loads -jar ./target/scala-2.12/obfuscator-0.1.0-SNAPSHOT.jar
