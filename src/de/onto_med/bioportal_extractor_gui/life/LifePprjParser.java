package de.onto_med.bioportal_extractor_gui.life;

import java.util.ArrayList;
import java.util.Collection;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.Slot;

public class LifePprjParser extends LifeOntologyParser {
	/**
	 * Constructor.
	 * @param ontologyPath path to pprj file
	 */
	public LifePprjParser(String ontologyPath) {
		super(ontologyPath);
	}
	
	@SuppressWarnings("unchecked")
	public void load() {
		KnowledgeBase kb     = new Project(path, new ArrayList<String>()).getKnowledgeBase();
		Cls superClass       = kb.getCls("Item");
		Slot descriptionSlot = kb.getSlot("description");
		Slot nameSlot        = kb.getSlot("name");	
		
		items = new ArrayList<LifeItem>();
		
		((Collection<Cls>) superClass.getSubclasses()).forEach(cls -> {
			cls.getInstances().forEach(instance -> {
				try {
					items.add(new LifeItem(
						instance.getOwnSlotValue(nameSlot).toString(),
						instance.getOwnSlotValue(descriptionSlot).toString()
					));
				} catch (Exception e) { }
			});
		});
		
		iterator = items.iterator();
	}
}
