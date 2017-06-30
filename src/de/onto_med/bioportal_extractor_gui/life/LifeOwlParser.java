package de.onto_med.bioportal_extractor_gui.life;

import java.util.ArrayList;
import java.util.Iterator;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.XMLUtils;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.search.EntitySearcher;

public class LifeOwlParser extends LifeOntologyParser {
	public LifeOwlParser(String path) {
		super(path);
	}

	public void load() throws Exception {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology       = manager.loadOntologyFromOntologyDocument(file);
		
		items = new ArrayList<LifeItem>();
		ontology.individualsInSignature().parallel().forEach(individual -> {
			Iterator<OWLDataProperty> propertyIterator = ontology.dataPropertiesInSignature().iterator();
			OWLDataProperty descriptionProperty        = propertyIterator.next();
			OWLDataProperty relatedProperty            = propertyIterator.next();
	
			ArrayList<String> related = new ArrayList<String>();
			EntitySearcher.getDataPropertyValues(individual, relatedProperty, ontology).parallel().forEach(
				literal -> related.add(literal.getLiteral())
			);
					
			items.add(new LifeItem(
				XMLUtils.getNCNameSuffix(individual.getIRI().toString()),
				EntitySearcher.getDataPropertyValues(individual, descriptionProperty, ontology).iterator().next().getLiteral(),
				related
			));
		});
		
		iterator = items.listIterator();
	}
}