README for UPPAAL2k 3.3.22, April 2002
Uppsala University and Aalborg University.
Copyright (c) 1995 - 2002. All right reserved.

April 30th, 2002

1. Introduction
2. Installation
3. New features in this release
4. Known issues is this release
5. License Agreement

1. Introduction
===============

This is a development snapshot 3.3.22 of UPPAAL2k -- a model checker
for timed automata -- that will eventually lead to a new stable
release. The verification engine in this release is identical to the
engine in release 3.2.9.

Note that UPPAAL2k is free for non-profit applications but we want all
users to fill in a license agreement. This can be done online on the
web site http://www.uppaal.com/ or by sending in the form attached
below.

This product includes software developed by the Apache Software
Foundation (http://www.apache.org/).

2. Installation
===============

To install, unzip the zip-file uppaal-3.3.22.zip. This should create
the directory uppaal-3.3.22 containing at least the files uppaal2k,
uppaal2k.jar, and the directories lib, bin-Linux, bin-SunOS,
bin-Win32, and demo. The bin-directories should all contain the two
files verifyta(.exe) and server(.exe) plus some additional files, 
depending on the platform. The demo-directory should contain some 
demo files with suffixes .xml and .q.

Note that UPPAAL2k will not run without Java 2 installed on the host
system. Java 2 for SunOS, Windows95/98/Me/NT/2000/XP, and Linux can be
downloaded from http://java.sun.com.

The present version of UPPAAL2k does no longer support versions 1.1
and 1.2 of the Java Runtime Environment (JRE). You need at least JRE
1.3, but we strongly recommend using the most recent version available
for your platform, currently version 1.4.

Notice that JRE versions prior to version 1.4 contain a bug making
exporting your X display on a Unix host (Linux, Solaris) painfully
slow. On Windows, JRE versions prior to 1.4 have problems with font
scaling making the font look funny at low zoom levels.

To run UPPAAL2k on Linux or SunOS systems run the uppaal2k script. To
run on Windows95/98/Me/NT/2000/XP systems double click the file
uppaal2k.jar.

3. New features and changes since 3.2.x
=======================================

1.  Simulation traces can now be shown as Message Sequence Charts.
2.  New editor interface based on the idea of drawing tools. Read the
    build in help for instructions on how to use the new interface 
    (Tip: Double click the tool button in order to make it stick).
3.  Moved to Java2D - added anti aliasing for transitions and locations.
4.  Important settings are now stored in an .uppaalrc file
5.  Moved to Xerces 2 XML parser and DOM implementation
6.  XTA/TA import cannot read invariants placed directly on transitions.
    This feature has been deprecated.

4. Known issues is this release
===============================

1. Save as postscript is missing in the simulator (feature is
   available in UPPAAL2k 3.0).
2. On Windows we have observed problems with scrolling and font scaling.
   These problems disappear if JRE 1.4 is used, so this is considered
   a Java problem.

5. License Agreement
====================

Please read the license agreement carefully, fill in the form, and
send it to 

  Wang Yi
  Department of Computer Systems
  Uppsala University
  Box 325
  751 05 Uppsala, Sweden

The text of the agreement follows:
                                              
We (the licensee) understand that UPPAAL2k includes the programs:
uppaal2k.jar, uppaal2k, server, socketserver, atg2ugi,
atg2ta, atg2hs2ta, hs2ta, checkta, simta, verifyta, uppaal, and
xuppaal and that they are supplied "as is", without expressed or
implied warranty. We agree on the following:

1. You (the licensers) do not have any obligation to provide any
   maintenance or consulting help with respect to UPPAAL2k. 

2. You neither have any responsibility for the correctness of systems
   verified using UPPAAL2k, nor for the correctness of UPPAAL2k itself.

3. We will never distribute or modify any part of the UPPAAL2k code
   (i.e. the source code and the object code) without a written
   permission of Wang Yi (Uppsala University) or Kim G Larsen (Aalborg
   University).
   
4. We will only use UPPAAL2k for non-profit research purposes. This
   implies that neither UPPAAL2k nor any part of its code should be used
   or modified for any commercial software product.

In the event that you should release new versions of UPPAAL2k to us, we
agree that they will also fall under all of these terms.
