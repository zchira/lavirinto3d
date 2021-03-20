package zgame.states;

import java.io.IOException;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import zgame.GameWindow;
import zgame.StaticRenderTools;
import zgame.ZSounds;
import zgame.gui.BitmapFont;
import zgame.sound.Sound;
import zgame.sound.SoundLoader;
import zgame.texture.Texture;
import zgame.texture.TextureLoader;

/**
 * 
 * @author zchira
 */
public abstract class AbstractStoryState implements GameState {

	public static final String NAME = "story";

	private Texture background;

	private String[] description;

	private Sound clickSound;

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
	public void enter(GameWindow window) {

//		titleString = Status.getGamePack().getPackName();

		try {
			background = TextureLoader.getInstance().getTexture(
					"res/textures/bg.jpg");
//			Texture fontTexture = TextureLoader.getInstance().getTexture(
//					"res/textures/font.png");
//			font = new BitmapFont(fontTexture, 32, 32);
		} catch (IOException e) {
			e.printStackTrace();
		}
		description = splitText(getPackDescription());

	}

	protected String[] splitText(String text) {
		String[] toReturn = text.trim().split("\n");
		return toReturn;
	}

	protected BitmapFont getDescriptionFont(){
		return StaticRenderTools.getFont512();
	}

	protected abstract String getPackDescription();

	protected abstract String getTargetState();

	protected abstract String getTitleString();
	
	

	@Override
	public void init(GameWindow window) throws IOException {
		clickSound = SoundLoader.getInstance().getOgg(ZSounds.CLICK);

	}

	protected abstract boolean isDrawPackName();
	
	@Override
	public void leave(GameWindow window) {
		background = null;
		description = null;
//		font = null;
	}
	
	@Override
	public void render(GameWindow window, int delta) {
		GL11.glColor3f(0.9f, 0.9f, 0.9f);
		drawBackground(window);

		window.enterOrtho();

		int step = 20;
		GL11.glColor3f(1f, 0.59f, 0);
		for (int i = 0; i < description.length; i++) {

			getDescriptionFont().drawString(1, description[i], 30, 95 + 2 * i * step);
		}
		GL11.glColor3f(1f, 1f, 1f);

		if (isDrawPackName()) {
			getDescriptionFont().drawString(1, getTitleString(), 30, 30);
		}
		getDescriptionFont().drawString(1, "Press space to continue", 30, 550);

		window.leaveOrtho();
	}

	@Override
	public void update(GameWindow window, int delta) {
		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState()) {
				if (Keyboard.getEventKey() == Keyboard.KEY_RETURN || 
						Keyboard.getEventKey() == Keyboard.KEY_SPACE) {
					clickSound.play(1, 1);
					window.changeToState(getTargetState());// InGameState.NAME);
				}
			}
		}

	}
	
	@Override
	public void loadTextures() throws IOException {
		// TODO Auto-generated method stub
		
	}
}
