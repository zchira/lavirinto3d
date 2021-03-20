package zgame.particles;

import org.lwjgl.opengl.GL11;

import zentity.Entity;
import zgame.texture.Texture;

/**
 * 
 * @author zchira
 */
public abstract class AbstractParticleSystem implements Entity {

	protected float x, y, z;

	protected Texture texture;

	protected float blendMode;

	protected int systemId;

	protected Particle[] particles;

	protected int numOfAlive;

	public AbstractParticleSystem(int numOfParticles, float posX, float posY,
			float posZ, float blendMode, Texture tex, int Id) {

		particles = new Particle[numOfParticles];
		texture = tex;
		systemId = Id;
		this.blendMode = blendMode;
		x = posX;
		y = posY;
		z = posZ;
		init();

	}

	public float getZ() {
		return z;
	}

	public abstract void init();

	public abstract void setParticleDefaults(Particle p);

	public abstract void setUpShape(int nr);

	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}

	protected void testFaceDirection() {
		// rotated
		GL11.glPushMatrix();
		GL11.glTranslatef(7, 7, 1);
		GL11.glRotatef(45, 1, 0, 0);
		GL11.glTranslatef(-7, -7, -1);
		GL11.glColor3f(0, 1, 0);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glNormal3f(0, 0, 1);
		GL11.glVertex3f(7, 7, 1); // Top Right
		GL11.glVertex3f(8, 7, 1); // Top Left
		GL11.glVertex3f(8, 8, 1); // Bottom Right
		GL11.glVertex3f(7, 8, 1);
		GL11.glEnd();
		GL11.glPopMatrix();

		// not rotated
		GL11.glPushMatrix();

		// GL11.glRotatef(45, 1, 0, 0);

		GL11.glBegin(GL11.GL_QUADS);
		GL11.glNormal3f(0, 0, 1);
		GL11.glVertex3f(7, 7, 1);
		GL11.glVertex3f(8, 7, 1);
		GL11.glVertex3f(8, 8, 1);
		GL11.glVertex3f(7, 8, 1);
		GL11.glEnd();
		GL11.glPopMatrix();

		GL11.glLineWidth(3);
		GL11.glBegin(GL11.GL_LINE_STRIP);

		GL11.glVertex3f(0, 0, 1);
		GL11.glVertex3f(10, 0, 1);
		GL11.glVertex3f(10, 10, 1);
		GL11.glVertex3f(0, 10, 1);
		GL11.glVertex3f(0, 0, 1);
		GL11.glEnd();

		//
	}

	public void update(int delta) {
		this.update(null, delta);
	}
}
