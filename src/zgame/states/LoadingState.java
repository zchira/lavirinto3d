package zgame.states;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import zgame.GameWindow;
import zgame.StaticRenderTools;

public class LoadingState implements GameState {
	public static final String NAME = "loading";

	@Override
	public void enter(GameWindow window) {

	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void init(GameWindow window) throws IOException {

	}

	@Override
	public void leave(GameWindow window) {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(GameWindow window, int delta) {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glColor3f(0f, 0f, 0f);

		window.enterOrtho();

		StaticRenderTools.setOrangeColor();
		StaticRenderTools.getFont512().drawString(0,
				"Lavrinto3D", 10, 10);
		
		StaticRenderTools.setWhiteColor();
		StaticRenderTools.getFont256().drawString(0,
				"Loading...  " + delta + "%", 640, 580);
		window.leaveOrtho();

	}

	@Override
	public void update(GameWindow window, int delta) {
		window.initStates();
		window.changeToState(MenuState.NAME);

	}

	@Override
	public void loadTextures() throws IOException {
		
	}

}
