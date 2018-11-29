yoob
====

_yoob_ is a framework for implementing esoteric programming languages in Java.
It aims to make it easy for a user to experiment with a large number of
esolangs with a minimal install burden (i.e. in a Java applet), and to make
it easy for designers to rapidly implement and showcase their esolangs.

See also: [yoob.js](http://catseye.tc/node/yoob.js).

Features
--------

yoob provides interfaces and base classes for components commonly used in the
implementation of esolangs, including program states, playfields, and tapes.
This allows one to implement an esolang, similar to existing esolangs, fairly
rapidly.

yoob "understands" how to control, display, and animate an esolang, so long
as it is implemented in a way that conforms to the interfaces that it
provides.  It uses this "understanding" to provide a graphical, interactive
Java GUI (potentially in an applet) for developing in the language.  It could
also in theory provide other interfaces, such as a traditional command-line
interface, to any yoob-conformant implementation.

A Note on Tapes
---------------

Tapes are depicted vertically, rotated 90 clockwise from the traditional
horizontal orientation: the "left" of the tape is at the top of the display
and the "right" of the tape is at the bottom.  This is because in many
esolangs, each tape cell may contain an unbounded integer, and in Arabic
numeral notation, this takes up more horizontal area than vertical; thus
displaying the tape vertically is a more efficient use of screen area.

History
-------

The current released version of yoob is 0.3.  There are a few minor UI
improvements in this version, but the main enhancement is that example
programs are modelled as proper Java objects, and they support being loaded
from remote URLs.  This allows esolang implementations which are under one
license to have example programs which are under another license; this in
turn allows a few esolang implementations (Ale, Sceql, Qdeql) to be released
in this distribution without embedded (possibly non-public domain) example
programs, thus keeping the whole distribution in the public domain.

The previous released version of yoob was 0.2.  I decided to release it,
in spite of the source being embarassingly bad, so that the masses might
mock it, and perhaps, in their collective moment of weakness, submit patches,
or at least actionable flames (note: actionable.)

Version 0.1 of yoob was purely a technology preview and was never officially
released.

The API should not be expected to be stable through the 0.x series.

Development
-----------

See the [yoob entry][] at [Cat's Eye Technologies][] for details of development.

TODO
----

(Scavenged from some miscellaneous notes I made at one point.  Not sure how
much of this duplicates what's on the Issue Tracker.)

* Allow different alternate views to be selected between. (3)
* Actual error handling.
* Specify which example program to initially select on command line (1)
* Some way to link directly to a particular language in the applet,
  using Javascript (2)
* A test harness, including specifying how many cycles to run and what
  playfields/tapes should look like afterwards and mocking I/O (5)
* Improve BasicStack to raise an exception or something on underflow.
* Allow PRNG to be seeded (not just seeded but supplied with a series of
  values to produce)
* Welcome panel - HTML doesn't cut it in some browsers (?)
* Document the policy for mutation of Playfields and Tapes and States and
  such. (?)

For Further Information
-----------------------

For further information, please see the [yoob entry][] at
[Cat's Eye Technologies][].

Yet more information can be found in the [yoob article][] on the [esowiki][].

An instance of yoob running as a Java JNLP application can be found in the
[yoob installation][] at Cat's Eye Technologies.

[yoob article]: http://www.esolangs.org/wiki/yoob
[esowiki]: http://www.esolangs.org/wiki/
[yoob installation]: http://catseye.tc/installation/yoob/
[yoob entry]: http://catseye.tc/node/yoob
[Cat's Eye Technologies]: http://catseye.tc/
