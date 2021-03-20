package zgame;

import java.io.IOException;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Sphere;

import zentity.FieldEntity;
import zentity.ZCamera;
import zgame.model.ObjLoader;
import zgame.model.ObjModel;
import zgame.particles.AbstractParticleSystem;
import zgame.particles.SpriteExplodeParticleSystem;
import zgame.texture.Texture;
import zgame.texture.TextureLoader;

public class SceneRenderer {

	public static ZCamera camera;

	private static FloatBuffer material;

	private static boolean materialInitialized = false;

	public static float angle = 90;

	private static float zoomFactor = 1f;
	private static int zoomDirection = 1;

	private static int planeID;
	
	private static int planeMirrorID;

	private static float explodeTimeMax = 6000;
	private static float explodeTime = 0;

	private static AbstractParticleSystem particles;

	public static void renderMenuScene(GameWindow window) {
		// GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
		// drawBackground(window);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();

		defineMaterial();
		// display();
		getCamera().render();
		GL11.glEnable(GL11.GL_LIGHTING);
		defineLight();

		/**
		 * reflection
		 */
		GL11.glLoadIdentity();
		getCamera().render();
		GL11.glColorMask(false, false, false, false);

		GL11.glEnable(GL11.GL_STENCIL_TEST);
		GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 1);
		GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		drawPlaneMirror();
		StaticRenderTools.drawShadow(0, 0, 0.0001f, 2, 2);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glColorMask(true, true, true, true);
		GL11.glStencilFunc(GL11.GL_EQUAL, 1, 1);
		GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);

		GL11.glPushMatrix();
		GL11.glScalef(1.0f, 1.0f, -1.0f);
		// GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, this.buffer);

		GL11.glTranslatef(0.0f, 0, 0.0f);
		GL11.glRotatef(angle, 0, 0, -1);
		drawBall();
		drawBallR();
		getParticles().render();
		GL11.glPopMatrix();

		GL11.glDisable(GL11.GL_STENCIL_TEST);
		// GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, this.buffer);

		GL11.glEnable(GL11.GL_BLEND);

		GL11.glDisable(GL11.GL_LIGHTING);
		// GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, this.buffer); //
		// Lighting
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.8f);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA); // Blending

		drawPlaneMirror();
		
		drawPlane();
		StaticRenderTools.drawShadow(0, 0, 0.0001f, 2, 2);
		GL11.glEnable(GL11.GL_LIGHTING); // Enable Lighting
		GL11.glEnable(GL11.GL_BLEND); // Disable Blending
		drawBall();
		drawBallR();
		// drawPlane();
		getParticles().render();
		/**
		 * end reflection
		 */
		// GL11.glRotatef(-70, 1, 0, 0);
		// getParticles().render();
		// GL11.glRotatef(70, 1, 0, 0);
		// drawBall();
		// drawBallR();
		// drawPlane();
		//
		 StaticRenderTools.drawShadow(0, 0, 0.005f, 2, 2);
		GL11.glPopMatrix();
	}

	private static ZCamera getCamera() {
		if (camera == null) {
			camera = new ZCamera(null);
			camera.setEyeX(3);
			camera.setEyeY(1);
			camera.setEyeZ(2f);
			camera.setCenterX(-10);
			camera.setCenterY(-10);
			camera.setCenterZ(0);

		}
		return camera;
	}

	public static void update(GameWindow window, int delta) {
		angle += delta / 20f;

		getParticles().update(delta);

		zoomFactor += (float) (delta) / 5000f * zoomDirection;
		if (zoomFactor > 3.5f) {
			zoomDirection = -1;
			zoomFactor = 3.5f;
		}
		if (zoomFactor < 1f) {
			zoomDirection = 1;
			zoomFactor = 1f;
		}

		getCamera().setPerspective(zoomFactor);

		if (explodeTime > explodeTimeMax) {
			getParticles().init();
			explodeTime = 0;

		} else {
			explodeTime += delta;
		}
	}

	public static void resetAll() {

		GL11.glDeleteLists(planeID, 1);
		planeID = 0;

		particles = null;
	}

	private static void drawPlane() {
		if (planeID == 0) {
			try {

				Texture fieldTextureCenter = TextureLoader.getInstance()
						.getTexture("res/textures/tileDark.jpg");
				Texture fieldTexture = TextureLoader.getInstance().getTexture(
						"res/textures/tileLight.jpg");

				ObjModel model = ObjLoader.loadObj("res/models/cube4.obj");

				FieldEntity f00 = new FieldEntity(1, 0, 0, fieldTexture,
						fieldTextureCenter, model);
				f00.setPipes(true, true, true, true);
				f00.setVisitedColor(new ZColor(1f, 0.0f, 0.0f));
				f00.setNotVisitedColor(new ZColor(1f, 0.59f, 0));

				FieldEntity f_1_1 = new FieldEntity(2, -1, -1, fieldTexture,
						fieldTextureCenter, model);
				f_1_1.setPipes(true, true, false, true);
				f_1_1.setVisitedColor(new ZColor(1f, 0.0f, 0.0f));
				f_1_1.setNotVisitedColor(new ZColor(1f, 0.59f, 0));

				FieldEntity f0_1 = new FieldEntity(3, 0, -1, fieldTexture,
						fieldTextureCenter, model);
				f0_1.setPipes(false, true, true, true);
				f0_1.setVisitedColor(new ZColor(1f, 0.0f, 0.0f));
				f0_1.setNotVisitedColor(new ZColor(1f, 0.59f, 0));

				FieldEntity f1_1 = new FieldEntity(3, 1, -1, fieldTexture,
						fieldTextureCenter, model);
				f1_1.setPipes(false, false, true, true);
				f1_1.setVisitedColor(new ZColor(1f, 0.0f, 0.0f));
				f1_1.setNotVisitedColor(new ZColor(1f, 0.59f, 0));

				FieldEntity f10 = new FieldEntity(3, 1, 0, fieldTexture,
						fieldTextureCenter, model);
				f10.setPipes(false, true, false, true);
				f10.setVisitedColor(new ZColor(1f, 0.0f, 0.0f));
				f10.setNotVisitedColor(new ZColor(1f, 0.59f, 0));

				FieldEntity f_10 = new FieldEntity(3, -1, 0, fieldTexture,
						fieldTextureCenter, model);
				f_10.setPipes(true, true, false, false);
				f_10.setVisitedColor(new ZColor(1f, 0.0f, 0.0f));
				f_10.setNotVisitedColor(new ZColor(1f, 0.59f, 0));

				FieldEntity f_11 = new FieldEntity(3, -1, 1, fieldTexture,
						fieldTextureCenter, model);
				f_11.setPipes(true, true, false, false);
				f_11.setVisitedColor(new ZColor(1f, 0.0f, 0.0f));
				f_11.setNotVisitedColor(new ZColor(1f, 0.59f, 0));

				FieldEntity f01 = new FieldEntity(3, 0, 1, fieldTexture,
						fieldTextureCenter, model);
				f01.setPipes(true, true, false, true);
				f01.setVisitedColor(new ZColor(1f, 0.0f, 0.0f));
				f01.setNotVisitedColor(new ZColor(1f, 0.59f, 0));

				FieldEntity f11 = new FieldEntity(3, 1, 1, fieldTexture,
						fieldTextureCenter, model);
				f11.setPipes(false, false, false, true);
				f11.setVisitedColor(new ZColor(1f, 0.0f, 0.0f));
				f11.setNotVisitedColor(new ZColor(1f, 0.59f, 0));

				planeID = GL11.glGenLists(1);
				GL11.glNewList(planeID, GL11.GL_COMPILE);

				GL11.glPushMatrix();
				// GL11.glPushMatrix();
				// GL11.glRotatef(angle / 3, 0, 0, -1);
				// GL11.glColor3f(0.4f, 0.4f, 0.9f);
				GL11.glColor4f(1f, 0.59f, 0, 0.8f);
				// getTableTexture().bind();
				//

				// float dim = 20;
				// GL11.glBegin(GL11.GL_QUADS);
				// GL11.glTexCoord2f(1, 0);
				// GL11.glVertex3f(dim, -dim, 0.5f);
				// GL11.glTexCoord2f(1, 1);
				// GL11.glVertex3f(dim, dim, 0.5f);
				// GL11.glTexCoord2f(0, 1);
				// GL11.glVertex3f(-dim, dim, 0.5f);
				// GL11.glTexCoord2f(0, 0);
				// GL11.glVertex3f(-dim, -dim, 0.5f);
				// GL11.glEnd();
				f_1_1.render();
				f0_1.render();
				f1_1.render();
				f_10.render();
				f00.render();
				f10.render();
				f_11.render();
				f01.render();
				f11.render();
				GL11.glPopMatrix();
				GL11.glEndList();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		GL11.glPushMatrix();
		GL11.glRotatef(angle / 9, 0, 0, 1);
		GL11.glCallList(planeID);
		GL11.glPopMatrix();

	}
	
	private static void drawPlaneMirror() {
		if (planeMirrorID == 0) {
			try {

				Texture fieldTextureCenter = TextureLoader.getInstance()
						.getTexture("res/textures/tileDark.jpg");
				Texture fieldTexture = TextureLoader.getInstance().getTexture(
						"res/textures/tileLight.jpg");

				ObjModel model = ObjLoader.loadObj("res/models/cube4.obj");

				FieldEntity f00 = new FieldEntity(1, 0, 0, fieldTexture,
						fieldTextureCenter, model);
				f00.setPipes(true, true, true, true);
				f00.setVisitedColor(new ZColor(1f, 0.0f, 0.0f));
				f00.setNotVisitedColor(new ZColor(1f, 0.59f, 0));

				FieldEntity f_1_1 = new FieldEntity(2, -1, -1, fieldTexture,
						fieldTextureCenter, model);
				f_1_1.setPipes(true, true, false, true);
				f_1_1.setVisitedColor(new ZColor(1f, 0.0f, 0.0f));
				f_1_1.setNotVisitedColor(new ZColor(1f, 0.59f, 0));

				FieldEntity f0_1 = new FieldEntity(3, 0, -1, fieldTexture,
						fieldTextureCenter, model);
				f0_1.setPipes(false, true, true, true);
				f0_1.setVisitedColor(new ZColor(1f, 0.0f, 0.0f));
				f0_1.setNotVisitedColor(new ZColor(1f, 0.59f, 0));

				FieldEntity f1_1 = new FieldEntity(3, 1, -1, fieldTexture,
						fieldTextureCenter, model);
				f1_1.setPipes(false, false, true, true);
				f1_1.setVisitedColor(new ZColor(1f, 0.0f, 0.0f));
				f1_1.setNotVisitedColor(new ZColor(1f, 0.59f, 0));

				FieldEntity f10 = new FieldEntity(3, 1, 0, fieldTexture,
						fieldTextureCenter, model);
				f10.setPipes(false, true, false, true);
				f10.setVisitedColor(new ZColor(1f, 0.0f, 0.0f));
				f10.setNotVisitedColor(new ZColor(1f, 0.59f, 0));

				FieldEntity f_10 = new FieldEntity(3, -1, 0, fieldTexture,
						fieldTextureCenter, model);
				f_10.setPipes(true, true, false, false);
				f_10.setVisitedColor(new ZColor(1f, 0.0f, 0.0f));
				f_10.setNotVisitedColor(new ZColor(1f, 0.59f, 0));

				FieldEntity f_11 = new FieldEntity(3, -1, 1, fieldTexture,
						fieldTextureCenter, model);
				f_11.setPipes(true, true, false, false);
				f_11.setVisitedColor(new ZColor(1f, 0.0f, 0.0f));
				f_11.setNotVisitedColor(new ZColor(1f, 0.59f, 0));

				FieldEntity f01 = new FieldEntity(3, 0, 1, fieldTexture,
						fieldTextureCenter, model);
				f01.setPipes(true, true, false, true);
				f01.setVisitedColor(new ZColor(1f, 0.0f, 0.0f));
				f01.setNotVisitedColor(new ZColor(1f, 0.59f, 0));

				FieldEntity f11 = new FieldEntity(3, 1, 1, fieldTexture,
						fieldTextureCenter, model);
				f11.setPipes(false, false, false, true);
				f11.setVisitedColor(new ZColor(1f, 0.0f, 0.0f));
				f11.setNotVisitedColor(new ZColor(1f, 0.59f, 0));

				planeMirrorID = GL11.glGenLists(1);
				GL11.glNewList(planeMirrorID, GL11.GL_COMPILE);

				GL11.glPushMatrix();
				// GL11.glPushMatrix();
				// GL11.glRotatef(angle / 3, 0, 0, -1);
				// GL11.glColor3f(0.4f, 0.4f, 0.9f);
				GL11.glColor4f(1f, 0.59f, 0, 0.8f);
				// getTableTexture().bind();
				//

				// float dim = 20;
				// GL11.glBegin(GL11.GL_QUADS);
				// GL11.glTexCoord2f(1, 0);
				// GL11.glVertex3f(dim, -dim, 0.5f);
				// GL11.glTexCoord2f(1, 1);
				// GL11.glVertex3f(dim, dim, 0.5f);
				// GL11.glTexCoord2f(0, 1);
				// GL11.glVertex3f(-dim, dim, 0.5f);
				// GL11.glTexCoord2f(0, 0);
				// GL11.glVertex3f(-dim, -dim, 0.5f);
				// GL11.glEnd();
				f_1_1.renderMirrorPlane();
				f0_1.renderMirrorPlane();
				f1_1.renderMirrorPlane();
				f_10.renderMirrorPlane();
				f00.renderMirrorPlane();
				f10.renderMirrorPlane();
				f_11.renderMirrorPlane();
				f01.renderMirrorPlane();
				f11.renderMirrorPlane();
				GL11.glPopMatrix();
				GL11.glEndList();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		GL11.glPushMatrix();
		GL11.glRotatef(angle / 9, 0, 0, 1);
		GL11.glCallList(planeMirrorID);
		GL11.glPopMatrix();

	}

	private static Texture getBallTexture() {
		try {
			return TextureLoader.getInstance().getTexture(
					"res/textures/field.jpg");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static Sphere sphere;

	private static Sphere getSphere() {
		if (sphere == null) {
			sphere = new Sphere();
			sphere.setTextureFlag(true);
		}
		return sphere;
	}

	private static void drawBall() {

		GL11.glPushMatrix();

		GL11.glTranslatef(0.0f, 0, 1.0f);
		GL11.glColor3f(0.7f, 0.7f, 0.7f);
		GL11.glRotatef(90, 1, 0, 0);

		getBallTexture().bind();
		GL11.glRotatef(-angle, 0, 1, 0);
		getSphere().draw(1, 32, 32);
		GL11.glPopMatrix();
	}

	private static void drawBallR() {
		if (!StaticRenderTools.fullDetails)
			return;

		GL11.glEnable(GL11.GL_TEXTURE_GEN_S); // Enable Texture Coord
		// Generation For S ( NEW )
		GL11.glEnable(GL11.GL_TEXTURE_GEN_T);

		GL11.glEnable(GL11.GL_BLEND);

		GL11.glPushMatrix();

		GL11.glTranslatef(0.0f, 0, 1.0f);
		GL11.glColor4f(0.7f, 0.7f, 0.7f, 0.4f);
		GL11.glRotatef(90, 1, 0, 0);
		try {
			TextureLoader.getInstance().getTexture(
					"res/textures/spheremapReflection.png").bind();
		} catch (IOException e) {
		}
		// getBallTexture().bind();
		GL11.glRotatef(-angle, 0, 1, 0);
		getSphere().draw(1f, 32, 32);
		GL11.glPopMatrix();
		GL11.glDisable(GL11.GL_TEXTURE_GEN_S);
		GL11.glDisable(GL11.GL_TEXTURE_GEN_T);
	}

	private static AbstractParticleSystem getParticles() {
		if (particles == null) {
			particles = new SpriteExplodeParticleSystem(40, 0f, 0f, 0f, 1f,
					getBallTexture(), 1) {

				@Override
				public void setUpShape(int nr) {
					float x = particles[nr].posX;
					float y = particles[nr].posY;
					float z = particles[nr].posZ;
					float size = particles[nr].size * 0.5f;
					GL11.glPushMatrix();
					GL11.glTranslatef(x, y, z);
					GL11.glRotatef(particles[nr].energy * 3, 0, 0, 1);
					GL11.glScalef(size, size, size);
					GL11.glColor3f(0.3f, 0.3f, 0.3f);
					texture.bind();
					// model.render();
					getSphere().draw(1, 8, 8);
					GL11.glPopMatrix();
				}

			};
		}
		return particles;
	}

	/**
	 * Defint the light setup to view the scene
	 */
	private static void defineLight() {
		// if (!lightInitialized) {

		FloatBuffer buffer;

		buffer = BufferUtils.createFloatBuffer(4);
		buffer.put(0).put(0).put(0).put(0);
		buffer.flip();
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_AMBIENT, buffer);

		buffer = BufferUtils.createFloatBuffer(4);
		buffer.put(0.5f).put(0.5f).put(0.5f).put(1f);
		buffer.flip();
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, buffer);

		buffer = BufferUtils.createFloatBuffer(4);
		buffer.put(1f).put(1f).put(1f).put(1f);
		buffer.flip();
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_SPECULAR, buffer);

		// setup the ambient light
		buffer = BufferUtils.createFloatBuffer(4);
		buffer.put(0.4f).put(0.4f).put(0.4f).put(0.4f);
		buffer.flip();
		GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, buffer);
		// GL11.glLightModeli(GL11.GL_LIGHT_MODEL_TWO_SIDE, GL11.GL_TRUE);

		// set up the position of the light
		buffer = BufferUtils.createFloatBuffer(4);
		buffer.put(20).put(20).put(2).put(0);
		buffer.flip();
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, buffer);

		GL11.glEnable(GL11.GL_LIGHT0);
		// lightInitialized = true;
		// }

	}

	private static void defineMaterial() {

		if (!materialInitialized) {
			// material.put(1).put(1).put(1).put(1);
			// material.flip();
			// GL11.glMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE, material);
			// GL11.glMaterial(GL11.GL_BACK, GL11.GL_DIFFUSE, material);

			float[] no_mat = { 0.0f, 0.0f, 0.0f, 1.0f };
			float[] mat_ambient = { 1f, 1f, 1f, 1.0f };
			// float[] mat_ambient_color = { 0.8f, 0.8f, 0.2f, 1.0f };
			float[] mat_diffuse = { 0.1f, 0.5f, 0.8f, 1.0f };
			// float[] mat_specular = {1.0f, 1.0f, 1.0f, 1.0f};
			float[] mat_specular = { 0.5f, 0.5f, 0.5f, 0.5f };
			// float no_shininess = 0.0f;
			float low_shininess = 5.0f;
			// float high_shininess = 100.0f;
			// float[] mat_emission = { 0.3f, 0.2f, 0.2f, 0.0f };

			material = BufferUtils.createFloatBuffer(4);

			material.put(mat_ambient).flip();
			GL11.glMaterial(GL11.GL_FRONT, GL11.GL_AMBIENT, material);

			material.put(mat_diffuse).flip();// .put(1).put(1).put(1).put(1);
			GL11.glMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE, material);

			material.put(mat_specular).flip();
			GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, material);

			// material.put(low_shininess);
			GL11.glMaterialf(GL11.GL_FRONT, GL11.GL_SHININESS, low_shininess);

			material.put(no_mat).flip();
			GL11.glMaterial(GL11.GL_FRONT, GL11.GL_EMISSION, material);
			materialInitialized = true;
		}
	}

}
