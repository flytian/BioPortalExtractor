package de.onto_med.bioportal_extractor.view;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;

import de.onto_med.bioportal_extractor.Node;

@SuppressWarnings("rawtypes")
public class NodeList extends JList<Node> {
	private static final long serialVersionUID = -4413652171922520104L;

	public NodeList() {
        super();
        
        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                NodeList theList = (NodeList) e.getSource();
                ListModel model = theList.getModel();
                int index = theList.locationToIndex(e.getPoint());
                if (index > -1) {
                    theList.setToolTipText(null);
                    Node node = (Node) model.getElementAt(index);
                    theList.setToolTipText(node.getTooltip());
                }
            }
        });
    }

    // Expose the getToolTipText event of our JList
    public String getToolTipText(MouseEvent e) {
        return super.getToolTipText();
    }
}
