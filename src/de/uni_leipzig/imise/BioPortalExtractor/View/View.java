package de.uni_leipzig.imise.BioPortalExtractor.View;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.jena.ontology.OntClass;

import de.uni_leipzig.imise.BioPortalExtractor.Extractor.Extractor;
import de.uni_leipzig.imise.BioPortalExtractor.Extractor.Node;
import de.uni_leipzig.imise.BioPortalExtractor.LIFEOntologyParser.Parser;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.awt.GridLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

public class View extends JFrame {
	
	private static final long serialVersionUID = -8815936031219372775L;
	private static Parser parser;
	private static Extractor extractor;
	
	private JPanel contentPane;
	private JTextArea txtTranslation;
	private JTextArea lblCurrentItem;
	private JPanel panelResult;
	private NodeList list;

	public static void main(String[] args) {
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

	public View() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileNameExtensionFilter("pprj", "pprj"));
		if (fileChooser.showOpenDialog(new JFrame()) != JFileChooser.APPROVE_OPTION)
			System.exit(0);
		
		extractor = new Extractor();
		parser    = new Parser(fileChooser.getSelectedFile().getAbsolutePath());
		
		setTitle("BioPortalExtractor");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 480, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panelInput = new JPanel();
		panelInput.setBounds(5, 11, 449, 112);
		contentPane.add(panelInput);
		panelInput.setLayout(null);
		
		JLabel label1 = new JLabel("Current LIFE-Item:");
		label1.setBounds(10, 11, 108, 14);
		panelInput.add(label1);
		
		txtTranslation = new JTextArea();
		txtTranslation.setRows(2);
		txtTranslation.setBounds(118, 44, 321, 32);
		panelInput.add(txtTranslation);
		txtTranslation.setColumns(10);
		
		JLabel label2 = new JLabel("Translation:");
		label2.setBounds(10, 47, 108, 14);
		panelInput.add(label2);
		
		lblCurrentItem = new JTextArea("");
		lblCurrentItem.setRows(2);
		lblCurrentItem.setEditable(false);
		lblCurrentItem.setBounds(118, 11, 321, 32);
		panelInput.add(lblCurrentItem);
		
		JButton btnNewButton = new JButton("Next Item");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reset();
				lblCurrentItem.setText(parser.getNext());
			}
		});
		btnNewButton.setBounds(331, 78, 108, 23);
		panelInput.add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("Get Classes");
		btnNewButton_1.addActionListener(new ActionListener() {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			public void actionPerformed(ActionEvent e) {
				String text = getSearchString();
				
				if (text == null || text.equals("")) {
					JOptionPane.showMessageDialog(new JFrame(), "No LIFE item to search for!");
					return;
				}
				
				ArrayList<Node> nodes = extractor.extract(text);
				
				DefaultListModel model = new DefaultListModel();
				for (Node node : nodes) {
					model.addElement(node);
				}
				
				list.setModel(model);
				validate();
			}
		});
		btnNewButton_1.setBounds(213, 78, 108, 23);
		panelInput.add(btnNewButton_1);
		
		JButton btnUseSelectedClasses = new JButton("Use Selected Classes");
		btnUseSelectedClasses.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					for (Node node : list.getSelectedValuesList()) {
						OntClass cls = extractor.createClass(node);
						extractor.addOrigin(cls, getSearchString());
						extractor.addParentsToNode(node.json, cls);
					}
					
					reset();
					lblCurrentItem.setText(parser.getNext());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		btnUseSelectedClasses.setBounds(152, 228, 164, 23);
		contentPane.add(btnUseSelectedClasses);
		
		JButton btnSaveOntology = new JButton("Save Ontology");
		btnSaveOntology.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				extractor.saveOntology();
				JOptionPane.showMessageDialog(new JFrame(), "Ontology saved. You can continue working on this ontology.");
			}
		});
		btnSaveOntology.setBounds(326, 228, 128, 23);
		contentPane.add(btnSaveOntology);
		
		panelResult = new JPanel();
		panelResult.setBounds(5, 123, 449, 94);
		contentPane.add(panelResult);
		panelResult.setLayout(new GridLayout(0, 1, 0, 0));
		
		list = new NodeList();
		list.setVisibleRowCount(5);
				
		JScrollPane scrollPane = new JScrollPane(list);
		panelResult.add(scrollPane);
	}
	
	private void reset() {
		lblCurrentItem.setText("");
		txtTranslation.setText("");
		list.setModel(new DefaultListModel<Node>());
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
}
