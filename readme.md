[![Build Status](https://travis-ci.org/brailleapps/dotify-cli.svg?branch=master)](https://travis-ci.org/brailleapps/dotify-cli)
[![Type](https://img.shields.io/badge/type-application-blue.svg)](https://github.com/brailleapps/wiki/wiki/Types)

# Dotify
Dotify Braille Translation System is an open source Braille translator written in Java.  Dotify is designed for collaborative, open source braille software development.

## Main Features ##
  * Translates and formats braille
  * Includes all features of [Braille Utils](https://github.com/brailleapps/braille-utils-cli)

### Performance ###
Less than 10 seconds/book or over 70 braille pages/second (based on a selection of novels) utilizing a single core of a modern PC and including startup activites. Startup activites account for up to 4 seconds of the total processing time in this example. The performance is thus even better when running in a server environment (e.g. as part of a Daisy Pipeline 2 installation), since the startup activites rarely have to be repeated. 

Performance could be improved further by utilizing more than one core.

## Limitations ##
Formatting with hyphenation is supported for over 50 languages. Unfortunately, only Swedish _braille_ is supported. However, depending on your requirements, it may be quite simple to add support for another locale.

## Using ##
Download the [latest release](https://github.com/brailleapps/dotify-cli/releases) and unpack it. For more information see, the user guide
in the `docs` folder.

## Building ##
Build with `gradlew build` (Windows) or `./gradlew build` (Mac/Linux)

## Testing ##
Tests are run with `gradlew test` (Windows) or `./gradlew test` (Mac/Linux)

## Requirements & Compatibility ##
* Requires Java 8
* Compatible with SPI

## More information ##
See the [common wiki](https://github.com/brailleapps/wiki/wiki) for more information.
