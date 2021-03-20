package zgame;

import java.io.IOException;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;

import ZGameStatistic.ZSettings;
import zgame.gui.BitmapFont;
import zgame.texture.Texture;
import zgame.texture.TextureLoader;

public class StaticRenderTools {

	public static boolean fullDetails = true;
	
	public enum DisplayModes {
		_640X480, _800X600, _1024X768, _1280X1024
	}

	private static BitmapFont font256;

	private static BitmapFont font512;

	public static void drawGrayQuad(float x, float y, float w, float h) {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL11.glColor4f(0.2f, 0.2f, 0.2f, 0.6f);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex2d(x, y);
		GL11.glVertex2d(x, y + h);
		GL11.glVertex2d(x + w, y + h);
		GL11.glVertex2d(x + w, y);
		GL11.glEnd();
		GL11.glDisable(GL11.GL_BLEND);
	}

	public static void drawQuad(float x, float y, float w, float h) {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex2d(x, y);
		GL11.glVertex2d(x, y + h);
		GL11.glVertex2d(x + w, y + h);
		GL11.glVertex2d(x + w, y);
		GL11.glEnd();
		GL11.glDisable(GL11.GL_BLEND);
	}

	public static void drawShadow(float x, float y, float z, float w, float h) {
		if (!fullDetails) return;
		try {
						
			TextureLoader.getInstance().getTexture("res/textures/shadow.png")
					.bind();
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glColor3f(1, 1, 1);
			GL11.glPushMatrix();
			GL11.glTranslatef(x - w / 2, y - h / 2, z);
			GL11.glBegin(GL11.GL_QUADS);

			GL11.glTexCoord2f(0, 0);
			GL11.glVertex2d(0, 0);

			GL11.glTexCoord2f(1, 0);
			GL11.glVertex2d(0 + w, 0);

			GL11.glTexCoord2f(1, 1);
			GL11.glVertex2d(0 + w, 0 + h);

			GL11.glTexCoord2f(0, 1);
			GL11.glVertex2d(0, 0 + h);

			GL11.glEnd();

			GL11.glPopMatrix();
			GL11.glEnable(GL11.GL_LIGHTING);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static BitmapFont getFont512() {
		if (font512 == null) {
			Texture fontTexture512;
			try {
				fontTexture512 = TextureLoader.getInstance().getTexture(
						"res/textures/font.png");
				font512 = new BitmapFont(fontTexture512, 32, 32);
			} catch (IOException e) {
				System.err.println("Font texture not found!");
				return null;
			}
		}
		return font512;
	}

	public static BitmapFont getFont256() {
		if (font256 == null) {
			Texture fontTexture256;
			try {
				fontTexture256 = TextureLoader.getInstance().getTexture(
						"res/textures/font256.png");
				font256 = new BitmapFont(fontTexture256, 16, 16);
			} catch (IOException e) {
				System.err.println("Font texture not found!");
				return null;
			}
		}
		return font256;
	}

	public static void setOrangeColor() {
		GL11.glColor3f(1f, 0.59f, 0);
	}

	public static void setWhiteColor() {
		GL11.glColor3f(1f, 1f, 1f);
	}

	public static String[] splitText(String text) {
		String[] toReturn = text.trim().split("\n");
		return toReturn;
	}

	public static void setDisplayMode(DisplayModes dspMode, GameWindow window) {
		try {

			if (dspMode == null) {
				dspMode = DisplayModes._1024X768;
			}
			int currentBpp = Display.getDisplayMode().getBitsPerPixel();
			DisplayMode mode = null;// findDisplayMode(800, 600, currentBpp);
			switch (dspMode) {
			case _640X480:
				mode = findDisplayMode(640, 480, currentBpp);
				break;
			case _800X600:
				mode = findDisplayMode(800, 600, currentBpp);
				break;
			case _1024X768:
				mode = findDisplayMode(1024, 768, currentBpp);
				break;
			case _1280X1024:
				mode = findDisplayMode(1280, 1024, currentBpp);
				break;
			}

			// if can't find a mode, notify the user the give up
			if (mode == null) {
				Sys.alert("Error", dspMode.toString() + "x" + currentBpp
						+ " display mode unavailable");
				return;
			}

			// ali privremeno stoji ovo zbog problema sa
			// color depth na linuxu kad je povezan TV
			if (!Display.isCreated()) {
				Display.setDisplayMode(mode);				
				Display.setFullscreen(ZSettings.getSettings().isFullScreen());
				Display.create(new PixelFormat(8,8,8));
			} else {
				Display.destroy();
				Display.setDisplayMode(mode);

				Display.create(new PixelFormat(8,8,8));
				TextureLoader.getInstance().clearAllTextures();
			}
			init();
			window.reloadTextures();
			SceneRenderer.resetAll();
			font256 = null;
			font512 = null;
		} catch (LWJGLException e) {
			e.printStackTrace();
			Sys.alert("Error", "Failed: " + e.getMessage());
		}

	}

	public static void setFullscreenMode(boolean a) {

		DisplayMode mode = Display.getDisplayMode();
		try {
			Display.setDisplayMode(mode);
			Display.setFullscreen(a);
		} catch (LWJGLException e) {
			System.err.println("Unable to switsh to fullscreen mode");
			e.printStackTrace();

		}
	}

	/**
	 * Determine an available display that matches the specified paramaters.
	 * 
	 * @param width
	 *            The desired width of the screen
	 * @param height
	 *            The desired height of the screen
	 * @param bpp
	 *            The desired colour depth (bits per pixel) of the screen
	 * @return The display mode matching the requirements or null if none could
	 *         be found
	 * @throws LWJGLException
	 *             Indicates a failure interacting with the LWJGL library.
	 */
	private static DisplayMode findDisplayMode(int width, int height, int bpp)
			throws LWJGLException {
		DisplayMode[] modes = Display.getAvailableDisplayModes();
		DisplayMode mode = null;

		for (int i = 0; i < modes.length; i++) {
			if ((modes[i].getBitsPerPixel() == bpp) || (mode == null)) {
				if ((modes[i].getWidth() == width)
						&& (modes[i].getHeight() == height)) {
					mode = modes[i];
				}
			}
		}

		return mode;
	}

	public static void init() {
		// run through some based OpenGL capability settings. Textures
		// enabled, back face culling enabled, depth texting is on,
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_DEPTH_TEST);

		GL11.glDepthFunc(GL11.GL_LEQUAL);
		GL11.glShadeModel(GL11.GL_SMOOTH);

		// define the properties for the perspective of the scene
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GLU.gluPerspective(45.0f, ((float) 800) / ((float) 600), 0.1f, 500.0f);

		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
		GL11.glEnable(GL11.GL_COLOR_MATERIAL);
		
//		GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE);
//		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GL11.glTexGeni(GL11.GL_S, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_SPHERE_MAP); // Set
		GL11.glTexGeni(GL11.GL_T, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_SPHERE_MAP);
		
		GL11.glClearStencil(0);
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		
	}
}
