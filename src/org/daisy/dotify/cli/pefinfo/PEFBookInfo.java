package org.daisy.dotify.cli.pefinfo;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.daisy.braille.utils.pef.PEFBook;

public class PEFBookInfo {	

	private final List<Detail> infos;
	
	public PEFBookInfo() {
		this(DetailSet.DEFAULT);
	}
	
	public PEFBookInfo(DetailSet set) {
		this(set.newDetailSet());
	}
	
	public PEFBookInfo(Detail ... data) {
		this(Arrays.asList(data));
	}
	
	public PEFBookInfo(Collection<Detail> data) {
		infos = new ArrayList<>(data);
	}

	public void print(PEFBook book, PrintStream ps) {
		for (Detail i : infos) {
			printIterable(ps, i.getTitle(), i.getDetails(book));
		}
	}
	
	private void printIterable(PrintStream ps, String title, Iterable<String> items) {
		if (items==null) {
			return;
		} else {
			ps.println(title);
			for (String s : items) {
				ps.println("\t" +s);
			}
		}
	}

}
