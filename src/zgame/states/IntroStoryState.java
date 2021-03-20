package zgame.states;

import zgame.Status;

/**
 * State za prikazivanje uvodne priche
 * @author zchira
 *
 */
public class IntroStoryState extends AbstractStoryState{

	public static final String NAME = "introstory";

	@Override
	protected String getPackDescription() {
		Status.resetStatus();
		return Status.getGamePack().getStartStory();
	}

	@Override
	protected String getTargetState() {
		return LevelInfoState.NAME;
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
		return Status.getGamePack().getPackName();
	}
}
