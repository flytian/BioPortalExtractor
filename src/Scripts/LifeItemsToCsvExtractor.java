package Scripts;

import java.io.PrintWriter;
import de.uni_leipzig.imise.BioPortalExtractor.OntologyParser.LifeOntologyParser.LifeItem;
import de.uni_leipzig.imise.BioPortalExtractor.OntologyParser.LifeOntologyParser.LifePprjParser;

public class LifeItemsToCsvExtractor {
	
	public static void main(String[] args) {
		LifePprjParser parser = new LifePprjParser("H:/LIFE-Metadaten/life.pprj");
		PrintWriter writer = null;
		
		try {
			writer = new PrintWriter("LIFEItems.csv");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		for (LifeItem item : parser.getItems()) {
			writer.println(item.getId() + ";\"" + item.getDescription().replaceAll("\\s", " ").replaceAll("\"", "'") + "\"");
		}
		
		writer.close();
	}

}
