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
public class EmittigParticleSystem extends AbstractParticleSystem {

	private float GRAVITY_Z = 4f;

//	private float SPEED = 0.01f;

	private float FADE_SPEED = 3f;

	private Random rnd;

	// private float energyQuant = 80;
	float sgn = 1;

	public EmittigParticleSystem(int numOfParticles, float posX, float posY,
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
		return x;
	}

	@Override
	public float getY() {
		return y;
	}

	@Override
	public void init() {
		rnd = new Random();

		for (int i = 0; i < particles.length; i++) {
			particles[i] = new Particle();
			setParticleDefaults(particles[i]);

		}
		numOfAlive = particles.length;
	}

	@Override
	public void render() {

		GL11.glPushMatrix();
		GL11.glTranslatef(getX(), getY(), getZ());
		// testFaceDirection();

		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);

		texture.bind();

		for (int loop = 0; loop < particles.length; loop++) // Loop
		{
			// System.out.println(particles[loop].energy);
			if (particles[loop].posZ < -getZ()-2) {
				GL11.glEnable(GL11.GL_DEPTH_TEST);
			} else {
				GL11.glDisable(GL11.GL_DEPTH_TEST);
			}
			// GL11.glColor4f(particles[loop].r, particles[loop].g,
			// particles[loop].b, 0.5f);

//			float x = particles[loop].posX;
//			float y = particles[loop].posY;
//			float z = particles[loop].posZ;

			// float size = particles[loop].size;

			GL11.glColor4f(particles[loop].r, particles[loop].g,
					particles[loop].b, particles[loop].energy / 100);

			setUpShape(loop);
			// GL11.glBegin(GL11.GL_TRIANGLE_STRIP); // Build
			// // Strip
			// GL11.glTexCoord2d(1, 1);
			// GL11.glVertex3f(x + size, y + size, z); // Top Right
			// GL11.glTexCoord2d(0, 1);
			// GL11.glVertex3f(x - size, y + size, z); // Top Left
			// GL11.glTexCoord2d(1, 0);
			// GL11.glVertex3f(x + size, y - size, z); // Bottom Right
			// GL11.glTexCoord2d(0, 0);
			// GL11.glVertex3f(x - size, y - size, z); // Bottom Left
			// GL11.glEnd(); // Done

		}
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glPopMatrix();
	}
	



	@Override
	public void setParticleDefaults(Particle p) {

		float noise = (float) rnd.nextFloat() * 0.8f - 0.4f;

		p.posX = 0;
		p.posY = 0;
		p.posZ = 0;

		p.dx = noise * 3; // rnd.nextFloat() * 8 - 4;
		p.dy = noise * 3; // rnd.nextFloat() * 8 - 4;
		p.dz = rnd.nextFloat() * 8;

		p.oldX = 0;
		p.oldY = 0;
		p.oldZ = 0;

		p.energy = 100 - 50 * rnd.nextFloat();
		p.size = 0.1f;

		p.r = 1;
		p.g = rnd.nextFloat() / 8;
		p.b = 0.0f;

	}

	@Override
	public void setUpShape(int nr) {

		float x = particles[nr].posX;
		float y = particles[nr].posY;
		float z = particles[nr].posZ;

		// float x0 = particles[nr].oldX;
		// float y0 = particles[nr].oldY;
		// float z0 = particles[nr].oldZ;

		// float dx = (x - x0) * 125;
		// float dy = (y - y0) * 125;
		// float dz = (z - z0) * 125;

		float size = particles[nr].size;

		GL11.glPushMatrix();

		GL11.glTranslatef(x, y, z);
		GL11.glRotatef(45, 1, 0, 0);
		GL11.glTranslatef(-x, -y, -z);

		GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
		GL11.glTexCoord2d(1, 1);
		GL11.glVertex3f(x + size, y + size, z); // Top Right
		GL11.glTexCoord2d(0, 1);
		GL11.glVertex3f(x - size, y + size, z); // Top Left
		GL11.glTexCoord2d(1, 0);
		GL11.glVertex3f(x + size, y - size, z); // Bottom Right
		GL11.glTexCoord2d(0, 0);
		GL11.glVertex3f(x - size, y - size, z); // Bottom Left
		GL11.glEnd();

		GL11.glPopMatrix();

	}
	@Override
	public void update(EntityManager manager, int delta) {
		
		
		
		if (x > 5) {
			sgn *= -1;
			
		}
		
		if (x < 0){
			sgn *= -1;
//			x += SPEED;
		}
//		x += SPEED * sgn;

		for (int loop = 0; loop < particles.length; loop++) {

			if (particles[loop].energy > 0) // If The
			{
				// if (particles[loop].energy < energyQuant) {
				particles[loop].oldX = particles[loop].posX;
				particles[loop].oldY = particles[loop].posY;
				particles[loop].oldZ = particles[loop].posZ;
				// energyQuant -=20;
				// if (energyQuant <= 0) energyQuant = 100;
				// }

				particles[loop].posX += particles[loop].dx * delta / 500f
						* particles[loop].energy / 100;// - 2 * SPEED * sgn;
				particles[loop].posY += particles[loop].dy * delta / 500f
						* particles[loop].energy / 100;

				particles[loop].dx = particles[loop].dx;

				particles[loop].dy = particles[loop].dy;

				particles[loop].dz -= GRAVITY_Z * delta / 1000f;

				particles[loop].posZ += particles[loop].dz * delta / 1000f;

				particles[loop].energy -= FADE_SPEED * delta / 100f;

//				float noise = rnd.nextFloat() / 5f;

				particles[loop].r += FADE_SPEED * delta / 40000f;
				particles[loop].g += FADE_SPEED * delta / 20000f;
				// particles[loop].b -= FADE_SPEED * delta / 4000f;

				particles[loop].size = (100 - particles[loop].energy) / 200;
			} else {
				setParticleDefaults(particles[loop]);
			}

		}

	}


}
