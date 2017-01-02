package de.onto_med.ontology_parser.life;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
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
		OWLOntology ontology = null;
		
		try {
			ontology = manager.createOntology();
		} catch (OWLOntologyCreationException e1) {
			e1.printStackTrace();
		}
		
		int counter = 0;
		for (LifeItem item : parser.getItems()) {
			counter++;
			if (counter > maxItems) break;
			
			if (item.getDescription().split(" +").length > 5) continue;
			
			OWLDataPropertyAssertionAxiom axiom = factory.getOWLDataPropertyAssertionAxiom(
				description,
				factory.getOWLNamedIndividual(IRI.create(iri + item.getId())),
				Translator.translate(item.getDescription())
			);
			manager.applyChange(new AddAxiom(ontology, axiom));
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
