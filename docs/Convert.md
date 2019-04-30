[Table of Contents](toc.md)

# Convert #
Converts a document into braille.

Dotify requires two arguments to run:
  * path to an input file
  * path to the output file

## Input File Requirements ##
The input file should, of course, be something that Dotify understands. Because Dotify has been developed for and funded by [Swedish Agency for Accessible Media, MTM](http://www.mtm.se), the current capabilities may seem odd to an average user. Dotify supports:
  * DTBook
  * HTML
  * Epub 3
  * XML
  * Plain text
  * OBFL

DTBook is the most developed input format, and it produces a result that meets MTM's braille standards.

HTML/Epub support is well developed, but tables are not as advanced as in DTBook.

The generic XML support is also very basic. Dotify attempts to render the file without understanding the content, which is unlikely to produce a satisfactory result. However, the feature could be useful in some cases.

A plain text can also be used, with similar capabilities as for general XML, i.e. containing very limited formatting. However, depending on the way the text file is formatted, it can provide suprisingly good results.

OBFL is the intermediary format used by Dotify, and is likely only of interest to developers.

## Output File Formats ##
The output format is determined by examining the extension of the specified output file name. Supported extensions are:
  * pef
  * txt
  * obfl

PEF is the main output format. Dotify produces high quality PEF-files.

Text produces a plain text file.

OBFL is the intermediary format used by Dotify, and is likely only of interest to developers.

## Optional Arguments ##
The following optional arguments are available:
  * preset
  * locale
  * outputFormat
  * writeTempFiles
  * tempFilesDirectory
  * table

### preset ###
The preset specifies some key properties of the finished product, such as row spacing, characters/line, rows/page and sheets/volume. Each of these can be set
manually, or they can be included in a file using the XML flavor of [Java properties](https://docs.oracle.com/javase/tutorial/essential/environment/properties.html).
The path to this file can be passed a the value to this option.

For example:
`--preset=/path/to/file.xml`

### locale ###
The locale is very important, because it determines the fall-back language used for braille translation and hyphenation.

### outputFormat ###
If specified, the output format is determined by the value of this parameter, instead of from the file name extension.

### writeTempFiles ###
Set to true to write temp files.

### tempFilesDirectory ###
Sets the directory to write temp data. If not specified, the user default is used.

### table ###
If specified, an ASCII-braille file is generated in addition to the PEF-file (requires that the output format is PEF).

## Switches ##
The following switches are available:
  * watch
  * listOptions
  * configs

### watch ###
If present, watches the input file for changes and runs the conversion when changes occur.

### listOptions ##
If present, lists additional options available in the context of the current job. Due to the dynamic
design of the system, the options are listed *after* the conversion has finished running. To use these options,
append them to the options list of the job just finished and run again. 

### configs ###
If present, lists the available combinations of locale and braille translators.