package controllers.approx;

import java.util.ArrayList;
import java.util.Random;

import ontology.Types;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import tools.Vector2d;
import core.game.Observation;
import core.game.StateObservation;
import core.player.AbstractPlayer;

public class Agent extends AbstractPlayer{

	ACTIONS[] actions;
	
	Random r;
               
    int blockSize = -1;
    int heightOfLevel = -1;
    int widthOfLevel = -1;
    
    
    public Agent(StateObservation so, ElapsedCpuTimer elapsedTimer)
    {
        ArrayList<Types.ACTIONS> act = so.getAvailableActions();
        actions = new Types.ACTIONS[act.size()];
        for(int i = 0; i < actions.length; ++i)
        {
            actions[i] = act.get(i);
        }
    	r = new Random();
   
    	blockSize = so.getBlockSize();
    	heightOfLevel = (int) (so.getWorldDimension().height / so.getBlockSize());
    	widthOfLevel = (int) (so.getWorldDimension().width / so.getBlockSize());
    	
    	
    	String gameTitle = Tools.detectGame(so);
    	Tools.getWinTermination(gameTitle);
    }
	   




	@Override
	public ACTIONS act(StateObservation so, ElapsedCpuTimer et) {
		
		int bestAction = 0;
		
		Action[] actionList = new Action[actions.length];
		
        for(int i = 0; i < actions.length; ++i)
        {
        	actionList[i] = new Action(i, so.copy());
        	actionList[i].so.advance(actions[i]);
        }
		
        //////////////////
        //Main loop     //
        //////////////////
        double avgTimeTaken = 0, acumTimeTaken = 0;
        long remaining = et.remainingTimeMillis();
        int numIters = 0, remainingLimit = 5;
        ElapsedCpuTimer elapsedTimerIteration = new ElapsedCpuTimer();
        while(remaining > 2*avgTimeTaken && remaining > remainingLimit)
        {
    
            numIters++;
            acumTimeTaken = (elapsedTimerIteration.elapsedMillis());
            avgTimeTaken = acumTimeTaken/numIters;
            remaining = et.remainingTimeMillis();
        }
        
//        evaluateState(so);
		
   
		return actions[bestAction];
	}
	
	


	

	
   public double evaluateState(StateObservation stateObs) {
        Vector2d avatarPosition = stateObs.getAvatarPosition();

        ArrayList<Observation>[] avatarShotPositions = stateObs.getFromAvatarSpritesPositions();
        ArrayList<Observation>[] npcPositions = stateObs.getNPCPositions(avatarPosition);
        ArrayList<Observation>[] movablePositions = stateObs.getMovablePositions(avatarPosition);
        ArrayList<Observation>[] immovablePositions = stateObs.getImmovablePositions(avatarPosition);
        ArrayList<Observation>[] resourcePositions = stateObs.getResourcesPositions(avatarPosition);
        ArrayList<Observation>[] portalPositions = stateObs.getPortalsPositions(avatarPosition);
//        HashMap<Integer, Integer> resources = stateObs.getAvatarResources();
//
//        ArrayList<Observation>[] npcPositionsNotSorted = stateObs.getNPCPositions();

        double score = 0;
        
        //Score
        
        //Has ended (won / lost)
        
        //npcPositions
        System.out.println("shots:");
        if (avatarShotPositions != null) {
        	for (ArrayList<Observation> shots : avatarShotPositions) {
        		if (shots.size() > 0) System.out.println(shots.get(0).itype);
        	}
        }
        
        System.out.println("npcs:");
        if (npcPositions != null) {
        	for (ArrayList<Observation> npcs : npcPositions) {
        		System.out.println(npcs.get(0).itype);
        	}
        }
        
        System.out.println("movable:");
        if (movablePositions != null) {
        	for (ArrayList<Observation> mov : movablePositions) {
        		System.out.println(mov.get(0).itype);
        	}
        }
        
        System.out.println("immovable:");
        if (immovablePositions != null) {
        	for (ArrayList<Observation> immov : immovablePositions) {
        		System.out.println(immov.get(0).itype);
        	}
        }
        
        System.out.println("resources:");
        if (resourcePositions != null) {
        	for (ArrayList<Observation> res : resourcePositions) {
        		System.out.println(res.get(0).itype);
        	}
        }
        
        System.out.println("portals:");
        if (portalPositions != null) {
        	for (ArrayList<Observation> port : portalPositions) {
        		System.out.println(port.get(0).itype);
        	}
        }
        
        
        //portalPositions
        
        
        
        
        
        
//        double won = 0;
//        if (stateObs.getGameWinner() == Types.WINNER.PLAYER_WINS) {
//            won = 1000000000;
//        } else if (stateObs.getGameWinner() == Types.WINNER.PLAYER_LOSES) {
//            return -999999999;
//        }
//
//
//        double minDistance = Double.POSITIVE_INFINITY;
//        Vector2d minObject = null;
//        int minNPC_ID = -1;
//        int minNPCType = -1;
//
//        int npcCounter = 0;
//        if (npcPositions != null) {
//            for (ArrayList<Observation> npcs : npcPositions) {
//                if(npcs.size() > 0)
//                {
//                    minObject   = npcs.get(0).position; //This is the closest guy
//                    minDistance = npcs.get(0).sqDist;   //This is the (square) distance to the closest NPC.
//                    minNPC_ID   = npcs.get(0).obsID;    //This is the id of the closest NPC.
//                    minNPCType  = npcs.get(0).itype;    //This is the type of the closest NPC.
//                    npcCounter += npcs.size();
//                }
//            }
//        }

//        if (portalPositions == null) {
//
//            double score = 0;
//            if (npcCounter == 0) {
//                score = stateObs.getGameScore() + won*100000000;
//            } else {
//                score = -minDistance / 100.0 + (-npcCounter) * 100.0 + stateObs.getGameScore() + won*100000000;
//            }
//
//            return score;
//        }
//
//        double minDistancePortal = Double.POSITIVE_INFINITY;
//        Vector2d minObjectPortal = null;
//        for (ArrayList<Observation> portals : portalPositions) {
//            if(portals.size() > 0)
//            {
//                minObjectPortal   =  portals.get(0).position; //This is the closest portal
//                minDistancePortal =  portals.get(0).sqDist;   //This is the (square) distance to the closest portal
//            }
//        }
//
//        double score = 0;
//        if (minObjectPortal == null) {
//            score = stateObs.getGameScore() + won*100000000;
//        }
//        else {
//            score = stateObs.getGameScore() + won*1000000 - minDistancePortal * 10.0;
//        }

        return score;
    }

    

}
