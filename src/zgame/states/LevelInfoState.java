package zgame.states;

import zgame.Status;

/**
 * 
 * @author zchira
 */
public class LevelInfoState extends AbstractStoryState{
	public static final String NAME = "levelinfo";
	@Override
	protected String getPackDescription() {
		return Status.getCurrentLevel().getLevelDescription();
	}

	@Override
	protected String getTargetState() {
		return InGameState.NAME;
	}

	@Override
	public String getName() {
		return NAME;
	}
	
	

	@Override
	protected boolean isDrawPackName() {
		return true;
	}

	@Override
	protected String getTitleString() {
		return Status.getCurrentLevel().getLevelName();
	}

}
