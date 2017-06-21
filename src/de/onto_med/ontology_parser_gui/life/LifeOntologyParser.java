package de.onto_med.ontology_parser_gui.life;

import java.util.ArrayList;
import java.util.Iterator;

public abstract class LifeOntologyParser {
	protected ArrayList<LifeItem> items;
	protected LifeItem curItem;
	protected Iterator<LifeItem> iterator;
	
	
	public LifeItem current() {
		return curItem;
	}
	
	public LifeItem next() {
		if (!iterator.hasNext())
			return null;
		
		curItem = iterator.next();
		return curItem;
	}
	
	public ArrayList<LifeItem> getItems() {
		return items;
	}
	
	public void goTo(LifeItem item) {
		if (items.contains(item)) {
			iterator = items.iterator();
			while (iterator.hasNext())
				if (next().equals(item))
					break;
		}
	}
}
