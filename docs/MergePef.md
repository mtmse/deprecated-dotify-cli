[Table of Contents](toc.md)

# Merge several PEF-files #
Merges several PEF files into one.

The purpose is to facilitating the use of PEF-files with braille editors that do not support multi volume files.

Three arguments are required: _path to input folder_, _path to output file_ and identifier.

## Optional Arguments ##
The following optional arguments are available:
  * sort

### Sort ###
Set the sorting method to use when ordering files in the input folder.

Example:

`dotify merge /path/to/input /path/to/output identifier --sort=alpha`