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
public abstract class SpriteExplodeParticleSystem extends
		AbstractParticleSystem {
	// private Sphere sphere;

//	private long numOfDeath;

	private Random rnd;
	protected float gravityZ = 7f;

	// private float SPEED = 0.01f;

	private float FADE_SPEED = 1f;

	public SpriteExplodeParticleSystem(int numOfParticles, float posX,
			float posY, float posZ, float blendMode, Texture tex, int Id) {
		super(numOfParticles, posX, posY, posZ, blendMode, tex, Id);
		texture = tex;
		// sphere = new Sphere();
		// sphere.setTextureFlag(true);
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

	protected float getGravityZ() {
		return gravityZ;
	}

	// @Override
	// public void setUpShape(int nr) {
	// float x = particles[nr].posX;
	// float y = particles[nr].posY;
	// float z = particles[nr].posZ;
	//
	// float x0 = particles[nr].oldX;
	// float y0 = particles[nr].oldY;
	// float z0 = particles[nr].oldZ;
	//
	//	
	//
	// float size = particles[nr].size;
	//
	// GL11.glPushMatrix();
	//
	// GL11.glTranslatef(x, y, z);
	// GL11.glRotatef(particles[nr].energy*3, 1, 0, 1);
	// // GL11.glTranslatef(-x, -y, -z);
	// texture.bind();
	// sphere.draw(size, 32, 32);
	// GL11.glPopMatrix();
	//
	// }

	@Override
	public float getSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getX() {
		// TODO Auto-generated method stub
		return x;
	}

	@Override
	public float getY() {
		// TODO Auto-generated method stub
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
//		numOfDeath = 0;

	}

	@Override
	public void render() {
		GL11.glPushMatrix();
		GL11.glTranslatef(getX(), getY(), getZ());
		// testFaceDirection();

		// GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);

		texture.bind();

		for (int loop = 0; loop < particles.length; loop++) // Loop
		{
			// if (particles[loop].posZ < -getZ()-2) {
			// GL11.glEnable(GL11.GL_DEPTH_TEST);
			// } else {
			// GL11.glDisable(GL11.GL_DEPTH_TEST);
			// }
			// float x = particles[loop].posX;
			// float y = particles[loop].posY;
			// float z = particles[loop].posZ;

			GL11.glColor4f(particles[loop].r, particles[loop].g,
					particles[loop].b, particles[loop].energy / 100);

			setUpShape(loop);
		}
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glPopMatrix();

	}

	protected void setGravityZ(float gravityZ) {
		this.gravityZ = gravityZ;
	}

	@Override
	public void setParticleDefaults(Particle p) {
		float noise = (float) rnd.nextFloat() * 0.8f - 0.4f;

		p.posX = noise;
		p.posY = noise;
		p.posZ = noise;

		p.dx = rnd.nextFloat() * 8 - 4;
		p.dy = rnd.nextFloat() * 8 - 4;
		p.dz = rnd.nextFloat() * 8;

		p.oldX = 0;
		p.oldY = 0;
		p.oldZ = 0;

		p.energy = 100;// - 50 * rnd.nextFloat();
		p.size = 0.5f;

		p.r = 1;
		p.g = 1;
		p.b = 1f;

	}

	public abstract void setUpShape(int nr);

	@Override
	public void update(EntityManager manager, int delta) {

		for (int loop = 0; loop < particles.length; loop++) {

			if (particles[loop].energy > 0) // If The
			{

				particles[loop].oldX = particles[loop].posX;
				particles[loop].oldY = particles[loop].posY;
				particles[loop].oldZ = particles[loop].posZ;

				particles[loop].posX += particles[loop].dx * delta / 500f
						* particles[loop].energy / 100;// - 2 * SPEED * sgn;
				particles[loop].posY += particles[loop].dy * delta / 500f
						* particles[loop].energy / 100;

				particles[loop].dx = particles[loop].dx;

				particles[loop].dy = particles[loop].dy;

				particles[loop].dz -= gravityZ * delta / 1000f;

				particles[loop].posZ += particles[loop].dz * delta / 500f;

				particles[loop].energy -= FADE_SPEED * delta / 100f;

				// float noise = rnd.nextFloat() / 5f;

				// particles[loop].r += FADE_SPEED * delta / 40000f;
				// particles[loop].g += FADE_SPEED * delta / 20000f;
				// // particles[loop].b -= FADE_SPEED * delta / 4000f;

				// particles[loop].size = (particles[loop].energy) / 100;
			} else {
				// numOfDeath ++;
				//				
				// if (numOfDeath > particles.length){
				// System.out.println("end");
				// }

				// setParticleDefaults(particles[loop]);
			}

		}

	}

}
