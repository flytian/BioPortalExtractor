package de.onto_med.bioportal_extractor;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

public class Node {
	public String ontology;
	public String label;
	public String id;
	public String definition;
	public JsonNode json;
	public List<String> synonyms = new ArrayList<String>();
	public List<Node> parents = new ArrayList<Node>();
	
	public Node() {}
	
	public Node(String id, String label)  {
		this.id    = id;
		this.label = label;
	}
	
	public Node(JsonNode json) {
		ontology = json.get("links").get("ontology").asText();
		label = json.get("prefLabel").asText();
    	id = json.get("@id").asText();
    	
    	for (JsonNode synonym : json.get("synonym")) {
    		synonyms.add(synonym.asText());
    	}
    		
    	if (json.has("definition") && json.get("definition").has(0))
    		definition = json.get("definition").get(0).asText();
	}
	
	public String toXML() {
		String string
			= "<node>"
			+ "<id>" + id + "</id>"
			+ "<label>" + label + "</label>"
			+ "<synonyms>";
		
		for (String synonym : synonyms) {
			string += "<synonym>" + synonym + "</synonym>";
		}
		
		string
			+="</synonyms>"
			+ "<definition>" + definition + "</definition>"
			+ "<parents>";
		
		for (Node parent : parents) {
			string += parent.toString();
		}
		
		string
			+="</parents>"
			+ "</node>";
		
		return string;
	}
	
	public String toString() {
		String string = label;
		if (ontology != null)
			string += " [" + ontology.replaceAll(".*/", "") + "]";
		
		return string;
	}
	
	public String getTooltip() {
		String tooltip
			= "<html>"
			+ "ID: " + id + "<br>"
			+ "Definition: " + (definition != null ? definition : "-") + "<br>"
			+ "Synonyms:";
		
		for (String synonym : synonyms) {
			tooltip += "<br>" + synonym;
		}
		
		return tooltip + "</html>";
	}
}