#!/bin/bash

IN="../dot/"
OUT="../png/"

if [ ! -d "$IN" ]; then
  echo "input directory not found: $IN"
  exit 0
fi

if [ ! -d "$OUT" ]; then
  mkdir "$OUT"
fi

for i in $(ls "$IN") ; do
  dot -Tpng "$IN$i" -o "$OUT$i.png"
done
