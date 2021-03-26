import levelgenerator.LevelGenerator;

public class test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Test settings

		LevelGenerator lg = new LevelGenerator(8, 2, 124, 15);
		lg.print();
		lg.printMatrix();
		
		System.out.println("----------------");

		String level = lg.getLevelXml();
		System.out.println(level);


		/*
		 * ZSettings settings = ZSettings.getSettings();
		 * System.out.println(settings.toString()); ZSettings.saveSettings(false, false,
		 * DisplayModes._800X600, 5, 6); settings = ZSettings.getSettings();
		 * System.out.println(settings.toString());
		 */



		// Test scores
//		PlayerInfo pi = ManageStatistics.addPlayer("Player 1");
//		ManageStatistics.addPlayerResults(pi.getPlayerId(), "pack1", 2, 100);
//		ManageStatistics.addPlayerResults(pi.getPlayerId(), "pack1", 3, 150);
//		ManageStatistics.addPlayerResults(pi.getPlayerId(), "pack1", 2, 100);
//		ManageStatistics.addPlayerResults(pi.getPlayerId(), "pack2", 2, 100);
//		
//		PlayerInfo pi2 = ManageStatistics.addPlayer("Player 2");
//		ManageStatistics.addPlayerResults(pi2.getPlayerId(), "pack1", 4, 200);
//		ManageStatistics.addPlayerResults(pi2.getPlayerId(), "pack1", 5, 150);		
//		ManageStatistics.addPlayerResults(pi2.getPlayerId(), "pack2", 1, 100);
//		
//		
//		ManageStatistics.setIsPlayerActive(2, false);
//		
//		ManageStatistics.SaveStats();
//				
//		ArrayList<PlayerInfo> players = ManageStatistics.getAllPlayers();
//		for (PlayerInfo playerInfo : players) {
//			System.out.println(playerInfo.getName());
//		}
//		
//		ArrayList<PlayerResault> resaults = ManageStatistics.getTopResults(5, "pack1");
//		for (PlayerResault r : resaults) {
//			System.out.println(r.getName() + " " + " " + r.getPlayerId()+ " "  + r.getPoints());
//		}
//
//		ArrayList<PlayerInfo> aresaults = ManageStatistics.getActivePlayers();
//		for (PlayerInfo r : aresaults) {
//			System.out.println(r.getName() + " " + r.getPlayerId() + " active ");
//		}

	}

}
