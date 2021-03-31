package zgame.states;

import java.io.IOException;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import ZGameStatistic.ManageStatistics;
import ZGameStatistic.DataModel.PlayerInfo;

import zgame.GameWindow;
import zgame.StaticRenderTools;
import zgame.Status;
import zgame.ZSounds;
import zgame.sound.SoundLoader;
import zgame.texture.Texture;
import zgame.texture.TextureLoader;

public class LevelChooserState implements GameState {
	public static final String NAME = "levelchooser";

	private final static int maxLevelsInRow = 5;

	private int selected;

	private int levelsCompleted;

	private float deltaX = 50f;

	private float deltaY = 150f;

	private Texture background;

	@Override
	public void enter(GameWindow window) {
		selected = 0;
		PlayerInfo activePlayer = ManageStatistics.getLastPlayer();

		levelsCompleted = ManageStatistics.getAchievedLeve(activePlayer.getPlayerId(),
				Status.getGamePack().getPackName());

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

	}

	@Override
	public void render(GameWindow window, int delta) {
		GL11.glColor3f(0.9f, 0.9f, 0.9f);
		drawBackground(window);

		window.enterOrtho();

		StaticRenderTools.getFont512().drawString(0, Status.getGamePack().getPackName(), 50, 20);
		StaticRenderTools.getFont512().drawString(0, "Select level", 50, 70);

		StaticRenderTools.getFont512().drawString(1, "Press space to continue", 30, 530);
		StaticRenderTools.getFont256().drawString(1, "Press esc to go back", 30, 560);

		int descX = 330;

		StaticRenderTools.drawGrayQuad(descX - 10, deltaY, 420, 300);

		StaticRenderTools.setOrangeColor();
		StaticRenderTools.getFont256().drawString(0, "Level: ", descX, (int) deltaY + 10);
		StaticRenderTools.getFont256().drawString(0, "Description: ", descX, (int) deltaY + 40);

		StaticRenderTools.setWhiteColor();
		StaticRenderTools.getFont256().drawString(0, Status.getGamePack().getLevel(selected).getLevelName(), descX + 70,
				(int) deltaY + 10);

		String[] desc = StaticRenderTools.splitText(Status.getGamePack().getLevel(selected).getLevelDescription());

		for (int i = 0; i < desc.length; i++) {
			StaticRenderTools.getFont256().drawString(0, desc[i], descX, (int) deltaY + 70 + i * 20);
		}

		int row = 0;
		int col = 0;
		int levelCount = Status.getGamePack().getLevelCount();
		if (levelCount < Integer.MAX_VALUE) {
			for (int i = 0; i < Status.getGamePack().getLevelCount(); i++) {
				drawLevelButton(col, row, i + 1);
				col++;

				if (col == maxLevelsInRow) {
					col = 0;
					row++;
				}
			}
		} else {
			drawLevelButton(0, 0, 1);
			if (levelsCompleted > 1) {
				drawLevelButton(1, 0, levelsCompleted);
			}

		}

		window.leaveOrtho();
	}

	private void drawLevelButton(int col, int row, int level) {
		String lvl = Integer.toString(level);
		if (level < 10) {
			lvl = " " + lvl;
		}

		float x = deltaX + col * 50;
		float y = deltaY + row * 50;

		if (level != selected + 1) {
			GL11.glColor4f(0.2f, 0.2f, 0.2f, 0.1f);
			StaticRenderTools.drawQuad(x, y, 35, 30);
			if (level > levelsCompleted) {
				GL11.glColor3f(0.2f, 0.2f, 0.2f);
			} else {
				StaticRenderTools.setOrangeColor();
			}
		} else {
			GL11.glColor4f(0.2f, 0.2f, 0.2f, 0.8f);
			StaticRenderTools.drawGrayQuad(x, y, 35, 30);
			StaticRenderTools.setWhiteColor();
		}
		StaticRenderTools.getFont512().drawString(1, lvl, (int) x, (int) y);

	}

	@Override
	public void update(GameWindow window, int delta) {
		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState()) {
				try {
					SoundLoader.getInstance().getOgg(ZSounds.CLICK).play(1f, 1f);
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (levelsCompleted != 0) {
					int levelCount = Status.getGamePack().getLevelCount();
					if (levelCount < Integer.MAX_VALUE) {
						if (Keyboard.getEventKey() == Keyboard.KEY_DOWN) {
							selected += maxLevelsInRow;
							if (selected >= levelsCompleted) {
								selected = levelsCompleted - 1;
							}
						}
						if (Keyboard.getEventKey() == Keyboard.KEY_UP) {
							selected -= maxLevelsInRow;
							if (selected < 0) {
								selected = 0;
							}
						}
						if (Keyboard.getEventKey() == Keyboard.KEY_RIGHT) {
							selected++;
							if (selected >= levelsCompleted) {
								selected = 0;
							}
						}
						if (Keyboard.getEventKey() == Keyboard.KEY_LEFT) {
							selected--;
							if (selected < 0) {
								selected = levelsCompleted - 1;
							}
						}
					} else {
						if (Keyboard.getEventKey() == Keyboard.KEY_DOWN ||
							Keyboard.getEventKey() == Keyboard.KEY_UP ||
							Keyboard.getEventKey() == Keyboard.KEY_LEFT ||
							Keyboard.getEventKey() == Keyboard.KEY_RIGHT) {
							if (selected == 0) {
								selected = levelsCompleted - 1;
							} else {
								selected = 0;
							}
						}
					}

				}
				if (Keyboard.getEventKey() == Keyboard.KEY_RETURN || Keyboard.getEventKey() == Keyboard.KEY_SPACE) {

					Status.setCurrentLevel(selected);
					window.changeToState(IntroStoryState.NAME);

				}
				if (Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
					window.changeToState(LevelPackChooserState.NAME);
				}
			}
		}
	}

	private void drawBackground(GameWindow window) {
		window.enterOrtho();
		background.bind();
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex2i(0, 0);
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex2i(0, 600);
		GL11.glTexCoord2f(1, 0);
		GL11.glVertex2i(800, 600);
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex2i(800, 0);
		GL11.glEnd();
		window.leaveOrtho();
	}

	@Override
	public void loadTextures() throws IOException {
		background = TextureLoader.getInstance().getTexture("res/textures/bg.jpg");

	}

}
