package zentity;

import java.util.ArrayList;
import java.util.Iterator;

import zgame.StaticRenderTools;
import zgame.ZGlobals;
import ZTableUtils.Direction;
import ZTableUtils.FieldListener;
import ZTableUtils.RotationDirection;
import ZTableUtils.SpriteListener;
import ZTableUtils.SpriteMovedToField;
import ZTableUtils.ZRectangle;

/**
 * 
 * @author zchira
 */
public class AbstractSprite extends AbstractEntity implements FieldListener {

	protected FieldEntity currentField;

	protected boolean locked;
	
	/**
	 * Used only in monster classes. 
	 */
	protected boolean leavingCenter = false;

	/**
	 * Postavlja AbstractEntity u centar polja 'polje' i setuje to polje u
	 * currentField. Ako je polje == null, to se operacija se obavlja sa
	 * trenutnim poljem.
	 * 
	 * @param polje
	 */
	public void setInitialPosition(FieldEntity polje) {
		if (polje == null)
			polje = getCurrentField();
		if (polje != null) {
			setCurrentField(polje);
			positionX = polje.getX();
			positionY = polje.getY();
		}
		locked = false;
	}

	@Override
	public void collide(EntityManager manager, Entity other) {
	}

	@Override
	public float getSize() {
		return 0;
	}

	@Override
	public void render() {
//		StaticRenderTools.drawShadow(0, 0, -0.95f, 2f, 2f);
	}
	
	public void renderShadows() {
		StaticRenderTools.drawShadow(0, 0, -0.99f, 1.9f, 1.9f);
	}

	public FieldEntity getCurrentField() {
		return currentField;
	}

	public void setCurrentField(FieldEntity currentField) {
		// this.currentField = currentField;
		if (this.currentField != null) {
			this.currentField.removeFieldListener(this);
		}
		this.currentField = currentField;
		this.currentField.addFieldListener(this);
		this.leavingCenter = false;

	}

	@Override
	public void update(EntityManager manager, int delta) {
		ZRectangle rect = getCurrentField().getConstraints();

		double vy = velocityY * delta / 1000f;
		double vx = velocityX * delta / 1000f;

		// UP constraints
		if (vy < 0) {
			if (positionY + vy < rect.y - rect.height) {
				positionY = (float) (rect.y - rect.height);
				velocityY = 0;
			}
		}

		// DOWN constraints
		if (vy > 0) {
			if (positionY + vy > rect.y) {
				positionY = (float) (rect.y);
				velocityY = 0;
			}
		}

		// LEFT constraints
		if (vx < 0) {
			if (positionX + vx < rect.x) {
				positionX = (float) rect.x;
				velocityX = 0;
			}
		}

		// RIGHT constraints
		if (vx > 0) {
			if (positionX + vx > rect.x + rect.width) {
				positionX = (float) (rect.x + rect.width);
				velocityX = 0;
			}
		}

		if (!locked) {
			super.update(manager, delta);
		}

		double x0 = getCurrentField().getX() - ZGlobals.GL_WIDTH / 2;
		double y0 = getCurrentField().getY() + ZGlobals.GL_WIDTH / 2;
		double width = ZGlobals.GL_WIDTH;

		double x1 = x0 + width;
		double y1 = y0 - width;

		if (positionX > x1) {
			if (!getCurrentField().getNeighbour(Direction.RIGHT).isRotating()) {
				fireSpriteEvent(new SpriteMovedToField(getCurrentField(),
						getCurrentField().getNeighbour(Direction.RIGHT), this));
			} else {
				vx = -vx;
			}
		}

		if (positionX < x0) {
			if (!getCurrentField().getNeighbour(Direction.LEFT).isRotating()) {
				fireSpriteEvent(new SpriteMovedToField(getCurrentField(),
						getCurrentField().getNeighbour(Direction.LEFT), this));
			} else {
				vx = -vx;
			}
		}

		if (positionY < y1) {
			if (!getCurrentField().getNeighbour(Direction.DOWN).isRotating()) {
				fireSpriteEvent(new SpriteMovedToField(getCurrentField(),
						getCurrentField().getNeighbour(Direction.DOWN), this));
			} else {
				vy = -vy;
			}
		}

		if (positionY > y0) {
			if (!getCurrentField().getNeighbour(Direction.UP).isRotating()) {
				fireSpriteEvent(new SpriteMovedToField(getCurrentField(),
						getCurrentField().getNeighbour(Direction.UP), this));

			} else {
				vy = -vy;
			}
		}

	}

	private ArrayList<SpriteListener> _listeners = new ArrayList<SpriteListener>();

	public synchronized void addSpriteListener(SpriteListener l) {
		_listeners.add(l);
	}

	public synchronized void removeSpriteListener(SpriteListener l) {
		_listeners.remove(l);
	}

	private synchronized void fireSpriteEvent(SpriteMovedToField e) {
		Iterator<SpriteListener> listeners = _listeners.iterator();
		while (listeners.hasNext()) {
			((SpriteListener) listeners.next()).spriteMovedToField(e);
		}
	}

	
	@Override
	public void rotationFinished(FieldEntity field) {
		locked = false;
	}

	@Override
	public void rotationStarted(FieldEntity field, RotationDirection dir) {
		locked = true;
	}

}
