package controllers.MCTSishPlus;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import ontology.Types;
import ontology.Types.ACTIONS;
import ontology.Types.WINNER;
import tools.ElapsedCpuTimer;
import tools.Vector2d;
import core.game.StateObservation;
import core.player.AbstractPlayer;

public class Agent extends AbstractPlayer{

	ACTIONS[] actions;
	
	Random r;
               
    int blockSize = -1;
    int heightOfLevel = -1;
    int widthOfLevel = -1;
    
    double pow_constant = 2; 
    
    static int EXPAND_DEPTH = 4;
    
    ArrayDeque<Node> q = new ArrayDeque<Node>(); 
  
	int[][] randomActMap;
	static int[] faculty = new int[6];
	
	double[] boringPlaces;
		
	static final boolean VERBOSE = false;
	
	static{
		for (int i = 0; i < faculty.length; i++) {
			int val = 1;
			for (int j = i; j > 1; j--) {
				val *= j; 
			}
			faculty[i] = val;
		}
	}
    
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
    	
    	randomActMap = new int[faculty[actions.length]][];
    	int n = actions.length;
		int counter = 0;
		for (int i = 0; i < n; i++) {
			int n2 = n > 1 ? n : 1;
			for (int j = 0; j < n2; j++) {
				if (n2 > 1 && j == i) continue;
				int n3 = n > 2 ? n : 1;
				for (int j2 = 0; j2 < n3; j2++) {
					if (n3 > 1 && (j2 == i || j2 == j)) continue;
					int n4 = n > 3 ? n : 1;
					for (int k = 0; k < n4; k++) {
						if (n4 > 1 && (k == i || k == j || k == j2)) continue;
						int n5 = n > 4 ? n : 1;
						for (int k2 = 0; k2 < n5; k2++) {
							if (n5 > 1 && (k2 == i || k2 == j || k2 == j2 || k2 == k)) continue;
							int mapping = i + j*n + j2*n*n + k*n*n*n + k2*n*n*n*n;
							randomActMap[counter] = new int[n];
							for (int l = 0; l < n; l++) {
								int map = (mapping / ((int)Math.pow(n, l))) % n;
								randomActMap[counter][l] = map;
								mapping -= map;
							}
							
							counter++;
						}
					}
				}
			}
		}
    	
    	boringPlaces = new double[((heightOfLevel+2) * (widthOfLevel+2)) + 1];
    	for (int i = 0; i < boringPlaces.length; i++) {
    		boringPlaces[i] = 1;
		}
    }
	   
	@Override
	public ACTIONS act(StateObservation so, ElapsedCpuTimer et) {
		
		Vector2d initialPos = so.getAvatarPosition();
		
		//Boringplaces gives each tile in the game a float-value (between 0 and 1), which increase whenever the avatar stands on it.
		boringPlaces[getPositionKey(initialPos)] *= 2;  //Math.pow(boringPlaces[getPositionKey(initialPos)], boringPlacesExponent);
		
		
		q.clear();
        
        Node currentNode = new Node(so, new LinkedList<Integer>(), so.getAvatarPosition(), -1);
        q.add(currentNode);
		
        boolean[] wallActions = new boolean[actions.length];
        double[] bestActions = new double[actions.length];
        double[] boringActions = new double[actions.length];
		
        //////////////////
        //Main loop     //
        //////////////////
        double avgTimeTaken = 0, acumTimeTaken = 0;
        long remaining = et.remainingTimeMillis();
        int numIters = 0, remainingLimit = 5;
        ElapsedCpuTimer elapsedTimerIteration = new ElapsedCpuTimer();
        while(remaining > 2*avgTimeTaken && remaining > remainingLimit)
        {
        	if (q.isEmpty()){
        		throw new RuntimeException("THIS SHOULDN'T HAPPEN!");
//        		q.add(currentNode);
        	}
        	
        	Node n = q.poll();
        	boolean gameEnded = false;
        	
        	
        	double lastScore = n.state.getGameScore();
        	int depth = n.list.size();
        	int lastAct = depth > 0 ? n.list.peekLast() : -1;
        	int firstAct = depth > 0 ? n.list.peekFirst() : -1;
        	Vector2d lastPos = n.lastAvatarPos;
        	
        	if (depth > 0){
        		//Advance
        		n.state.advance(actions[lastAct]);
        		//Check for "wall actions"
        	}
        	//Node n is now initialized
        	
        	double score = n.state.getGameScore();
        	WINNER won = n.state.getGameWinner();
        	
        	if (won == WINNER.PLAYER_LOSES){
        		gameEnded = true;
        		score = lastScore - 100;
        	}else if (won == WINNER.PLAYER_WINS){
        		gameEnded = true;
        		score = lastScore + 100;
        	}
        	
        	if (VERBOSE) {System.out.println("Node initialized. Depth: " + depth + ", score: " + score + ", lastScore: " + lastScore + ", won: " + won);
        	System.out.println("last pos: " + lastPos + ", current pos: " + n.state.getAvatarPosition());
        	System.out.println(" action list: " + getActionList(n.list));}

        	//Set wall-actions
    		if (depth == 1){
    			if (n.state.getAvatarPosition().equals(lastPos) && actions[firstAct] != ACTIONS.ACTION_USE && score == lastScore) wallActions[firstAct] = true;
    		}
        	
    		//Set best-actions and boring-actions
        	if (depth > 0){
        		bestActions[firstAct] += (score - lastScore) * Math.pow(pow_constant, -(depth-1));
        		double boringPlaceVal = boringPlaces[getPositionKey(n.state.getAvatarPosition())];
        		
        		double boringActVal = boringPlaceVal;
//        		if (actions[lastAct] == ACTIONS.ACTION_USE) boringActVal *= 0.5;
        		
        		boringActVal *= Math.pow(pow_constant, (depth-1));
        		boringActions[firstAct] += boringActVal;
        	}
        	
        	
        	if (!gameEnded){
        		if (depth == 0){
        			int seed = Math.abs(r.nextInt()) % faculty[actions.length];
	        		for (int i = 0; i < actions.length; i++) {
	        			int act = randomActMap[seed][i];
						q.add(new Node(so.copy(), new LinkedList<Integer>(), initialPos, act));
//						q.add(new Node(n.state.copy(), (LinkedList<Integer>)n.list.clone(), initialPos, i));
					}
        		}else{
        			int seed = Math.abs(r.nextInt()) % faculty[actions.length];
	        		for (int i = 0; i < actions.length; i++) {
	        			int act = randomActMap[seed][i];
	        			if (isOppositeAction(act, firstAct)) continue;
	        			q.add(new Node(n.state, (LinkedList<Integer>)n.list.clone(), n.state.getAvatarPosition(), act));
	        			
	        			break; //<---- NOTICE - LOOP ONLY RUNS ONCE
	        		}
        		}
        	}else{
        		//If lost, expanding with current best action
                int bestAction = -1;
                double highestScore = Double.NEGATIVE_INFINITY;
                int seed = Math.abs(r.nextInt()) % faculty[actions.length];
                for (int i = 0; i < bestActions.length; i++) {
                	int act = randomActMap[seed][i];
                	double actScore = bestActions[i];
        			if (!wallActions[act] && actScore > highestScore){
        				highestScore = actScore;
        				bestAction = act;
        			}
                }
        		q.add(new Node(so.copy(), new LinkedList<Integer>(), initialPos, bestAction));
        	}
        	
        	
            numIters++;
            acumTimeTaken = (elapsedTimerIteration.elapsedMillis());
            avgTimeTaken = acumTimeTaken/numIters;
            remaining = et.remainingTimeMillis();
        }
		
        int bestAction = -1;
        double highestScore = Double.NEGATIVE_INFINITY;
        double lowestScore = Double.POSITIVE_INFINITY;
        boolean allScoresSame = true;
        double lastScore = 0;
        int seed = Math.abs(r.nextInt()) % faculty[actions.length];
        for (int i = 0; i < bestActions.length; i++) {
        	int act = randomActMap[seed][i];
        	double score = bestActions[act];
			if (!wallActions[act] && score > highestScore){
				highestScore = score;
				bestAction = act;
			}
			if (!wallActions[act] && score < lowestScore) lowestScore = score;
			
			if (i > 0 && score != lastScore) allScoresSame = false;
			lastScore = score;
			if (VERBOSE){System.out.println("bestActions["+act+"("+actions[act]+")]: " + bestActions[act]);
			System.out.println("is wall action? " + wallActions[act]);}
		}
        
        
        if (allScoresSame || highestScore-lowestScore < 0.01){ //Take least boring action
        	int leastBoringAction = -1;
        	double leastBoringness = Double.POSITIVE_INFINITY;
        	seed = Math.abs(r.nextInt()) % faculty[actions.length];
        	for (int i = 0; i < boringActions.length; i++) {
        		int act = randomActMap[seed][i];
        		if (wallActions[act]) continue;
        		if (boringActions[act] < leastBoringness){
        			leastBoringness = boringActions[act];
        			leastBoringAction = act;
        		}
    			if (VERBOSE)System.out.println("boringActions["+act+"("+actions[act]+")]: " + boringActions[act]);

			}

        	bestAction = leastBoringAction;
        }
		
        
        
        
        if (VERBOSE) System.out.println("Returning.. Iterations: " + numIters + " action: "+ actions[bestAction] + (allScoresSame?" -- allScoresSame!":"") + ", highestScore: "+ highestScore);
		
		return actions[bestAction];
	}
	
	

	private boolean isOppositeAction(int act, int lastAct) {
		ACTIONS action = actions[act];
		ACTIONS lastAction = actions[lastAct];
		switch (action) {
		case ACTION_UP:
			if (lastAction == ACTIONS.ACTION_DOWN) return true;
			break;
		case ACTION_DOWN:
			if (lastAction == ACTIONS.ACTION_UP) return true;
			break;
		case ACTION_LEFT:
			if (lastAction == ACTIONS.ACTION_RIGHT) return true;
			break;
		case ACTION_RIGHT:
			if (lastAction == ACTIONS.ACTION_LEFT) return true;
			break;
		default:
			break;
		}
		
		return false;
	}

	ArrayList<ACTIONS> getActionList(LinkedList<Integer> list){
    	ArrayList<ACTIONS> result = new ArrayList<ACTIONS>();
    	
    	for (Integer integer : list){
    		result.add(actions[integer]);
		}
    	return result;
    }
    
    private int getPositionKey(Vector2d vec){
    	if (vec == null) return 0;
    	if (vec.x < 0 || vec.y < 0 || vec.x > blockSize*widthOfLevel || vec.y > blockSize*heightOfLevel) return widthOfLevel *heightOfLevel + 5;
		return (int)((vec.x/blockSize) + (vec.y/blockSize) * widthOfLevel);
    	
    }
}
