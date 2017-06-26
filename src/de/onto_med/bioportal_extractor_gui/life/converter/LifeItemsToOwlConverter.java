package de.onto_med.bioportal_extractor_gui.life.converter;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
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

import de.onto_med.bioportal_extractor.Translator;
import de.onto_med.bioportal_extractor_gui.life.LifeItem;
import de.onto_med.bioportal_extractor_gui.life.LifeItemFilter;
import de.onto_med.bioportal_extractor_gui.life.LifePprjParser;

public class LifeItemsToOwlConverter {

	private static String iri    = "http://imise.uni-leipzig.de/life#";
	private static int maxItems  = 150;
	private static int maxLength = 5;
	
	
	public static void main(String[] args) {
		LifePprjParser parser       = new LifePprjParser("H:/LIFE-Metadaten/life.pprj");
		OWLOntologyManager manager  = OWLManager.createOWLOntologyManager();
		OWLDataFactory factory      = manager.getOWLDataFactory();
		OWLDataProperty description = factory.getOWLDataProperty(IRI.create(iri + "description"));
		OWLDataProperty related     = factory.getOWLDataProperty(IRI.create(iri + "related"));
		OWLOntology ontology        = null;
		
		try {
			ontology = manager.createOntology();
		} catch (OWLOntologyCreationException e1) {
			e1.printStackTrace();
		}
		
		int counter = 0;
		HashMap<String, IRI> cache = new HashMap<String, IRI>();
		
		Comparator<LifeItem> comparator = new Comparator<LifeItem>() {
			public int compare(LifeItem i1, LifeItem i2) {
				return i1.getDescription().length() - i2.getDescription().length();
			}
		};
		
		ArrayList<LifeItem> items = parser.getItems();
		items.sort(comparator);
		
		for (LifeItem item : items) {
			if (item.getDescription().split(" +").length > maxLength
				|| LifeItemFilter.isUseless(item)
			) {
				System.out.println("Skiped: " + item.getId() + " - '" + item.getDescription() + "'");
				continue;
			}
			
			if (counter >= maxItems) break;
			
			String translatedDescription = Translator.translate(item.getDescription());
			
			if (cache.containsKey(translatedDescription)) {
				OWLNamedIndividual individual = factory.getOWLNamedIndividual(cache.get(translatedDescription));
				OWLDataPropertyAssertionAxiom axiom = factory.getOWLDataPropertyAssertionAxiom(related, individual, item.getId());
				manager.applyChange(new AddAxiom(ontology, axiom));
				System.err.println(counter + ": " + translatedDescription + " allready in ontology");
			} else {
				OWLDataPropertyAssertionAxiom axiom = factory.getOWLDataPropertyAssertionAxiom(
					description,
					factory.getOWLNamedIndividual(IRI.create(iri + item.getId())),
					translatedDescription
				);
				manager.applyChange(new AddAxiom(ontology, axiom));
				cache.put(translatedDescription, IRI.create(iri + item.getId()));
				counter++;
				System.out.println(counter + ": " + item.getDescription() + " -> " + translatedDescription);
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
