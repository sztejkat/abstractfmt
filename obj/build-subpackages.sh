#!/bin/bash
echo "Building abstractfmt package"
./build-package.sh

echo "Building test subpackage"
cd test
./build-package.sh
cd ..
