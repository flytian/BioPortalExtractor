package de.uni_leipzig.imise.BioPortalExtractor.LIFEOntologyParser;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JList;
import javax.swing.ListModel;

public class LIFEItemList extends JList<LIFEItem> {
	private static final long serialVersionUID = 3456447653090821395L;

	public LIFEItemList() {
        super();
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                LIFEItemList list = (LIFEItemList) e.getSource();
                ListModel<LIFEItem> model = list.getModel();
                int index = list.locationToIndex(e.getPoint());
                if (index > -1) {
                    LIFEItem item = (LIFEItem) model.getElementAt(index);
                    list.setToolTipText(item.getDescription());
                }
            }
        });
    }

    // Expose the getToolTipText event of our JList
    public String getToolTipText(MouseEvent e) {
        return super.getToolTipText();
    }
}
