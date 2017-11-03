[Table of Contents](toc.md)

# Generate a PEF-file #
Generates a random PEF-file for testing purposes.

One argument is required: _path to output file_

Example:

`dotify generate output.pef`

## Optional Arguments ##
The following optional arguments are available:
  * volumes
  * sections
  * pages
  * rows
  * cols

### volumes ###
Set the number of volumes to generate.

Example:

`dotify generate output.pef --volumes=3`

### sections ###
Sets the number of sections in each volume.

Example:

`dotify generate output.pef --sections=3`

### pages ###
Set the number of pages in each volume.

Example:

`dotify generate output.pef --pages=50`

### rows ###
Set the maximum numbers of rows on a page.

Example:

`dotify generate output.pef --rows=29`

### cols ###
Set the maximum number of characters on a row.

Example:

`dotify generate output.pef --cols=28`

## Switches ##
The following switches are available:
  * full-range (-f)
  * simplex (-s)

### full-range ###
Set to true to include 8-dot patterns.

Example:

`dotify generate output.pef -f`


### simplex ###
Creates a single sided PEF-file.

Example:

`dotify generate output.pef -s`
