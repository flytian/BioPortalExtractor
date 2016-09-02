import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.jena.ontology.AnnotationProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Extractor {

	private static final String API_KEY      = "";
	private static final String REST_URL     = "http://data.bioontology.org";
	private static final String IRI          = "http://localhost/annotation.owl";
	private static final ObjectMapper mapper = new ObjectMapper();
	
    private static OntModel model;
    private static AnnotationProperty definition, synonym;

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        String[] texts = new String[0];
        String ontologies = "";
		
		if (args.length == 0 ||  args[0] == null || args[0].equals("")) {
        	System.err.println("Error: Please enter text to search for!");
        	return;
        } else {
        	texts = args[0].split("\\s*,\\s*");
        }
        
        if (args.length > 1)
        	ontologies = args[1];
		
        createOntology();
        
        for (String text : texts) {
        	System.out.println("\nStarting search for '" + text + "' in ontologies '" + ontologies + "'...");
			JsonNode rootNode = jsonToNode(get(
				REST_URL + "/annotator"
				+ "?text=" + text
				+ "&ontologies=" + ontologies
			));
	        
			for (JsonNode node : rootNode) {
	        	JsonNode cls = jsonToNode(get(node.get("annotatedClass").get("links").get("self").asText()));
	        	Node leaf = new Node(cls);
	        	
	        	if (leaf.label == null || leaf.label.equals(""))
	        		continue;
	
	        	String source = jsonToNode(
	        		get(node.get("annotatedClass").get("links").get("ontology").asText())
	        	).get("acronym").asText();
	        	System.out.println("Found class: '" + leaf.label + "' in '" + source + "'.");
	        	
	        	OntClass leafClass = createClass(leaf);
	        	addParentsToNode(cls, leafClass);
	        }
        }
      
		saveOntology(model);
	}
	
	private static void createOntology() {
		model = ModelFactory.createOntologyModel();
        model.createOntology(IRI);
        definition = model.createAnnotationProperty(IRI + "#definition");
        synonym = model.createAnnotationProperty(IRI + "#synonym");
	}
	
	private static void saveOntology(OntModel model) {
		try {
			File file = new File ("annotation.owl");
			FileOutputStream stream = new FileOutputStream (file);
			model.write(stream, "rdf/xml", IRI);
			System.out.println("\nSaved ontologie in '" + file.getAbsolutePath() + "'.");
		} catch (Exception e) {
			System.err.println("\nError: Could not save owl file. (" + e.getMessage() + ")");
		}
	}
	
	private static void addParentsToNode(JsonNode node, OntClass leafClass) {
		for (JsonNode parent : jsonToNode(get(node.get("links").get("parents").asText()))) {
			OntClass parentClass = createClass(new Node(parent));
			leafClass.addSuperClass(parentClass);
			
			addParentsToNode(parent, parentClass);
		}
	}
	
	private static OntClass createClass(Node node) {
		OntClass cls = model.createClass(node.id);
		cls.addLabel(node.label, "en");
    	
		if (node.definition != null && !node.definition.equals(""))
    		cls.addProperty(definition, node.definition);
    	for (String curSynonym : node.synonyms) {
    		cls.addProperty(synonym, curSynonym);
    	}
    	
    	return cls;
	}

	private static JsonNode jsonToNode(String json) {
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

    private static String get(String urlToGet) {
        URL url;
        HttpURLConnection conn;
        BufferedReader rd = null;
        String line;
        String result = "";
        try {
            url = new URL(urlToGet);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "apikey token=" + API_KEY);
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
}
