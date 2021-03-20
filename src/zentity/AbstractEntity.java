package zentity;

/**
 * 
 * @author zchira
 */
public abstract class AbstractEntity implements Entity {
	
	protected static float speed = 6;
	
	/** The current rotation on the Z axis of this entity */
	protected float rotationZ;
	protected float rotationY;
	protected float rotationX;
	
	/** The x position of this entity */
	protected float positionX = 0;
	/** The y position of this entity */
	protected float positionY = 0;
	
	/** The X component of the velocity of this entity */
	protected float velocityX = 0;
	/** The y component of the velocity of this entity */
	protected float velocityY = 0;
	
	/**
	 * @see zentity.Entity#update(zentity.EntityManager, int)
	 */
	public void update(EntityManager manager, int delta) {
		// update the position of this entity based on its current
		// velocity. 
		positionX += (velocityX * delta) / 1000.0f;
		positionY += (velocityY * delta) / 1000.0f;
		
	}
	
	/**
	 * @see zentity.Entity#getX()
	 */
	public float getX() {
		return positionX;
	}
	
	/**
	 * @see zentity.Entity#getY()
	 */
	public float getY() {
		return positionY;
	}
	
	/**
	 * @see zentity.Entity#collides(zentity.Entity)
	 */
	public boolean collides(Entity other) {
		// We're going to use simple circle collision here since we're 
		// only worried about 2D collision.
		//
		// Normal math tells us that if the distance between the two
		// centres of the circles is less than the sum of their radius
		// then they collide. However, working out the distance between
		// the two would require a square root (Math.sqrt((dx*dx)+(dy*dy))
		// which could be quite slow.
		//
		// Instead we're going to square the sum of their radius and compare
		// that against the un-rooted value. This is equivilent but 
		// much faster
		
		// Get the size of the other entity and combine it with our
		// own, giving the range of collision. Square this so we can
		// compare it against the current distance.
		float otherSize = other.getSize();
		float range = (otherSize + getSize());
		range *= range;

		// Get the distance on X and Y between the two entities, then
		// find the squared distance between the two.
		float dx = getX() - other.getX();
		float dy = getY() - other.getY();
		float distance = (dx*dx)+(dy*dy);
		
		// if the squared distance is less than the squared range
		// then we've had a collision!
		return (distance <= range);
	}

	public float getRotationZ() {
		return rotationZ;
	}

	public void setPositionX(float positionX) {
		this.positionX = positionX;
	}

	public void setPositionY(float positionY) {
		this.positionY = positionY;
	}

	public void setRotationZ(float rotationZ) {
		this.rotationZ = rotationZ;
	}
	
	public void rotateZ(float step){
		this.rotationZ += step;
	}
}
