#!/bin/bash
echo "Building bin package"
./build-package.sh

cd plain
./build-subpackages.sh
cd ..

