package de.uni_leipzig.imise.BioPortalExtractor.LIFEOntologyParser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.Slot;

public class Parser {
	private Stack<String> items;
	
	@SuppressWarnings("unchecked")
	public Parser(String ontologyPath) {
		KnowledgeBase kb    = new Project(ontologyPath, new ArrayList<String>()).getKnowledgeBase();
		Slot description    = kb.getSlot("description");
		Cls superClass      = kb.getCls("Item");
		items = new Stack<String>();
		
		for (Cls cls : (Collection<Cls>) superClass.getSubclasses()) {
			for (Instance instance : cls.getInstances()) {
				try {
					items.push(instance.getOwnSlotValue(description).toString());
				} catch (Exception e) { }
			}
		}
	}
	
	public String getNext() {
		return items.pop();
	}

}
