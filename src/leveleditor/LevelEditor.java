package leveleditor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import leveleditor.components.TablePanel;
import leveleditor.dialogs.NewDialog;

public class LevelEditor extends JFrame {

	private static final long serialVersionUID = 1L;

	private TablePanel tablePanel;

	private JScrollPane scrollPane;

	private JMenuBar menu;

	private JPanel mainPanel;

	private JToolBar toolbar;

	public LevelEditor() {
		super();
		setSize(800, 600);
		setTitle("Lavirinto3d: level editor");
		setJMenuBar(getMennu());

		init();
	}

	private void init() {

		setContentPane(getMainPanel());
	}

	private JPanel getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel(new BorderLayout());
			mainPanel.add(getToolbar(), BorderLayout.NORTH);
			mainPanel.add(getScrollPane(), BorderLayout.CENTER);

		}

		return mainPanel;
	}

	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane(getTablePanel());
		}
		return scrollPane;
	}

	private TablePanel getTablePanel() {
		if (tablePanel == null) {
			tablePanel = new TablePanel(3, 3);
		}

		return tablePanel;
	}

	private JMenuBar getMennu() {
		if (menu == null) {
			menu = new JMenuBar();
			JMenu file = new JMenu("File");

			JMenuItem newTable = new JMenuItem(newAction);
//			newTable.addActionListener(newAction);

			file.add(newTable);

			menu.add(file);
		}
		return menu;
	}

	
	private NewAction newAction = new NewAction();
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LevelEditor editor = new LevelEditor();

		editor.setVisible(true);
		editor.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				System.out.println("bye");
			}
		});

	}

	private JToolBar getToolbar() {
		if (toolbar == null) {
			toolbar = new JToolBar(JToolBar.HORIZONTAL);
			toolbar.setFloatable(false);
			JButton newTable = new JButton(newAction);
			
			toolbar.add(newTable);

		}
		return toolbar;
	}

	class NewAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		private NewDialog dialog;

		public NewAction() {
			super("New table...");
			dialog = new NewDialog(LevelEditor.this);
			
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println(1);
			dialog.setVisible(true);
			System.out.println(2);
			if (dialog.getAction() == NewDialog.OK_ACTION) {
				getTablePanel().recreateTable(dialog.getTableWidth(), dialog
						.getTableHeight());
			}
		}
	}

}
