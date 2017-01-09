package de.onto_med.ontology_parser.life;

import java.util.ArrayList;

import org.apache.jena.ontology.OntClass;
import de.onto_med.bioportal_extractor.Extractor;
import de.onto_med.bioportal_extractor.Node;

public class AutomatedExtractionForTranslatedLifeItems {

	public static void main(String[] args) {
		try {
			LifeOwlParser parser = new LifeOwlParser(args[0]);
			Extractor extractor = new Extractor(
				"",
				"http://imise.uni-leipzig.de/annotation.owl#",
				"auto_annotation.owl",
				"NCIT"
			);
			
			LifeItem item;
			while ((item = parser.next()) != null) {
				ArrayList<Node> nodes = extractor.extract(item.getDescription());
				
				if (nodes.isEmpty()) continue;
				Node node = nodes.get(0);
				
				OntClass cls = extractor.createClass(node);
				extractor.addOrigin(cls, item.getId());
				extractor.addParentsToNode(node.json, cls);
			}
			
			extractor.saveOntology();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
