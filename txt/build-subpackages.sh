#!/bin/bash
echo "Building bin package"
./build-package.sh

cd plain
./build-subpackages.sh
cd ..

cd xml
./build-subpackages.sh
cd ..
