package zentity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.lwjgl.opengl.GL11;

import zgame.Status;
import zgame.ZColor;
import zgame.ZGlobals;
import zgame.Status.PlayngState;
import zgame.model.ObjModel;
import zgame.particles.AbstractParticleSystem;
import zgame.particles.SpriteExplodeParticleSystem;
import zgame.texture.Texture;
import zgraphutils.ZNode;
import ZTableUtils.Direction;
import ZTableUtils.FieldListener;
import ZTableUtils.RotationDirection;
import ZTableUtils.ZRectangle;

/**
 * 
 * @author zchira
 */
public class FieldEntity extends AbstractEntity {
	private static Texture textureSides;
	private static Texture textureCenter;
	private ObjModel model;
	private ZNode<String> node;

	private ArrayList<AbstractSprite> spriteList;

	private HashMap<Direction, FieldEntity> neighbourHashMap;

	private float fieldObjAngle = 0;

	private int i, j;

	protected boolean U, R, D, L;

	private boolean isRotating;

	private ArrayList<FieldListener> _listeners = new ArrayList<FieldListener>();

	private AbstractParticleSystem particleEngineField;

	private ZColor notVisitedColor;

	private ZColor activeColor = new ZColor(0.5f, 0.5f, 0.6f);

	private ZColor visitedColor;

	// koristi se kod iscrtavanja centralnog polja
	private int rotateFactor;

	private double rotationSpeed;

	private final double ROTATION_SPEED = 0.6;

	private int auraTime = ZGlobals.ALL_CONNECTED_TIMEOUT;

	private float auraAlpha = 1;

	public FieldEntity(int id, int _i, int _j, Texture tex, Texture textCenter,
			final ObjModel model) {
		neighbourHashMap = new HashMap<Direction, FieldEntity>();
		i = _i;
		j = _j;
		textureSides = tex;

		textureCenter = textCenter;

		initLists();

		this.model = model;
		node = new ZNode<String>(id, "");

		positionX = (float) ((float) i * ZGlobals.GL_WIDTH);
		positionY = -(float) ((float) j * ZGlobals.GL_WIDTH);

		isRotating = false;

	}

	public synchronized void addFieldListener(FieldListener l) {
		_listeners.add(l);
	}

	public void addSprite(AbstractSprite e) {
		getSpriteList().add(e);
		e.setCurrentField(this);
	}

	@Override
	public void collide(EntityManager manager, Entity other) {
	}

	/**
	 * Koriguje koordinate sprajtova posle rotacije.
	 */
	private void coorectSpriteCoordinates() {
		double xc = getX();
		double yc = getY();

		RotationDirection rotation = RotationDirection.ccw;

		for (AbstractSprite _sprite : getSpriteList()) {
			double xa = _sprite.getX() - xc;
			double ya = _sprite.getY() - yc;

			double xb = -ya;
			double yb = xa;
			if (rotationZ <= -90) {
				rotation = RotationDirection.cw;
				xb = ya;
				yb = -xa;

			}

			if (_sprite instanceof AbstractMonster) {
				((AbstractMonster) _sprite).rotateDirection(rotation);
			}
			_sprite.setPositionX((float) (xc + xb));
			_sprite.setPositionY((float) (yc + yb));
			_sprite.rotateZ((float) rotationZ);

		}
	}

	private synchronized void fireRotationFinished() {
		Iterator<FieldListener> listeners = _listeners.iterator();
		while (listeners.hasNext()) {
			((FieldListener) listeners.next()).rotationFinished(this);
		}
	}

	private synchronized void fireRotationStarted(RotationDirection dir) {
		Iterator<FieldListener> listeners = _listeners.iterator();
		while (listeners.hasNext()) {
			((FieldListener) listeners.next()).rotationStarted(this, dir);
		}
	}

	/**
	 * Vraca granice u kojima Sprite moze da se krece.
	 */
	public ZRectangle getConstraints() {
		double yUp, yDown, xLeft, xRight;
		// odredjujemo gornju granicu
		if (this.U) {
			if (getNeighbour(Direction.UP) != null) {
				if (getNeighbour(Direction.UP).D
						&& !getNeighbour(Direction.UP).isRotating()) {
					yUp = this.getY() + ZGlobals.GL_WIDTH;
				} else {
					yUp = this.getY() + ZGlobals.GL_WIDTH / 3;
				}
			} else {
				yUp = this.getY() + ZGlobals.GL_WIDTH / 3;
			}
		} else {
			yUp = getY();
		}

		// odredjujemo donju granicu
		if (this.D) {
			if (getNeighbour(Direction.DOWN) != null) {
				if (getNeighbour(Direction.DOWN).U
						&& !getNeighbour(Direction.DOWN).isRotating()) {
					yDown = this.getY() - ZGlobals.GL_WIDTH;
				} else {
					yDown = this.getY() - ZGlobals.GL_WIDTH / 3;
				}
			} else {
				yDown = this.getY() - ZGlobals.GL_WIDTH / 3;
			}
		} else {
			yDown = getY();
		}

		// odredjujemo levu granicu
		if (this.L) {
			if (getNeighbour(Direction.LEFT) != null) {
				if (getNeighbour(Direction.LEFT).R
						&& !getNeighbour(Direction.LEFT).isRotating()) {
					xLeft = this.getX() - ZGlobals.GL_WIDTH;
				} else {
					xLeft = this.getX() - ZGlobals.GL_WIDTH / 3;
				}
			} else {
				xLeft = this.getX() - ZGlobals.GL_WIDTH / 3;
			}
		} else {
			xLeft = getX();
		}

		// odredjujemo desnu granicu
		if (this.R) {
			if (getNeighbour(Direction.RIGHT) != null) {
				if (getNeighbour(Direction.RIGHT).L
						&& !getNeighbour(Direction.RIGHT).isRotating()) {
					xRight = this.getX() + ZGlobals.GL_WIDTH;
				} else {
					xRight = this.getX() + ZGlobals.GL_WIDTH / 3;
				}
			} else {
				xRight = this.getX() + ZGlobals.GL_WIDTH / 3;
			}
		} else {
			xRight = getX();
		}
		double w = xRight - xLeft;
		double h = yUp - yDown;

		return new ZRectangle(xLeft, yUp, w, h);
	}

	public ZNode<String> getField() {
		return node;
	}

	public FieldEntity getNeighbour(Direction s) {
		return neighbourHashMap.get(s);
	}

	/**
	 * Vraca pravac sledeceg kraka u cw smeru, ako se dolazi iz pravca d.
	 * 
	 * @param d
	 * @return
	 */
	public Direction getNextCcwDirection(Direction d) {
		if (d != null) {
			switch (d) {
			case UP:
				if (this.L) {
					return Direction.LEFT;
				} else {
					d = Direction.RIGHT;
				}
			case RIGHT:
				if (this.U) {
					return Direction.UP;
				} else {
					d = Direction.DOWN;
				}
			case DOWN:
				if (this.R) {
					return Direction.RIGHT;
				} else {
					d = Direction.LEFT;
				}
			case LEFT:
				if (this.D) {
					return Direction.DOWN;
				} else if (this.L) {
					return Direction.LEFT;
				} else if (this.U) {
					return Direction.UP;
				} else if (this.R) {
					return Direction.RIGHT;
				}
				break;
			default:
				break;
			}
		}

		return null;
	}

	/**
	 * Vraca pravac sledeceg kraka u cw smeru, ako se dolazi iz pravca d.
	 * 
	 * @param d
	 * @return
	 */
	public Direction getNextCwDirection(Direction d) {
		switch (d) {
		case UP:
			if (this.R) {
				return Direction.RIGHT;
			} else {
				d = Direction.LEFT;
			}
		case LEFT:
			if (this.U) {
				return Direction.UP;
			} else {
				d = Direction.DOWN;
			}
		case DOWN:
			if (this.L) {
				return Direction.LEFT;
			} else {
				d = Direction.RIGHT;
			}
		case RIGHT:
			if (this.D) {
				return Direction.DOWN;
			} else if (this.R) {
				return Direction.RIGHT;
			} else if (this.U) {
				return Direction.UP;
			} else if (this.L) {
				return Direction.LEFT;
			}
			break;
		default:
			break;
		}

		return null;
	}

	public ZNode<String> getNode() {
		return node;
	}

	private int getNumOfPipes() {
		int size = 0;
		if (this.U) {
			size++;
		}
		if (this.L) {
			size++;
		}
		if (this.D) {
			size++;
		}
		if (this.R) {
			size++;
		}
		return size;
	}

	@Override
	public float getSize() {
		return 0;
	}

	public ArrayList<AbstractSprite> getSpriteList() {
		if (spriteList == null) {
			spriteList = new ArrayList<AbstractSprite>();
		}
		return spriteList;
	}

	private void initParticleEngine(int num) {
		particleEngineField = new SpriteExplodeParticleSystem(num, 0f, 0f, 0f,
				1f, textureSides, 1) {

			private int i, j, k;

			private boolean init = false;

			private void initRotateAxis() {
				i = 0;
				j = 0;
				k = 0;
				int id = getNode().getId();
				int ost = (id % 3);
				if (ost == 0) {
					i = 1;
				}
				if (ost == 1) {
					j = 1;
				}
				if (ost == 2) {
					k = 1;
				}
				init = true;
				// System.out.println(ost);
			}

			@Override
			public void setUpShape(int nr) {

				if (!init) {
					initRotateAxis();
				}

				float x = particles[nr].posX;
				float y = particles[nr].posY;
				float z = particles[nr].posZ;
				// float size = particles[nr].size;
				GL11.glPushMatrix();
				GL11.glTranslatef(x, y, z);

				GL11.glRotatef(particles[nr].energy * 42, i, j, k);
				// GL11.glScalef(size, size, size);
				setFieldColor(1);
				// GL11.glColor3f(size, 0, 0);
				texture.bind();
				model.render();
				GL11.glPopMatrix();
			}
		};
	}

	public boolean isRotating() {
		return isRotating;
	}

	private void postRotation() {
		// if (direction != null) {
		if (rotationZ < 0) {
			boolean tmp = this.U;
			this.U = this.L;
			this.L = this.D;
			this.D = this.R;
			this.R = tmp;
			rotateFactor--;
		} else if (rotationZ > 0) {
			boolean tmp = this.U;
			this.U = this.R;
			this.R = this.D;
			this.D = this.L;
			this.L = tmp;
			rotateFactor++;
		}
		coorectSpriteCoordinates();

		if (rotationZ <= -90) {
			fieldObjAngle -= 90;
		} else {
			fieldObjAngle += 90;
		}

		if (fieldObjAngle > 90) {
			fieldObjAngle = 0;
		} else if (fieldObjAngle < 0) {
			fieldObjAngle = 90;
		}
		// System.out.println(fieldObjAngle);
		isRotating = false;
		rotationSpeed = 0;
		rotationZ = 0;

		fireRotationFinished();
		// alpha = 0;
		// alphaNew = 0;
		// direction = null;
	}

	private void preRotation() {

	}

	public synchronized void removeFieldListener(FieldListener l) {
		_listeners.remove(l);
	}

	public void removeSprite(AbstractSprite e) {
		getSpriteList().remove(e);
	}

	@Override
	public void render() {
		// GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_LIGHTING);

		GL11.glPushMatrix();
		GL11.glTranslatef(positionX, positionY, -0.005f);
		GL11.glRotatef(rotationZ, 0, 0, 1);

		setFieldColor(1);
		textureSides.bind();

		// draw the model
		int i = 0;
		double segment = ZGlobals.GL_WIDTH / 3;

		if (Status.getPlayngState() == PlayngState.gameover) {
			particleEngineField.render();
		} else {
			boolean paused = false;
			if (Status.getPlayngState() == PlayngState.paused) {
				paused = true;
			}

			if (D || paused) {
				GL11.glPushMatrix();
				GL11.glTranslated(0f, -segment, 0f);
				GL11.glRotatef(-90, 0, 0, 1);
				model.render();
				GL11.glPopMatrix();
				i++;
			}
			if (L || paused) {
				GL11.glPushMatrix();
				GL11.glTranslated(-segment, 0, 0);
				GL11.glRotatef(180, 0, 0, 1);
				model.render();
				GL11.glPopMatrix();
				i++;
			}
			if (R || paused) {
				GL11.glPushMatrix();
				GL11.glTranslated(segment, 0, 0);
				model.render();
				GL11.glPopMatrix();
				i++;
			}
			if (U || paused) {
				GL11.glPushMatrix();
				GL11.glTranslated(0, segment, 0);
				GL11.glRotatef(90, 0, 0, 1);
				model.render();
				GL11.glPopMatrix();
				i++;
			}
			if (i > 0) {
				GL11.glPushMatrix();
				GL11.glRotatef(90 * rotateFactor, 0, 0, 1);
				textureCenter.bind();
				model.render();
				GL11.glPopMatrix();
			}
		}

		GL11.glPopMatrix();

	}

	public void renderMirrorPlane() {

		GL11.glEnable(GL11.GL_LIGHTING);

		GL11.glPushMatrix();
		GL11.glTranslatef(positionX, positionY, 0);
		GL11.glRotatef(rotationZ, 0, 0, 1);

		setFieldColor(0.95f);
		textureSides.bind();

		// draw the model
		int i = 0;
		double segment = ZGlobals.GL_WIDTH / 3;

		if (Status.getPlayngState() == PlayngState.gameover) {
			// particleEngineField.render();
		} else {
			boolean paused = false;
			if (Status.getPlayngState() == PlayngState.paused) {
				paused = true;
			}

			if (D || paused) {
				GL11.glPushMatrix();
				GL11.glTranslated(0f, -segment, 0f);
				GL11.glRotatef(-90, 0, 0, 1);
				renderMirrorTileSide();
				GL11.glPopMatrix();
				i++;
			}
			if (L || paused) {
				GL11.glPushMatrix();
				GL11.glTranslated(-segment, 0, 0);
				GL11.glRotatef(180, 0, 0, 1);
				renderMirrorTileSide();
				GL11.glPopMatrix();
				i++;
			}
			if (R || paused) {
				GL11.glPushMatrix();
				GL11.glTranslated(segment, 0, 0);
				renderMirrorTileSide();
				GL11.glPopMatrix();
				i++;
			}
			if (U || paused) {
				GL11.glPushMatrix();
				GL11.glTranslated(0, segment, 0);
				GL11.glRotatef(90, 0, 0, 1);
				renderMirrorTileSide();
				GL11.glPopMatrix();
				i++;
			}
			if (i > 0) {
				GL11.glPushMatrix();
				GL11.glRotatef(90 * rotateFactor, 0, 0, 1);
				renderMirrorTileCenter();
				GL11.glPopMatrix();
			}
		}
		GL11.glPopMatrix();
	}

	private static int tileSideId = 0;

	private static void renderMirrorTileSide() {
		GL11.glPushMatrix();
		textureSides.bind();
		GL11.glCallList(tileSideId);
		GL11.glPopMatrix();
	}

	private static int tileCenterId = 0;

	private static void initLists() {
		if (tileCenterId == 0) {
			float x = -1;
			float y = -1;
			float w = 2, h = 2;
			tileCenterId = GL11.glGenLists(1);
			GL11.glNewList(tileCenterId, GL11.GL_COMPILE);

			GL11.glPushMatrix();
			GL11.glBegin(GL11.GL_QUADS);

			GL11.glTexCoord2f(0, 0);
			GL11.glVertex2d(x, y);

			GL11.glTexCoord2f(1, 0);
			GL11.glVertex2d(x + w, y);

			GL11.glTexCoord2f(1, 1);
			GL11.glVertex2d(x + w, y + h);

			GL11.glTexCoord2f(0, 1);
			GL11.glVertex2d(x, y + h);

			GL11.glEnd();
			GL11.glPopMatrix();

			GL11.glEndList();
		}

		if (tileSideId == 0) {
			float x = -1;
			float y = -1;
			float w = 2, h = 2;
			tileSideId = GL11.glGenLists(1);

			GL11.glNewList(tileSideId, GL11.GL_COMPILE);
			GL11.glPushMatrix();

			GL11.glBegin(GL11.GL_QUADS);

			GL11.glTexCoord2f(0, 0);
			GL11.glVertex2d(x, y);

			GL11.glTexCoord2f(1, 0);
			GL11.glVertex2d(x + w, y);

			GL11.glTexCoord2f(1, 1);
			GL11.glVertex2d(x + w, y + h);

			GL11.glTexCoord2f(0, 1);
			GL11.glVertex2d(x, y + h);

			GL11.glEnd();

			GL11.glPopMatrix();
			GL11.glEndList();
		}

	}

	private static void renderMirrorTileCenter() {
		GL11.glPushMatrix();
		textureCenter.bind();
		GL11.glCallList(tileCenterId);
		GL11.glPopMatrix();
	}

	protected void renderSprites() {
		for (AbstractEntity sprite : getSpriteList()) {
			sprite.render();
		}
	}

	protected void renderSpritesShadows() {
		for (AbstractEntity sprite : getSpriteList()) {
			if (sprite instanceof AbstractSprite) {
				((AbstractSprite) sprite).renderShadows();
			}

		}
	}

	protected void renderFieldAura() {
		// GL11.glPushMatrix();
		if ((Status.getPlayngState() == PlayngState.allFieldsConnected || Status
						.getPlayngState() == PlayngState.levelCompleted)) {

			GL11.glPushMatrix();
			GL11.glTranslatef(positionX, positionY, -0.005f);
			GL11.glRotatef(rotationZ, 0, 0, 1);

			
			
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_DEPTH_TEST);

			// float size = 1.05f + (((float) ZGlobals.ALL_CONNECTED_TIMEOUT -
			// auraTime) / (float) ZGlobals.ALL_CONNECTED_TIMEOUT) / 4;

			float size = 1.1f;

			GL11.glColor4f(0f, 0f, 0f, 1 - auraAlpha);
			// clearing texture

			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

			GL11.glScaled(size, size, size);
			int i = 0;
			double segment = ZGlobals.GL_WIDTH / 3;
			boolean paused = false;
			if (Status.getPlayngState() == PlayngState.paused) {
				paused = true;
			}
			if (D || paused) {
				GL11.glPushMatrix();
				// GL11.glRotatef(180, 0, 0, 1);
				GL11.glTranslated(0f, -segment, 0f);
				GL11.glRotatef(-90, 0, 0, 1);
				model.render();
				GL11.glPopMatrix();
				i++;
			}
			if (L || paused) {
				GL11.glPushMatrix();
				GL11.glTranslated(-segment, 0, 0);
				GL11.glRotatef(180, 0, 0, 1);
				model.render();
				GL11.glPopMatrix();
				i++;
			}
			if (R || paused) {
				GL11.glPushMatrix();
				GL11.glTranslated(segment, 0, 0);
				// GL11.glRotatef(180, 0, 0, 1);
				model.render();
				GL11.glPopMatrix();
				i++;
			}
			if (U || paused) {
				GL11.glPushMatrix();
				GL11.glTranslated(0, segment, 0);
				GL11.glRotatef(90, 0, 0, 1);
				model.render();
				GL11.glPopMatrix();
				i++;
			}
			if (i > 0) {
				GL11.glPushMatrix();
				GL11.glRotatef(90 * rotateFactor, 0, 0, 1);
				// textureCenter.bind();

				model.render();
				GL11.glPopMatrix();
			}
			// model.render();

			// getSphere().draw(size, 12, 12);

			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_DEPTH_TEST);

			GL11.glPopMatrix();
		}
	}

	public void rotate(RotationDirection dir) {
		if (dir == RotationDirection.ccw) {
			rotationSpeed = -ROTATION_SPEED;
		} else if (dir == RotationDirection.cw) {
			rotationSpeed = ROTATION_SPEED;
		}
		isRotating = true;
		fireRotationStarted(dir);
		preRotation();
	}

	public void setActiveColor(ZColor activeColor) {
		this.activeColor = activeColor;
	}

	private void setFieldColor(float alpha) {
		switch (getNode().getStatus()) {
		case notVisited:
			notVisitedColor.setAsGlColor(alpha);
			break;
		case active:
			activeColor.setAsGlColor(alpha);
			break;
		case visited:
			visitedColor.setAsGlColor(alpha);
			break;

		default:
			break;
		}
	}

	// private Sphere sphere;
	//
	// private Sphere getSphere() {
	// if (sphere == null) {
	// sphere = new Sphere();
	// }
	// return sphere;
	// }

	public void setNeighbour(Direction s, FieldEntity polje) {
		neighbourHashMap.put(s, polje);
	}

	public void setNotVisitedColor(ZColor notVisitedColor) {
		this.notVisitedColor = notVisitedColor;
	}

	// private void resetAuraTime(){
	// auraTime = ZGlobals.ALL_CONNECTED_TIMEOUT;
	// }

	public void setPipes(boolean u, boolean r, boolean d, boolean l) {
		this.U = u;
		this.R = r;
		this.D = d;
		this.L = l;
		initParticleEngine(getNumOfPipes());
	}

	public void setVisitedColor(ZColor visitedColor) {
		this.visitedColor = visitedColor;
	}

	@Override
	public void update(EntityManager manager, int delta) {
		if (Status.getPlayngState() == PlayngState.gameover) {
			particleEngineField.update(manager, delta);
		} else {
			rotationZ += delta * rotationSpeed;
			if (Math.abs(rotationZ) >= 90) {
				postRotation();
			}
		}

		if (Status.getPlayngState() == PlayngState.allFieldsConnected) {
			auraTime -= delta;
			auraAlpha = (float) auraTime
					/ (float) ZGlobals.ALL_CONNECTED_TIMEOUT;
			if (auraAlpha < 0.4) {
				auraAlpha = 0.4f;
			}

			if (auraTime < 0) {
				auraTime = 0;
			}
		}

	}

}
