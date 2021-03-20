package ZTableUtils;

import java.util.EventObject;

import zentity.AbstractSprite;
import zentity.FieldEntity;

/**
 * Custom event koji se javlja kad sprite predje sa jednog polja na drugo.
 * 
 * @author zchira
 *
 */
public class SpriteMovedToField extends EventObject {

	private static final long serialVersionUID = 1L;

	private FieldEntity oldField, newField;

	private AbstractSprite sprite;

	public SpriteMovedToField(FieldEntity oldField, FieldEntity newField, AbstractSprite sprite) {
		super(sprite);
		this.newField = newField;
		this.oldField = oldField;
		this.sprite = sprite;
	}

	public FieldEntity getOldField() {
		return oldField;
	}

	public FieldEntity getNewField() {
		return newField;
	}

	public AbstractSprite getSprite() {
		return sprite;
	}

}
