# An abstract stream format

This project contains a definition and JAVA library
dedicated for working with a concept of an
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

This package introduces multi-level specification of an "abstract
data format" based on idea of "begin-end" _signals_ surrounding
more or less specific data produced by a squence of calls to
data-type sensitive API.

Some of concepts are Java specific, but some can
be used in other languages.

Since markdown is not by favourite format please
see [overview](overview.html).


# Build system

The build system is ANT.

It is controlled by a file:
<pre>
package-build.xml
</pre>
placed in each package. This file do
compute a path from a package name and
locates and includes following common
control files:
<pre>
build-common.xml
setup-xml
</pre>
Those files are located in a top of repository source tree.

_Originally this was my common build system for all my works
 and was providing a consistent build environment for all
 packages I was creating. For needs of this library it is
 extracted and provided as a stand alone set of files.
 This is why it looks a bit awkward._

 Build scripts are (either *.sh or *.bat)
 * build-package  - build single package;
 * build-package-doc - build single package javadoc in <code>package-doc</code> folder;
 * build-subpackages - build package and sub-packages, by recursive process;
 * test-package - run package Junit tests;
 * build-library-doc - build "library" documentation in <code>library-doc</code> folder;
 * build-jar - pack library into a jar file;

 # Using in Your own code.

 I highly recommend just import all the sources to Your own
 source tree. This is, in my opinion, a best way to work
 with some libraries if You need to touch their source.

 But, of course, You may also build a JAR file.

 ## Setting up build system

 ### Placing code in a proper locations

   Create a "work-dir" folder in some place You like:

<pre>
     /home/crazylibraries/
</pre>

Create following path in there:

<pre>
     /home/crazylibraries/src/sztejkat/abstractfmt
</pre>
Clone this repository there or just copy it there:
<pre>
     /home/crazylibraries/src/sztejkat/abstractfmt
     /home/crazylibraries/src/sztejkat/abstractfmt/.git
     /home/crazylibraries/src/sztejkat/abstractfmt/Readme.md
                                                            .... and etc.
</pre>
Then create the path where java class files will be produced:
<pre>
     /home/crazylibraries/out
</pre>
   Yes, I know it is different from what You are used to. If You
   don't like it You will have to alter <code>build-common.xml</code>
   or throw away my build-scripts completely.

 ### Configuring build environmet

   First get ANT.

   Second You absolutely MUST edit the <code>setup.xml</code> file.
   This file may carry on some default settings which will NOT work
   on Your machine. Follow the description there.



# External dependencies

 ANT as a build system.

 Junit 4.x. Apropriate jar files are provided in this repository
 so no external downloads except ANT are necessary.

 JDK 8 as minimum build environment (tested on Open-JDK 11).


# Warranty

 None.

# Licensing

 Creative Commons Attribution.

 Use, copy, modify, but remember to tell from where did
 You get it.

# Possible copyright issues

 This library includes, clearly for sake of usability and
 archiving important information following data which
 are not produced by their authors:

  1. Junit.jar - junit.org library, output form.
  2. hamcrest-core-1.3.jar - another library needed by junit.
  3. txt/xml/doc-files - various revisions of XML specification copyrighted
by W3C org.
  4. txt/json/doc-files - a copy of JSON specification from ECMA International.
  5. txt/json/doc-files - a copy of JSONLint - The JSON Validator.

 Author of the library do believe that preserving these important
 works in as many copies as possible is a more safe and sane way
 that referencing to a single source over an internet which may
 dissapear in any moment rendering the whole work useless.