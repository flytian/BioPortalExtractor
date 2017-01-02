package de.onto_med.ontology_parser.life;

public class LifeItem {
	private String id;
	private String description;
	
	public LifeItem(String id, String description) {
		this.id          = id;
		this.description = description;
	}
	
	public LifeItem clone() {
		return new LifeItem(id, description);
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
		return this.id.equals(((LifeItem) o).getId());
	}
	
	public LifeItem setDescription(String description) {
		this.description = description;
		return this;
	}
}
