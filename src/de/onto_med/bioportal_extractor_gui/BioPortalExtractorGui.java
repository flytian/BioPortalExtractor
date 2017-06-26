package de.onto_med.bioportal_extractor_gui;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import de.onto_med.bioportal_extractor_gui.view.View;

public class BioPortalExtractorGui {
	private static View frame;
	
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		
		try {
			frame = new View();
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
