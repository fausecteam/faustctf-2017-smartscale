#!/usr/bin/env bash
socat tcp-l:"8089",fork,reuseaddr EXEC:"java -cp ../../src/SmartScale.jar\:../../lib/* ninja.faust.smartscale.SmartScale ./storage"
