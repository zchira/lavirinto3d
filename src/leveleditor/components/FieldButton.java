package leveleditor.components;

import java.awt.Color;

import javax.swing.JButton;

public class FieldButton extends JButton{
	private static final long serialVersionUID = 1L;
	
	private ButtonModel model;
	
	public FieldButton(ButtonModel m){
		super();
		setText(".");
		model = m;
		setBackground(Color.white);
	}

	public int isPressed() {
		return model.getState();
	}

	public void setPressed(int pressed) {
		this.model.setState(pressed);
	}
	
	
}
