package de.uni_leipzig.imise.BioPortalExtractor.View;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultTreeModel;

import org.apache.commons.io.FilenameUtils;
import org.apache.jena.ontology.OntClass;
import de.uni_leipzig.imise.BioPortalExtractor.Extractor.Extractor;
import de.uni_leipzig.imise.BioPortalExtractor.Extractor.Node;
import de.uni_leipzig.imise.BioPortalExtractor.OntologyParser.LifeOntologyParser.LifeItem;
import de.uni_leipzig.imise.BioPortalExtractor.OntologyParser.LifeOntologyParser.LifeOntologyParser;
import de.uni_leipzig.imise.BioPortalExtractor.OntologyParser.LifeOntologyParser.LifeOwlParser;
import de.uni_leipzig.imise.BioPortalExtractor.OntologyParser.LifeOntologyParser.LifePprjParser;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.awt.GridLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

public class View extends JFrame {
	
	private static final long serialVersionUID = -8815936031219372775L;
	private static LifeOntologyParser parser;
	private static Extractor extractor;
	
	private JPanel contentPane;
	private JTextArea txtTranslation;
	private JTextArea lblCurrentItem;
	private JPanel panelResult;
	private NodeList list;
	private JFrame ontologyFrame;
	private JTree ontologyTree;
	private JFrame lifeItemsFrame;
	private LIFEItemList lifeItemList;

	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					View frame = new View();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public View() throws Exception {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileNameExtensionFilter("Ontology", "pprj,owl".split(",")));
		if (fileChooser.showOpenDialog(new JFrame()) != JFileChooser.APPROVE_OPTION)
			System.exit(0);
		
		extractor = new Extractor();
		
		File file = fileChooser.getSelectedFile();
		if (FilenameUtils.getExtension(file.getName()).equals("pprj")) {
			parser = new LifePprjParser(file.getAbsolutePath());
		} else if (FilenameUtils.getExtension(file.getName()).equals("owl")) {
			parser = new LifeOwlParser(file.getAbsolutePath());
		} else {
			System.exit(0);
		}
		
		setTitle("BioPortalExtractor");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 480, 334);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 0, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panelInput = new JPanel();
		panelInput.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelInput.setBounds(5, 11, 449, 112);
		contentPane.add(panelInput);
		panelInput.setLayout(null);
		
		JLabel label1 = new JLabel("Current LIFE-Item:");
		label1.setBounds(10, 11, 108, 14);
		panelInput.add(label1);
		
		txtTranslation = new JTextArea();
		txtTranslation.setWrapStyleWord(true);
		txtTranslation.setRows(2);
		txtTranslation.setBounds(118, 44, 321, 32);
		panelInput.add(txtTranslation);
		txtTranslation.setColumns(10);
		
		JLabel label2 = new JLabel("Translation:");
		label2.setBounds(10, 47, 108, 14);
		panelInput.add(label2);
		
		lblCurrentItem = new JTextArea("");
		lblCurrentItem.setLineWrap(true);
		lblCurrentItem.setRows(2);
		lblCurrentItem.setEditable(false);
		lblCurrentItem.setBounds(118, 11, 321, 32);
		panelInput.add(lblCurrentItem);
		
		JButton btnNextItem = new JButton("Next Item");
		btnNextItem.addActionListener(new NextItemActionListener());
		btnNextItem.setBounds(331, 78, 108, 23);
		panelInput.add(btnNextItem);
		
		JButton btnExtractClasses = new JButton("Extract Classes");
		btnExtractClasses.addActionListener(new ExtractClassesActionListener());
		btnExtractClasses.setBounds(196, 78, 125, 23);
		panelInput.add(btnExtractClasses);
		
		JButton btnNewButton = new JButton("Ignore Item");
		btnNewButton.addActionListener(new IgnoreItemActionListener());
		btnNewButton.setBounds(64, 78, 120, 23);
		panelInput.add(btnNewButton);
		
		JButton btnUseSelectedClasses = new JButton("Use Selected Classes");
		btnUseSelectedClasses.addActionListener(new UseSelectedClassesActionListener());
		btnUseSelectedClasses.setBounds(288, 226, 164, 23);
		contentPane.add(btnUseSelectedClasses);
		
		JButton btnSaveOntology = new JButton("Save Ontology");
		btnSaveOntology.addActionListener(new SaveOntologyActionListener());
		btnSaveOntology.setBounds(326, 263, 128, 23);
		contentPane.add(btnSaveOntology);
		
		panelResult = new JPanel();
		panelResult.setBounds(5, 123, 449, 94);
		contentPane.add(panelResult);
		panelResult.setLayout(new GridLayout(0, 1, 0, 0));
		
		list = new NodeList();
		list.setVisibleRowCount(5);
				
		JScrollPane scrollPane = new JScrollPane(list);
		panelResult.add(scrollPane);
		
		JButton btnShowOntology = new JButton("Show Ontology");
		btnShowOntology.addActionListener(new ShowOntologyActionListener());
		btnShowOntology.setBounds(5, 263, 125, 23);
		contentPane.add(btnShowOntology);
		
		JButton btnShowItems = new JButton("Show Items");
		btnShowItems.setBounds(5, 226, 125, 23);
		contentPane.add(btnShowItems);
		btnShowItems.addActionListener(new ShowItemsActionListener());
		
		ontologyFrame = new JFrame("Extracted Ontology");
		ontologyFrame.setBounds(580, 100, 480, 600);
		
		ontologyTree = new JTree();
		JScrollPane ontologyScrollPane = new JScrollPane(ontologyTree);
		ontologyFrame.getContentPane().add(ontologyScrollPane);
		ontologyFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		ontologyFrame.setVisible(false);
		
		lifeItemsFrame = new JFrame("LIFE Items");
		lifeItemsFrame.setBounds(1060, 100, 480, 600);
		
		lifeItemList = new LIFEItemList();
		lifeItemList.addMouseListener(new LifeItemsListMouseListener());
		JScrollPane lifeItemsScrollPane = new JScrollPane(lifeItemList);
		
		lifeItemsFrame.getContentPane().add(lifeItemsScrollPane);
		lifeItemsFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		lifeItemsFrame.setVisible(false);
	}
	
	private void reset() {
		lblCurrentItem.setText("");
		txtTranslation.setText("");
		list.setModel(new DefaultListModel<Node>());
		if (lifeItemsFrame.isVisible()) 
			lifeItemList.setModel(getLIFEItemsListModel());
		if (ontologyFrame.isVisible())
			ontologyTree.setModel(new DefaultTreeModel(extractor.getTreeNodeForClass(null)));
		lblCurrentItem.setText(parser.current().getDescription());
	}
	
	private String getSearchString() {
		String text = null;
		
		if (txtTranslation.getText() != null && !txtTranslation.getText().equals("")) {
			text = txtTranslation.getText();
		} else if (lblCurrentItem.getText() != null && !lblCurrentItem.getText().equals("")) {
			text = lblCurrentItem.getText();
		}
		
		return text;
	}
	
	private DefaultListModel<LifeItem> getLIFEItemsListModel() {
		DefaultListModel<LifeItem> model = new DefaultListModel<LifeItem>();
		for (LifeItem item : parser.getItems()) {
			LifeItem copy = item.clone();
			OntClass cls = extractor.getAnnotatedClass(item.getId());
			if (cls != null) {
				if (cls.getLocalName().equals(copy.getId()))
					copy.setDescription("<html><body style='background-color:yellow'>" + copy.toString() + " - IGNORED</body></html>");
				else
					copy.setDescription("<html><body style='background-color:green'>" + copy.toString() + " - ANNOTATED</body></html>");
			}
			
			model.addElement(copy);
		}
		
		return model;
	}
	
	
	
	
	private class SaveOntologyActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				extractor.saveOntology();
				JOptionPane.showMessageDialog(new JFrame(), "Ontology saved. You can continue working on this ontology.");
			} catch (Exception err) {
				JOptionPane.showMessageDialog(new JFrame(), err.getLocalizedMessage());
			}
		}
	}
	
	private class UseSelectedClassesActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (parser.current() == null) {
				JOptionPane.showMessageDialog(new JFrame(), "No LIFE item for annotation selected!");
				return;
			}
			
			try {
				for (Node node : list.getSelectedValuesList()) {
					OntClass cls = extractor.createClass(node);
					extractor.addOrigin(cls, parser.current().getId());
					extractor.addParentsToNode(node.json, cls);
				}
				
				parser.next();
				reset();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
	
	private class ExtractClassesActionListener implements ActionListener {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public void actionPerformed(ActionEvent e) {
			String text = getSearchString();
			
			if (text == null || text.equals("")) {
				JOptionPane.showMessageDialog(new JFrame(), "No LIFE item to search for!");
				return;
			}
			
			DefaultListModel model = new DefaultListModel();
			for (Node node : extractor.extract(text)) {
				model.addElement(node);
			}
			
			list.setModel(model);
			validate();
		}
	}
	
	private class NextItemActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			do {
				parser.next();
			} while (parser.current() != null && extractor.isAnnotated(parser.current().getId()));
			reset();
		}
	}
	
	private class ShowItemsActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			lifeItemList.setModel(getLIFEItemsListModel());
			lifeItemsFrame.setVisible(true);
		}
	}
	
	private class ShowOntologyActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			ontologyTree.setModel(new DefaultTreeModel(extractor.getTreeNodeForClass(null)));
			ontologyFrame.setVisible(true);
		}
	}

	private class IgnoreItemActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (parser.current() == null) {
				JOptionPane.showMessageDialog(new JFrame(), "Nothing to ignore!");
				return;
			}
				
			Node node = new Node(parser.current().getId(), parser.current().getDescription());
			extractor.ignoreClass(node);
			parser.next();
			reset();
		}
	}

	private class LifeItemsListMouseListener implements MouseListener {
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) {
				LifeItem item = lifeItemList.getSelectedValue();
				parser.goTo(item);
				reset();
			}
		}

		public void mousePressed(MouseEvent e) {}

		public void mouseReleased(MouseEvent e) {}

		public void mouseEntered(MouseEvent e) {}

		public void mouseExited(MouseEvent e) {}
	}
}
