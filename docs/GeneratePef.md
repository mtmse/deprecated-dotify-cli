[Table of Contents](toc.md)

# Generate a PEF-file #
Generate a random PEF-file for testing purposes.

One argument is required: _path to output file_

<pre>
Example:<br>
dotify generate output.pef<br>
</pre>

Optional arguments include:
  * volumes
  * pages
  * eightdot
  * rows
  * cols
  * duplex

## Volumes ##
Set the number of volumes to generate.

<pre>
Example:<br>
dotify generate output.pef -volumes=3<br>
</pre>

## Pages ##
Set the number of pages in each volume.

<pre>
Example:<br>
dotify generate output.pef -pages=50<br>
</pre>

## Eight dot ##
Set to true to include 8-dot patterns.

<pre>
Example:<br>
dotify generate output.pef -eightdot=true<br>
</pre>

## Rows ##
Set the maximum numbers of rows on a page.

<pre>
Example:<br>
dotify generate output.pef -rows=29<br>
</pre>

## Cols ##
Set the maximum number of characters on a row.
<pre>
Example:<br>
dotify generate output.pef -cols=28<br>
</pre>

## Duplex ##
Set the duplex property.
<pre>
Example:<br>
dotify generate output.pef -duplex=true<br>
</pre>