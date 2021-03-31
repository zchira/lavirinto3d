package zgame;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import zgame.sound.SoundLoader;
import zgame.states.AboutState;
import zgame.states.GameOverState;
import zgame.states.GameState;
import zgame.states.InGameState;
import zgame.states.IntroStoryState;
import zgame.states.LevelChooserState;
import zgame.states.LevelInfoState;
import zgame.states.LevelPackChooserState;
import zgame.states.LoadingState;
import zgame.states.MenuState;
import zgame.states.OptionsState;
import zgame.states.OutroStoryState;
import zgame.states.PlayerProfileState;
import ZGameStatistic.ManageStatistics;
import ZGameStatistic.ZSettings;

/**
 * A window to display the game in LWJGL.
 * 
 * java -Djava.library.path=libs/native -jar yourgame.jar
 * 
 * @author zchira
 * @author Kevin Glass
 */
public class GameWindow {
	/** The list of game states currently registered */
	private HashMap<String, GameState> gameStates = new HashMap<String, GameState>();
	/** The current state being rendered */
	private GameState currentState;

	/**
	 * Create a new game window
	 */
	public GameWindow() {
		// sets icon to the window
		setIcon();
		Display.setTitle("Lavirinto 3D: Zen");

		//Loading settings
		
		StaticRenderTools.setDisplayMode(ZSettings.getSettings().getMode(),
				this);
		SoundLoader.getInstance().setSoundVolume(
				(float) ZSettings.getSettings().getSoundVolume() / 10f);

		SoundLoader.getInstance().setMusicVolume(
				(float) ZSettings.getSettings().getMusicVolume() / 10f);
		
		StaticRenderTools.fullDetails = ZSettings.getSettings().isFullDetails();

		loadSomeTexturesInCache();
		// initialise the game states
		init();
	}

	/**
	 * Start the game
	 */
	public void startGame() {
		// enter the game loop
		gameLoop();
	}

	/**
	 * Get the current time in milliseconds based on the LWJGL high res system
	 * clock.
	 * 
	 * @return The time in milliseconds based on the LWJGL high res clock
	 */
	private long getTime() {
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}

	/**
	 * Add a game state to this window. This state can be used via its unique
	 * name.
	 * 
	 * @param state
	 *            The state to be added
	 * @see GameState.getName()
	 */
	public void addState(GameState state) {
		if (currentState == null) {
			currentState = state;
		}

		gameStates.put(state.getName(), state);
	}

	/**
	 * Initialise the window and the resources used for the game
	 */
	public void init() {

		SoundLoader.getInstance().init();
		StaticRenderTools.init();
		// add the game states that build up our game, the menu
		// state allows starting of the game. The ingame state rendered
		// the asteroids and the player
		addState(new LoadingState());
		addState(new MenuState());
		addState(new LevelPackChooserState());
		addState(new AboutState());
		addState(new InGameState());

		addState(new GameOverState());
		// addState(new TestState());

		addState(new IntroStoryState());
		addState(new OutroStoryState());
		addState(new LevelInfoState());

		addState(new LevelChooserState());
		addState(new OptionsState());
		addState(new PlayerProfileState());
		// addState(new TestState());

	}

	public void initStates() {
		try {
			// initialse all the game states we've just created. This allows
			// them to load any resources they require
			Iterator<GameState> states = gameStates.values().iterator();

			// loop through all the states that have been registered
			// causing them to initialise
			int percent = 0;
			int total = gameStates.values().size();
			int step = 100 / total;
			while (states.hasNext()) {
				GameState state = (GameState) states.next();

				state.init(this);
				currentState.render(this, percent);
				Display.update();
				percent += step;
			}
		} catch (IOException e) {
			// if anything goes wrong, show an error message and then exit.
			// This is a bit abrupt but for the sake of this tutorial its
			// enough.
			Sys.alert("Error", "Unable to initialise state: " + e.getMessage());
			System.exit(0);
		}
	}

	public void reloadTextures() {
		try {
			Iterator<GameState> states = gameStates.values().iterator();

			// int percent = 0;
			// int total = gameStates.values().size();
			// int step = 100 / total;
			while (states.hasNext()) {
				GameState state = (GameState) states.next();

				state.loadTextures();

				// currentState.render(this, percent);
				// Display.update();
				// percent += step;
			}
		} catch (IOException e) {
			Sys.alert("Error", "Unable to reload textures");
			e.printStackTrace();
		}
	}

	/**
	 * The main game loop which is cycled rendering and updating the registered
	 * game states
	 */
	public void gameLoop() {
		boolean gameRunning = true;
		long lastLoop = getTime();

		currentState.enter(this);

		// while the game is running we loop round updating and rendering
		// the current game state
		while (gameRunning) {
			// calculate how long it was since we last came round this loop
			// and hold on to it so we can let the updating/rendering routine
			// know how much time to update by
			int delta = (int) (getTime() - lastLoop);
			lastLoop = getTime();

			// clear the screen and the buffer used to maintain the appearance
			// of depth in the 3D world (the depth buffer)
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT
					| GL11.GL_STENCIL_BUFFER_BIT);
			// cause the game state that we're currently running to update
			// based on the amount of time passed
			int remainder = delta % 10;
			int step = delta / 10;
			for (int i = 0; i < step; i++) {
				currentState.update(this, 10);
			}
			if (remainder != 0) {
				currentState.update(this, remainder);
			}

			// cause the game state that we're currently running to be
			// render
			currentState.render(this, delta);

			// finally tell the display to cause an update. We've now
			// rendered out scene we just want to get it on the screen
			// As a side effect LWJGL re-checks the keyboard, mouse and
			// controllers for us at this point
			Display.update();

			// if the user has requested that the window be closed, either
			// pressing CTRL-F4 on windows, or clicking the close button
			// on the window - then we want to stop the game
			if (Display.isCloseRequested()) {
				gameRunning = false;
				ManageStatistics.SaveStats();
				System.exit(0);
			}
		}
	}

	/**
	 * Change the current state being rendered and updated. Note if no state
	 * with the specified name can be found no action is taken.
	 * 
	 * @param name
	 *            The name of the state to change to.
	 */
	public void changeToState(String name) {
		GameState newState = (GameState) gameStates.get(name);
		if (newState == null) {
			return;
		}

		currentState.leave(this);
		currentState = newState;
		currentState.enter(this);
	}

	/**
	 * Enter the orthographic mode by first recording the current state, next
	 * changing us into orthographic projection.
	 */
	public void enterOrtho() {
		// store the current state of the renderer
		GL11.glPushAttrib(GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_ENABLE_BIT);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();

		// now enter orthographic projection
		GL11.glLoadIdentity();
		GL11.glOrtho(0, 800, 600, 0, -1, 1);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_LIGHTING);
	}

	/**
	 * Leave the orthographic mode by restoring the state we store in
	 * enterOrtho()
	 * 
	 * @see enterOrtho()
	 */
	public void leaveOrtho() {
		// restore the state of the renderer
		GL11.glPopMatrix();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPopMatrix();
		GL11.glPopAttrib();
	}

	private void loadSomeTexturesInCache() {
		// try {
		// TextureLoader.getInstance().getTexture(
		// "res/levelpacks/tutorial/background.png");
		// } catch (IOException e) {
		// System.out.println("Can't load resource.");
		// }
	}

	/**
	 * The entry point into our game. This method is called when you execute the
	 * program. Its simply responsible for creating the game window
	 * 
	 * @param argv
	 *            The command line arguments provided to the program
	 */
	public static void main(String argv[]) {
		GameWindow g = new GameWindow();
		g.startGame();
	}

	private void setIcon() {
		// try {
		// ByteBuffer image = TextureLoader.getInstance().getIconAsByteBuffer(
		// "res/textures/icon.png");
		//
		// Display.setIcon(new ByteBuffer[] { image });
		// } catch (IOException e) {
		// System.out.println("Using default icon...");
		// }
	}
}
