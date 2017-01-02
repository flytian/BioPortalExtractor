package de.onto_med.bioportal_extractor.view;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JList;
import javax.swing.ListModel;

import de.onto_med.ontology_parser.life.LifeItem;

public class LifeItemList extends JList<LifeItem> {
	private static final long serialVersionUID = 3456447653090821395L;

	public LifeItemList() {
        super();
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                LifeItemList list = (LifeItemList) e.getSource();
                ListModel<LifeItem> model = list.getModel();
                int index = list.locationToIndex(e.getPoint());
                if (index > -1) {
                    LifeItem item = (LifeItem) model.getElementAt(index);
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
