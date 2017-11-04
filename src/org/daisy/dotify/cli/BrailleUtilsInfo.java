package org.daisy.dotify.cli;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.daisy.braille.utils.api.factory.Factory;
import org.daisy.braille.utils.api.factory.FactoryCatalog;
import org.daisy.braille.utils.api.factory.FactoryProperties;
import org.daisy.braille.utils.api.table.TableCatalog;
import org.daisy.streamline.cli.Definition;
import org.daisy.streamline.cli.ShortFormResolver;

/**
 * Provides lazy loading of braille utils related details
 * @author Joel Håkansson
 *
 */
class BrailleUtilsInfo {
	private ShortFormResolver tableSF;
	private TableCatalog tableCatalog;

	/**
	 * Creates a list of braille tables.
	 * @return returns a list of definitions
	 */
	List<Definition> getDefinitionList() {
		ShortFormResolver resolver = getShortFormResolver();
		FactoryCatalog<? extends Factory> catalog = getTableCatalog();
		List<Definition> ret = new ArrayList<>();
		for (String key : resolver.getShortForms()) {
			ret.add(new Definition(key, catalog.get(resolver.resolve(key)).getDescription()));
		}
		return ret;
	}

	ShortFormResolver getShortFormResolver() {
		if (tableSF==null) {
			Collection<String> idents = new ArrayList<String>();
			for (FactoryProperties p : getTableCatalog().list()) { idents.add(p.getIdentifier()); }
			tableSF = new ShortFormResolver(idents);
		}
		return tableSF;
	}

	private TableCatalog getTableCatalog() {
		if (tableCatalog==null) {
			tableCatalog = TableCatalog.newInstance();
		}
		return tableCatalog;
	}
}
