package controllers.MCTSish;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
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
    
    static int EXPAND_DEPTH = 4;
    
    static int BAD_NODE_NOT_COUNTED_DEPTH = 4;
    static float PROB_MOVING_INTO_WALL = 0.5f;
    
    ArrayDeque<Node> q = new ArrayDeque<Node>(); 
  
	int[][] randomActMap;
	static int[] faculty = new int[6];
	
	static final boolean VERBOSE = false;

	private int currentNodeAdds;
	
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
    }
	   
	@Override
	public ACTIONS act(StateObservation so, ElapsedCpuTimer et) {
		
		q.clear();
        
        Node currentNode = new Node(so, new LinkedList<Integer>(), so.getAvatarPosition(), -1);
        q.add(currentNode);
		
        boolean[] wallActions = new boolean[actions.length];
        double[] bestActions = new double[actions.length];
		
        //////////////////
        //Main loop     //
        //////////////////
        double avgTimeTaken = 0, acumTimeTaken = 0;
        long remaining = et.remainingTimeMillis();
        int numIters = 0, remainingLimit = 5;
        int[] numItersPerAct = new int[actions.length];
        currentNodeAdds = 0;
        boolean expandNew = true;
        ElapsedCpuTimer elapsedTimerIteration = new ElapsedCpuTimer();
        while(remaining > 2*avgTimeTaken && remaining > remainingLimit)
        {
        	if (q.isEmpty()){
        		q.add(currentNode);
        		currentNodeAdds++;
        	}
        	
        	Node n = q.poll();
        	boolean expandFromNode = true;
        	
        	
        	int lastScore = (int) n.state.getGameScore();
        	int depth = n.list.size();
        	int lastAct = depth > 0 ? n.list.peekLast() : -1;
        	int firstAct = depth > 0 ? n.list.peekFirst() : -1;
        	Vector2d lastPos = n.lastAvatarPos;
        	
        	if (depth > 0){
        		//Advance
        		n.state.advance(actions[lastAct]);
        		
        		if (n.state.getAvatarPosition() == null) return ACTIONS.ACTION_NIL;
        		
        		//Check for "wall actions"
        		if (depth == 1 && n.state.getAvatarPosition().equals(lastPos) && actions[firstAct] != ACTIONS.ACTION_USE) wallActions[firstAct] = true;
        	}
        	//Node n is now initialized
        	
        	double score = n.state.getGameScore();
        	WINNER won = n.state.getGameWinner();
        	
//        	if (VERBOSE) {System.out.println("Node initialized. Depth: " + depth + " action list: " + getActionList(n.list) + ", score: " + score + ", won: " + won);
//        	System.out.println("last pos: " + lastPos + ", current pos: " + n.state.getAvatarPosition());}

        	if (won == WINNER.PLAYER_LOSES){
        		expandFromNode = false;
        		score = lastScore - 100;
        	}else if (won == WINNER.PLAYER_WINS){
        		score = lastScore + 10000;
        	}
        	
        	if (depth > 0){
//        		bestActions[n.list.peekFirst()] += (score - lastScore) * Math.pow(actions.length, -(depth-1));
        		if (score > lastScore){
        			bestActions[n.list.peekFirst()] += (score - lastScore) * Math.pow(actions.length, -(depth-1));
        		}else if (score < lastScore && depth < BAD_NODE_NOT_COUNTED_DEPTH){
        			if (won == WINNER.PLAYER_LOSES){
        				bestActions[n.list.peekFirst()] += (score - lastScore) * Math.pow(actions.length, -(depth-1));
        			}
        		}
        		
        	}
        	
        	
        	if (expandFromNode){
				if (depth == 0){
        			int seed = Math.abs(r.nextInt()) % faculty[actions.length];
	        		for (int i = 0; i < actions.length; i++) {
	        			int act = randomActMap[seed][i];
						q.add(new Node(n.state.copy(), cloneOf(n.list), lastPos, act));
					}
	        		expandNew = true;
        		}else{
        			int seed = Math.abs(Math.abs(r.nextInt()) % faculty[actions.length]);
	        		for (int i = 0; i < actions.length; i++) {
	        			int act = randomActMap[seed][i];
	        			if (isOppositeAction(act, firstAct)) continue;
	        			q.add(new Node(n.state, cloneOf(n.list), n.state.getAvatarPosition(), act));
	        			
	        			break; //<---- NOTICE - LOOP ONLY RUNS ONCE
	        		}
        		}

        		if (expandNew && depth == EXPAND_DEPTH){
        			q.add(currentNode);
        			expandNew = false;
        			currentNodeAdds++;
        		}
        	}
        	
            numIters++;
            if (depth>0)numItersPerAct[firstAct]++;
            acumTimeTaken = (elapsedTimerIteration.elapsedMillis());
            avgTimeTaken = acumTimeTaken/numIters;
            remaining = et.remainingTimeMillis();
        }
		
        int bestAction = -1;
        double highestScore = Double.NEGATIVE_INFINITY;
        boolean allScoresSame = true;
        double lastScore = 0;
        for (int i = 0; i < bestActions.length; i++) {
        	double score = bestActions[i];
			if ((!wallActions[i] || r.nextFloat() > PROB_MOVING_INTO_WALL) && score > highestScore){
				highestScore = score;
				bestAction = i;
			}
			if (i > 0 && score != lastScore) allScoresSame = false;
			lastScore = score;
			if (VERBOSE){System.out.println("bestActions["+i+"("+actions[i]+")]: " + bestActions[i]);
			System.out.println("is wall action? " + wallActions[i]);}
		}
        
        if (allScoresSame || highestScore < -999999) bestAction = r.nextInt(actions.length);
		
        if (VERBOSE) System.out.println("Returning.. Iterations: " + numIters + " action: "+ actions[bestAction] + (allScoresSame?" -- allScoresSame!":"") + ", highestScore: "+ highestScore);
		if (VERBOSE) System.out.println("numItersPerAct: " + Arrays.toString(numItersPerAct) + " - currentNodeAdds: " + currentNodeAdds);
		return actions[bestAction];
	}
	
	

	private LinkedList<Integer> cloneOf(LinkedList<Integer> list) {
		LinkedList<Integer> listClone = new LinkedList<Integer>();
		
		if (list.clone() instanceof LinkedList<?>){
			for(int i = 0; i < ((LinkedList<?>)list).size(); i++){
				Object item = ((LinkedList<?>)list).get(i);
	            if(item instanceof Integer){
	            	listClone.add((Integer) item);
	            }
			}
		}
		return listClone;
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
    
	

    

}
