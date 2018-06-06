#!/usr/bin/env bash

#set -x

CONFIG_FILE=""
TRACE=0
PATH="obfuscator-0.1.0-SNAPSHOT.jar"

while getopts ":tc:p:" optname
  do
    case "$optname" in
      "t")
        echo "Option $optname is specified"
        TRACE=1
        ;;
      "c")
        echo "Option $optname has value [$OPTARG]"
        CONFIG_FILE="$OPTARG"
        ;;
      "p")
        echo "Option $optname has value [$OPTARG]"
        PATH="$OPTARG$PATH"
        ;;
      "?")
        echo "Unknown option $OPTARG"
        ;;
      ":")
        echo "No argument value for option [$OPTARG]"
        ;;
      *)
      # Should not occur
        echo "Unknown error while processing options"
        ;;
    esac
    echo "DEBUG $OPTIND"
  done

CMD="/usr/bin/java"

if [ -n "$CONFIG_FILE" ]; then
   CMD="$CMD -Dconfig.file=$CONFIG_FILE"
fi

if [ $TRACE -eq 1 ]; then
   CMD="$CMD -Dconfig.trace=loads"
fi

CMD="$CMD -jar $PATH"

echo "command to execute: $CMD"

( exec $CMD )
