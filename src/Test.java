import java.io.File;
import java.io.FileDescriptor;
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
        String sampleOLMCTSController = "controllers.sampleOLMCTS.Agent";
        String sampleGAController = "controllers.sampleGA.Agent";
        String dontDieController = "controllers.dontDie.Agent";
        String puzzleSolverController = "controllers.puzzleSolver.Agent";
        String puzzleSolverPlusController = "controllers.puzzleSolverPlus.Agent";
        String randomOneStepController = "controllers.randomOneStep.Agent";
        String randomController = "controllers.random.Agent";
        String doNothingController = "controllers.doNothing.Agent";
        String MCTSishController = "controllers.MCTSish.Agent";
        String MCTSishPlusController = "controllers.MCTSishPlus.Agent";
        String approxController = "controllers.approx.Agent";

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
        
//        games = new String[]{"aliens", "boulderdash", "butterflies", "chase", "frogs",
//                "missilecommand", "portals", "sokoban", "survivezombies", "zelda",
//                "camelRace", "digdug", "firestorms", "infection", "firecaster",
//                "overload", "pacman", "seaquest", "whackamole", "eggomania",
//                "bait", "boloadventures", "bombuzal", "brainman", "chipschallenge",
//                "modality", "painter", "realsokoban", "thecitadel", "zenpuzzle"};

        //Other settings
        boolean visuals = true;
        String recordActionsFile = null; //where to record the actions executed. null if not to save.
        int seed = new Random().nextInt();

        //Game and level to play
        
        String gameTitle = "centipede";
        String mutatedGamesPath = "../GameChanger/mutatedgames/";
        String generatedGamesPath = "../GameChanger/rnd_gen_games/";
        String atariGamesPath = "examples/atarigames/";
        
//        
//        try {
//			System.setOut(new PrintStream(new FileOutputStream("gamedata.txt")));
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
        
        int gameIdx = 8;
        int levelIdx =0; //level names from 0 to 4 (game_lvlN.txt).
        String game = gamesPath + gameTitle + ".txt";
        String level1 = gamesPath + gameTitle + "_lvl" + levelIdx +".txt";

        
//        game = atariGamesPath + gameTitle + ".txt";
//        level1 = atariGamesPath + gameTitle + "_lvl" + levelIdx +".txt";
        
        // 1. This starts a game, in a level, played by a human.
//        ArcadeMachine.playOneGame(game, level1, recordActionsFile, seed);

//         2. This plays a game in a level by the controller.
//        ArcadeMachine.runOneGame(game, level1, visuals, sampleMCTSController, recordActionsFile, seed);

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
        
        String[] controllers = new String[]{dontDieController, sampleOLMCTSController, MCTSishController, randomOneStepController,
        		sampleOneStepController, randomController, doNothingController};
        
        
        //"Good games"
        games = new String[]{"aliens", "astroblast", "boulderdash", "centipede", "crackpots",
        		"digdug", "eggomania", "frogs", "missilecommand", "pacman", "seaquest", 
        		"solarfox", "zelda"};
        
        //Atari games
//      games = new String[]{"solarfox", "centipede", "astroblast"};
        
//        controllers = new String[]{dontDieController};
        
        
        //6. Let all controllers play through a series of games
//        int N = games.length, L = 1, M = 10;
//        boolean saveActions = true;
//        String[] levels = new String[L];
//        String[] actionFiles = new String[L*M];
//        for (int c = 0; c < controllers.length; c++) {
//        	String foldername = controllers[c].split("\\.")[1] + "_2000ticks_designed";
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
//	            ArcadeMachine.runGames(game, levels, M, controllers[c], saveActions? actionFiles:null);
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
//     int MUTATIONS = 10, N = 20, L = 1, M = 25;
//     boolean saveActions = true;
//     for (int c = 0; c < controllers.length; c++) {
//    	 String foldername =  controllers[c].split("\\.")[1] + "800t25pt";
//	
//	     for (int z = 0; z < MUTATIONS; z++) {
//	    	 String outputFolder = foldername + "_mutation_"+ z;
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
//     gameTitle = "butterflies";
//        game = mutatedGamesPath + gameTitle + "_mutation_" + 11 + ".txt";
//        level1 = gamesPath + gameTitle + "_lvl" + 0 +".txt";
//        ArcadeMachine.playOneGame(game, level1, recordActionsFile, seed);
//      ArcadeMachine.runOneGame(game, level1, visuals, dontDieController, recordActionsFile, seed);
       
       
       
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
       //127, 142, 145, 287
        //72
//        gen_game_4779, gen_game_4036, gen_game_1215, gen_game_3954, gen_game_4708, gen_game_4381
//        int genGameNr = 4381;
//     game = generatedGamesPath + "gen_game_" + genGameNr + ".txt";
//     level1 = generatedGamesPath + "gen_game_" + genGameNr + "_lvl" + 0 +".txt";
//     ArcadeMachine.playOneGame(game, level1, recordActionsFile, seed);
//   ArcadeMachine.runOneGame(game, level1, true, dontDieController, recordActionsFile, seed);
        
        
        
        
    //10. Let all controllers play through generated games
//        controllers = new String[]{dontDieController};

//        int[] genGameList = new int[]{0, 4, 6, 8, 10, 13, 16, 23, 26, 37, 38, 49, 65, 70, 72, 73, 77, 80, 89, 90, 99, 108, 111, 112, 114, 115, 121, 127, 130, 137, 139, 142, 144, 145, 149, 150, 155, 157, 163, 169, 170, 174, 177, 178, 181, 189, 197, 205, 206, 207, 211, 212, 216, 218, 220, 223, 235, 239, 242, 247, 249, 254, 262, 266, 267, 272, 276, 278, 284, 286, 287, 289, 291, 307, 309, 317, 319, 323, 324, 326, 339, 344, 354, 356, 358, 360, 363, 365, 370, 371, 372, 380, 388, 394};

//        controllers = new String[]{"controllers.dontDie.Agent", "controllers.sampleGA.Agent", 
//        		"controllers.randomOneStep.Agent", "controllers.sampleonesteplookahead.Agent", "controllers.random.Agent",
//        		"controllers.doNothing.Agent"};
        
//        int[] genGameList = new int[]{6, 9, 45, 46, 53, 55, 63, 65, 68, 71, 72, 75, 78, 81, 87, 93, 96, 107, 110, 111, 124, 130, 133, 139, 144, 146, 149, 152, 155, 157, 167, 168, 189, 198, 204, 205, 246, 249, 251, 258, 261, 264, 266, 268, 276, 292, 294, 306, 308, 312, 314, 316, 318, 326, 341, 342, 345, 356, 362, 375, 379, 381, 386, 392, 401, 406, 416, 428, 444, 453, 456, 459, 460, 471, 472, 473, 474, 476, 495, 497, 511, 513, 517, 533, 537, 543, 545, 549, 555, 556, 565, 575, 578, 582, 584, 592, 597, 601, 610, 630, 632, 634, 635, 637, 638, 645, 651, 655, 661, 665, 666, 673, 676, 679, 687, 691, 695, 697, 701, 704, 706, 711, 714, 725, 729, 734, 741, 745, 746, 747, 758, 771, 780, 792, 798, 801, 805, 816, 819, 824, 834, 844, 849, 850, 859, 863, 865, 866, 869, 875, 877, 894, 899, 900, 904, 907, 911, 928, 939, 940, 941, 949, 952, 953, 956, 959, 977, 995, 996, 998, 1006, 1007, 1011, 1013, 1025, 1036, 1038, 1041, 1045, 1049, 1050, 1052, 1066, 1067, 1068, 1071, 1077, 1079, 1084, 1085, 1086, 1090, 1093, 1096, 1099, 1115, 1117, 1120, 1121, 1122, 1127, 1132, 1136, 1138, 1141, 1142, 1144, 1147, 1154, 1157, 1158, 1159, 1160, 1169, 1173, 1176, 1185, 1189, 1191, 1195, 1197, 1201, 1203, 1207, 1209, 1210, 1211, 1212, 1215, 1216, 1225, 1232, 1233, 1235, 1238, 1240, 1242, 1260, 1262, 1265, 1266, 1271, 1276, 1281, 1283, 1287, 1288, 1292, 1310, 1313, 1320, 1324, 1332, 1342, 1343, 1344, 1351, 1358, 1364, 1366, 1375, 1376, 1381, 1384, 1392, 1395, 1405, 1406, 1409, 1412, 1422, 1439, 1448, 1456, 1457, 1459, 1463, 1466, 1468, 1469, 1471, 1487, 1488, 1490, 1492, 1497, 1500, 1503, 1512, 1515, 1517, 1525, 1527, 1529, 1531, 1535, 1544, 1545, 1551, 1556, 1570, 1574, 1580, 1589, 1590, 1601, 1629, 1634, 1643, 1645, 1648, 1649, 1655, 1660, 1661, 1666, 1667, 1669, 1670, 1677, 1679, 1689, 1697, 1699, 1701, 1718, 1719, 1727, 1728, 1743, 1746, 1747, 1752, 1754, 1768, 1769, 1776, 1778, 1791, 1797, 1803, 1806, 1810, 1815, 1816, 1821, 1824, 1825, 1828, 1841, 1845, 1849, 1858, 1863, 1871, 1878, 1886, 1892, 1894, 1901, 1903, 1904, 1907, 1910, 1915, 1923, 1926, 1936, 1948, 1950, 1958, 1968, 1969, 1974, 1980, 1985, 1989, 1998, 1999, 2002, 2007, 2009, 2012, 2015, 2016, 2021, 2057, 2068, 2073, 2079, 2083, 2084, 2085, 2092, 2096, 2097, 2100, 2101, 2102, 2108, 2116, 2117, 2118, 2119, 2121, 2122, 2126, 2127, 2132, 2138, 2142, 2149, 2153, 2155, 2157, 2159, 2162, 2167, 2171, 2173, 2175, 2182, 2195, 2200, 2217, 2219, 2221, 2228, 2231, 2232, 2236, 2237, 2239, 2254, 2258, 2259, 2273, 2278, 2287, 2288, 2292, 2297, 2313, 2317, 2341, 2344, 2347, 2349, 2355, 2358, 2378, 2379, 2383, 2387, 2389, 2393, 2394, 2402, 2408, 2410, 2411, 2418, 2435, 2442, 2444, 2448, 2451, 2452, 2453, 2458, 2463, 2470, 2479, 2489, 2490, 2495, 2499, 2503, 2506, 2513, 2517, 2522, 2523, 2524, 2525, 2533, 2534, 2542, 2548, 2550, 2551, 2552, 2555, 2560, 2567, 2573, 2582, 2586, 2589, 2593, 2597, 2613, 2621, 2633, 2637, 2641, 2647, 2660, 2665, 2667, 2668, 2671, 2675, 2676, 2679, 2681, 2684, 2699, 2711, 2712, 2749, 2760, 2764, 2766, 2767, 2774, 2799, 2800, 2806, 2811, 2812, 2818, 2821, 2829, 2830, 2834, 2835, 2847, 2848, 2849, 2857, 2860, 2870, 2872, 2882, 2887, 2902, 2903, 2920, 2921, 2928, 2930, 2932, 2933, 2934, 2935, 2938, 2940, 2960, 2961, 2964, 2965, 2971, 2976, 2981, 2987, 2991, 3009, 3012, 3013, 3015, 3019, 3024, 3029, 3031, 3033, 3046, 3047, 3048, 3053, 3076, 3096, 3097, 3101, 3106, 3116, 3117, 3118, 3119, 3120, 3125, 3145, 3151, 3159, 3161, 3167, 3172, 3173, 3174, 3182, 3190, 3195, 3200, 3208, 3210, 3214, 3217, 3227, 3242, 3246, 3250, 3251, 3260, 3268, 3269, 3272, 3282, 3284, 3294, 3295, 3302, 3308, 3309, 3310, 3313, 3320, 3328, 3347, 3354, 3369, 3374, 3378, 3380, 3394, 3397, 3405, 3408, 3417, 3418, 3426, 3431, 3435, 3444, 3445, 3460, 3469, 3472, 3476, 3481, 3490, 3491, 3493, 3494, 3504, 3507, 3509, 3510, 3512, 3517, 3518, 3520, 3525, 3531, 3538, 3541, 3550, 3566, 3570, 3576, 3577, 3578, 3583, 3589, 3590, 3594, 3596, 3601, 3613, 3618, 3621, 3624, 3638, 3656, 3658, 3663, 3666, 3670, 3679, 3681, 3683, 3687, 3692, 3697, 3698, 3699, 3705, 3712, 3735, 3737, 3738, 3742, 3747, 3751, 3759, 3763, 3768, 3770, 3775, 3781, 3782, 3789, 3795, 3798, 3803, 3805, 3808, 3809, 3814, 3819, 3822, 3824, 3834, 3845, 3850, 3853, 3855, 3858, 3864, 3868, 3875, 3878, 3885, 3914, 3916, 3925, 3927, 3930, 3936, 3951, 3953, 3954, 3961, 3975, 3977, 3978, 3980, 3981, 3991, 3999, 4007, 4010, 4015, 4021, 4024, 4027, 4036, 4042, 4044, 4046, 4050, 4055, 4057, 4065, 4070, 4073, 4084, 4089, 4095, 4096, 4100, 4103, 4109, 4110, 4117, 4120, 4121, 4125, 4136, 4143, 4145, 4147, 4156, 4159, 4160, 4163, 4168, 4169, 4175, 4180, 4193, 4196, 4197, 4205, 4211, 4212, 4218, 4220, 4224, 4229, 4237, 4240, 4242, 4246, 4248, 4250, 4251, 4263, 4265, 4274, 4277, 4282, 4283, 4284, 4293, 4298, 4300, 4302, 4319, 4322, 4328, 4331, 4333, 4338, 4342, 4351, 4365, 4381, 4385, 4386, 4391, 4393, 4395, 4413, 4416, 4419, 4426, 4431, 4442, 4445, 4449, 4450, 4452, 4475, 4480, 4483, 4486, 4491, 4498, 4505, 4523, 4531, 4536, 4537, 4546, 4551, 4553, 4567, 4572, 4573, 4574, 4575, 4581, 4583, 4587, 4592, 4593, 4607, 4612, 4614, 4615, 4630, 4643, 4644, 4645, 4646, 4648, 4653, 4654, 4665, 4666, 4668, 4671, 4676, 4702, 4708, 4714, 4753, 4755, 4757, 4762, 4775, 4779, 4780, 4782, 4784, 4788, 4789, 4792, 4793, 4802, 4813, 4817, 4824, 4833, 4834, 4835, 4837, 4851, 4853, 4857, 4859, 4863, 4865, 4872, 4881, 4882, 4883, 4891, 4900, 4903, 4907, 4913, 4914, 4915, 4920, 4934, 4937, 4938, 4942, 4945, 4956, 4957, 4969, 4972, 4977, 4980, 4996, 4997};
//        int[] genGameList = new int[]{6, 9, 45, 46, 53, 55, 63, 65, 68, 71, 72, 75, 78, 81, 87, 93, 96, 107, 110, 111, 124, 130, 133, 139, 144, 146, 149, 152, 155, 157, 167, 168, 189, 198, 204, 205, 246, 249, 251, 258, 261, 264, 266, 268, 276, 292, 294, 306, 308, 312, 314, 316, 318, 326, 341, 342, 345, 356, 362, 375, 379, 381, 386, 392, 401, 406, 416, 428, 444, 453, 456, 459, 460, 471, 472, 473, 474, 476, 495, 497, 511, 513, 517, 533, 537, 543, 545, 549, 555, 556, 565, 575, 578, 582, 584, 592, 597, 601, 610, 630, 632, 634, 635, 637, 638, 645, 651, 655, 661, 665, 666, 673, 676, 679, 687, 691, 695, 697, 701, 704, 706, 711, 714, 725, 729, 734, 741, 745, 746, 747, 758, 771, 780, 792, 798, 801, 805, 816, 819, 824, 834, 844, 849, 850, 859, 863, 865, 866, 869, 875, 877, 894, 899, 900, 904, 907, 911, 928, 939, 940, 941, 949, 952, 953, 956, 959, 977, 995, 996, 998, 1006, 1007, 1011, 1013, 1025, 1036, 1038, 1041, 1045, 1049, 1050, 1052, 1066, 1067, 1068, 1071, 1077, 1079, 1084, 1085, 1086, 1090, 1093, 1096, 1099, 1115, 1117, 1120, 1121, 1122, 1127, 1132, 1136, 1138, 1141, 1142, 1144, 1147, 1154, 1157, 1158, 1159, 1160, 1169, 1173};
        
        
        int N = 400, L = 1, M = 10;
	    boolean saveActions = true;
	    String[] levels = new String[L];
	    String[] actionFiles = new String[L*M];
      	for (int c = 0; c < controllers.length; c++) {
//          	for (int c = 0; c < 1; c++) {

      		PrintStream ps = null;
      		
      		String foldername =  controllers[c].split("\\.")[1] + "_2000ticks_rndgen";
	        try {
				File dir = new File(foldername);
				dir.mkdir();
				ps = new PrintStream(new FileOutputStream(foldername+"/gamedata.txt"));
				System.setOut(ps);
	 		} catch (FileNotFoundException e) {
	 			e.printStackTrace();
	 		}
      	
	        for(int i = 0; i < N; ++i){
//	        for(int g = 0; g < genGameList.length; ++g){
//	        	int i = genGameList[g];
	        	long timer = System.nanoTime();
	        	
	        	System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
	        	System.out.println("Playing game: " + i);
	        	
	        	System.setOut(ps);
	        	 
	            int actionIdx = 0;
	            game = generatedGamesPath + "gen_game_" + i + ".txt";
	            for(int j = 0; j < L; ++j){	            	
	                levels[j] = generatedGamesPath + "gen_game_" + i + "_lvl" + j +".txt";
	                if(saveActions) for(int k = 0; k < M; ++k)
	                    actionFiles[actionIdx++] = foldername + "/" + "actions_game_" + i + "_level_" + j + "_" + k + ".txt";
	            }
	            ArcadeMachine.runGames(game, levels, M, controllers[c], saveActions? actionFiles:null);
	            
	        	System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
	            System.out.println("Time taken: " + (System.nanoTime() - timer)/1000000000.0 + " s");
	        }  
	        
      }
        
        
        
//        11. Let all controllers play through a series of games, with generated levels
//      int N = 20, L = 5, M = 25;
//      String genLvlsPath = "../GameChanger/levelgen/";
//      boolean saveActions = true;
//      String[] levels = new String[L];
//      String[] actionFiles = new String[L*M];
//      for (int c = 0; c < controllers.length; c++) {
//      	String foldername = controllers[c].split("\\.")[1] + "800t25pt_designed_genlvls";
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
//	                levels[j] = genLvlsPath + games[i] + "_genlvl" + j +".txt";
//	                if(saveActions) for(int k = 0; k < M; ++k)
//	                    actionFiles[actionIdx++] = foldername + "/" + "actions_game_" + i + "_genlevel_" + j + "_" + k + ".txt";
//	            }
//	            ArcadeMachine.runGames(game, levels, M, controllers[c], saveActions? actionFiles:null, seed);
//	        }  
//      	}
    }
}
