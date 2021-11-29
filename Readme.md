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

None of the is good, each has own disadvantages and advantages.

Surely there is easier way to store data than using java Serialization.
But then there is no harder mechanism to debug and publish.

XML on the other hand... well... Surely humans can read it. This
is superb. But why 1kB of raw binary data must take at leas four times
of that?

JSON? Why not, but sooner or later it is just a less clunky XML.

And what if You like to have it easy, but still properitary?
I mean, when debugging be able to save data to XML like. But when
selling product to save in well obfuscated binary format. Or just
save place.

And what if You do _upgrage_ the data format? How to transparently
handle this upgrade in older versions of Your code?

If You ever wondered about it, this is a place You should read.
Not necessairly take a code and use, but at least read about concepts.

# An event based format

This package introduces multi-level specification of a abstract
data format. Some of its conepts are Java specific, but some can
be used in other languages.

But first things first.

## What is a critical element of any data format?

I dare to say, that it is the ability to tell this piece of data from
that piece of data. In other words - to be able to put and see some
boundaries.

I call those boundaries to be _events_.

## What is an EVENT

Just a block of data. Event may be contained one in an another.

The boundaries between events are idicated by _signals_

# And this is...

And this is all.

Since markdown is not by favourite format please
see [overview](overview.html)
