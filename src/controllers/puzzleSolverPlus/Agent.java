package controllers.puzzleSolverPlus;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;

import ontology.Types;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import tools.ElapsedCpuTimer.TimerType;
import tools.Vector2d;
import controllers.puzzleSolverPlus.Moveable;
import core.competition.CompetitionParameters;
import core.game.Observation;
import core.game.StateObservation;
import core.player.AbstractPlayer;

public class Agent extends AbstractPlayer{

	
	ACTIONS[] actions;
	Random r;
    int blockSize = -1;
    int heightOfLevel = -1;
    int widthOfLevel = -1;
    
    boolean foundSolution = false;
    LinkedList<Integer> solution = new LinkedList<Integer>();
//    int[] solution;
    int solutionIndex = 0;
    
    ArrayDeque<Node> q = new ArrayDeque<Node>();
    HashSet<Node> visitedNodes = new HashSet<Node>();
    
    
    final int MAX_VISITED_NODES = 800000;
	
    final boolean VERBOSE = false;
    final boolean LOOP_VERBOSE = false;
    
    final boolean depthFirst = false;
    
    boolean[] wallPositions;
    
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
    	
    	wallPositions = new boolean[heightOfLevel * widthOfLevel + 1];
    	
		if (so.getImmovablePositions() != null){
			for (ArrayList<Observation> arrayList : so.getImmovablePositions()) {
				for (Observation observation : arrayList) {
					if (observation.itype != 0) continue; //<- wall
					wallPositions[getPositionKey(observation.position)] = true;
				}
			}
		}    	
    }
    boolean first = true;
    long tim;

    
	public ACTIONS act(StateObservation so, ElapsedCpuTimer ect) {
		if (first){
			first = false;
			tim = System.currentTimeMillis();
			
			return ACTIONS.ACTION_NIL;
		}
//		if (foundSolution && solutionIndex< solution.size()-1){
		if (foundSolution){
			solutionIndex++;
			return actions[solution.get(solutionIndex)];
//			return actions[solution[solutionIndex]];
			
		}
				
		if (q.isEmpty()){
			Node currentNode = new Node(so, new LinkedList<Integer>(), new HashSet<Moveable>(), -1);
//			Node currentNode = new Node(so, new int[MAX_ACTIONS], 0, -1);
	        q.add(currentNode);
		}
		
        double avgTimeTaken = 0, acumTimeTaken = 0;
        long remaining = ect.remainingTimeMillis();
        int numIters = 0, remainingLimit = 5, lastDepth = 0;
        ElapsedCpuTimer elapsedTimerIteration = new ElapsedCpuTimer(TimerType.CPU_TIME);
        boolean queueEmpty = false;
        while(remaining > 2*avgTimeTaken && remaining > remainingLimit)
        {
            if (LOOP_VERBOSE) System.out.println("START LOOP--" + elapsedTimerIteration.elapsedMillis() + " --> " + acumTimeTaken + " (" + remaining + "),  avgTimeTaken: " + avgTimeTaken + " - iters: " +numIters);
            if (LOOP_VERBOSE) System.out.println("visited node size: " + visitedNodes.size());
            
        	boolean nodeAlreadyExists = false;
            boolean moveablesHaveChanged = false;
            boolean nodeInteresting = true;
        	
        	if (q.isEmpty()){
        		if (VERBOSE) System.out.println("QUEUE EMPTY!!!");
        		
        		wasteTime(1);
        		return ACTIONS.ACTION_NIL; 
        	}
        	
        	if (visitedNodes.size() + q.size() > MAX_VISITED_NODES){
        		if (VERBOSE) System.out.println("Too many visited nodes! (over " + MAX_VISITED_NODES + ")");
        		
        		wasteTime(1);
        		return ACTIONS.ACTION_NIL; 
        	}
        	
        	Node n = null;
    		n = q.pollFirst();
        	
        	
        	if (n.lastAction >= 0){
        		n.list = (LinkedList<Integer>) n.list.clone();
//        		n.list = n.list.clone();
	        	n.addAction(n.lastAction);
	        	n.state = n.state.copy();
        	}
        	
        	int d = n.list.size();
                        
        	HashSet<Moveable> lastMoveables = (HashSet<Moveable>) n.moveables.clone();
            
            double lastVal = value(n.state);
            if (d > 0){
            	n.state.advance(actions[n.lastAction]);
            }

            n.moveables = getMoveables(n.state);
        	n.avatarPos = n.state.getAvatarPosition();
        	double val = value(n.state);
        	//Node has now finished initializing
        	
        	
        	if (!lastMoveables.equals(n.moveables)){
        		moveablesHaveChanged = true;
        		nodeInteresting = true;
        	}
        	if (val > lastVal) nodeInteresting = true;
        	
        	if (LOOP_VERBOSE){
        		System.out.println("---New node initialized!--- (iteration: " + numIters + " - q length: + " + q.size() +")");
        		System.out.println("Node action list: " + getActionList(n.list));
        		System.out.println("Node value: " + val);
	            System.out.println("Avatar pos: " + n.avatarPos);
	            System.out.println("Moveables: " + n.moveables);
        	}
        	
        	if (visitedNodes.contains(n)){
//        		Node existingNode = null;
//        		for (Node node : visitedNodes) if (node.equals(n)) existingNode = node;
//        		if (n.list.size() < existingNode.list.size()) {
////        		if (n.currIdx < existingNode.currIdx) {
//        			existingNode.list = n.list;
//        		}
        		if (LOOP_VERBOSE){
	        		Node existingNode = null;
	        		for (Node node : visitedNodes) if (node.equals(n)) existingNode = node;
	        		System.out.println("VISITED ALREADY NODES CONTAIN NODE!! (visited node size: " + visitedNodes.size() + ")");
	        		System.out.println("orig actions: "+getActionList(existingNode.list));
	        		System.out.println("new actions: "+getActionList(n.list));
        		}
        		nodeAlreadyExists = true;
        	}


        	if (!nodeAlreadyExists){

	        	boolean gameLost = false;
	        	if (n.state.isGameOver() ){
	        		if (val > 0){
		        		solution = n.list;
		        		foundSolution = true;
		        		if (VERBOSE){ System.out.println("FOUND SOLUTION!");
			        		System.out.println("Solution: " + getActionList(solution));
			        		System.out.println("Solution length: " + n.list.size());
	//		        		System.out.println("Solution length: " + n.currIdx);
			        		System.out.println("Visited nodes: " + visitedNodes.size());
			        		System.out.println("Queue size: " + q.size());
		        		}
		        		
		        		System.out.println("puzzleSolverPlus.Agent duration: " + (System.currentTimeMillis()-tim));
		        		
//		        		return actions[solution[solutionIndex]];
		        		return actions[solution.get(solutionIndex)];
	        		}else{
	        			gameLost = true;
	        		}
	        	}
	
	        	//Expansion
	        	if (!gameLost){
	        		
	    			
	    			//Dont expand if action list is too long already
//	    			if (n.currIdx < MAX_ACTIONS - 1){
	    			
			    		for (int i = 0; i < actions.length; i++) {
			    			//Dont expand into walls
			    			Vector2d expectedNewPos = changePosByAction(n.avatarPos, i);
			    			if (wallPositions[getPositionKey(expectedNewPos)]){
	//		    				System.out.println();
			    				continue;
			    			}
			    			
			    			//Dont expand in opposite direction, if moveables haven't changed
			    			if (!moveablesHaveChanged){
			    				if (n.lastAction >= 0 && actions[i] == oppositeDirectionAction(n.lastAction)){
	//		    					System.out.println("Skipping expansion in opposite direction");
			    					continue;
			    				}
			    			}
	
			    			
			    			Node n_new = new Node(n.state, n.list, n.moveables, i);
//			    			n_new.currIdx = n.currIdx;
//			        		if (nodeInteresting){
//			        			q.addFirst(n_new);
//			        		}else{
//			        			q.add(n_new);
//			        		}
			    			if (depthFirst){
			    				q.addFirst(n_new);
			    			}else{
			    				q.add(n_new);
			    			}
			    		}
//	    			}
	        	}
	    		
	    		visitedNodes.add(n);
        	}
        	
        	n.state = null;
        	
            if (!nodeAlreadyExists) numIters++;
            acumTimeTaken = (elapsedTimerIteration.elapsedMillis());
            avgTimeTaken = numIters == 0 ? 0 : acumTimeTaken/numIters;
            remaining = ect.remainingTimeMillis();
            lastDepth = d;
            if (LOOP_VERBOSE) System.out.println("END LOOP--" + elapsedTimerIteration.elapsedMillis() + " --> " + acumTimeTaken + " (" + remaining + "),  avgTimeTaken: " + avgTimeTaken + " - iters: " +numIters);
        }
        
//        if (VERBOSE) printVisitedNodes();
        
        
        if (VERBOSE) System.out.println("Haven't found solution yet - returning ACTION_NIL");
        if (VERBOSE) System.out.println("Visited nodes: " + visitedNodes.size() + ", Queue size: " + q.size() + ", lastDepth: " + lastDepth);
        return ACTIONS.ACTION_NIL;	//haven't found solution yet - return nil
  	}

	private void wasteTime(float factor) {
		System.out.println("COULDN'T FIND SOLUTION FOR GAME - WASTING TIME");
		int c = 0;
		for (int i = 0; i < 10000000 * factor; i++) {
			c = (int) Math.pow(i, 2);
		}
		
	}

	HashSet<Moveable> getMoveables(StateObservation so){
		
		HashSet<Moveable> result = new HashSet<Moveable>();
		
		
		if (so.getMovablePositions() != null){
			for (ArrayList<Observation> arrayList : so.getMovablePositions()) {
				for (Observation observation : arrayList) {
					result.add(new Moveable(observation.position, observation.itype));
				}
			}
		}
			
				
		if (so.getImmovablePositions() != null){
			for (ArrayList<Observation> arrayList : so.getImmovablePositions()) {
				for (Observation observation : arrayList) {
					if (observation.itype == 0) continue; //<- wall
					result.add(new Moveable(observation.position, observation.itype));
				}
			}
		}
		
		if (so.getResourcesPositions() != null){
			for (ArrayList<Observation> arrayList : so.getResourcesPositions()) {
				for (Observation observation : arrayList) {
					result.add(new Moveable(observation.position, observation.itype));
				}
			}
		}

		return result;
	}
	
	
    ArrayList<ACTIONS> getActionList(LinkedList<Integer> list){
    	ArrayList<ACTIONS> result = new ArrayList<ACTIONS>();
    	
    	for (Integer integer : list){
    		result.add(actions[integer]);
		}
    	return result;
    }
    
    ArrayList<ACTIONS> getActionList(int[] list){
    	ArrayList<ACTIONS> result = new ArrayList<ACTIONS>();
    	
		for (int i = 0; i < list.length; i++) {
			ACTIONS act = actions[list[i]];
			result.add(act);
		}
    	return result;
    }
    
    private double value(StateObservation a_gameState) {

        boolean gameOver = a_gameState.isGameOver();
        Types.WINNER win = a_gameState.getGameWinner();
        double rawScore = a_gameState.getGameScore();

        if(gameOver && win == Types.WINNER.PLAYER_LOSES)
            return -100000;

        if(gameOver && win == Types.WINNER.PLAYER_WINS){
            return 1000 + rawScore; //rawScore + a_gameState.getGameTick() > 1000 ? 100000 : 0;  //WINNING IS ONLY GOOD LATE IN GAME
        }
            
        return rawScore;
    }
    
    private void printVisitedNodes(){

    	System.out.println("------------------------");
    	System.out.println("--PRINTING VISITED NODES");
    	System.out.println("------------------------");
    	for (Node n : visitedNodes) {
    		printNode(n);
		}
    }
    
    private void printNode(Node n){
    	int[][] map = new int[widthOfLevel][];
    	for (int i = 0; i < widthOfLevel; i++) {
    		map[i] = new int[heightOfLevel];
		}
    	
       	int avatar_x = (int) (n.avatarPos.x / blockSize);
    	int avatar_y = (int) (n.avatarPos.y / blockSize);
    	
    	map[avatar_x][avatar_y] = "A".charAt(0) - 36;
    	
    	System.out.println("Avatar pos: " + n.avatarPos + " -> int pos: " + avatar_x + ", " + avatar_y);
//    	for (Moveable m : n.moveables) {
//    		
//    		int x = (int) (m.pos.x / blockSize);
//    		int y = (int) (m.pos.y / blockSize);
//    		System.out.println(m + " -> int pos: " + x + ", " + y);
//    		map[x][y] = (char)m.type;
//		}
    	
    	String mapString = "";
    	for (int j = 0; j < heightOfLevel; j++) {
    		for (int i = 0; i < widthOfLevel; i++) {
				if (map[i][j] > 0){
					mapString += (char)(map[i][j] + 36);
				}else{
					mapString += ".".charAt(0);
				}
			}
			mapString += "\n";
		}
    	
    	
//    	System.out.println(getActionList(n.list));
    	System.out.println(mapString);
    	System.out.println();
    }
    
    
    private int getPositionKey(Vector2d vec){
    	if (vec == null) return 0;
    	if (vec.x < 0 || vec.y < 0 || vec.x >= blockSize*widthOfLevel || vec.y >= blockSize*heightOfLevel) return widthOfLevel * heightOfLevel;
		return (int)((vec.x/blockSize) + (vec.y/blockSize) * widthOfLevel);
    }
    
    private Vector2d getPositionFromKey(int key){
    	Vector2d result = new Vector2d();
    	result.x = (key % widthOfLevel) * blockSize;
    	result.y = (key / widthOfLevel) * blockSize;
		return result;
    	
    }
    
    private Vector2d changePosByAction(Vector2d pos, int action){
    	Vector2d newPos = pos.copy();
    	
    	switch (actions[action]) {
		case ACTION_DOWN:
			newPos.y += blockSize;
			break;
		case ACTION_LEFT:
			newPos.x -= blockSize;
			break;
		case ACTION_RIGHT:
			newPos.x += blockSize;
			break;
		case ACTION_UP:
			newPos.y -= blockSize;
			break;
		case ACTION_USE:
			break;
		default:
			break;
		}
    	return newPos;
    }
    
    
    private ACTIONS oppositeDirectionAction(int action){
    	switch (actions[action]) {
		case ACTION_DOWN:
			return ACTIONS.ACTION_UP;
		case ACTION_LEFT:
			return ACTIONS.ACTION_RIGHT;
		case ACTION_RIGHT:
			return ACTIONS.ACTION_LEFT;
		case ACTION_UP:
			return ACTIONS.ACTION_DOWN;
		default:
			break;
    	}
    	return ACTIONS.ACTION_NIL;
    }
}
