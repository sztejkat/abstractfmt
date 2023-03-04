#!/bin/bash
echo "Building txt package"
./build-package.sh

echo "Building txt.plain package"
cd plain
./build-subpackages.sh
cd ..

echo "Building txt.xml package"
cd xml
./build-subpackages.sh
cd ..

echo "Building txt.json package"
cd json
./build-subpackages.sh
cd ..
