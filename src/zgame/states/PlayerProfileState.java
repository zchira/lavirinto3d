package zgame.states;

import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import ZGameStatistic.ManageStatistics;
import ZGameStatistic.DataModel.PlayerInfo;
import zgame.GameWindow;
import zgame.StaticRenderTools;
import zgame.ZSounds;
import zgame.sound.SoundLoader;

public class PlayerProfileState extends AbstractMenuState {
	public static final String NAME = "playerprofile";

	private boolean createNewPlayerMode = false;

	private String newPlayerName = "";

	@Override
	protected void renderMenu(GameWindow window) {
		if (createNewPlayerMode) {
			renderCreatePlayerMenu(window);
		} else {
			renderSelectPlayerMenu(window);
		}
	}

	private void renderCreatePlayerMenu(GameWindow window) {
		window.enterOrtho();

		StaticRenderTools.drawGrayQuad(0, 250, 800, 50);
		GL11.glColor3f(0, 0, 0);
		StaticRenderTools.drawQuad(230, 260, 300, 30);
		StaticRenderTools.setWhiteColor();
		StaticRenderTools.getFont512().drawString(1, "Create new player", 50,
				220);

		StaticRenderTools.getFont512().drawString(1, "Enter name:", 50, 260);

		StaticRenderTools.getFont256().drawString(1, "ENTER - create player",
				550, 257);
		StaticRenderTools.getFont256()
				.drawString(1, "ESC   - cancel", 550, 277);

		StaticRenderTools.setOrangeColor();

		StaticRenderTools.getFont512().drawString(1, newPlayerName, 240, 260);

		window.leaveOrtho();
	}

	private void renderSelectPlayerMenu(GameWindow window) {
		window.enterOrtho();
		ArrayList<PlayerInfo> players = ManageStatistics.getActivePlayers();

		for (int i = 0; i < players.size(); i++) {

			StaticRenderTools.setOrangeColor();
			if (players.get(selected).equals(players.get(i))) {
				StaticRenderTools.setWhiteColor();
			}
			String player = players.get(i).getName();

			if (players.get(i).getPlayerId() == ManageStatistics
					.getLastPlayer().getPlayerId()) {
				player += "  <";
			}

			StaticRenderTools.getFont256().drawString(0, player, 50,
					220 + (i * 25));

		}

		StaticRenderTools.setWhiteColor();
		StaticRenderTools.getFont512().drawString(1, "Available players:", 30,
				170);

		StaticRenderTools.getFont512().drawString(1, "ENTER  - select player",
				30, 470);
		StaticRenderTools.getFont512().drawString(1,
				"N      - create new player", 30, 500);
		StaticRenderTools.getFont512().drawString(1, "DELETE - remove player",
				30, 530);
		StaticRenderTools.getFont256().drawString(1, "Press esc to go back",
				30, 560);
		window.leaveOrtho();
	}

	@Override
	protected void updateKeyboard(GameWindow window) {
		if (createNewPlayerMode) {
			updateCreatePlayerMenu(window);
		} else {
			updateSelectPlayerMenu(window);
		}
	}

	private void updateCreatePlayerMenu(GameWindow window) {
		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState()) {
				try {
					SoundLoader.getInstance().getOgg(ZSounds.CLICK)
							.play(1f, 1f);
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
					if (ManageStatistics.getActivePlayers().size() > 0) {
						createNewPlayerMode = false;
					}
					return;
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_RETURN) {
					if (newPlayerName == "" || newPlayerName.length() == 0) {
						createNewPlayerMode = false;
					} else {
						PlayerInfo newPlayer = ManageStatistics
								.addPlayer(newPlayerName);
						ManageStatistics
								.setLastPlayer(newPlayer.getPlayerId());
						selectActivePlayer();
						createNewPlayerMode = false;
						window.changeToState(MenuState.NAME);
					}
					return;
				}

				if (Keyboard.getEventKey() == Keyboard.KEY_BACK) {
					if (newPlayerName.length() > 0) {
						newPlayerName = newPlayerName.substring(0,
								newPlayerName.length() - 1);
					}

					return;
				}

				if (newPlayerName.length() > 16) {
					return;
				}

				if (Keyboard.getEventKey() == Keyboard.KEY_SPACE) {
					if (newPlayerName.length() > 0) {
						newPlayerName += " ";
					}
					return;
				}

				String key = Keyboard.getKeyName(Keyboard.getEventKey());

				if (key.length() == 1) {
					newPlayerName += key;
				}

			}
		}
	}

	private void updateSelectPlayerMenu(GameWindow window) {
		ArrayList<PlayerInfo> players = ManageStatistics.getActivePlayers();
		int length = ManageStatistics.getActivePlayers().size();

		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState()) {
				try {
					SoundLoader.getInstance().getOgg(ZSounds.CLICK)
							.play(1f, 1f);
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_UP) {

					selected--;
					if (selected < 0) {
						selected = length - 1;
					}
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_DOWN) {
					selected++;
					if (selected >= length) {
						selected = 0;
					}
				}

				if (Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
					window.changeToState(MenuState.NAME);
				}

				if (Keyboard.getEventKey() == Keyboard.KEY_N) {
					createNewPlayerMode = true;
					newPlayerName = "";
				}

				if (Keyboard.getEventKey() == Keyboard.KEY_DELETE) {
					if (players.get(selected).getPlayerId() == ManageStatistics
							.getLastPlayer().getPlayerId()) {

						ManageStatistics.setIsPlayerActive(players.get(selected)
								.getPlayerId(), false);
//						ManageStatistics.removePlayer(players.get(selected)
//								.getPlayerId());
						players = ManageStatistics.getActivePlayers();
						if (players.size() == 0) {
							createNewPlayerMode = true;
							newPlayerName = "";
						} else {
							ManageStatistics.setLastPlayer(players.get(0)
									.getPlayerId());
						}
					} else {
						ManageStatistics.removePlayer(players.get(selected)
								.getPlayerId());
					}
					selectActivePlayer();
				}

				if (Keyboard.getEventKey() == Keyboard.KEY_RETURN
						|| Keyboard.getEventKey() == Keyboard.KEY_SPACE) {

					ManageStatistics.setLastPlayer(players.get(selected)
							.getPlayerId());
					ManageStatistics.SaveStats();
					window.changeToState(MenuState.NAME);
				}
			}
		}
	}

	@Override
	public void enter(GameWindow window) {
		if (ManageStatistics.getActivePlayers().size() == 0) {
			createNewPlayerMode = true;
		} else {
			selectActivePlayer();
		}
	}

	private void selectActivePlayer() {
		ArrayList<PlayerInfo> players = ManageStatistics.getActivePlayers();
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).getPlayerId() == ManageStatistics
					.getLastPlayer().getPlayerId()) {
				selected = i;
				return;
			}
		}
	}

	@Override
	public void leave(GameWindow window) {
		ManageStatistics.SaveStats();
		super.leave(window);
	}

	@Override
	public String getName() {
		return NAME;
	}
}
