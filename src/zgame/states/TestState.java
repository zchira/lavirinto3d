	package zgame.states;

import java.io.IOException;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Sphere;

import zgame.GameWindow;
import zgame.texture.Texture;
import zgame.texture.TextureLoader;

/**
 * 
 * @author zchira
 */
public class TestState implements GameState {
	public static final String NAME = "Test";

	private Texture fieldTexture;

	/** The texture to display in the background */
	private Texture background;

	private Sphere sphere;

	/** The font to draw to the screen with */
//	private BitmapFont font;

	@Override
	public void enter(GameWindow window) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		return NAME;
	}

//	private float lightPosition[] = { 0.0f, 0.0f, 2.0f };
	FloatBuffer buffer;

	private void defineLight() {
		FloatBuffer buffer;

		buffer = BufferUtils.createFloatBuffer(4);
		buffer.put(1).put(1).put(1).put(1);
		buffer.flip();
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_AMBIENT, buffer);

		buffer = BufferUtils.createFloatBuffer(4);
		buffer.put(1).put(1).put(1).put(1);
		buffer.flip();
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, buffer);

		// setup the ambient light
		buffer = BufferUtils.createFloatBuffer(4);
		buffer.put(0.8f).put(0.8f).put(0.8f).put(0.8f);
		buffer.flip();
		GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, buffer);
		GL11.glLightModeli(GL11.GL_LIGHT_MODEL_TWO_SIDE, GL11.GL_TRUE);

		// set up the position of the light
		buffer = BufferUtils.createFloatBuffer(4);
		buffer.put(0).put(0).put(5).put(0);
		buffer.flip();
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, buffer);

		GL11.glEnable(GL11.GL_LIGHT0);

	}

	@Override
	public void init(GameWindow window) throws IOException {
		defineLight();
		background = TextureLoader.getInstance().getTexture(
				"res/textures/bg.jpg");
		fieldTexture = TextureLoader.getInstance().getTexture(
				"res/textures/field.jpg");

		sphere = new Sphere();
		sphere.setTextureFlag(true);

		buffer = BufferUtils.createFloatBuffer(4);
		buffer.put(0).put(0).put(2).put(0);
		buffer.flip();

		GL11.glTexGeni(GL11.GL_S, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_SPHERE_MAP); // Set
		// Up
		// Sphere
		// Mapping
		GL11.glTexGeni(GL11.GL_T, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_SPHERE_MAP); // Set
		// Up
		// Sphere
		// Mapping

	}

	@Override
	public void leave(GameWindow window) {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(GameWindow window, int delta) {
		GL11.glColor3f(0.2f, 0.7f, 0.7f);
		drawBackground(window);

		GL11.glLoadIdentity();

		// GL11.glTranslatef(0, 0, -50);
		// GL11.glRotatef(-45, 1, 0, 0);
		// // GL11.glColor3f(0.2f, 0.4f, 0.4f);
		//		
		//		
		// GL11.glTranslatef(0, 0, 5);
		// drawBall();
		// GL11.glTranslatef(0, 0, -5);
		//		
		// drawPlane();

		display();

	}

	private void display() {
//		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT
//				| GL11.GL_STENCIL_BUFFER_BIT);
//		double eqr[] = { 0.0f, 0.0f, 0.0f, 0.0f };
//		DoubleBuffer buffer;
//
//		buffer = BufferUtils.createDoubleBuffer(4);
//		buffer.put(0).put(0).put(0).put(0);
//		buffer.flip();

		GL11.glLoadIdentity(); // Reset The Modelview Matrix
		GL11.glTranslatef(0.0f, 0f, -50);
		GL11.glRotatef(-75, 1, 0, 0); // Zoom And Raise Camera Above The Floor
		// (Up 0.6 Units)
		GL11.glColorMask(false, false, false, false);

		GL11.glEnable(GL11.GL_STENCIL_TEST); // Enable Stencil Buffer For
		// "marking" The Floor
		GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 1); // Always Passes, 1 Bit
		// Plane, 1 As Mask
		GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);

		GL11.glDisable(GL11.GL_DEPTH_TEST); // Disable Depth Testing
		drawPlane();

		GL11.glEnable(GL11.GL_DEPTH_TEST); // Enable Depth Testing
		GL11.glColorMask(true, true, true, true); // Set Color Mask to TRUE,
		// TRUE, TRUE, TRUE
		GL11.glStencilFunc(GL11.GL_EQUAL, 1, 1);

		GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP); // Don't
		// Change
		// The
		// Stencil
		// Buffer
		GL11.glEnable(GL11.GL_CLIP_PLANE0); // Enable Clip Plane For Removing
		// Artifacts
		//		
//		GL11.glClipPlane(GL11.GL_CLIP_PLANE0, buffer);

		GL11.glPushMatrix(); // Push The Matrix Onto The Stack
		GL11.glScalef(1.0f, 1.0f, -1.0f); // Mirror Y Axis
//		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, this.buffer);

		GL11.glTranslatef(0.0f, 0, 3.0f);
		GL11.glRotatef(angle, 0, 0, -1);
		drawBall();
		GL11.glPopMatrix();

//		GL11.glDisable(GL11.GL_CLIP_PLANE0); // Disable Clip Plane For
		// Drawing The Floor
		GL11.glDisable(GL11.GL_STENCIL_TEST); // We Don't Need The Stencil
		// Buffer Any More (Disable)
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, this.buffer); // Set
		// Up
		// Light0
		// Position

		GL11.glEnable(GL11.GL_BLEND); // Enable Blending (Otherwise The
		// Reflected Object Wont Show)
		GL11.glDisable(GL11.GL_LIGHTING); // Since We Use Blending, We Disable
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, this.buffer); // Lighting
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.8f); // Set Color To White With 80%
		// Alpha
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA); // Blending

		drawPlane();

		GL11.glEnable(GL11.GL_LIGHTING); // Enable Lighting
		GL11.glDisable(GL11.GL_BLEND); // Disable Blending
		GL11.glTranslatef(0.0f, 0, 3.0f);
		GL11.glRotatef(angle, 0, 0, 1);
		drawBall();

	}

	private float angle = 0;

	private void drawBall() {
		GL11.glColor3f(1, 1, 1);
		fieldTexture.bind();

		sphere.draw(3, 16, 16);
	}

	private void drawPlane() {
		float dim = 15;

		// GL11.glPushMatrix();

		background.bind();

		// GL11.glColor3f(0.2f, 0.7f, 0.7f);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(1, 0);
		GL11.glVertex3f(dim, -dim, 0);
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex3f(dim, dim, 0);
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex3f(-dim, dim, 0);
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex3f(-dim, -dim, 0);
		GL11.glEnd();
	}

	/**
	 * Draw a background to the window
	 * 
	 * @param window
	 *            The window to which the background should be drawn
	 */
	private void drawBackground(GameWindow window) {
		window.enterOrtho();

		// background.bind();
		//
		// GL11.glBegin(GL11.GL_QUADS);
		// GL11.glTexCoord2f(0, 0);
		// GL11.glVertex2i(0, 0);
		// GL11.glTexCoord2f(0, 1);
		// GL11.glVertex2i(0, 600);
		// GL11.glTexCoord2f(1, 1);
		// GL11.glVertex2i(800, 600);
		// GL11.glTexCoord2f(1, 0);
		// GL11.glVertex2i(800, 0);
		// GL11.glEnd();

		window.leaveOrtho();
	}

	@Override
	public void update(GameWindow window, int delta) {
		// TODO Auto-generated method stub
		angle += delta / 50f;
	}

	@Override
	public void loadTextures() throws IOException {
		// TODO Auto-generated method stub
		
	}

}