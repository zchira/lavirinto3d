package leveleditor.dialogs;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class NewDialog extends JDialog{

	private static final long serialVersionUID = 1L;

	private JPanel mainPanel;
	
	private JButton jbtnOK;
	
	private JButton jbtnCancel;
	
	private JSpinner jSpnWidth;
	
	private JSpinner jSpnHeight;
	
	private int action;
	
	public static int OK_ACTION = 1;
	
	public static int CANCEL_ACTION = 0;
	
	private JSpinner getJSpnWidth() {
		if (jSpnWidth == null) {
			SpinnerNumberModel model = new SpinnerNumberModel(3,1,50,1);
			jSpnWidth = new JSpinner(model);
		}
		return jSpnWidth;
	}

	private JSpinner getJSpnHeight() {
		if (jSpnHeight == null) {
			SpinnerNumberModel model = new SpinnerNumberModel(3,1,50,1);
			jSpnHeight = new JSpinner(model);
			
		}
		return jSpnHeight;
	}

	public NewDialog(Frame owner){
		super(owner);
		setSize(250,200);
		setResizable(false);
		setTitle("New table");
		setLocationRelativeTo(owner);
		this.setModal(true);
		init();
	}

	private void init(){
		setContentPane(getMainPanel());
	}
	
	private JPanel getMainPanel(){
		if (mainPanel == null){
			mainPanel = new JPanel(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			JLabel jlblWidth = new JLabel("Width:");
			JLabel jlblHeight = new JLabel("Height:");
			
			c.insets = new Insets(5,5,5,5);
			
			c.gridx = 0;
			c.gridy = 0;
			
			mainPanel.add(jlblWidth, c);
			c.gridy = 1;
			
			mainPanel.add(jlblHeight, c);
			
			c.gridx = 1;
			c.gridy = 0;
			mainPanel.add(getJSpnWidth(), c);
			c.gridy = 1;
			mainPanel.add(getJSpnHeight(), c);
			
			c.gridy = 2;
			c.gridx = 0;
			mainPanel.add(getJbtnOK(),c);
			c.gridx = 1;
			mainPanel.add(getJbtnCancel(), c);
		}
		return mainPanel;
	}

	private JButton getJbtnOK() {
		if (jbtnOK == null) {
			jbtnOK = new JButton("OK");
			jbtnOK.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					action = OK_ACTION;
					setVisible(false);					
				}
			});
		}
		return jbtnOK;
	}

	private JButton getJbtnCancel() {
		if (jbtnCancel == null) {
			jbtnCancel = new JButton("Cancel");
			jbtnCancel.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					action = CANCEL_ACTION;
					setVisible(false);
				}
			});
			
		}
		return jbtnCancel;
	}
	
	public int getTableWidth(){
		return (Integer)getJSpnWidth().getValue();
	}
	
	public int getTableHeight(){
		return (Integer)getJSpnHeight().getValue();
	}
	
	public int getAction(){
		return action;
	}

}
