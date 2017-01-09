package de.onto_med.ontology_parser.life;

import java.util.ArrayList;

public class LifeItem {
	private String id;
	private String description;
	private ArrayList<String> related;
	
	public LifeItem(String id, String description, ArrayList<String> related) {
		this.id          = id;
		this.description = description;
		this.related     = related;
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
		return getDescription().substring(0, Math.min(getDescription().length(), 100)) + " (" + getId() + ")";
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
}
