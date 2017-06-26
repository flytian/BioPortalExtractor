package de.onto_med.bioportal_extractor_gui.life.converter;

import java.io.PrintWriter;

import de.onto_med.bioportal_extractor_gui.life.LifeItem;
import de.onto_med.bioportal_extractor_gui.life.LifePprjParser;

public class LifeItemsToCsvConverter {
	
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
