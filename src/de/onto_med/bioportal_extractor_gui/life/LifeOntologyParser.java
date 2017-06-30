package de.onto_med.bioportal_extractor_gui.life;

import java.io.File;
import java.util.ArrayList;
import java.util.ListIterator;

public abstract class LifeOntologyParser {
	protected ArrayList<LifeItem> items;
	protected LifeItem curItem;
	protected ListIterator<LifeItem> iterator;
	protected File file;
	protected String path;
	protected Boolean lastWasNext = true;
	
	public LifeOntologyParser(String path) {
		this.path = path;
		file = new File(path);
	}
	
	public LifeItem current() {
		return curItem;
	}
	
	public LifeItem next() {
		if (!iterator.hasNext())
			return null;
		
		if (!lastWasNext) iterator.next();
		curItem = iterator.next();
		lastWasNext = true;
		return curItem;
	}
	
	public LifeItem previous() {
		if (!iterator.hasPrevious())
			return null;
		
		if (lastWasNext) iterator.previous();
		curItem = iterator.previous();
		lastWasNext = false;
		return curItem;
	}
	
	public ArrayList<LifeItem> getItems() {
		return items;
	}
	
	public void goTo(LifeItem item) {
		if (items.contains(item)) {
			iterator = items.listIterator();
			while (iterator.hasNext())
				if (next().equals(item))
					break;
		}
	}

	public abstract void load() throws Exception;
}
