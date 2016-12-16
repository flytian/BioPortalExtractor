package de.uni_leipzig.imise.BioPortalExtractor.OntologyParser.LifeOntologyParser;

import java.io.File;
import java.util.ArrayList;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.XMLUtils;
import org.semanticweb.owlapi.model.OWLDataProperty;
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
				OWLDataProperty descriptionProperty = ontology.getDataPropertiesInSignature().iterator().next();
				items.add(new LifeItem(
					XMLUtils.getNCNameSuffix(individual.getIRI().toString()),
					EntitySearcher.getDataPropertyValues(individual, descriptionProperty, ontology).iterator().next().getLiteral()
				));
			} catch (Exception e) { }
		}
		
		iterator = items.iterator();
	}
}