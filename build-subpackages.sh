#!/bin/bash
echo "Building abstractfmt package"
./build-package.sh

echo "Building test subpackage"
cd test
./build-package.sh
cd ..


echo "Building logging subpackage"
cd logging
./build-package.sh
cd ..

echo "Building obj subpackage"
cd obj
./build-package.sh
cd ..

echo "Building utils subpackage"
cd utils
./build-package.sh
cd ..
