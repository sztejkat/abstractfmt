# An abstract stream format

This project contains a set of definitions of an
"abstract stream format".

# Introduction

Have You ever had enough of XML? Had You ever struggled
with what format You should choose for Your data? XML? JSON?
E... too much size overhead... maybe then java Serialization?
Well... no, too much tied to Java. So maybe You should directly
use java.io.DataOutputStream?

Decissions, decissions, decissions....

If You ever wondered about it, this is a place You should read.
Not necessairly take a code and use, but at least read about concepts.

# An event based format

This package introduces multi-level specification of a abstract
data format. Some of its concepts are Java specific, but some can
be used in other languages.

Since markdown is not by favourite format please
see [overview](overview.html)


# Build system

The build system is ANT.

It is controlled by
<pre>
package-build.xml
</pre>
file which includes:
<pre>
build-common.xml
setup-xml
</pre>

_Originally this was my common build system for all my works
 and was providing a consistent build environment for all
 packages I was creating. For needs of this library it will
 be extracted and provided as a stand alone set of files._

 Build scripts are (either *.sh or *.bat)
 * build-package  - build single package
 * build-package-doc - build single package javadoc in <code>package-doc</code> folder.
 * build-subpackages - build package and sub-packages, by recursive process.
 * test-package - run package Junit tests
 * build-library-doc - build "library" documentation in <code>library-doc</code> folder.


 # External dependencies

 Junit 4.0. Apropriate jar files will be provided together with
 sources, so no external downloads except ANT are necessary.

 JDK 8 as minimum build environment (tested on Open-JDK 11)


