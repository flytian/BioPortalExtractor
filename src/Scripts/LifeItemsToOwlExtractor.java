package Scripts;

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

import de.uni_leipzig.imise.BioPortalExtractor.OntologyParser.LifeOntologyParser.LifeItem;
import de.uni_leipzig.imise.BioPortalExtractor.OntologyParser.LifeOntologyParser.LifePprjParser;

public class LifeItemsToOwlExtractor {

	private static String iri = "http://imise.uni-leipzig.de/life#";
	
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
		
		for (LifeItem item : parser.getItems()) {
			if (item.getDescription().split(" +").length > 5) continue;
			
			OWLDataPropertyAssertionAxiom axiom = factory.getOWLDataPropertyAssertionAxiom(
				description,
				factory.getOWLNamedIndividual(IRI.create(iri + item.getId())),
				item.getDescription()
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
