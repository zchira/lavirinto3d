package zentity;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Sphere;

import zgame.Status;
import zgame.ZGlobals;
import zgame.Status.PlayngState;
import zgame.model.ObjModel;
import zgame.particles.AbstractParticleSystem;
import zgame.particles.SpriteExplodeParticleSystem;
import zgame.texture.Texture;
import zgraphutils.ZNode;
import ZTableUtils.Direction;
import ZTableUtils.RotationDirection;
import ZTableUtils.ZGraphForGame;

/**
 * 
 * @author zchira
 */
public abstract class AbstractMonster extends AbstractSprite {
	private boolean stuckOnBorder;

	private boolean rotated;

	private AbstractSprite targetSprite;

	protected ObjModel model;

	private Sphere sphere;

	AbstractParticleSystem engineFlyingMonster;

	AbstractParticleSystem engineDyingMonster;


	@SuppressWarnings("unused")
	protected Direction direction;

	protected ArrayList<FieldEntity> path;

	private ZGraphForGame<ZNode<String>> graf;

	public AbstractMonster(AbstractSprite target,
			ZGraphForGame<ZNode<String>> graf, ObjModel m) {
		setTargetSprite(target);
		setGraf(graf);
		stuckOnBorder = false;
		rotated = false;
		model = m;
		sphere = new Sphere();
		sphere.setTextureFlag(true);

	}

	protected void init(){
		engineFlyingMonster = new SpriteExplodeParticleSystem(1, 0f, 0f, 0f,
				1f, getTexture(), 1) {

			@Override
			public void setUpShape(int nr) {
				float x = particles[nr].posX;
				float y = particles[nr].posY;
				float z = particles[nr].posZ;
				// float size = particles[nr].size;
				GL11.glPushMatrix();
				GL11.glTranslatef(x, y, z);
				// GL11.glRotatef(particles[nr].energy * 3, 0, 0, 1);
				// GL11.glScalef(size, size, size);
				texture.bind();
				sphere.draw(1, 16, 16);
				GL11.glPopMatrix();
			}
		};

		engineDyingMonster = new SpriteExplodeParticleSystem(8, 0f, 0f, 0f, 1f,
				getTexture(), 1) {

			@Override
			public void setUpShape(int nr) {
				setGravityZ(7f);
				float x = particles[nr].posX;
				float y = particles[nr].posY;
				float z = particles[nr].posZ;
				float size = particles[nr].size;
				GL11.glPushMatrix();
				GL11.glTranslatef(x, y, z);
				GL11.glRotatef(particles[nr].energy * 3, 0, 0, 1);
				GL11.glScalef(size, size, size);
				texture.bind();
				sphere.draw(1, 8, 8);
				GL11.glPopMatrix();
			}
		};
	}
	
	private void checkCollision() {
	}

	/**
	 * Vraca putanju od trenutnog polja do polja target-a. Putanja se vraca u
	 * obliku liste ZField-ova.
	 * 
	 * @param target
	 *            ciljno polje
	 * @return putanja (lista ZFieldova)
	 */
	protected abstract ArrayList<FieldEntity> createPath(FieldEntity target);

	protected void fireCollisionEvent() {
		// System.out.println("COLLISION!!!");

	}

	public ZGraphForGame<ZNode<String>> getGraf() {
		return graf;
	}

	public AbstractSprite getTargetSprite() {
		return targetSprite;
	}

	private boolean isInCenter() {
		double centerX = getCurrentField().getX();
		double centerY = getCurrentField().getY();
		if (!leavingCenter) {
			if ((positionX > centerX - ZGlobals.TOLLERANCE && positionX < centerX
					+ ZGlobals.TOLLERANCE)
					&& positionY > centerY - ZGlobals.TOLLERANCE
					&& positionY < centerY + ZGlobals.TOLLERANCE) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Poziva se kad je sprite u centru. Donosi odluku o novom pravcu kretanja
	 * (tj. setuje prametre dx i dy).
	 */
	protected abstract void makeDecision();

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

		// rotate the rock round to its current Z axis rotate
		

		switch (Status.getPlayngState()) {
		case gameover:
			GL11.glRotatef(rotationZ, 0, 0, 1);
			engineFlyingMonster.render();
			break;
		case levelCompleted:
//			GL11.glRotatef(rotationZ, 0, 0, 1);
			engineDyingMonster.render();
			break;
		default:
			super.render();
			GL11.glRotatef(rotationZ, 0, 0, 1);
			GL11.glColor3f(1, 1, 1);
			getTexture().bind();
			sphere.draw(1, 16, 16);
			break;
		}

		GL11.glPopMatrix();
	}
	
	@Override
	public void renderShadows() {
		// enable lighting over the rock model
		GL11.glEnable(GL11.GL_LIGHTING);

		GL11.glPushMatrix();

		GL11.glTranslatef(getCurrentField().getX(), getCurrentField().getY(),
				0f);
		GL11.glRotatef(getCurrentField().getRotationZ(), 0, 0, 1);
		GL11.glTranslatef(positionX - getCurrentField().getX(), positionY
				- getCurrentField().getY(), 1f);

		switch (Status.getPlayngState()) {
		case gameover:
			break;
		case levelCompleted:
			break;
		default:
			super.renderShadows();
			break;
		}

		GL11.glPopMatrix();
	}

	public void rotateDirection(RotationDirection d) {
		switch (d) {
		case ccw:
			switch (direction) {
			case UP:
				direction = Direction.LEFT;
				break;
			case RIGHT:
				direction = Direction.UP;
				break;
			case DOWN:
				direction = Direction.RIGHT;
				break;
			case LEFT:
				direction = Direction.DOWN;
				break;
			default:
				break;
			}
			break;
		case cw:
			switch (direction) {
			case UP:
				direction = Direction.RIGHT;
				break;
			case RIGHT:
				direction = Direction.DOWN;
				break;
			case DOWN:
				direction = Direction.LEFT;
				break;
			case LEFT:
				direction = Direction.UP;
				break;
			default:
				break;
			}
			break;
		default:
			break;
		}

		rotated = true;

	}

	private void setGraf(ZGraphForGame<ZNode<String>> graf) {
		this.graf = graf;
	}

	public void setTargetSprite(AbstractSprite targetSprite) {
		this.targetSprite = targetSprite;
	}

	@Override
	public void update(EntityManager manager, int delta) {
		float _dx = 0;
		float _dy = 0;

		rotationZ += getRotationSpeadFactor() * delta;

		// checking GameState. If gameOver, do not update
		// monster position - update only particle system....
		if (Status.getPlayngState() == PlayngState.gameover) {
			engineFlyingMonster.update(manager, delta);
		} else if (Status.getPlayngState() == PlayngState.levelCompleted) {
			engineDyingMonster.update(manager, delta);
		} else {

			if (isInCenter() || stuckOnBorder) {
				if (isInCenter()) {
					makeDecision();
					leavingCenter = true;
				} else {
					if (stuckOnBorder) {
						switch (direction) {
						case UP:
							direction = Direction.DOWN;
							break;
						case RIGHT:
							direction = Direction.LEFT;
							break;
						case DOWN:
							direction = Direction.UP;
							break;
						case LEFT:
							direction = Direction.RIGHT;
							break;
						default:
							break;
						}
						leavingCenter = false;
					}
				}
				stuckOnBorder = false;

				if (direction != null) {
					switch (direction) {
					case UP:
						_dy = speed;
						_dx = 0;
						// setStep(0, -YSTEP);
						break;
					case RIGHT:
						_dx = speed;
						_dy = 0;
						// setStep(XSTEP, 0);
						break;
					case DOWN:
						_dy = -speed;
						_dx = 0;
						// setStep(0, YSTEP);
						break;
					case LEFT:
						_dx = -speed;
						_dy = 0;
						// setStep(-XSTEP, 0);
						break;
					default:
						break;
					}
					velocityX = _dx;
					velocityY = _dy;
					// setStep(_dx, _dy);
				}
			}

			// super.update(manager, delta);

			if (velocityX == 0 && velocityY == 0) {
				stuckOnBorder = true;
				leavingCenter = false;
			}

			if (rotated) {
				switch (direction) {
				case UP:
					_dy = speed;
					// setStep(0, -YSTEP);
					break;
				case RIGHT:
					_dx = speed;
					// setStep(XSTEP, 0);
					break;
				case DOWN:
					_dy = -speed;
					// setStep(0, YSTEP);
					break;
				case LEFT:
					_dx = -speed;
					// setStep(-XSTEP, 0);
					break;
				default:
					break;
				}
				velocityX = _dx;
				velocityY = _dy;
				// setStep(_dx, _dy);
				rotated = false;
			}

			super.update(manager, delta);

			if (_dx != velocityX) {
				if (velocityX < 0) {
					direction = Direction.LEFT;
				} else if (velocityX > 0) {
					direction = Direction.RIGHT;
				}
			} else if (_dy != velocityY) {
				if (velocityY < 0) {
					direction = Direction.DOWN;
				} else if (velocityY > 0) {
					direction = Direction.UP;
				}
			}

			checkCollision();
		}
	}

	@Override
	public float getSize() {
		return 0.7f;
	}

	protected abstract Texture getTexture();
	
	protected abstract float getRotationSpeadFactor();

}
