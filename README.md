[![Build Status](https://travis-ci.org/brailleapps/braille-utils-cli.svg?branch=master)](https://travis-ci.org/brailleapps/braille-utils-cli)

# BrailleUtils #
BrailleUtils provides a cross platform Java API for embossing and converting braille in PEF-format. Conversion to and from commonly used "braille" text formats is also supported.

## Type ##
Application

## Main Features ##
  * Emboss a PEF-file
  * Validate a PEF-file
  * Search meta data in collection of PEF-files
  * Convert from text to a PEF-file
  * Convert from a PEF-file to text
  * Split a PEF-file into one file per volume
  * Merge several PEF-files into one

## Building ##
Build with `gradlew build` (Windows) or `./gradlew build` (Mac/Linux)

## Testing ##
Tests are run with `gradlew test` (Windows) or `./gradlew test` (Mac/Linux).

## Requirements & Compatibility ##
* Requires Java 8
* Compatible with SPI