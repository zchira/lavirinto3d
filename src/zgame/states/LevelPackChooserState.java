package zgame.states;

import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import zgame.GameWindow;
import zgame.StaticRenderTools;
import zgame.Status;
import zgame.ZSounds;
import zgame.sound.SoundLoader;
import ZGameStatistic.ManageStatistics;
import ZGameStatistic.DataModel.PlayerResault;
import ZXMLUtils.GamePackReader;
import ZXMLUtils.LevelPacksReader;

/**
 * 
 * @author zchira
 */
public class LevelPackChooserState extends AbstractMenuState {
	/** The game unique name of this state */
	public static final String NAME = "packchooser";

	private LevelPacksReader reader;

	// private void drawGrayQuad(float x, float y, float w, float h) {
	// GL11.glBegin(GL11.GL_QUADS);
	// GL11.glVertex2d(x, y);
	// GL11.glVertex2d(x, y + h);
	// GL11.glVertex2d(x + w, y + h);
	// GL11.glVertex2d(x + w, y);
	// GL11.glEnd();
	// }

	private void drawLevelPackDetails() {

		if (selected != reader.getGamePacksCount()) {
			GL11.glEnable(GL11.GL_BLEND);
			// clear texture
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
			GL11.glColor4f(0.2f, 0.2f, 0.2f, 0.6f);
			// score
			StaticRenderTools.drawGrayQuad(300, 150, 450, 220);
			GL11.glDisable(GL11.GL_BLEND);

			StaticRenderTools.setOrangeColor();
			// GL11.glColor3f(1f, 0.59f, 0);

			StaticRenderTools.getFont512().drawString(0, "Levels:", 310, 150);

			StaticRenderTools.getFont512().drawString(0, "Description: ", 310,
					180);

			StaticRenderTools.setWhiteColor();// GL11.glColor3f(1f, 1f, 1f);

			GamePackReader levelPack = reader.getGamePack(selected);

			StaticRenderTools.getFont512().drawString(0,
					Integer.toString(levelPack.getLevelCount()), 430, 150);

			StaticRenderTools.getFont256().drawString(0,
					levelPack.getPackDescription(), 310, 220);
		}
	}

	private void renderLevelPackScores() {
		if (selected != reader.getGamePacksCount()) {
			GamePackReader levelPack = reader.getGamePack(selected);
			ArrayList<PlayerResault> results = ManageStatistics.getTopResults(
					5, levelPack.getPackName());

			StaticRenderTools.drawGrayQuad(300, 380, 450, 200);
			StaticRenderTools.setOrangeColor();
			StaticRenderTools.getFont512().drawString(1, "High scores", 310,
					390);

			StaticRenderTools.setWhiteColor();
			int index = 1;
			String resultString = "";
			for (PlayerResault result : results) {
				resultString += Integer.toString(index) + ". ";
				resultString += result.getName();

				StaticRenderTools.getFont256().drawString(1, resultString, 310,
						410 + index * 20);
				StaticRenderTools.getFont256().drawString(1,
						Integer.toString(result.getPoints()), 680,
						410 + index * 20);
				index++;
				resultString = "";
			}
		}
	}

	@Override
	public void enter(GameWindow window) {
		// StaticRenderTools.setFullscreenMode(true);
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void init(GameWindow window) throws IOException {
		super.init(window);

		reader = new LevelPacksReader("/res/levelpacks.xml");

		int numOfPacks = reader.getGamePacksCount();

		options = new String[numOfPacks + 1];

		for (int i = 0; i < numOfPacks; i++) {
			options[i] = reader.getGamePackName(i);
		}

		options[numOfPacks] = "...back";

		scaleLogo = 0.4f;

	}

	protected void renderMenu(GameWindow window) {
		window.enterOrtho();

		StaticRenderTools.getFont512().drawString(0, "Select level pack", 50,
				90);

		for (int i = 0; i < options.length; i++) {
			GL11.glColor3f(1f, 0.59f, 0);
			if (selected == i) {
				GL11.glColor3f(1f, 1f, 1f);
			}
			StaticRenderTools.getFont512().drawString(0, options[i], 50,
					150 + (i * 40));
		}

		drawLevelPackDetails();
		renderLevelPackScores();
		window.leaveOrtho();
	}

	@Override
	public void update(GameWindow window, int delta) {
		super.update(window, delta);

	}

	@Override
	protected void updateKeyboard(GameWindow window) {
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

					if (selected == reader.getGamePacksCount()) {
						window.changeToState(MenuState.NAME);
					} else {
						GamePackReader gamePackReader = reader
								.getGamePack(selected);
						Status.setGamePack(gamePackReader);
						// window.changeToState(IntroStoryState.NAME);
						window.changeToState(LevelChooserState.NAME);
					}

				}

				if (Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
					window.changeToState(MenuState.NAME);
				}
			}
		}
	}

}
