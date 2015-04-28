
=== Braille Utils ===
Contributors: Joel Håkansson, Bert Frees
Code license: GNU Lesser GPL
Language: Java
Platform: Cross platform
Code URL: http://brailleutils.googlecode.com


== Contents ==
1	Description
2	Installation
3	Help Resources


== 1 Description ==
Braille Utils is a cross platform utility package for embossing and converting 
PEF-files.

Braille Utils provides a cross platform API for embossing and converting braille
in PEF-format. Conversion to and from commonly used "braille" text formats is
also supported.

The package is written in Java and contains basic user interfaces for common
operations. However, the main purpose of the package is as part of other
software, such as the Daisy Pipeline and Odt2braille.

= Main Features =
 * Emboss PEF-files
 * Convert to or from PEF-files
 * Split or merge PEF-files

= Supported embossers =
The package supports the following embosser families:
 * Braillo
 * Cidat
 * Enabling Technologies
 * Index Braille
 * Mountbatten
 * ViewPlus

For details, see the complete list of supported embossers:
  http://code.google.com/p/brailleutils/wiki/SupportedEmbossers


== 2 Installation ==
You can download a ready-to-run binary variant of this library from
  http://brailleutils.googlecode.com

A list of the dependencies is located in the developer documentation (e. g. in
the source under doc/).

Compilation is done just by
  % ant
in the source directory. The built library can be found under ant-build/output/.

NOTE: On Debian systems, you should use 'ant -DuseExternalJarsOnLinux=true' in
      order to use the Debian-own libraries.


== 3 Help Resources ==
See the latest version of the written documentation on
  http://brailleutils.googlecode.com

There is also a mailing list where development and usage is discussed. For details,
see
  http://groups.google.com/group/brailleutils


On Debian systems, documentation can be found at /usr/share/doc/brailleutils/ 
or at the web site mentioned above.