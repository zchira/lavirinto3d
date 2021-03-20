package zgame.states;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import zgame.GameWindow;
import zgame.SceneRenderer;
import zgame.ZSounds;
import zgame.sound.Sound;
import zgame.sound.SoundLoader;
import zgame.texture.Texture;
import zgame.texture.TextureLoader;

public abstract class AbstractMenuState implements GameState {
	protected abstract void updateKeyboard(GameWindow window);

	protected abstract void renderMenu(GameWindow window);

	/** The texture to display in the background */
	protected Texture background;

	protected Texture logoTexture;

	/** The index of the option selected */
	protected int selected = 0;

	/** The options to present to the user */
	protected String[] options;

	protected static Sound music;
	
	protected float scaleLogo = 1f;

	/**
	 * @see zgame.states.GameState#update(zgame.GameWindow, int)
	 */
	public void update(GameWindow window, int delta) {
		SceneRenderer.update(window, delta);

		updateKeyboard(window);
	}

	@Override
	public void loadTextures() throws IOException {
		background = TextureLoader.getInstance().getTexture(
				"res/textures/bg.jpg");
		logoTexture = TextureLoader.getInstance().getTexture(
				"res/textures/logo.png");
	}

	/**
	 * @see zgame.states.GameState#render(zgame.GameWindow, int)
	 */
	public void render(GameWindow window, int delta) {
		GL11.glEnable(GL11.GL_BLEND);
		drawBackground(window);
		//
		GL11.glLoadIdentity();
		// display();
		//
		// GL11.glRotatef(-70, 1, 0, 0);
		// particleEngine.render();
		// GL11.glRotatef(70, 1, 0, 0);
		// drawPlane();
		// GL11.glColor3f(1f, 1f, 1f);

		SceneRenderer.renderMenuScene(window);

		GL11.glColor3f(1f, 1f, 1f);
		renderMenu(window);
		GL11.glColor3f(1f, 1f, 1f);
		drawLogo(window);
	}

	private void drawLogo(GameWindow window) {
		window.enterOrtho();
		GL11.glEnable(GL11.GL_BLEND);
		logoTexture.bind();
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex2i(0, 0);
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex2f(0, 250 * scaleLogo);
		GL11.glTexCoord2f(1, 0);
		GL11.glVertex2f(1000* scaleLogo, 250* scaleLogo);
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex2f(1000* scaleLogo, 0);
		GL11.glEnd();
		GL11.glDisable(GL11.GL_BLEND);
		window.leaveOrtho();
	}

	/**
	 * Draw a background to the window
	 * 
	 * @param window
	 *            The window to which the background should be drawn
	 */
	private void drawBackground(GameWindow window) {
		GL11.glColor3f(1f, 1f, 1f);
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

	/**
	 * @see zgame.states.GameState#init(zgame.GameWindow)
	 */
	public void init(GameWindow window) throws IOException {
		loadTextures();

//		Random randomator = new Random();
//
//		int random = randomator.nextInt(3);
		String theme = "";
//		switch (random) {
//		case 0:
//			theme = ZSounds.MENU_MUSIC;
//			break;
//		case 1:
//			theme = ZSounds.LEVEL_PACK_COMPLETED_MUSIC;
//			break;
//		case 2:
//			theme = ZSounds.GAME_OVER_STATE_MUSIC;
//			break;
//
//		default:
//			theme = ZSounds.MENU_MUSIC;
//			break;
//		}

		
		if (music == null) {
			theme = ZSounds.MENU_MUSIC;
			music = SoundLoader.getInstance().getOgg(theme);
		}
	}

	/**
	 * @see zgame.states.GameState#leave(zgame.GameWindow)
	 */
	public void leave(GameWindow window) {
	}
}
