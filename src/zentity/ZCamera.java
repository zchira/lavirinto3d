package zentity;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import zgame.ZGlobals;

/**
 * 
 * @author zchira
 */
public class ZCamera implements Entity {

	private static float DELTA = 0.2f;

	private static final float MAX_ZOOM_FACTOR = 2f;

	private static final float MIN_ZOOM_FACTOR = 0.2f;

	private static final float fovy = 45f;

	private static final float aspect = ((float) 800) / ((float) 600);

	private static final float zNear = 0.1f;

	private static final float zFar = 500f;

	private static final double pi180 = 0.017453292519943295769236907684886;

	private float eyeX, eyeY, eyeZ;

	private float centerX, centerY, centerZ;

	private Entity target;
	private float newCenterX, newCenterY;
	private float newEyeX, newEyeY;
	private boolean zoomChanged;
	private float zoomFactor = 1f;

	// private float backupEyeX, backupEyeX, backupEyeX;

	// private boolean ortho;

	public ZCamera(Entity target) {
		this.target = target;

		if (target != null) {
			newEyeX = target.getX();
			newEyeY = target.getY() - 21;
			// newEyeZ = 35;

			eyeX = target.getX();
			eyeY = target.getY() - 21;
			eyeZ = 35;
		}else{
			
		}

	}
	
	public void resetZoom(){
//		zoomFactor = 1f;
		zoomChanged = true;
//		if (target != null) {
//			newEyeX = target.getX();
//			newEyeY = target.getY() - 21;
//			// newEyeZ = 35;
//
//			eyeX = target.getX();
//			eyeY = target.getY() - 21;
//			eyeZ = 35;
//		}else{
//			
//		}
	}

	@Override
	public void collide(EntityManager manager, Entity other) {

	}

	@Override
	public boolean collides(Entity other) {
		return false;
	}

	public float getCenterX() {
		return centerX;
	}

	public float getCenterY() {
		return centerY;
	}

	public float getCenterZ() {
		return centerZ;
	}

	public float getEyeX() {
		return eyeX;
	}

	public float getEyeY() {
		return eyeY;
	}

	public float getEyeZ() {
		return eyeZ;
	}

	@Override
	public float getSize() {
		return 0;
	}

	public Entity getTarget() {
		return target;
	}

	@Override
	public float getX() {
		return eyeX;
	}

	@Override
	public float getY() {
		return eyeY;
	}

	public float getZ() {
		return eyeZ;
	}

	@Override
	public void render() {
		if (zoomChanged) {
			setPerspective(zoomFactor);
			zoomChanged = false;
		}
		GLU.gluLookAt(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, 0, 0, 1);
	}

	public void setCenterX(float centerX) {
		this.centerX = centerX;
	}

	public void setCenterY(float centerY) {
		this.centerY = centerY;
	}

	public void setCenterZ(float centerZ) {
		this.centerZ = centerZ;
	}

	public void setEyeX(float eyeX) {
		this.eyeX = eyeX;
	}

	public void setEyeY(float eyeY) {
		this.eyeY = eyeY;
	}

	public void setEyeZ(float eyeZ) {
		this.eyeZ = eyeZ;
	}

	public void setPerspective(float zoom) {
		double top, bottom, left, right;
		top = zNear * Math.tan(pi180 * fovy / 2);
		bottom = -top;
		right = aspect * top;
		left = -right;
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glFrustum(left * zoom, right * zoom, bottom * zoom, top * zoom,
				zNear, zFar);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
	}

	public void setTarget(Entity target) {
		this.target = target;
	}

	@Override
	public void update(EntityManager manager, int delta) {
		if (target != null) {
			newCenterX = target.getX();
			newCenterY = target.getY();
		} else {
			newCenterX = 0;
			newCenterX = 0;
		}
		float deltaX = newCenterX - centerX;
		float deltaY = newCenterY - centerY;

		float step = (float) delta / 150f;

		if (Math.abs(deltaX) > DELTA) {
			if (deltaX > 0) {
				centerX += step;
				eyeX += step;
				newEyeX += step;
			} else {
				centerX -= step;
				eyeX -= step;
				newEyeX -= step;
			}
		}

		if (Math.abs(deltaY) > DELTA) {
			if (deltaY > 0) {
				centerY += step;
				eyeY += step;
				newEyeY += step;
			} else {
				centerY -= step;
				eyeY -= step;
				newEyeY -= step;
			}
		}

		// float dx = eyeX - centerX;
		// float dy = eyeY - centerY;
		// float dz = eyeZ - centerZ;

		// float r3 = dx * dx + dy * dy + dz * dz;

		step = (float) delta / 50f;

		float r2 = eyeY * eyeY + eyeZ * eyeZ;
		if (Keyboard.isKeyDown(ZGlobals.KEY_CAMERA_UP)) {
			if (eyeY >= centerY - 0.1f) {
				eyeY = centerY - 0.1f;
			} else {
				eyeY += step;
				eyeZ = (float) Math.sqrt((r2 - eyeY * eyeY));
				// eyeZ += step;
			}
		}

		if (Keyboard.isKeyDown(ZGlobals.KEY_CAMERA_DOWN)) {
			if (eyeZ <= 6) {
				eyeZ = 6;
			} else {
				eyeY -= step;
				eyeZ = (float) Math.sqrt((r2 - eyeY * eyeY));
			}
		}

		// float r = (float) Math.sqrt(r3);

		// System.out.println(r);
		if (Keyboard.isKeyDown(ZGlobals.KEY_CAMERA_ZOOM_IN)) {
			if (zoomFactor < MAX_ZOOM_FACTOR) {
				zoomFactor *= 1.01f;
				zoomChanged = true;
			}

		}

		if (Keyboard.isKeyDown(ZGlobals.KEY_CAMERA_ZOOM_OUT)) {
			if (zoomFactor > MIN_ZOOM_FACTOR) {
				zoomFactor *= 0.99f;
				zoomChanged = true;
			}
		}

		setCenterX(centerX);
		setCenterY(centerY);
		setCenterZ(1);
	}

}
