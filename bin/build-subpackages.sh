#!/bin/bash
echo "Building bin package"
./build-package.sh

echo "Building bin.chunk package"
cd chunk
./build-subpackages.sh
cd ..

echo "Building bin.escape package"
cd escape
./build-subpackages.sh
cd ..

