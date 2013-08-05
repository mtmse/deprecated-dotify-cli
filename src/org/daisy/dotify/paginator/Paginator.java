package org.daisy.dotify.paginator;

import java.io.Closeable;
import java.io.IOException;

import org.daisy.dotify.formatter.BlockSequence;
import org.daisy.dotify.formatter.CrossReferences;
import org.daisy.dotify.formatter.LayoutMaster;
import org.daisy.dotify.formatter.Row;
import org.daisy.dotify.translator.BrailleTranslator;
import org.daisy.dotify.writer.PagedMediaWriter;



/**
 * <p>Breaks a stream of {@link Row} into pages.</p>
 * 
 * <p>The Paginator implementation is responsible for breaking
 * pages when required by the properties of the {@link LayoutMaster}. It
 * is also responsible for placing page dependent items such
 * as footnotes, but not headers and footers, as these might need
 * to search the entire page structure.</p>
 * 
 * <p>The final result is passed on to the {@link PagedMediaWriter}.</p>
 * 
 * @author Joel Håkansson
 *
 */
public interface Paginator extends Closeable {
	
	/**
	 * Opens for writing to the supplied writer
	 * @param formatterFactory the factory
	 * @param fs The BlockSequences to paginate 
	 */
	public void open(BrailleTranslator translator, Iterable<BlockSequence> fs);

	/**
	 * Paginates the block sequence
	 * @param refs cross references
	 * @throws IOException
	 */
	public PageStruct paginate(CrossReferences refs) throws PaginatorException;
	
	//public PageStruct getPageStruct();
}
