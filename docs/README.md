# Dotify #
 - Contributors: Joel Håkansson, Bert Frees
 - Code license: GNU Lesser GPL
 - Language: Java
 - Platform: Cross platform
 - Code URL: https://github.com/brailleapps/dotify-cli


## Contents ##
1.	Description
2.	Installation
3.	Help Resources


## 1 Description ##
Dotify is a cross platform command line interface.

It's features include braille production as well as embossing and managing
PEF-files, including upgrading from and downgrading to commonly 
used "braille" text formats.

### Main Features ###
  * Translates and formats braille
  * Emboss a PEF-file
  * Validate a PEF-file
  * Search meta data in collection of PEF-files
  * Convert from text to a PEF-file
  * Convert from a PEF-file to text
  * Split a PEF-file into one file per volume
  * Merge several PEF-files into one

### Supported embossers ###
Dotify supports a range of embossers, including popular [Index](http://www.indexbraille.com/) and [Braillo](http://www.braillo.com/) embossers. Note however that several embossers are untested, due to lack of access and/or time.

For details, see the complete list of supported embossers by typing the following command:
  `dotify list embossers`
  
### Translator Limitations ###
Formatting with hyphenation is supported for over 50 languages. Unfortunately, only Swedish _braille_ is supported when
using the following command:
`dotify convert`

## 2 Installation ##
You can download a ready-to-run binary variant of this library from
  https://github.com/brailleapps/dotify-cli

### Building ###
Build with `gradlew build` (Windows) or `./gradlew build` (Mac/Linux)
in the source directory. The built library can be found under build/distributions/.


## 3 Help Resources ##
See the latest version of the written documentation on
  https://github.com/brailleapps/dotify-cli
