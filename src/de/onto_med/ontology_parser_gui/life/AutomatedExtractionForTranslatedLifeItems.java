package de.onto_med.ontology_parser_gui.life;

import java.util.List;

import org.apache.jena.ontology.OntClass;

import de.onto_med.bioportal_extractor.BioPortalExtractor;
import de.onto_med.bioportal_extractor.NodeConverter;
import de.onto_med.bioportal_extractor.Node;

public class AutomatedExtractionForTranslatedLifeItems {

	public static void main(String[] args) {
		try {
			LifeOwlParser parser = new LifeOwlParser(args[0]);
			BioPortalExtractor extractor = new BioPortalExtractor(
				"01766580-322b-48aa-997a-9bc4462e471d",
				"NCIT"
			);
			NodeConverter converter = new NodeConverter(
				"http://onto_med.de/annotation",
				"auto_annotation.owl"
			);
			
			LifeItem item;
			while ((item = parser.next()) != null) {
				List<Node> nodes = extractor.annotate(item.getDescription());
				
				if (nodes.isEmpty()) continue;
				
				if (nodes.size() == 1) {
					Node node = nodes.get(0);
					
					OntClass cls = converter.createClass(node);
					converter.addOrigin(cls, item.getId());
				}
			}
			
			converter.saveOntology();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
