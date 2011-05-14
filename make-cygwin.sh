#!/bin/sh -x

# Helper script for compiling yoob under Cygwin.
# The contents of this file have been placed in the public domain.

make \
    JAVAC='"/cygdrive/c/Program Files/Java/jdk1.6.0_22/bin/javac"' \
    JAVA='"/cygdrive/c/Program Files/Java/jre6/bin/java"' \
    PATHSEP=';' \
    $*
