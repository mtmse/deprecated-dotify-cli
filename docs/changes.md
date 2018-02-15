[Table of Contents](toc.md)

# 3.0.0 #
## New since 2.3 (released in September 2014) ##

### CLI ###
#### New commands ####
  - Includes all commands previously found in Braille Utils CLI (the "default command" in Dotify 2.3 is now called convert)
  - Adds translator command

#### Changed options ####
  - convert (the "default command" in Dotify 2.3)
    - Adds option to re-run conversion upon change in source file  with -w
    - Lists advanced options with -o
    - Makes it possible to override xslt localization data
    - Removes options related to a specific path (fixes #5, fixes #7)
    - Uses page-height and page-width instead of rows and cols (fixes #10)
  - list
    - Adds hyphenators to list command
#### Improved commands ####
  - convert (the "default command" in Dotify 2.3)
    - Help text lists supported input and output formats
    - Sorts presets in the help text
    - OBFL validation is now supported for OBFL results
    - Improves format detection
  - emboss
    - Makes it possible to send embosser data to a folder instead of a device
    - Displays some key properties of the selected embosser implementation in embosser settings (8-dot support, volume support, line spacing support)
  - find
    - Corrects the scanning folder (fixes #14)
  - validate
    - OBFL is now supported as input

### Embossing ###
- Supports Index V5 embossers
- Adds 8-dot embossing for Index V4 and V5 embossers
- Adds unprintable margins for Index V4 and V5 (fixes brailleapps/braille-utils.impl#3)
- Corrects width calculation on Braillo 300 (fixes brailleapps/braille-utils.impl#1)

### Formatter ###
- Adds support for html and epub 3
- Adds support for tables
- Adds support for margin markers (including brailleapps/dotify#148)
- Adds support for collapsing margins
- Adds support for attribute "underline-pattern"
- Adds support for new marker-reference scope "spread-content"
- Adds support for grouping of collection items according to volume
- Adds support for additional page counters in OBFL
- Makes it possible to disallow hyphen at the last line of the last page of a volume
- Improves volume splitting (including #2, brailleapps/dotify.formatter.impl#28, brailleapps/dotify.formatter.impl#30)
- Improves pagination algorithm
- Fixes row-spacing combined with footer (brailleapps/dotify.formatter.impl#29 and brailleapps/dotify#196)
- Takes margin and padding into account when computing table layout (fixes brailleapps/dotify.formatter.impl#35)
- Raises the log level for non-braille characters in PEF output
- Removes obfl-output-location from metadata
- Improves code and performance (including brailleapps/dotify.formatter.impl#32 and brailleapps/dotify.formatter.impl#33)

### Source file conversion ###
- Adds support for Danish, German and Norwegian text output (not braille)
- Adds an option to disable the toc preamble
- Adds option to remove title page (enabled by default)
- Adds an option to disable the cover page
- Uses the same header and row spacing for all pages when duplex is off
- Sets the position of rear jacket copy and colphon to right after the cover page when placed at the beginning of the book
- Supports using page-height and page-width instead of rows and cols and adds deprecation warnings for rows, cols and rowgap
- Adds code to handle missing values for identifier and date
- Displays the name of the root element instead of just "XML" in xml conversion tasks
- Fixes a problem with footnotes/rearnotes sections in combination with toc in epub 3
- Updates OBFL validation

### Other ###
- Requires Java 8
- Improves PEF-validator
- Adds a version file to tar and zip distributions


# 3.0.0-rc.1 #
## New in this version ##
- CLI
  - Uses streamline-api validators (fixes #12, fixes #13) - OBFL validation is now supported by the `convert` command for OBFL results and as input to the `validate` command
- Formatter
  - Takes margin and padding into account when computing table layout (closes https://github.com/brailleapps/dotify.formatter.impl/pull/35)
  - Makes it possible to disallow hyphen at the last line of the last page of a volume
- Other
  - Updates dotify.api, dotify.common, dotify.hyphenator.impl, dotify.translator.impl, dotify.text.impl, dotify.formatter.impl and dotify.task.impl to v4.0.0
  - Updates streamline-api, streamline-engine and streamline-cli to v1.0.0

# 3.0.0-beta6 #
## New in this version ##
- CLI
  - Improves locales list in the "translate" command's help text
- Embossing
  - Corrects width calculation on Braillo 300 (fixes brailleapps/braille-utils.impl#1)
- Formatter
  - Improves volume splitting (including brailleapps/dotify.formatter.impl#28, brailleapps/dotify.formatter.impl#30)
  - Adds support for margin regions with different size (brailleapps/dotify#148)
  - Fixes row-spacing combined with footer (brailleapps/dotify.formatter.impl#29 and brailleapps/dotify#196)
  - Improves code and performance (including brailleapps/dotify.formatter.impl#32 and brailleapps/dotify.formatter.impl#33)

# 3.0.0-beta5 #
## New in this version ##
- CLI
  - Makes it possible to send embosser data to a folder instead of a device
  - Displays some key properties of the selected embosser implementation in embosser settings (8-dot support, volume support, line spacing support)
  - Adds hyphenators to list command
  - Removes options related to a specific path (fixes #5, fixes #7)
  - Sorts presets in the help text
  - Replaces deprecated configuration parameters (fixes #10)
- Embossing
  - Supports Index V5 embossers
  - Adds 8-dot embossing for Index V4 and V5 embossers
  - Adds unprintable margins for Index V4 and V5 (fixes brailleapps/braille-utils.impl#3)
- Formatter
  - Supports additional page counters in OBFL
- Source file conversion
  - Adds an option to disable the toc preamble
  - Adds option to remove title page (enabled by default)
  - Adds an option to disable the cover page
  - Uses the same header and row spacing for all pages when duplex is off
  - Sets the position of rear jacket copy and colphon to right after the cover page when placed at the beginning of the book
  - Supports using page-height and page-width instead of rows and cols and adds deprecation warnings for rows, cols and rowgap
  - Adds code to handle missing values for identifier and date
  - Updates OBFL validation
- Other
  - Merge with braille utils
  - Improves PEF-validator

# 3.0.0-beta4 #
## New in this version ##
- Adds support for Danish, German and Norwegian
- Raises the log level for non-braille characters in PEF output
- Fixes a problem with some options that were not displayed
- Removes obfl-output-location from metadata
- Makes it possible to override xslt localization data from CLI
- Improves a file copy error message
- Adds a version file to tar and zip distributions

### Improvements to OBFL input processing ###
- OBFL-validation now support tables and xml-data inside blocks
- Adds support for attribute "underline-pattern"
- Adds support for new marker-reference scope "spread-content"
- Adds support for grouping of collection items according to volume
- Removes "no block allowed within block with underline properties" restriction

### Improvements to runtime plugin support ###
- Supports identification factories
- Improves support for providing several enhancers for the same format
- All consumers now calls setCreatedWithSPI

# 3.0.0-beta3 #
 - Improves format detection
 - Help text lists supported input and output formats
 - Fixes manual volume breaking (fixes [issue #2](https://github.com/brailleapps/dotify.formatter.impl/issues/2))
 - Introduces a dynamic task assembly
 - Displays the name of the root element instead of just "XML" in xml conversion tasks
 - Fixes a problem with footnotes/rearnotes sections in combination with toc in epub 3
 - Restores support for text output with en-US locale

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