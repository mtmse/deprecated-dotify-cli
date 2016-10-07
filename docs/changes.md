[Table of Contents](toc.md)

# 3.0.0-beta2 #
  * List advanced options with -o
  * Improved epub 3/html support

# 3.0.0-beta1 #
  * Moved to Java 8
  * Added support for tables
  * Added support for margin markers
  * Basic support for html and epub 3
  * Included all commands from Braille Utils CLI
  * Added option to re-run conversion upon change in source file
  * Added translator command
  * Improved volume breaking
  * Improved pagination algorithm  
  * Added support for collapsing margins
  * Added additional variables in OBFL
  
# 2.3 #
  * Added support for footnotes and end of volume notes in API and formatter implementation
  * Added support for note/noteref to the dtbook conversion
  * Reimplemented cross reference handler
  * Added numeral-format keyword to ExpressionImpl
  * Added methods for an in-memory pipeline
  * Added an unsynchronized stack implementation
  * Added information about current configuration to CLI
  * Moved to Java 6
  * Cleanup, refactoring and documentation

Resolved issues:
  * Using PagedMediaWriterFactory methods (fixes [issue #90](https://code.google.com/p/dotify/issues/detail?id=#90))
  * Added İ to the Swedish table (fixes [issue #88](https://code.google.com/p/dotify/issues/detail?id=#88))
  * Removed secondary data structure (fixes [issue #83](https://code.google.com/p/dotify/issues/detail?id=#83))
  * Added a list method to translator interface (fixes [issue #30](https://code.google.com/p/dotify/issues/detail?id=#30))
  * Clarified how getPageIndex currently works (although it perhaps should work differently) (fixes [issue #21](https://code.google.com/p/dotify/issues/detail?id=#21)).
  * Removed use of custom data structure in obfl parser (fixes [issue #84](https://code.google.com/p/dotify/issues/detail?id=#84))
  * Made use of DynamicContent and Condition in implementation (fixes [issue #82](https://code.google.com/p/dotify/issues/detail?id=#82))

# 2.2.1 #
  * Added hyphenation option to DTBook input

# 2.2 #
  * Added border support
  * Fixed double line spacing
  * Restored support for text output for all languages

Behind the scenes:
  * Added namespace for OBFL
  * Added OSGi support for components

# 2.1 #
  * Added validation of PEF result
  * Added option to convert PEF result into ASCII-braille
  * Added support for text-only input (experimental)
  * Added text and paragraph aligning to OBFL-parser
  * Added metadata support to OBFL validation and parsing and PEF writing
  * Rewritten cover support using OBFL only
  * Added support for styles in translator

  * Fixed a problem in volume splitting that produced a non-optimal solution in some cases

# 2.0.3 #
  * Added option to remove temp files if processing were successful (requires that writing temp files is on)
  * Resolved an issue with TOC at the end of the dtbook
  * Resolved an issue with obfl output

# 2.0.2 #
  * Fixed an issue where TOC entries near page boundaries could be incorrect

# 2.0.1 #
  * Fixed an issue where TOC entries near volume boundaries would be incorrect
  * Fixed an issue in volume splitting causing suboptimal use of volumes