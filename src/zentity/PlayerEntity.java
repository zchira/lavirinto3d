package zentity;

import java.io.IOException;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Sphere;

import zgame.Quat;
import zgame.StaticMathTools;
import zgame.StaticRenderTools;
import zgame.Status;
import zgame.ZGlobals;
import zgame.ZSounds;
import zgame.Status.PlayngState;
import zgame.model.ObjModel;
import zgame.particles.AbstractParticleSystem;
import zgame.particles.ExplosionParticleSystem;
import zgame.particles.SpriteExplodeParticleSystem;
import zgame.sound.Sound;
import zgame.sound.SoundLoader;
import zgame.texture.Texture;
import zgame.texture.TextureLoader;
import ZTableUtils.RotationDirection;

/**
 * 
 * @author zchira
 */
public class PlayerEntity extends AbstractSprite {
	enum PlayerState {
		alive, dying, respawning, goingToNextLevel
	}

	private Quat qrot;

	protected static float speed = 8;

	private Texture texture;
	// private ObjModel model;

	private Sound rotateSound;

	private Sound respawnSound;

	private AbstractParticleSystem enginePlayer, engineExplosion, flyingSystem;

	private int respawningTime = ZGlobals.RESPAWNING_TIME;
	private float auraAlpha = 1f;

	private PlayerState playerState;

	// private final double rotateSpeedIdle = 0.05;
	private final double rotateSpeedMoving = 0.5;

	// private boolean verticalRotation;

	private Sphere sphere;

	public PlayerEntity(Texture t, Texture particleTexture, ObjModel m) {
		texture = t;
		// model = m;
		// verticalRotation = false;
		sphere = new Sphere();
		sphere.setTextureFlag(true);
		playerState = PlayerState.alive;

		qrot = new Quat();

		qrot.x = qrot.y = qrot.z = 0;
		qrot.w = 1;

		try {
			rotateSound = SoundLoader.getInstance().getOgg(ZSounds.ROTATE);
			respawnSound = SoundLoader.getInstance().getOgg(ZSounds.RESPAWN);
		} catch (IOException e) {
			System.err.println("Unable to load sound...");
		}

		engineExplosion = new ExplosionParticleSystem(500, 0f, 0f, 0f, 1f,
				particleTexture, 1);
		enginePlayer = new SpriteExplodeParticleSystem(16, 0f, 0f, 0f, 1f, t, 1) {

			@Override
			public void setUpShape(int nr) {
				float x = particles[nr].posX;
				float y = particles[nr].posY;
				float z = particles[nr].posZ;
				float size = particles[nr].size;
				GL11.glPushMatrix();
				GL11.glTranslatef(x, y, z);
				GL11.glRotatef(particles[nr].energy * 3, 0, 0, 1);
				GL11.glScalef(size, size, size);
				texture.bind();
				// model.render();
				sphere.draw(1, 8, 8);
				GL11.glPopMatrix();
			}

		};

		flyingSystem = new SpriteExplodeParticleSystem(1, 0f, 0f, 0f, 1f, t, 1) {

			@Override
			public void setUpShape(int nr) {
				setGravityZ(-1);

				float x = particles[nr].posX;
				float y = particles[nr].posY;
				float z = particles[nr].posZ;
				// float size = particles[nr].size;
				GL11.glPushMatrix();
				GL11.glTranslatef(x, y, z);
				GL11.glRotatef(particles[nr].energy * 3, 0, 0, 1);
				GL11.glScalef(1, 1, 1);
				texture.bind();
				// model.render();
				sphere.draw(1, 8, 8);

				GL11.glEnable(GL11.GL_TEXTURE_GEN_S); // Enable Texture Coord
				// Generation For S (
				// NEW )
				GL11.glEnable(GL11.GL_TEXTURE_GEN_T);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glPushMatrix();

				GL11.glColor4f(0.7f, 0.7f, 0.7f, 0.4f);
				try {
					TextureLoader.getInstance().getTexture(
							"res/textures/spheremapReflection.png").bind();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				sphere.draw(1, 8, 8);
				GL11.glPopMatrix();
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glDisable(GL11.GL_TEXTURE_GEN_S); // Enable Texture Coord
				// Generation For S (
				// NEW )
				GL11.glDisable(GL11.GL_TEXTURE_GEN_T);
				GL11.glPopMatrix();
			}

		};

	}

	@Override
	public void collide(EntityManager manager, Entity other) {
		manager.playerHit();
	}

	@Override
	public boolean collides(Entity other) {
		if (playerState == PlayerState.alive) {
			if (super.collides(other)) {
				playerState = PlayerState.dying;
				return true;
			}
			return false;
		} else {
			return false;
		}
	}

	@Override
	public float getSize() {
		return 1;
	}

	public boolean isAlive() {
		if (playerState != PlayerState.dying) {
			return true;
		}
		return false;
	}

	public void kill() {
		playerState = PlayerState.dying;
	}

	@Override
	public void render() {
		// enable lighting over the rock model
		GL11.glEnable(GL11.GL_LIGHTING);
		// store the original matrix setup so we can modify it
		// without worrying about effecting things outside of this
		// class
		GL11.glPushMatrix();

		GL11.glTranslatef(getCurrentField().getX(), getCurrentField().getY(),
				0f);

		// position the model based on the players currently game
		// location
		GL11.glRotatef(getCurrentField().getRotationZ(), 0, 0, 1);
		GL11.glTranslatef(positionX - getCurrentField().getX(), positionY
				- getCurrentField().getY(), 1f);

		boolean isRespawning = false;
		// bind the texture we want to apply to our rock and then
		// draw the model
		switch (playerState) {
		case respawning:
			isRespawning = true;
		case alive:
			GL11.glColor3f(1, 1, 1);

			super.render();

			FloatBuffer m = BufferUtils.createFloatBuffer(16);

			float[] matrix = new float[16];

			GL11.glPushMatrix();
			StaticMathTools.quatToMatrix(qrot, matrix);
			m.put(matrix, 0, 16);
			m.rewind();
			GL11.glMultMatrix(m);
			// Nacrtaj objekat;
			texture.bind();
			// GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
			sphere.draw(1, 32, 32);

			renderSphereMapping();

			// model.render();
			if (isRespawning) {
				renderAura();
			}

			GL11.glPopMatrix();

			break;
		case dying:
			engineExplosion.render();
			enginePlayer.render();
			break;
		case goingToNextLevel:
			flyingSystem.render();
			break;
		default:
			break;
		}
		GL11.glPopMatrix();
	}

	@Override
	public void renderShadows() {
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPushMatrix();

		GL11.glTranslatef(getCurrentField().getX(), getCurrentField().getY(),
				0f);

		GL11.glRotatef(getCurrentField().getRotationZ(), 0, 0, 1);
		GL11.glTranslatef(positionX - getCurrentField().getX(), positionY
				- getCurrentField().getY(), 1f);
		switch (playerState) {

		case alive:
		case respawning:
			GL11.glColor3f(1, 1, 1);
			super.renderShadows();
			break;
		default:
			break;
		}

		GL11.glPopMatrix();
	}

	private void renderSphereMapping() {

		if (!StaticRenderTools.fullDetails)
			return;

		GL11.glEnable(GL11.GL_TEXTURE_GEN_S); // Enable Texture Coord
		// Generation For S ( NEW )
		GL11.glEnable(GL11.GL_TEXTURE_GEN_T);
		GL11.glEnable(GL11.GL_BLEND);

		GL11.glPushMatrix();

		GL11.glColor4f(0.7f, 0.7f, 0.7f, 0.4f);
		try {
			TextureLoader.getInstance().getTexture(
					"res/textures/spheremapReflection.png").bind();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// getBallTexture().bind();
		sphere.draw(1, 32, 32);
		GL11.glPopMatrix();

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_GEN_S); // Enable Texture Coord
		// Generation For S ( NEW )
		GL11.glDisable(GL11.GL_TEXTURE_GEN_T);
	}

	private void renderAura() {
		GL11.glPushMatrix();

//		FloatBuffer buffSrc = BufferUtils.createFloatBuffer(8);
//        buffSrc.clear();
//        
//        FloatBuffer buffDst = BufferUtils.createFloatBuffer(8);
//        buffDst.clear();
//
//		GL11.glGetFloat(GL11.GL_BLEND_SRC, buffSrc);
//		GL11.glGetFloat(GL11.GL_BLEND_DST, buffDst);

//		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_DST_ALPHA);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glScaled(1.4, 1.4, 1.4);
		GL11.glColor4f(1 - auraAlpha, 1f, 0f, auraAlpha);
		// clearing texture
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		sphere.draw(1, 16, 16);

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glPopMatrix();
		
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}

	public void respawn() {
		respawnSound.play(1, 1);
		playerState = PlayerState.respawning;
		engineExplosion.init();
		enginePlayer.init();

	}

	@Override
	public void update(EntityManager manager, int delta) {
		// if the player is pushing the thrust key (up) then
		// increse the velocity in the direction we're currently
		// facing
		velocityX = 0;
		velocityY = 0;

		if (playerState == PlayerState.respawning) {
			respawningTime -= delta;
			auraAlpha = (float) respawningTime
					/ (float) ZGlobals.RESPAWNING_TIME;
			if (auraAlpha < 0) {
				auraAlpha = 0;
			}

			if (respawningTime < 0) {
				respawnSound.stop();
				playerState = PlayerState.alive;
				respawningTime = ZGlobals.RESPAWNING_TIME;
				auraAlpha = 1f;
			}
		}

		// boolean moved = false;

		// while (Keyboard.next()) {
		// if (Keyboard.getEventKeyState()) {

		if (!getCurrentField().isRotating()) {
			if (playerState != PlayerState.dying
					&& Status.getPlayngState() != PlayngState.allFieldsConnected
					&& Status.getPlayngState() != PlayngState.levelCompleted) {

				if (Keyboard.isKeyDown(ZGlobals.KEY_ROTATE_CCW)) {
					rotateSound.play(1, 1);
					getCurrentField().rotate(RotationDirection.ccw);
				}
				if (Keyboard.isKeyDown(ZGlobals.KEY_ROTATE_CW)) {
					rotateSound.play(1, 1);
					getCurrentField().rotate(RotationDirection.cw);
				}
			}
		}

		if (Keyboard.isKeyDown(ZGlobals.KEY_UP)) {
			if (positionX >= getCurrentField().getX() - ZGlobals.TOLLERANCE
					&& positionX <= getCurrentField().getX()
							+ ZGlobals.TOLLERANCE) {
				positionX = getCurrentField().getX();
				velocityY = speed;
			} else {
				if (positionX < getCurrentField().getX() - ZGlobals.TOLLERANCE) {
					velocityX = speed;
				} else {
					velocityX = -speed;
				}
			}
			rotateQuat(rotateSpeedMoving * delta, 1f, 0f, 0f);
			// moved = true;
		}

		if (Keyboard.isKeyDown(ZGlobals.KEY_RIGHT)) {
			if (positionY >= getCurrentField().getY() - ZGlobals.TOLLERANCE
					&& positionY <= getCurrentField().getY()
							+ ZGlobals.TOLLERANCE) {
				positionY = getCurrentField().getY();
				velocityX = speed;
			} else {
				if (positionY < getCurrentField().getY() - ZGlobals.TOLLERANCE) {
					velocityY = speed;
				} else {
					velocityY = -speed;
				}
			}
			rotateQuat(-rotateSpeedMoving * delta, 0f, 1f, 0f);
			// moved = true;
		}

		if (Keyboard.isKeyDown(ZGlobals.KEY_DOWN)) {
			if (positionX >= getCurrentField().getX() - ZGlobals.TOLLERANCE
					&& positionX <= getCurrentField().getX()
							+ ZGlobals.TOLLERANCE) {
				positionX = getCurrentField().getX();
				velocityY = -speed;

			} else {
				if (positionX < getCurrentField().getX() - ZGlobals.TOLLERANCE) {
					velocityX = speed;
				} else {
					velocityX = -speed;
				}
			}

			rotateQuat(-rotateSpeedMoving * delta, 1f, 0f, 0f);
			rotationX = (float) rotateSpeedMoving * delta;
			// moved = true;
		}

		if (Keyboard.isKeyDown(ZGlobals.KEY_LEFT)) {
			if (positionY >= getCurrentField().getY() - ZGlobals.TOLLERANCE
					&& positionY <= getCurrentField().getY()
							+ ZGlobals.TOLLERANCE) {
				positionY = getCurrentField().getY();
				velocityX = -speed;
			} else {
				if (positionY < getCurrentField().getY() - ZGlobals.TOLLERANCE) {
					velocityY = speed;
				} else {
					velocityY = -speed;
				}
			}
			rotateQuat(rotateSpeedMoving * delta, 0f, 1f, 0f);
			// moved = true;
		}

		// if (moved) {
		// // rotationZ += rotateSpeedMoving * delta;
		// } else {
		// // rotationZ += rotateSpeedIdle * delta;
		// }

		if (Status.getPlayngState() == PlayngState.levelCompleted) {
			this.playerState = PlayerState.goingToNextLevel;
		}

		// call the update the abstract class to cause our generic
		// movement and anything else the abstract implementation
		// provides for us
		switch (playerState) {
		case alive:
			super.update(manager, delta);
			break;
		case dying:
			engineExplosion.update(manager, delta);
			enginePlayer.update(manager, delta);
			break;
		case goingToNextLevel:
			flyingSystem.update(manager, delta);
			break;
		default:
			super.update(manager, delta);
			break;
		}

	}

	private void rotateQuat(double deltaAngle, float x, float y, float z) {
		qrot = StaticMathTools.rotQuat(qrot, (float) deltaAngle, x, y, z);
	}

	@Override
	public void rotationStarted(FieldEntity field, RotationDirection dir) {
		super.rotationStarted(field, dir);
		fieldRotationDirection = dir;
	}

	private RotationDirection fieldRotationDirection;

	@Override
	public void rotationFinished(FieldEntity field) {
		super.rotationFinished(field);
		if (fieldRotationDirection != null) {
			if (fieldRotationDirection.equals(RotationDirection.cw)) {
				rotateQuat(-90, 0, 0, 1);
			} else if (fieldRotationDirection.equals(RotationDirection.ccw)) {
				rotateQuat(90, 0, 0, 1);
			}
		}
	}

}
