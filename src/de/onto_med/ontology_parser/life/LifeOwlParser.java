package de.onto_med.ontology_parser.life;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.XMLUtils;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.search.EntitySearcher;

public class LifeOwlParser extends LifeOntologyParser {
	/**
	 * Constructor.
	 * @param ontologyPath path to pprj file
	 * @throws OWLOntologyCreationException 
	 */
	public LifeOwlParser(String ontologyPath) throws OWLOntologyCreationException {
		File file = new File(ontologyPath);
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);
		
		items = new ArrayList<LifeItem>();
		
		for (OWLNamedIndividual individual : ontology.getIndividualsInSignature()) {
			try {
				Iterator<OWLDataProperty> propertyIterator = ontology.getDataPropertiesInSignature().iterator();
				OWLDataProperty descriptionProperty = propertyIterator.next();
				OWLDataProperty relatedProperty = propertyIterator.next();

				ArrayList<String> related = new ArrayList<String>();
				for (OWLLiteral literal : EntitySearcher.getDataPropertyValues(individual, relatedProperty, ontology)) {
					related.add(literal.getLiteral());
				}
				
				items.add(new LifeItem(
					XMLUtils.getNCNameSuffix(individual.getIRI().toString()),
					EntitySearcher.getDataPropertyValues(individual, descriptionProperty, ontology).iterator().next().getLiteral(),
					related
				));
			} catch (Exception e) { }
		}
		
		iterator = items.iterator();
	}
}