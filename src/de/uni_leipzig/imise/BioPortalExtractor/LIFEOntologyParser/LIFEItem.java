package de.uni_leipzig.imise.BioPortalExtractor.LIFEOntologyParser;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;

public class LIFEItem {
	private String id;
	private String description;
	
	private Slot descriptionSlot;
	private Slot nameSlot;
	private Instance instance;
	private KnowledgeBase kb;
	
	public LIFEItem(Instance instance, KnowledgeBase kb) {
		this.instance   = instance;
		this.kb         = kb;
		descriptionSlot = kb.getSlot("description");
		nameSlot        = kb.getSlot("name");	
		id              = instance.getOwnSlotValue(nameSlot).toString();
		description     = instance.getOwnSlotValue(descriptionSlot).toString();
	}
	
	public LIFEItem clone() {
		return new LIFEItem(instance, kb);
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getId() {
		return id;
	}
	
	public String toString() {
		return getDescription().substring(0, Math.min(getDescription().length(), 100)) + " (" + getId() + ")";
	}
	
	public boolean equals(Object o) {
		return this.id.equals(((LIFEItem) o).getId());
	}
	
	public LIFEItem setDescription(String description) {
		this.description = description;
		return this;
	}
}
