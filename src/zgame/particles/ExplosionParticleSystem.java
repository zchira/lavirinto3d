package zgame.particles;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import zentity.Entity;
import zentity.EntityManager;
import zgame.texture.Texture;

/**
 * 
 * @author zchira
 */
public class ExplosionParticleSystem extends AbstractParticleSystem {

	private float GRAVITY_Z = 4f;

	private float FADE_SPEED = 3f;

	private Random rnd;

	private int sgn;

	public ExplosionParticleSystem(int numOfParticles, float posX, float posY,
			float posZ, float blendMode, Texture tex, int Id) {
		super(numOfParticles, posX, posY, posZ, blendMode, tex, Id);

	}

	@Override
	public void collide(EntityManager manager, Entity other) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean collides(Entity other) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public float getSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getY() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void init() {
		rnd = new Random();
		sgn = 1;
		for (int i = 0; i < particles.length; i++) {
			particles[i] = new Particle();
			setParticleDefaults(particles[i]);

		}
	}

	@Override
	public void render() {
		GL11.glPushMatrix();
		GL11.glTranslatef(getX(), getY(), getZ());
		// testFaceDirection();

//		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		texture.bind();

		for (int loop = 0; loop < particles.length; loop++) // Loop
		{

			// GL11.glColor4f(particles[loop].r, particles[loop].g,
			// particles[loop].b, 0.5f);
			if (particles[loop].energy > 0) // If The
			{
				float x = particles[loop].posX;
				float y = particles[loop].posY;
				float z = particles[loop].posZ;

				float size = particles[loop].size;

				GL11.glColor4f(particles[loop].r, particles[loop].g,
						particles[loop].b, particles[loop].energy / 100);

				// GL11.glTranslatef(x, y, z);
				// GL11.glRotatef(45, 1, 0, 0);
				// GL11.glTranslatef(-x, -y, -z);
				GL11.glBegin(GL11.GL_TRIANGLE_STRIP); // Build
				// Strip
				GL11.glTexCoord2d(1, 1);
				GL11.glVertex3f(x + size, y + size, z); // Top Right
				GL11.glTexCoord2d(0, 1);
				GL11.glVertex3f(x - size, y + size, z); // Top Left
				GL11.glTexCoord2d(1, 0);
				GL11.glVertex3f(x + size, y - size, z); // Bottom Right
				GL11.glTexCoord2d(0, 0);
				GL11.glVertex3f(x - size, y - size, z); // Bottom Left
				GL11.glEnd(); // Done

			} else {
				//setParticleDefaults(particles[loop]);
			}

		}
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glPopMatrix();
	}

	@Override
	public void setParticleDefaults(Particle p) {

		float noise = (float) rnd.nextFloat() * 0.8f - 0.4f;

		p.posX = rnd.nextFloat() * 2 - 1;
		p.posY = (float) Math.sqrt(1 - p.posX * p.posX) * sgn;

		p.posZ = (float) Math.sqrt(1 - Math.sqrt(p.posX * p.posX + p.posY
				* p.posY));// 1;

		p.dx = p.posX * 8; // rnd.nextFloat() * 8 - 4;
		p.dy = p.posY * 8; // rnd.nextFloat() * 8 - 4;
		p.dz = (p.posZ + noise) * 5;

		p.posX = (p.posX + noise) / 2;
		p.posY = (p.posY + noise) / 2;
		p.posZ = 0;// (p.posY + noise);

		p.oldX = p.posX;
		p.oldY = p.posY;
		p.oldZ = p.posZ;

		p.energy = 100;
		p.size = 0.1f;

		p.r = 1f + noise / 2;
		p.g = 1f + noise / 2;
		p.b = 0.0f;
		sgn = sgn * (-1);

	}

	@Override
	public void setUpShape(int nr) {

	}

	@Override
	public void update(EntityManager manager, int delta) {
		for (int loop = 0; loop < particles.length; loop++) {
			particles[loop].posX += particles[loop].dx * delta / 500f
					* particles[loop].energy / 100;
			particles[loop].posY += particles[loop].dy * delta / 500f
					* particles[loop].energy / 100;

			particles[loop].dx = particles[loop].dx;// * particles[loop].energy
			// / 200;
			particles[loop].dy = particles[loop].dy;// * particles[loop].energy
			// / 200;

			particles[loop].dz -= GRAVITY_Z * delta / 1000f;// * (1-
			// particles[loop].energy
			particles[loop].posZ += particles[loop].dz * delta / 1000f;

			particles[loop].energy -= FADE_SPEED * delta / 100f;

			float noise = rnd.nextFloat() / 5f;

			particles[loop].r -= FADE_SPEED * delta / 3000f - noise;
			particles[loop].g -= FADE_SPEED * delta / 2000f;

			particles[loop].size = particles[loop].energy / 200;

		}

	}

}
