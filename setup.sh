#!/usr/bin/env bash

DL_BASE="http://ctf00.cs.fau.de/dl/smartscale"
ART="art"

if [ ! -d "$ART" ]; then
    wget --quiet -O /tmp/art.tar.gz $DL_BASE/art.tar.gz
    tar xzf /tmp/art.tar.gz -C src/
fi
