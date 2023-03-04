#!/bin/bash
echo "Building abstractfmt package"
./build-package.sh

echo "Building test subpackage"
cd test
./build-subpackages.sh
cd ..

echo "Building logging subpackage"
cd logging
./build-subpackages.sh
cd ..

echo "Building obj subpackage"
cd obj
./build-subpackages.sh
cd ..

echo "Building utils subpackage"
cd utils
./build-subpackages.sh
cd ..

echo "Building bin subpackage"
cd bin
./build-subpackages.sh
cd ..

echo "Building txt subpackage"
cd txt
./build-subpackages.sh
cd ..

echo "Building compat subpackage"
cd compat
./build-subpackages.sh
cd ..