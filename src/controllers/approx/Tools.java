package controllers.approx;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import tools.Utils;
import tools.Vector2d;
import core.game.Observation;
import core.game.StateObservation;

public class Tools {

    static final String gameSigFile = "src/controllers/approx/game_signatures.txt";

	
	public static void getWinTermination(String gameTitle) {
		
		String path = "examples/gridphysics/" + gameTitle + ".txt";
		
		 BufferedReader in;
			try {
				in = new BufferedReader(new FileReader(path));
		        String line = null;
		        boolean foundTermSet = false;
		        int c = 0;
		        while ((line = in.readLine()) != null) {
		        	if (!foundTermSet){
		        		if (line.contains("TerminationSet")) foundTermSet = true;
		        	}else{
		        		String trimmed = line.trim();
		        		trimmed = Utils.formatString(trimmed);
		        		System.out.println(trimmed);
		        		c++;
		        		if (c==2) break;
		        	}
		        	
		        }
		        in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
	}

	public static String detectGame(StateObservation so) {
		String title = "";
		
		ArrayList<GameSignature> signatures = parseSignatureFile();
		
		int xdim = so.getWorldDimension().width;
		int ydim = so.getWorldDimension().height;
		
		GameSignature gs = getSignatureValue(so);
		
		if (signatures.contains(gs)){
			
			for (GameSignature gameSignature : signatures) {
				if (gameSignature.equals(gs)){
					title = gameSignature.gameTitle;
					break;
				}
			}
		}else{
			storeSignature(gs);
		}
		
		return title;
		
	}

	public static void storeSignature(GameSignature gs) {
        FileWriter writer;

    	try {
    		writer = new FileWriter(gameSigFile, true);
    		String string = ""+gs.actCount + ",";
    		for (int i = 0; i < gs.itypArray.length; i++) {
				string += gs.itypArray[i] + ",";
			}
    		string += "unknown\n";
		    writer.write(string);
		    writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
//      writer = new PrintWriter(path, "UTF-8");

	
	}
	
	
	public static GameSignature getSignatureValue(StateObservation stateObs){
		int[] counts = new int[6];
		ArrayList<Observation>[][] observations = getObservations(stateObs);
		for (int i = 0; i < observations.length; i++) {
			if (observations[i] != null){
//				count += observations[i].length;
				for (int j = 0; j < observations[i].length; j++) {
					for (Observation obs : observations[i][j]) {
						counts[i] = obs.itype;
						break;
					}
				}
			}

		}
		
		
		return new GameSignature(stateObs.getAvailableActions().size(), counts);
	}
	
	public static ArrayList<GameSignature> parseSignatureFile() {
		ArrayList<GameSignature> result = new ArrayList<GameSignature>();
		
        BufferedReader in;
		try {
			in = new BufferedReader(new FileReader(gameSigFile));
	        String line = null;
	        while ((line = in.readLine()) != null) {
	        	String[] split = line.split(",");
	        	int[] vals = new int[split.length-1];
	        	int[] ityps = new int[split.length-2];
	        	for (int i = 0; i < vals.length; i++) {
					vals[i] = Integer.parseInt(split[i]);
					if (i<split.length-2) ityps[i] = Integer.parseInt(split[i+1]);
				}
	        	GameSignature sig = new GameSignature(vals[0],ityps);
	        	sig.gameTitle = split[split.length-1];
	        	
	        	result.add(sig);
	        }
	        in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		
		return result;
	}
	
	public static ArrayList<Observation>[][] getObservations(StateObservation stateObs){
		ArrayList<Observation>[][] result = new ArrayList[6][];
		
		Vector2d avatarPosition = stateObs.getAvatarPosition();
		 
        ArrayList<Observation>[] avatarShotPositions = stateObs.getFromAvatarSpritesPositions();
        ArrayList<Observation>[] npcPositions = stateObs.getNPCPositions(avatarPosition);
        ArrayList<Observation>[] movablePositions = stateObs.getMovablePositions(avatarPosition);
        ArrayList<Observation>[] immovablePositions = stateObs.getImmovablePositions(avatarPosition);
        ArrayList<Observation>[] resourcePositions = stateObs.getResourcesPositions(avatarPosition);
        ArrayList<Observation>[] portalPositions = stateObs.getPortalsPositions(avatarPosition);
		
        result[0] = avatarShotPositions;
        result[1] = npcPositions;
        result[2] = movablePositions;
        result[3] = immovablePositions;
        result[4] = resourcePositions;
        result[5] = portalPositions;
        
        
		return result;
	}
}
