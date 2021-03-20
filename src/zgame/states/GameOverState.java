package zgame.states;

import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import ZGameStatistic.ManageStatistics;
import ZGameStatistic.DataModel.PlayerInfo;
import ZGameStatistic.DataModel.PlayerResault;
import ZXMLUtils.GamePackReader;

import zgame.GameWindow;
import zgame.StaticRenderTools;
import zgame.Status;
import zgame.ZSounds;
import zgame.sound.SoundLoader;
import zgame.texture.Texture;
import zgame.texture.TextureLoader;

/**
 * 
 * @author zchira
 */
public class GameOverState implements GameState {
	public static final String NAME = "gameover";

	/** The texture to display in the background */
	private Texture background;
	// /** The font to draw to the screen with */
	// private BitmapFont font;

	private long score;

	@Override
	public void enter(GameWindow window) {
		score = Status.getPoints();
		Status.addPoints(Status.getLives() * 10);
		Status.addPoints((Status.getLevel()-1) * 10);

		int levelAchieved = 0;

		if (Status.getCurrentLevel() == null) {
			Status.addPoints(100);
			levelAchieved = Status.getGamePack().getLevelCount();
		} else {
			levelAchieved = Status.getLevel();
		}
		// System.out.println("Level achieved: " + levelAchieved);

		PlayerInfo player = ManageStatistics.getLastPlayer();
		ManageStatistics.addPlayerResults(player.getPlayerId(), Status
				.getGamePack().getPackName(), levelAchieved, (int) Status
				.getPoints());
		try {
			SoundLoader.getInstance().getOgg(ZSounds.GAME_OVER_STATE_MUSIC)
					.playAsMusic(1, 1);
		} catch (IOException e) {
			System.err.println("Unable to load sound:"
					+ ZSounds.GAME_OVER_STATE_MUSIC);
		}

	}

	private void renderLevelPackScores() {
		
			GamePackReader levelPack = Status.getGamePack();
			ArrayList<PlayerResault> results = ManageStatistics.getTopResults(
					5, levelPack.getPackName());

			StaticRenderTools.drawGrayQuad(300, 350, 450, 200);
			StaticRenderTools.setOrangeColor();
			StaticRenderTools.getFont512().drawString(1, "High scores", 310,
					360);

			int activePlayerId = ManageStatistics.getLastPlayer().getPlayerId();
			long score = Status.getPoints();
			
			StaticRenderTools.setWhiteColor();
			int index = 1;
			String resultString = "";
			for (PlayerResault result : results) {
				resultString += Integer.toString(index) + ". ";
				resultString += result.getName();
				StaticRenderTools.setWhiteColor();
				
				if (result.getPlayerId() == activePlayerId && result.getPoints() == (int) score){
					StaticRenderTools.setOrangeColor();
					StaticRenderTools.getFont256().drawString(1, ">", 300,
							380 + index * 20);
				}
				StaticRenderTools.getFont256().drawString(1, resultString, 310,
						380 + index * 20);
				StaticRenderTools.getFont256().drawString(1,
						Integer.toString(result.getPoints()), 680,
						380 + index * 20);
				index++;
				resultString = "";
			}
	
	}
	
	
	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void init(GameWindow window) throws IOException {
		loadTextures();
	}

	@Override
	public void leave(GameWindow window) {
		SoundLoader.getInstance().stopMusic();
		ManageStatistics.SaveStats();
	}

	@Override
	public void render(GameWindow window, int delta) {
		GL11.glColor3f(0.2f, 0.2f, 0.3f);
		drawBackground(window);

		window.enterOrtho();
		int step = 35;
		GL11.glColor3f(1f, 0.59f, 0f);

		StaticRenderTools.getFont512().drawString(0, "Score: " + score, 100,
				100 + step);

		StaticRenderTools.getFont512().drawString(
				0,
				"Level achieved: " + Status.getLevel() + "  (" + (Status.getLevel() - 1) * 10
						+ " bonus points)", 100, 100 + 2 * step);

		if (Status.getLives() > 0) {
			StaticRenderTools.getFont512().drawString(
					0,
					"Lives left: " + Status.getLives() + "  ("
							+ Status.getLives() * 10 + " bonus points)", 100,
					100 + 3 * step);
		}
		if (Status.getCurrentLevel() == null) {
			StaticRenderTools.getFont512().drawString(0,
					"GamePack completed bonus: 100", 100, 100 + 4 * step);
		}

		GL11.glColor3f(1f, 1f, 1f);
		StaticRenderTools.getFont512().drawString(1, "GAME OVER", 50, 50);

		StaticRenderTools.getFont512().drawString(0,
				"TOTAL: " + Status.getPoints(), 100, 100 + 5 * step);

		StaticRenderTools.getFont512().drawString(1, "Press space to continue",
				30, 550);
		// for (int i=0;i<options.length;i++) {
		// GL11.glColor3f(0.5f,0.5f,0);
		// if (selected == i) {
		// GL11.glColor3f(1,1,0.3f);
		// }
		// font.drawString(0, options[i], 270, 280+(i*40));
		// }
		renderLevelPackScores();
		
		
		window.leaveOrtho();
	}

	/**
	 * Draw a background to the window
	 * 
	 * @param window
	 *            The window to which the background should be drawn
	 */
	private void drawBackground(GameWindow window) {
		window.enterOrtho();

		background.bind();

		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex2i(0, 0);
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex2i(0, 600);
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex2i(800, 600);
		GL11.glTexCoord2f(1, 0);
		GL11.glVertex2i(800, 0);
		GL11.glEnd();

		window.leaveOrtho();
	}

	@Override
	public void update(GameWindow window, int delta) {
		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState()) {
				if (Keyboard.getEventKey() == Keyboard.KEY_RETURN
						|| Keyboard.getEventKey() == Keyboard.KEY_SPACE) {
					// setujemo musin na null
					// da bi se selektovala nova random menu-melodija
					MenuState.music = null;
					window.changeToState(MenuState.NAME);// InGameState.NAME);
				}
			}
		}

	}

	@Override
	public void loadTextures() throws IOException {
		background = TextureLoader.getInstance().getTexture(
				"res/textures/bg.jpg");
	}

}
