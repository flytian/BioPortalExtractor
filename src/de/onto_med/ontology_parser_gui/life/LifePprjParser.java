package de.onto_med.ontology_parser_gui.life;

import java.util.ArrayList;
import java.util.Collection;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.Slot;

public class LifePprjParser extends LifeOntologyParser {
	/**
	 * Constructor.
	 * @param ontologyPath path to pprj file
	 */
	@SuppressWarnings("unchecked")
	public LifePprjParser(String ontologyPath) {
		KnowledgeBase kb     = new Project(ontologyPath, new ArrayList<String>()).getKnowledgeBase();
		Cls superClass       = kb.getCls("Item");
		Slot descriptionSlot = kb.getSlot("description");
		Slot nameSlot        = kb.getSlot("name");	
		
		items = new ArrayList<LifeItem>();
		
		for (Cls cls : (Collection<Cls>) superClass.getSubclasses()) {
			for (Instance instance : cls.getInstances()) {
				try {
					items.add(new LifeItem(
						instance.getOwnSlotValue(nameSlot).toString(),
						instance.getOwnSlotValue(descriptionSlot).toString()
					));
				} catch (Exception e) { }
			}
		}
		
		iterator = items.iterator();
	}
}
