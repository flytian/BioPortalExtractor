package de.onto_med.bioportal_extractor_gui.view;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JList;
import javax.swing.ListModel;

import de.onto_med.bioportal_extractor_gui.life.LifeItem;

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
                    LifeItem item = model.getElementAt(index);
                    list.setToolTipText(
                    	"<html><body>" 
                    	+ item.getDescription() 
                    	+ (item.hasRelated() ? "<br>Related:<br>" + String.join("<br>", item.getRelated()) : "")
                    	+ "</body></html>"
                    );
                }
            }
        });
    }

    public String getToolTipText(MouseEvent e) {
        return super.getToolTipText();
    }
}
