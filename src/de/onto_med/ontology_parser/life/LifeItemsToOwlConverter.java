package de.onto_med.ontology_parser.life;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import de.onto_med.Translator;

public class LifeItemsToOwlConverter {

	private static String iri = "http://imise.uni-leipzig.de/life#";
	private static int maxItems = 50;
	
	
	public static void main(String[] args) {
		LifePprjParser parser = new LifePprjParser("H:/LIFE-Metadaten/life.pprj");
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory factory = manager.getOWLDataFactory();
		OWLDataProperty description = factory.getOWLDataProperty(IRI.create(iri + "description"));
		OWLDataProperty related = factory.getOWLDataProperty(IRI.create(iri + "related"));
		OWLOntology ontology = null;
		
		try {
			ontology = manager.createOntology();
		} catch (OWLOntologyCreationException e1) {
			e1.printStackTrace();
		}
		
		int counter = 0;
		HashMap<String, IRI> cache = new HashMap<String, IRI>();
		for (LifeItem item : parser.getItems()) {
			counter++;
			if (counter > maxItems) break;
			
			if (item.getDescription().split(" +").length > 5) continue;
			
			String translatedDescription = Translator.translate(item.getDescription());
			
			if (cache.containsKey(translatedDescription)) {
				OWLNamedIndividual individual = factory.getOWLNamedIndividual(cache.get(translatedDescription));
				OWLDataPropertyAssertionAxiom axiom = factory.getOWLDataPropertyAssertionAxiom(related, individual, item.getId());
				manager.applyChange(new AddAxiom(ontology, axiom));
			} else {
				OWLDataPropertyAssertionAxiom axiom = factory.getOWLDataPropertyAssertionAxiom(
					description,
					factory.getOWLNamedIndividual(IRI.create(iri + item.getId())),
					translatedDescription
				);
				manager.applyChange(new AddAxiom(ontology, axiom));
				cache.put(translatedDescription, IRI.create(iri + item.getId()));
			}
		}
		
		
		try {
			manager.saveOntology(ontology, new BufferedOutputStream(new FileOutputStream(new File("condensed_LifeOntology.owl"))));
		} catch (OWLOntologyStorageException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}
