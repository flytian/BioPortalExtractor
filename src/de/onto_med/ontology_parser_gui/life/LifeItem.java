package de.onto_med.ontology_parser_gui.life;

import java.util.ArrayList;

public class LifeItem {
	public static final short IS_TODO      = 0;
	public static final short IS_ANNOTATED = 1;
	public static final short IS_IGNORED   = 2;
	
	private String id;
	private String description;
	private ArrayList<String> related = new ArrayList<String>();
	private short status = IS_TODO;
	
	public LifeItem(String id, String description) {
		this.id          = id;
		this.description = description;
	}
	
	public LifeItem(String id, String description, ArrayList<String> related) {
		this(id, description);
		this.related = related;
	}
	
	public LifeItem clone() {
		return new LifeItem(id, description, related);
	}
	
	public String getDescription() {
		return description;
	}

	public ArrayList<String> getRelated() {
		return related;
	}
	
	public String getId() {
		return id;
	}
	
	public String toString() {
		String description = getDescription().substring(0, Math.min(getDescription().length(), 100)) + " (" + getId() + ")";
		
		if (status == IS_IGNORED)
			description = "<html><body style='background-color:yellow'>" + description + " - IGNORED</body></html>"; 
		else if (status == IS_ANNOTATED)
			description = "<html><body style='background-color:green'>" + description + " - ANNOTATED</body></html>";
		
		return description;
	}
	
	public boolean equals(Object o) {
		return this.id.equals(((LifeItem) o).getId());
	}
	
	public LifeItem setDescription(String description) {
		this.description = description;
		return this;
	}
	
	public LifeItem setRelated(ArrayList<String> related) {
		this.related = related;
		return this;
	}
	
	public LifeItem addRelated(String related) {
		this.related.add(related);
		return this;
	}
	
	public LifeItem setStatus(short status) {
		if (status != IS_TODO && status != IS_IGNORED && status != IS_ANNOTATED) {
			System.err.println(String.format("Value %d for attribute status is not allowed!", status));
			return this;
		}
		this.status = status;
		return this;
	}
	
	public short getStatus() {
		return status;
	}
	
	public boolean hasRelated() {
		return !related.isEmpty();
	}
}
