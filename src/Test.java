import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Random;

import core.ArcadeMachine;

/**
 * Created with IntelliJ IDEA.
 * User: Diego
 * Date: 04/10/13
 * Time: 16:29
 * This is a Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public class Test
{

    public static void main(String[] args)
    {
        //Available controllers:
        String sampleRandomController = "controllers.sampleRandom.Agent";
        String sampleOneStepController = "controllers.sampleonesteplookahead.Agent";
        String sampleMCTSController = "controllers.sampleMCTS.Agent";
        String sampleGAController = "controllers.sampleGA.Agent";
        String dontDieController = "controllers.dontDie.Agent";
        String puzzleSolverController = "controllers.puzzleSolver.Agent";
        String puzzleSolverPlusController = "controllers.puzzleSolverPlus.Agent";
        String randomOneStepController = "controllers.randomOneStep.Agent";
        String randomController = "controllers.random.Agent";
        String doNothingController = "controllers.doNothing.Agent";

        //Available games:
        String gamesPath = "examples/gridphysics/";

        //CIG 2014 Training Set Games
        //String games[] = new String[]{"aliens", "boulderdash", "butterflies", "chase", "frogs",
        //        "missilecommand", "portals", "sokoban", "survivezombies", "zelda"};

        //CIG 2014 Validation Set Games
        String games[] = new String[]{"camelRace", "digdug", "firestorms", "infection", "firecaster",
                "overload", "pacman", "seaquest", "whackamole", "eggomania"};

        
        games = new String[]{"aliens", "boulderdash", "butterflies", "chase", "frogs",
                "missilecommand", "portals", "sokoban", "survivezombies", "zelda",
                "camelRace", "digdug", "firestorms", "infection", "firecaster",
                "overload", "pacman", "seaquest", "whackamole", "eggomania"};
        
        games = new String[]{"aliens", "boulderdash", "butterflies", "chase", "frogs",
                "missilecommand", "portals", "sokoban", "survivezombies", "zelda",
                "camelRace", "digdug", "firestorms", "infection", "firecaster",
                "overload", "pacman", "seaquest", "whackamole", "eggomania",
                "bait", "boloadventures", "bombuzal", "brainman", "chipschallenge",
                "modality", "painter", "realsokoban", "thecitadel", "zenpuzzle"};

        //Other settings
        boolean visuals = true;
        String recordActionsFile = null; //where to record the actions executed. null if not to save.
        int seed = new Random().nextInt();

        //Game and level to play
        
        String gameTitle = "thecitadel";
        String mutatedGamesPath = "../GameChanger/mutatedgames/";
        String generatedGamesPath = "../GameChanger/generatedgames/";
        
//        
//        try {
//			System.setOut(new PrintStream(new FileOutputStream("gamedata.txt")));
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
        
        int gameIdx = 8;
        int levelIdx = 0; //level names from 0 to 4 (game_lvlN.txt).
        String game = gamesPath + gameTitle + ".txt";
        String level1 = gamesPath + gameTitle + "_lvl" + levelIdx +".txt";

        // 1. This starts a game, in a level, played by a human.
//        ArcadeMachine.playOneGame(game, level1, recordActionsFile, seed);

//         2. This plays a game in a level by the controller.
        ArcadeMachine.runOneGame(game, level1, visuals, puzzleSolverPlusController, recordActionsFile, seed);

        // 3. This replays a game from an action file previously recorded
        //String readActionsFile = "actionsFile_aliens_lvl0.txt";  //This example is for
        //ArcadeMachine.replayGame(game, level1, visuals, readActionsFile);

        // 4. This plays a single game, in N levels, M times :
        //String level2 = gamesPath + games[gameIdx] + "_lvl" + 1 +".txt";
        //int M = 3;
        //ArcadeMachine.runGames(game, new String[]{level1, level2}, M, sampleRandomController, null, seed);

        //5. This plays N games, in the first L levels, M times each. Actions to file optional (set saveActions to true).
//      int N = 20, L = 1, M = 10;
//      boolean saveActions = true;
//      String[] levels = new String[L];
//      String[] actionFiles = new String[L*M];
//      for(int i = 0; i < N; ++i)
//      {
//          int actionIdx = 0;
//          game = gamesPath + games[i] + ".txt";
//          for(int j = 0; j < L; ++j){
//              levels[j] = gamesPath + games[i] + "_lvl" + j +".txt";
//              if(saveActions) for(int k = 0; k < M; ++k)
//                  actionFiles[actionIdx++] = "actions_game_" + i + "_level_" + j + "_" + k + ".txt";
//          }
//          ArcadeMachine.runGames(game, levels, M, sampleRandomController, saveActions? actionFiles:null, seed);
//      }    
        
//        String[] controllers = new String[]{"controllers.sampleMCTS.Agent", "controllers.dontDie.Agent", "controllers.sampleGA.Agent", 
//        		"controllers.randomOneStep.Agent", "controllers.sampleonesteplookahead.Agent", "controllers.sampleRandom.Agent"};
        
        String[] controllers = new String[]{"controllers.sampleMCTS.Agent", "controllers.dontDie.Agent", "controllers.sampleGA.Agent", 
        		"controllers.randomOneStep.Agent", "controllers.sampleonesteplookahead.Agent", "controllers.random.Agent",
        		"controllers.doNothing.Agent"};
        
        
//        controllers = new String[]{"controllers.doNothing.Agent"};
        
        
//        //6. Let all controllers play through a series of games
//        int N = 20, L = 1, M = 10;
//        boolean saveActions = true;
//        String[] levels = new String[L];
//        String[] actionFiles = new String[L*M];
//        for (int c = 0; c < controllers.length; c++) {
//        	String foldername = controllers[c].split("\\.")[1] + "200ticks_all";
//	        try {
//				File dir = new File(foldername);
//				dir.mkdir();
//				System.setOut(new PrintStream(new FileOutputStream(foldername+"/gamedata.txt")));
//	 		} catch (FileNotFoundException e) {
//	 			e.printStackTrace();
//	 		}
//        	
//	        for(int i = 0; i < N; ++i){
//	            int actionIdx = 0;
//	            game = gamesPath + games[i] + ".txt";
//	            for(int j = 0; j < L; ++j){
//	                levels[j] = gamesPath + games[i] + "_lvl" + j +".txt";
//	                if(saveActions) for(int k = 0; k < M; ++k)
//	                    actionFiles[actionIdx++] = foldername + "/" + "actions_game_" + i + "_level_" + j + "_" + k + ".txt";
//	            }
//	            ArcadeMachine.runGames(game, levels, M, controllers[c], saveActions? actionFiles:null, seed);
//	        }  
//        }
  
      
      
        
      //7. Let a controller play a series of mutated games
//     int MUTATIONS = 100, N = 30, L = 1, M = 10;
//     boolean saveActions = false;
//     
//     String mutationFolderName = "ga200ticks_mutation_";
//     for (int z = 0; z < MUTATIONS; z++) {
//		
////       try {
////    	   File dir = new File(mutationFolderName + z);
////    	   dir.mkdir();
////			System.setOut(new PrintStream(new FileOutputStream(mutationFolderName + z + "/gamedata.txt")));
////		} catch (FileNotFoundException e) {
////			e.printStackTrace();
////		}
//	
//	     String[] levels = new String[L];
//	     String[] actionFiles = new String[L*M];
//	     for(int i = 0; i < N; ++i)
//	     {
//	         int actionIdx = 0;
//	         game = mutatedGamesPath + games[i] + "_mutation_" + z + ".txt";
//	         for(int j = 0; j < L; ++j){
//	             levels[j] = gamesPath + games[i] + "_lvl" + j +".txt";
//	             if(saveActions) for(int k = 0; k < M; ++k)
//	                 actionFiles[actionIdx++] = mutationFolderName + z + "/" + "actions_game_" + i + "_level_" + j + "_" + k + ".txt";
//	         }
//	         ArcadeMachine.runGames(game, levels, M, sampleGAController, saveActions? actionFiles:null, seed);
//	     } 
//     }
      
     
     
     //8. Let all controllers play a series of mutated games
//     int MUTATIONS = 20, N = 20, L = 1, M = 10;
//     boolean saveActions = true;
//     for (int c = 0; c < controllers.length; c++) {
//    	 String foldername =  controllers[c].split("\\.")[1] + "200ticks";
//	
//	     for (int z = 0; z < MUTATIONS; z++) {
//	    	 String outputFolder = foldername + "_mutation2_"+ z;
//	       try {
//	    	   File dir = new File(outputFolder);
//	    	   dir.mkdir();
//				System.setOut(new PrintStream(new FileOutputStream(outputFolder + "/gamedata.txt")));
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			}
//		
//		     String[] levels = new String[L];
//		     String[] actionFiles = new String[L*M];
//		     for(int i = 0; i < N; ++i)
//		     {
//		         int actionIdx = 0;
//		         game = mutatedGamesPath + games[i] + "_mutation_" + z + ".txt";
//		         for(int j = 0; j < L; ++j){
//		             levels[j] = gamesPath + games[i] + "_lvl" + j +".txt";
//		             if(saveActions) for(int k = 0; k < M; ++k)
//		                 actionFiles[actionIdx++] = outputFolder + "/" + "actions_game_" + i + "_level_" + j + "_" + k + ".txt";
//		         }
//		         ArcadeMachine.runGames(game, levels, M, controllers[c], saveActions? actionFiles:null, seed);
//		     } 
//	     }
//     }
//     gameTitle = "seaquest";
//        game = mutatedGamesPath + gameTitle + "_mutation_" + 1 + ".txt";
//        level1 = gamesPath + gameTitle + "_lvl" + 0 +".txt";
////        ArcadeMachine.playOneGame(game, level1, recordActionsFile, seed);
//      ArcadeMachine.runOneGame(game, level1, visuals, sampleMCTSController, recordActionsFile, seed);
       
       
       
       //9. Let the controller play a series of generated games
//       int GENERATEDGAMES = 400, L = 1, M = 1;
//       boolean saveActions = true;
//     for (int z = 0; z < GENERATEDGAMES; z++) {
//		
//    	 
////       try {
////    	   File dir = new File(mutationFolderName + z);
////    	   dir.mkdir();
////			System.setOut(new PrintStream(new FileOutputStream(mutationFolderName + z + "/gamedata.txt")));
////		} catch (FileNotFoundException e) {
////			e.printStackTrace();
////		}
//	
//	     String[] levels = new String[L];
//	     String[] actionFiles = new String[L*M];
//
//         int actionIdx = 0;
//         game = generatedGamesPath + "gen_game_" + z + ".txt";
//         for(int j = 0; j < L; ++j){
//             levels[j] = generatedGamesPath + "gen_game_" + z + "_lvl" + j +".txt";
//             if(saveActions) for(int k = 0; k < M; ++k)
//                 actionFiles[actionIdx++] = "actions_game_" + z + "_level_" + j + "_" + k + ".txt";
//         }
//         ArcadeMachine.runGames(game, levels, M, randomOneStepController, saveActions? actionFiles:null, seed);
//	     
//     }
       //127, 142, 145
//        int genGameNr = 219;
//     game = generatedGamesPath + "gen_game_" + genGameNr + ".txt";
//     level1 = generatedGamesPath + "gen_game_" + genGameNr + "_lvl" + 0 +".txt";
//     ArcadeMachine.playOneGame(game, level1, recordActionsFile, seed);
//   ArcadeMachine.runOneGame(game, level1, true, puzzleSolverController, recordActionsFile, seed);
        
        
        
        
    //10. Let all controllers play through generated games
//        controllers = new String[]{"controllers.randomOneStep.Agent", "controllers.random.Agent", "controllers.doNothing.Agent"};
//
//        
//
//        int N = 400, L = 1, M = 10;
//	    boolean saveActions = true;
//	    String[] levels = new String[L];
//	    String[] actionFiles = new String[L*M];
//      	for (int c = 0; c < controllers.length; c++) {
//      		String foldername =  controllers[c].split("\\.")[1] + "200ticks_gengames";
//	        try {
//				File dir = new File(foldername);
//				dir.mkdir();
//				System.setOut(new PrintStream(new FileOutputStream(foldername+"/gamedata.txt")));
//	 		} catch (FileNotFoundException e) {
//	 			e.printStackTrace();
//	 		}
//      	
//	        for(int i = 0; i < N; ++i){
//	            int actionIdx = 0;
//	            game = generatedGamesPath + "gen_game_" + i + ".txt";
//	            for(int j = 0; j < L; ++j){
//	                levels[j] = generatedGamesPath + "gen_game_" + i + "_lvl" + j +".txt";
//	                if(saveActions) for(int k = 0; k < M; ++k)
//	                    actionFiles[actionIdx++] = foldername + "/" + "actions_game_" + i + "_level_" + j + "_" + k + ".txt";
//	            }
//	            ArcadeMachine.runGames(game, levels, M, controllers[c], saveActions? actionFiles:null, seed);
//	        }  
//      	}
    }
}
