package de.uni_leipzig.imise.BioPortalExtractor.Extractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.jena.ontology.AnnotationProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

public class Extractor {
	
	private ObjectMapper mapper = new ObjectMapper();
	private String api_key, iri, outputPath, ontologies, rest_url;
    private OntModel model;
    private AnnotationProperty definition, synonym, origin;
	private OntClass ignored;
    

    public Extractor() {
    	try {
			readConfiguration();
		} catch (Exception e) {
			System.err.println("Error: Could not load 'settings.yml'! (" + e.getMessage() + ")");
			System.exit(1);
		}
    	
    	createOntology();
    }
	
	public void saveOntology() throws OWLOntologyCreationException, OWLOntologyStorageException, FileNotFoundException {
		File file = new File(outputPath);
		FileOutputStream stream = new FileOutputStream(file);
		model.setNsPrefix("", iri);
		model.write(stream, "RDF/XML", null);
		loadAndSaveWithOwlApi();
		System.out.println("\nSaved ontologie in '" + file.getAbsolutePath() + "'.");
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
        
		JsonNode rootNode = jsonToNode(get(
			rest_url + "/annotator"
			+ "?text=" + text
			+ "&ontologies=" + ontologies
		));
	    
		ArrayList<Node> nodes = new ArrayList<Node>();
		for (JsonNode node : rootNode) {
	        JsonNode cls = jsonToNode(get(node.get("annotatedClass").get("links").get("self").asText()));
	        Node leaf = new Node(cls);
	        leaf.json = cls;
	        	
	        if (leaf.label == null || leaf.label.equals(""))
	        	continue;
	
	        String source = jsonToNode(
	        	get(node.get("annotatedClass").get("links").get("ontology").asText())
	        ).get("acronym").asText();
	        System.out.println("Found class: '" + leaf.label + "' in '" + source + "'.");
	        
	        nodes.add(leaf);
	    }
		
		return nodes;
	}
	
	public void addOrigin(OntClass cls, String text) {
	  	cls.addProperty(origin, text);
	}
	 
	public void addParentsToNode(JsonNode json, OntClass cls) {
		for (JsonNode parentJson : jsonToNode(get(json.get("links").get("parents").asText()))) {
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
	
	private JsonNode jsonToNode(String json) {
        JsonNode root = null;
        try {
            root = mapper.readTree(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return root;
    }
	
	private void readConfiguration() throws FileNotFoundException, YamlException {
		YamlReader reader = new YamlReader(new FileReader("settings.yml"));
		@SuppressWarnings("unchecked")
		Map<String, String> map = (Map<String, String>) reader.read();
		api_key    = map.get("api_key");
		iri        = map.get("iri");
		outputPath = map.get("outputPath");
		ontologies = map.get("ontologies");
		rest_url   = map.get("bioportal_url");
	}
	
	
	private void createOntology() {
		model = ModelFactory.createOntologyModel();
		
		if (new File(outputPath).exists()) {
			System.out.println("Appending data to existing output file.");
			model.read(FileManager.get().open(outputPath), null);
		} else {
			model.createOntology(iri);
		}
		
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
	
	private void loadAndSaveWithOwlApi() throws OWLOntologyCreationException, OWLOntologyStorageException {
		File file = new File(outputPath);
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);
		
		manager.saveOntology(ontology);
	}	
    
	private String get(String urlToGet) {
        URL url;
        HttpURLConnection conn;
        BufferedReader rd = null;
        String line;
        String result = "";
        try {
            url = new URL(urlToGet);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "apikey token=" + api_key);
            conn.setRequestProperty("Accept", "application/json");
            
            boolean retry = true;
            
            do {
	            try {
	            	rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	            	retry = false;
	            } catch (Exception e) {
	            	System.err.println(e.getLocalizedMessage() + " - " + conn.getResponseMessage());
	            	System.out.println("Waiting 5 seconds to try again...");
	            	Thread.sleep(5000);
	            }
            } while (retry);
	            
            while ((line = rd.readLine()) != null) {
                result += line;
            }
            rd.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
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
