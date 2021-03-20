package zgame.states;

import java.io.IOException;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import zgame.GameWindow;
import zgame.StaticRenderTools;
import zgame.ZSounds;
import zgame.sound.SoundLoader;
import ZGameStatistic.ManageStatistics;
import ZGameStatistic.DataModel.PlayerInfo;

/**
 * 
 * @author zchira
 */
public class MenuState extends AbstractMenuState {
	/** The game unique name of this state */
	public static final String NAME = "menu";
	/** The index of the state the game option */
	private static final int START = 0;

	/** The index of the exit the game option */
	private static final int PROFILES = 1;
	/** The index of the exit the game option */
	private static final int OPTIONS = 2;
	/** The index of the exit the game option */
	private static final int EXIT = 4;
	/** The index of the TEST the game option */
	private static final int ABOUT = 3;
		
	private boolean noActivePlayer = false;

	/** The options to present to the user */
	protected String[] options = new String[] { "Start Game", "Player",
			"Options", "About", "Exit" };

	/**
	 * @see zgame.states.GameState#enter(zgame.GameWindow)
	 */
	public void enter(GameWindow window) {
		
		if (!SoundLoader.getInstance().isMusicPlaying()) {
			if (music == null) {
				try {
					init(window);
				} catch (IOException e) {
				}
			}
			music.playAsLoopMusic(1f, 1f);
		}

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GLU.gluPerspective(45.0f, ((float) 800) / ((float) 600), 0.1f, 500.0f);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		
		if (ManageStatistics.getAllPlayers().size() == 0){
			window.changeToState(PlayerProfileState.NAME);
		}
		
	}

	/**
	 * @see zgame.states.GameState#getName()
	 */
	public String getName() {
		return NAME;
	}

	protected void renderMenu(GameWindow window) {
		window.enterOrtho();

		 StaticRenderTools.setOrangeColor();
		 StaticRenderTools.getFont256().drawString(0, "version 0.6.2",
		 420,
		 40);

		
		
		for (int i = 0; i < options.length; i++) {
			GL11.glColor3f(1f, 0.59f, 0);
			if (selected == i) {
				GL11.glColor3f(1f, 1f, 1f);
			}
			String player = "";
			if (options[i] == options[PROFILES]) {
				PlayerInfo info = ManageStatistics.getLastPlayer();
				if (info != null) {
					player = ": " +info.getName();
				}else{
					noActivePlayer = true;
				}
			}
			StaticRenderTools.getFont512().drawString(0,
					options[i] +  player, 50, 300 + (i * 40));
		}

		window.leaveOrtho();
	}

	protected void updateKeyboard(GameWindow window) {
		if (noActivePlayer){
			//TODO:
		}
		
		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState()) {
				try {
					SoundLoader.getInstance().getOgg(ZSounds.CLICK)
							.play(1f, 1f);
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_UP) {

					selected--;
					if (selected < 0) {
						selected = options.length - 1;
					}
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_DOWN) {
					selected++;
					if (selected >= options.length) {
						selected = 0;
					}
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_RETURN
						|| Keyboard.getEventKey() == Keyboard.KEY_SPACE) {
					if (selected == START) {
						window.changeToState(LevelPackChooserState.NAME);

					}
					if (selected == PROFILES) {
						window.changeToState(PlayerProfileState.NAME);
					}
					if (selected == OPTIONS) {
						window.changeToState(OptionsState.NAME);
					}
					if (selected == EXIT) {
						ManageStatistics.SaveStats();
						System.exit(0);
					}
					if (selected == ABOUT) {
						window.changeToState(AboutState.NAME);
					}
				}
			}
		}
	}

}
