package de.uni_leipzig.imise.BioPortalExtractor.LIFEOntologyParser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;

public class Parser {
	private ArrayList<LIFEItem> items;
	private LIFEItem curItem;
	private Iterator<LIFEItem> iterator;
	
	@SuppressWarnings("unchecked")
	public Parser(String ontologyPath) {
		KnowledgeBase kb = new Project(ontologyPath, new ArrayList<String>()).getKnowledgeBase();
		Cls superClass   = kb.getCls("Item");
		items = new ArrayList<LIFEItem>();
		
		for (Cls cls : (Collection<Cls>) superClass.getSubclasses()) {
			for (Instance instance : cls.getInstances()) {
				try {
					items.add(new LIFEItem(instance, kb));
				} catch (Exception e) { }
			}
		}
		
		iterator = items.iterator();
	}
	
	public LIFEItem current() {
		return curItem;
	}
	
	public LIFEItem next() {
		if (!iterator.hasNext())
			return null;
		
		curItem = iterator.next();
		return curItem;
	}
	
	public ArrayList<LIFEItem> getItems() {
		return items;
	}
	
	public void goTo(LIFEItem item) {
		if (items.contains(item)) {
			iterator = items.iterator();
			while (iterator.hasNext())
				if (next().equals(item))
					break;
		}
	}
}
