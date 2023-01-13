#!/bin/bash
echo "Building bin package"
./build-package.sh

echo "Building chunk package"
cd chunk
./build-subpackages.sh
cd ..

echo "Building escape package"
cd escape
./build-subpackages.sh
cd ..

