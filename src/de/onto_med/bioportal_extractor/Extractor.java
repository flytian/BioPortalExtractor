package de.onto_med.bioportal_extractor;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.jena.ontology.AnnotationProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;

import de.onto_med.JsonRequest;
import de.onto_med.jenaapi_owlapi_integration.OntologyManager;

public class Extractor {
	
	private String api_key, iri, ontologies;
	private String rest_url = "http://data.bioontology.org";
	private OntologyManager manager;
    private OntModel model;
    private AnnotationProperty definition, synonym, origin;
	private OntClass ignored;
    

    public Extractor(String api_key, String iri, String outputPath, String ontologies) {
    	this.api_key    = api_key;
    	this.iri        = iri;
    	this.ontologies = ontologies;
    	
    	manager = new OntologyManager(outputPath, iri);
		model   = manager.getModel();
		
    	createProperties();
    }
	
	public void saveOntology() throws OWLOntologyCreationException, OWLOntologyStorageException, FileNotFoundException {
		manager.save();
	}
	
	public ArrayList<Node> extract(String text) {
		if (text == null || text.equals(""))
			return null;
		
        System.out.println("\nStarting search for '" + text + "' in ontologies '" + ontologies + "'...");
        
        try {
			text = URLEncoder.encode(text, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        
		JsonNode rootNode = JsonRequest.get(
			rest_url + "/annotator"
			+ "?text=" + text
			+ "&ontologies=" + ontologies,
			api_key
		);
	    
		ArrayList<Node> nodes = new ArrayList<Node>();
		for (JsonNode node : rootNode) {
	        JsonNode cls = JsonRequest.get(node.get("annotatedClass").get("links").get("self").asText(), api_key);
	        if (cls == null) continue;
	        Node leaf = new Node(cls);
	        leaf.json = cls;
	        	
	        if (leaf.label == null || leaf.label.equals(""))
	        	continue;
	
	        String source
	        	= JsonRequest.get(node.get("annotatedClass").get("links").get("ontology").asText(), api_key)
	        	.get("acronym").asText();
	        System.out.println("Found class: '" + leaf.label + "' in '" + source + "'.");
	        
	        nodes.add(leaf);
	    }
		
		return nodes;
	}
	
	public void addOrigin(OntClass cls, String text) {
	  	cls.addProperty(origin, text);
	}
	 
	public void addParentsToNode(JsonNode json, OntClass cls) {
		for (JsonNode parentJson : JsonRequest.get(json.get("links").get("parents").asText(), api_key)) {
			OntClass parentClass = createClass(new Node(parentJson));
			cls.addSuperClass(parentClass);
			
			addParentsToNode(parentJson, parentClass);
		}
	}
	
	public OntClass createClass(Node node) {
		OntClass cls = model.createClass(node.id);
		cls.addLabel(node.label, "en");
    	
		if (node.definition != null && !node.definition.equals(""))
    		cls.addProperty(definition, node.definition);
    	for (String curSynonym : node.synonyms) {
    		cls.addProperty(synonym, curSynonym);
    	}
    	
    	return cls;
	}

	public Boolean isAnnotated(String origin) {
		for (OntClass cls : Lists.newArrayList(model.listClasses())) {
			if (cls.hasLiteral(this.origin, origin))
				return true;
		}
		return false;
	}
	
	public OntClass getAnnotatedClass(String origin) {
		for (OntClass cls : Lists.newArrayList(model.listClasses())) {
			if (cls.hasLiteral(this.origin, origin))
				return cls;
		}
		return null;
	}
	
	
	private void createProperties() {		
        definition = model.createAnnotationProperty(iri + "definition");
        synonym    = model.createAnnotationProperty(iri + "synonym");
        origin     = model.createAnnotationProperty(iri + "origin");
        ignored    = model.createClass(iri + "ignored");
        ignored.setLabel("Ignored", "en");
	}
	
	public void ignoreClass(Node node) {
		OntClass cls = model.createClass(iri + node.id);
		cls.setLabel(node.label, "en");
		addOrigin(cls, node.id);
		
		cls.addSuperClass(ignored);
	}
	
    public DefaultMutableTreeNode getTreeNodeForClass(OntClass cls) {
    	DefaultMutableTreeNode node;
    	ArrayList<OntClass> classes;
    	
    	if (cls == null) {
    		node = new DefaultMutableTreeNode("Things");
    		classes = Lists.newArrayList(model.listHierarchyRootClasses());
    	} else {
    		node = new DefaultMutableTreeNode(cls.getLabel("en"));
    		classes = Lists.newArrayList(cls.listSubClasses());
    	}
    	
    	for (OntClass child : classes) {
    		node.add(getTreeNodeForClass(child));
    	}
    	
    	return node;
    }
}
