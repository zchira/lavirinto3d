package leveleditor.components;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

public class FieldPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private FieldModel model;

	public FieldPanel() {
		super(new GridBagLayout());
		model = new FieldModel();
		init();
	}

	private void init() {
		GridBagConstraints c = new GridBagConstraints();

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				final FieldButton button = new FieldButton(model
						.getButtonModel(j, i));
				button.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (button.isPressed() == 0) {
							button.setPressed(1);
							button.setBackground(Color.BLUE);
						} else {
							button.setPressed(0);
							button.setBackground(Color.white);
						}
					}
				});
				c.gridx = j;
				c.gridy = i;
				add(button, c);

			}
		}
	}
}
