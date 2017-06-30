package de.onto_med.bioportal_extractor_gui.view;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultTreeModel;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.ontology.OntClass;
import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;

import de.onto_med.bioportal_extractor.NodeConverter;
import de.onto_med.bioportal_extractor_gui.life.LifeItem;
import de.onto_med.bioportal_extractor_gui.life.LifeOntologyParser;
import de.onto_med.bioportal_extractor_gui.life.LifeOwlParser;
import de.onto_med.bioportal_extractor_gui.life.LifePprjParser;
import de.onto_med.bioportal_extractor.BioPortalExtractor;
import de.onto_med.bioportal_extractor.Node;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;
import java.util.concurrent.Callable;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.JScrollPane;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.GridLayout;
import java.awt.FlowLayout;

public class View extends JFrame {
	
	private static final long serialVersionUID = -8815936031219372775L;
	
	private static LifeOntologyParser parser    = null;
	private static BioPortalExtractor extractor = null;
	private static NodeConverter converter      = null;
	
	private JPanel contentPane;
	private JTextArea txtTranslation;
	private JTextArea lblCurrentItem;
	private JPanel panelResult;
	private NodeList list;
	private JTree ontologyTree;
	private LifeItemList lifeItemList;
	private DefaultListModel<LifeItem> lifeItemListModel;
	private Map<String, String> configuration;


	public View() throws Exception {
		configuration = readConfiguration();
		
		extractor = new BioPortalExtractor(
			configuration.get("api_key"),
			configuration.get("ontologies")
		);
		
		setTitle("BioPortal Extractor");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// setBounds(100, 100, 613, 548);
		setSize(700, 600);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 0, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JPanel filePanel = new JPanel();
		contentPane.add(filePanel, BorderLayout.NORTH);
		
		JButton btnOpenItemOntology = new JButton("Open Item Ontology...", UIManager.getIcon("FileView.directoryIcon"));
		btnOpenItemOntology.addActionListener(new OpenItemOntologyActionListener());
		filePanel.add(btnOpenItemOntology);
		
		JButton btnOpenOutputOntology = new JButton("Open Output Ontology...", UIManager.getIcon("FileView.directoryIcon"));
		btnOpenOutputOntology.addActionListener(new OpenOutputOntologyActionListener());
		filePanel.add(btnOpenOutputOntology);
		
		JButton btnSaveOntology = new JButton("Save Ontology", UIManager.getIcon("FileView.floppyDriveIcon"));
		filePanel.add(btnSaveOntology);
		btnSaveOntology.addActionListener(new SaveOntologyActionListener());
		
		JPanel contentPanel = new JPanel();
		contentPane.add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel inputPanel = new JPanel();
		inputPanel.setBorder(null);
		contentPanel.add(inputPanel, BorderLayout.NORTH);
		
		GridBagLayout gbl_inputPanel = new GridBagLayout();
		gbl_inputPanel.columnWidths  = new int[] {100, 200};
		gbl_inputPanel.rowHeights    = new int[] {50, 50, 0};
		gbl_inputPanel.columnWeights = new double[]{0.0, 1.0};
		gbl_inputPanel.rowWeights    = new double[]{0.0, 0.0, 1.0};
		inputPanel.setLayout(gbl_inputPanel);
		
		JLabel label1 = new JLabel("Current Item:");
		GridBagConstraints gbc_label1 = new GridBagConstraints();
		gbc_label1.anchor = GridBagConstraints.NORTHWEST;
		gbc_label1.insets = new Insets(0, 0, 5, 5);
		gbc_label1.gridx  = 0;
		gbc_label1.gridy  = 0;
		inputPanel.add(label1, gbc_label1);
		
		lblCurrentItem = new JTextArea("");
		GridBagConstraints gbc_lblCurrentItem = new GridBagConstraints();
		gbc_lblCurrentItem.fill   = GridBagConstraints.BOTH;
		gbc_lblCurrentItem.insets = new Insets(0, 0, 5, 0);
		gbc_lblCurrentItem.gridx  = 1;
		gbc_lblCurrentItem.gridy  = 0;
		inputPanel.add(lblCurrentItem, gbc_lblCurrentItem);
		lblCurrentItem.setLineWrap(true);
		lblCurrentItem.setRows(2);
		lblCurrentItem.setEditable(false);
		
		JLabel label2 = new JLabel("Translation:");
		GridBagConstraints gbc_label2 = new GridBagConstraints();
		gbc_label2.anchor = GridBagConstraints.NORTHWEST;
		gbc_label2.insets = new Insets(0, 0, 5, 5);
		gbc_label2.gridx  = 0;
		gbc_label2.gridy  = 1;
		inputPanel.add(label2, gbc_label2);
		
		txtTranslation = new JTextArea();
		GridBagConstraints gbc_txtTranslation = new GridBagConstraints();
		gbc_txtTranslation.fill   = GridBagConstraints.BOTH;
		gbc_txtTranslation.insets = new Insets(0, 0, 5, 0);
		gbc_txtTranslation.gridx  = 1;
		gbc_txtTranslation.gridy  = 1;
		inputPanel.add(txtTranslation, gbc_txtTranslation);
		txtTranslation.setWrapStyleWord(true);
		txtTranslation.setRows(2);
		txtTranslation.setColumns(10);
		
		JPanel splitPane = new JPanel();
		splitPane.setBorder(null);
		GridBagConstraints gbc_splitPane = new GridBagConstraints();
		gbc_splitPane.anchor = GridBagConstraints.WEST;
		gbc_splitPane.gridx  = 1;
		gbc_splitPane.gridy  = 2;
		inputPanel.add(splitPane, gbc_splitPane);
		
		JButton btnPreviousItem = new JButton("Previous Item");
		btnPreviousItem.addActionListener(new PreviousItemActionListener());
		splitPane.add(btnPreviousItem);
		
		JButton btnNextItem = new JButton("Next Item");
		splitPane.add(btnNextItem);
		
		JButton btnExtractClasses = new JButton("Extract Classes");
		splitPane.add(btnExtractClasses);
		
		JPanel resultPanel = new JPanel();
		contentPanel.add(resultPanel);
		resultPanel.setLayout(new GridLayout(0, 2, 0, 0));
		
		panelResult = new JPanel();
		resultPanel.add(panelResult);
		panelResult.setLayout(new BorderLayout(0, 0));
		
		list = new NodeList();
		list.setVisibleRowCount(5);
		
		JScrollPane resultScrollPane = new JScrollPane(list);
		panelResult.add(resultScrollPane, BorderLayout.CENTER);
		
		JPanel resultButtonsPanel = new JPanel();
		FlowLayout fl_resultButtonsPanel = (FlowLayout) resultButtonsPanel.getLayout();
		fl_resultButtonsPanel.setVgap(0);
		fl_resultButtonsPanel.setHgap(0);
		panelResult.add(resultButtonsPanel, BorderLayout.SOUTH);
		
		JButton btnNewButton = new JButton("Ignore Item");
		resultButtonsPanel.add(btnNewButton);
		
		JButton btnUseSelectedClasses = new JButton("Use Selected Classes");
		resultButtonsPanel.add(btnUseSelectedClasses);
		
		JPanel itemsPanel = new JPanel();
		resultPanel.add(itemsPanel);
		
		lifeItemList = new LifeItemList();
		lifeItemList.addMouseListener(new LifeItemsListMouseListener());
		itemsPanel.setLayout(new GridLayout(0, 1, 0, 0));
		JScrollPane itemsScrollPane = new JScrollPane(lifeItemList);
		itemsPanel.add(itemsScrollPane);
		
		
		ontologyTree = new JTree();
		JScrollPane ontologyScrollPane = new JScrollPane(ontologyTree);
		itemsPanel.add(ontologyScrollPane);
		btnUseSelectedClasses.addActionListener(new UseSelectedClassesActionListener());
		btnNewButton.addActionListener(new IgnoreItemActionListener());
		btnExtractClasses.addActionListener(new ExtractClassesActionListener());
		btnNextItem.addActionListener(new NextItemActionListener());
		
		reload();
		reset();
	}
	
	private void reset() {
		lblCurrentItem.setText("");
		txtTranslation.setText("");
		list.setModel(new DefaultListModel<Node>());
		lifeItemList.repaint();
		if (parser != null && parser.current() != null)
			lblCurrentItem.setText(parser.current().getDescription());
		if (converter != null)
			ontologyTree.setModel(new DefaultTreeModel(converter.getTreeNodeForClass(null)));
		else ontologyTree.setModel(new DefaultTreeModel(null));
	}
	
	private void reload() {
		if (parser != null) {
			lifeItemListModel = getLIFEItemsListModel();
			lifeItemList.setModel(lifeItemListModel);
		}
		
	}
	
	private String getSearchString() {
		String text = null;
		
		if (StringUtils.isNoneBlank(txtTranslation.getText())) {
			text = txtTranslation.getText();
		} else if (StringUtils.isNoneBlank(lblCurrentItem.getText())) {
			text = lblCurrentItem.getText();
		}
		
		return text;
	}
	
	private DefaultListModel<LifeItem> getLIFEItemsListModel() {
		DefaultListModel<LifeItem> model = new DefaultListModel<LifeItem>();
		for (LifeItem item : parser.getItems()) {
			if (converter != null) {
				for (OntClass cls : converter.getClassesOfOrigin(item.getId())) {
					if (cls.getLocalName().equals(item.getId()))
						item.setStatus(LifeItem.IS_IGNORED);
					else {
						item.setStatus(LifeItem.IS_ANNOTATED);
						item.addRelated(cls.getURI());
					}
				}
			}
			
			model.addElement(item);
		}
		
		return model;
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, String> readConfiguration() throws FileNotFoundException, YamlException {
		YamlReader reader = new YamlReader(new FileReader("settings.yml"));
		
		return (Map<String, String>) reader.read();
	}
	
	
	
	private class SaveOntologyActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (converter == null) {
				JOptionPane.showMessageDialog(new JFrame(), "Please specify an Output Ontology!");
				return;
			}
			try {
				converter.saveOntology();
				JOptionPane.showMessageDialog(new JFrame(), "Ontology saved. You can continue working on this ontology.");
			} catch (Exception err) {
				JOptionPane.showMessageDialog(new JFrame(), err.getMessage());
			}
		}
	}
	
	private class UseSelectedClassesActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (parser == null || parser.current() == null) {
				JOptionPane.showMessageDialog(new JFrame(), "No item for annotation selected!");
				return;
			} else if (converter == null) {
				JOptionPane.showMessageDialog(new JFrame(), "No Output Ontology for storing of annotations selected!");
				return;
			}
			
			try {
				for (Node node : list.getSelectedValuesList()) {
					parser.current().setStatus(LifeItem.IS_ANNOTATED);
					OntClass cls = converter.createClass(node);
					converter.addOrigin(cls, parser.current().getId());
					parser.current().addRelated(node.id);
				}
				
				parser.next();
				reset();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
	
	private class ExtractClassesActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			doInBackground("Searching for classes...", new Callable<Object>() {
				@Override
				public Object call() throws Exception {
					String text = getSearchString();
					if (StringUtils.isBlank(text)) {
						JOptionPane.showMessageDialog(new JFrame(), "No item to search for!");
					} else {
						DefaultListModel<Node> model = new DefaultListModel<Node>();
						extractor.annotate(text).forEach(e -> model.addElement(e));
						list.setModel(model);
					}
					return null;
				}
			});
		}
	}
	
	private <T> void doInBackground(String dialog, Callable<T> function) {
		final JDialog dlgProgress = new JDialog(new JFrame(), dialog, true);
		JProgressBar pbProgress = new JProgressBar(0, 100);
		pbProgress.setIndeterminate(true);
			
		dlgProgress.getContentPane().add(BorderLayout.CENTER, pbProgress);
		dlgProgress.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dlgProgress.setSize(300, 60);
		dlgProgress.setLocationRelativeTo(null);
			
		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				function.call();
				return null;
			}
				
			@Override
			protected void done() {
				dlgProgress.dispose();
			}
		};
			
		worker.execute();
		dlgProgress.setVisible(true);
			
		/* hide dialog */
		validate();
	}
	
	private class NextItemActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (parser == null) {
				JOptionPane.showMessageDialog(new JFrame(), "Please specifiy an Item Ontology!");
				return;
			}
			
			parser.next();
			reset();
		}
	}
	
	private class PreviousItemActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (parser == null) {
				JOptionPane.showMessageDialog(new JFrame(),  "Please specify an Item Ontology!");
				return;
			}
			
			parser.previous();
			reset();
		}
	}

	private class IgnoreItemActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (parser == null || parser.current() == null) {
				JOptionPane.showMessageDialog(new JFrame(), "Nothing to ignore!");
				return;
			} else if (converter == null) {
				JOptionPane.showMessageDialog(new JFrame(), "No Output Ontology for storing of annotations selected!");
				return;
			}
				
			Node node = new Node(parser.current().getId(), parser.current().getDescription());
			converter.createIgnoredClass(node);
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
	
	private class OpenItemOntologyActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileFilter(new FileNameExtensionFilter("Ontology", "pprj,owl".split(",")));
			if (fileChooser.showOpenDialog(new JFrame()) != JFileChooser.APPROVE_OPTION) return;
			
			File file = fileChooser.getSelectedFile();
			if (FilenameUtils.getExtension(file.getName()).equals("pprj")) {
				parser = new LifePprjParser(file.getAbsolutePath());
			} else if (FilenameUtils.getExtension(file.getName()).equals("owl")) {
				parser = new LifeOwlParser(file.getAbsolutePath());
			} else {
				return;
			}
			doInBackground("Loading Item Ontology...", new Callable<Object>() {
				@Override
				public DefaultListModel<LifeItem> call() throws Exception {
					parser.load();
					reload();
					return null;
				}
			});
		}
	}
	
	private class OpenOutputOntologyActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileFilter(new FileNameExtensionFilter("Ontology", "owl"));
			if (fileChooser.showOpenDialog(new JFrame()) != JFileChooser.APPROVE_OPTION) return;
			
			converter = new NodeConverter(
				configuration.get("iri"),
				StringUtils.defaultString(fileChooser.getSelectedFile().getAbsolutePath(), configuration.get("outputPath"))
			);
			reload();
		}
	}
}
