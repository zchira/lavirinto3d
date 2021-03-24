package zentity;

import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;


import zgame.model.ObjModel;
import zgame.texture.Texture;
import zgame.texture.TextureLoader;


import ZTableUtils.Direction;
import ZTableUtils.RotationDirection;

/**
 * 
 * @author zchira
 */
public class MonsterDummy extends AbstractMonster{
	
	RotationDirection rd = RotationDirection.ccw;

	public MonsterDummy(ObjModel m){
		this(m, RotationDirection.ccw);
	}
	
	public MonsterDummy(ObjModel m, RotationDirection rd){
		super(null, null, m);
		direction = Direction.LEFT;
		this.rd = rd;
		init();
	}
	
	@Override
	protected ArrayList<FieldEntity> createPath(FieldEntity target) {
		return null;
	}

	@Override
	protected float getRotationSpeadFactor() {
		switch (rd) {
		case ccw:
			return 0.1f;
		case cw:
			return -0.1f;
		}
		
		return 0f;
	}

	@Override
	protected Texture getTexture() {
		
		try {
			GL11.glColor3f(255, 255, 0);
			switch (rd) {
			case ccw:
				return TextureLoader.getInstance().getTexture("res/textures/dummyMonsterCcw.jpg");
			case cw:
				return TextureLoader.getInstance().getTexture("res/textures/dummyMonsterCw.jpg");
			}
		} catch (IOException e) {
			System.err.println(e);
		}
		return null;
	}
	
	@Override
	protected void makeDecision() {
		switch (rd) {
		case ccw:
			direction = getCurrentField().getNextCcwDirection(direction);
			break;
		case cw:
			direction = getCurrentField().getNextCwDirection(direction);
			break;
		}		
	}
}
