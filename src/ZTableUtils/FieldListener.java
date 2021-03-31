package ZTableUtils;

import zentity.FieldEntity;

/**
 * 
 * @author zchira
 */
public interface FieldListener {
	public void rotationStarted(FieldEntity field, RotationDirection dir);
	public void rotationFinished(FieldEntity field);
}
