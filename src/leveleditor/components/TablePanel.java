package leveleditor.components;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;

public class TablePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private TableModel model;

	public TablePanel(int w, int h) {
		super(new GridBagLayout());
		model = new TableModel(w, h);
		init();
	}

	public void recreateTable(int w, int h){
		model = new TableModel(w, h);
		removeAll();
		init();
	}
	
	private void init() {
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(2,2,2,2);
		
		for (int i = 0; i < model.getHeight(); i++) {
			for (int j = 0; j < model.getWidth(); j++) {
				FieldPanel field = new FieldPanel();
				c.gridx = j;
				c.gridy = i;
				add(field, c);
			}
		}
		revalidate();
		repaint();
	}

}
