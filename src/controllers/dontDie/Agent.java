package controllers.dontDie;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

import ontology.Types;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import tools.Vector2d;
import core.game.StateObservation;
import core.player.AbstractPlayer;

public class Agent extends AbstractPlayer{
	
    public static Types.ACTIONS[] actions;
	Random r;
    
    float feelingOfDeadliness = 0;
        
    float[] boringPlaces;
    
    int blockSize = -1;
    int heightOfLevel = -1;
    int widthOfLevel = -1;
    
    final boolean MINOR_VERBOSE = false;
    final boolean VERBOSE = false;
    final boolean LOOP_VERBOSE = false;
    
    //Constants
    float K_score; // = 1.414213562373095f;
    float K_death; // = 5;
    final float startBoringness = 0.001f;
    final float boringPlacesExponent = 8f/10f;
    final float expandRandomness = 0.1f;
    final int winValue = 1000;
    final int loseValue = -100000;
    final int reexpandDepth = 1;
    final float repeatActionBoringReduction = 0.125f;
    final float oppositeActionBoringIncrease = 0.125f;
    
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
    	
    	boringPlaces = new float[((heightOfLevel+2) * (widthOfLevel+2)) + 10];
    	for (int i = 0; i < boringPlaces.length; i++) {
    		boringPlaces[i] = startBoringness;
		}
    	
    	
    	
    	K_score = (float)Math.sqrt(actions.length);
    	K_death = (float)Math.sqrt(actions.length);
    }
	
	public ACTIONS act(StateObservation so, ElapsedCpuTimer et) {		
		int action = -1;
		
		//These values are used a lot - better store 'em
		Vector2d currentPos = so.getAvatarPosition();
		double currentScore = so.getGameScore();
		if (VERBOSE) System.out.println("ACT BEGUN - score: " + currentScore + " avatar pos: " + currentPos);
		
		//Boringplaces gives each tile in the game a float-value (between 0 and 1), which increase whenever the avatar stands on it.
		boringPlaces[getPositionKey(currentPos)] = (float)Math.pow(boringPlaces[getPositionKey(currentPos)], boringPlacesExponent);
		
		//The boringness of the current position
		float positionBoringness = boringPlaces[getPositionKey(currentPos)];

        //Initializing node queue which is used in the main loop, and adding current node
        ArrayDeque<Node> q = new ArrayDeque<Node>(); 
        Node currentNode = new Node(so, currentPos, new LinkedList<Integer>());
        q.add(currentNode);

        //bestVals/bestNodes examines the potential score increase of each possible action. Decides which action to actually take
        double[] bestVals = new double[actions.length]; 
        Node[] bestNodes = new Node[actions.length];
        
        //Examines the boringness of each possible action. Decides which action to take if bestVals are not interesting
        float[] leastBoringActions = new float[actions.length]; 
        
        //Examines how deadly each possible action is. Takes over from bestVals/leastBoring in deciding move, if the death-action values are too high.
        float[] deathActions = new float[actions.length]; 
        
        //Examines if possible (non-shoot) actions actually moves the avatar
        boolean[] wallActions = new boolean[actions.length]; 
        
        //Examines if possible actions can directly kill the avatar
        boolean[] directDeathActions = new boolean[actions.length]; 
        
        //Count the amount of times a search with each action has been started
        int[] actionIterations = new int[actions.length];
        
        for (int i = 0; i < actions.length; i++) {
        	bestVals[i] = 0;
        	bestNodes[i] = null;
        	leastBoringActions[i] = 0f;
        	deathActions[i] = 0f;
        	wallActions[i] = false;
        	directDeathActions[i] = false;
		}
        
        
        boolean allowReexpand = false;
        int expandDepth = 5;
        if (feelingOfDeadliness > 0.0000001){
        	expandDepth = 4;
        }
        if (feelingOfDeadliness > 0.00001){
        	expandDepth = 3;
        }
        if (feelingOfDeadliness > 0.001){
        	expandDepth = 2;
        }
        if (feelingOfDeadliness > 0.01){
        	expandDepth = 1;
        }
        
        
        //////////////////
        //Main loop     //
        //////////////////
        double avgTimeTaken = 0, acumTimeTaken = 0;
        long remaining = et.remainingTimeMillis();
        int numIters = 0, remainingLimit = 4, reexpands = 0;
        Vector2d pos = currentPos.copy();
        int lastDepth = 0;
        ElapsedCpuTimer elapsedTimerIteration = new ElapsedCpuTimer();
        while(remaining > 2*avgTimeTaken && remaining > remainingLimit)
        {
            if (LOOP_VERBOSE) System.out.println("START LOOP--" + elapsedTimerIteration.elapsedMillis() + " --> " + acumTimeTaken + " (" + remaining + "),  avgTimeTaken: " + avgTimeTaken);

            Node n = q.pollFirst();
            if (n == null){
            	if (VERBOSE) System.out.println("QUEUE IS EMPTY! - Adding new currentNode (empty)");
            	q.add(currentNode);
            	continue;
            }
            //Decides if the current node should be expanded from
            boolean expandFromNode = true; 

            //These values are used are lot. save 'em!
            int depth = n.list.size(); 
            int lastAct = depth>0 ? n.list.peekLast() : -1;
            int firstAct = depth>0 ? n.list.peekFirst() : -1;
            
        	if (depth > 0) actionIterations[firstAct]++;
            
            //Advance the state observation according to the last action
            if (depth > 0) n.state.advance(actions[lastAct]);  
                        
            //Get the new states avatar position and score
            pos = n.state.getAvatarPosition();
            double stateScore = value(n.state);
            if (pos == null) stateScore = loseValue; //avatar doesn't exists! Give stateScore a "lose"-value
            
            if (depth > 0){
            	//Examine the bestVals/bestNodes value: the score difference - dif
            	float diff = (float) (stateScore - currentScore);
            	float scoreDiff = (float) (diff/(float)Math.pow(K_score, depth));
            	bestVals[firstAct] += scoreDiff > 0 ? scoreDiff : 0;
            	bestNodes[firstAct] = n;
            	
            	//Examine the leastBoringAction value
            	leastBoringActions[firstAct] += boringPlaces[getPositionKey(pos)];
            	
            	//Examine the deathActions (and directDeathActions) value
                if (stateScore <= loseValue){
                	float newCount = deathActions[firstAct] + (1 / (float)Math.pow(K_death, depth));
                	deathActions[firstAct] =  newCount;
                	expandFromNode = false;
                	if (depth==1) directDeathActions[firstAct] = true;
                }
                
                //Examine wallActions - avatar moved into wall (OR BECAUSE OF COOLDOWN!) with last move.
                if (depth == 1 && pos != null && pos.equals(n.lastAvatarPos) && actions[lastAct] != ACTIONS.ACTION_USE){ 
                	wallActions[lastAct] = true;
                }
            }

            //Extra check for game over (probably not needed)
            if (n.state.isGameOver()) expandFromNode = false;
            
            
            if (depth > expandDepth) expandFromNode = false;
            
//            System.out.println("q.size(): " + q.size() + " depth: " + depth);
            //Very importantly! Add extra nodes from the start, to counter random events in games
            if (allowReexpand && q.size() < actions.length && depth > expandDepth){ 
            	q.add(currentNode);
            	reexpands++;
            	allowReexpand = false;
            }
            


            //////////////////
            //Node expansion//
            //////////////////
            int newAction = -1;
            if (depth == 0){
            	allowReexpand = true;
            	
            	
            	
            	//Advance with all possible actions
	            for (int i = 0; i < actions.length; i++) {	
	            	if (directDeathActions[i]) continue;
	            	
	            	StateObservation stCopy = n.state.copy();            
	            	Vector2d newPos = pos.copy();
	            	Node newNode = new Node(stCopy, newPos, cloneOf(n.list));
	            	newNode.addAction(i);
	            	q.add(newNode);
				}
            }else if (expandFromNode){
            	//Advance with least boring action
            	int leastBoringAct = action;
            	float leastBoringness = Float.MAX_VALUE;
	            for (int i = 0; i < actions.length; i++) {		            	
	            	Vector2d expectPos = changePosByAction(pos, i);
	            	if (wallActions[i]) continue;
	            	float boringness = boringPlaces[getPositionKey(expectPos)] + (r.nextFloat() - 0.5f) * expandRandomness;
	            	
	            	if (i == lastAct) {
	            		boringness -= repeatActionBoringReduction;
//	            		boringness = boringness < 0 ? 0 : boringness;
	            	}else if (isOppositeAction(i, lastAct)){
	            		boringness += oppositeActionBoringIncrease;
//	            		boringness = boringness > 1 ? 1 : boringness;
	            	}
	            	
	            	if (boringness < leastBoringness){
	            		leastBoringness = boringness;
	            		leastBoringAct = i;
	            	}
	            }
	            newAction = leastBoringAct;
           
            	StateObservation stCopy = n.state;
            	Vector2d newPos = pos.copy();
            	Node newNode = new Node(stCopy, newPos, cloneOf(n.list));
            	newNode.addAction(newAction);
            	q.add(newNode);
            }
            
            lastDepth = depth;
            
            numIters++;
            acumTimeTaken = (elapsedTimerIteration.elapsedMillis());
            avgTimeTaken = acumTimeTaken/numIters;
            remaining = et.remainingTimeMillis();
            if (LOOP_VERBOSE) System.out.println("Node action list: " + getActionList(n.list));
            if (LOOP_VERBOSE) System.out.println("Node value: " + stateScore);
            if (LOOP_VERBOSE) System.out.println(elapsedTimerIteration.elapsedMillis() + " --> " + acumTimeTaken + " (" + remaining + "),  avgTimeTaken: " + avgTimeTaken);
        }
        
        /////////////////////////////////////
        //Deciding action / Result analysis//
        /////////////////////////////////////
        
        //Normalise arrays
        for (int i = 0; i < actions.length; i++) {
        	bestVals[i] /= (float) actionIterations[i];
        	deathActions[i] /= (float) actionIterations[i];
        	leastBoringActions[i] /= (float) actionIterations[i];
		}
        
        
        //Find the best node of all actions
        double bestVal = -Double.MAX_VALUE;
        double worstVal = Double.MAX_VALUE;
        Node bestNode = currentNode;
        boolean noActualBest = true;
        for (int i = 0; i < actions.length; i++) {
        	if (directDeathActions[i]) continue;
        	Node n = bestNodes[i];
        	double val = bestVals[i];
        	if (n != null && n.list.size() > 0){
        		if (val > bestVal){
	        		bestVal = val;
	        		bestNode = n;
        		}
        		if (val < worstVal){
        			worstVal = val;
        		}
        	}
		}
       
        //Check if bestVals have too similar values (no best value)
        float ratioForActualBest = (float) (0.001*Math.pow(10, positionBoringness*2));
        if (Math.abs(worstVal-bestVal)/Math.abs(bestVal) > ratioForActualBest) noActualBest = false; 
        
        //Choose action if an "actualBest" node exists
        if (!noActualBest && bestNode.list.size() > 0) action = bestNode.list.peekFirst();
        
        //Otherwise pick the least boring move (from leastBoringActions)
        if (noActualBest){
        	float leastBoringness = Float.MAX_VALUE;
        	int leastBoringAct = -1;
        	
        	for (int i=0;i<leastBoringActions.length;i++) {
        		if (positionBoringness < 0.5 && wallActions[i]) continue;
        		if (directDeathActions[i]) continue;
				float val = leastBoringActions[i];
								
				if (val + (r.nextFloat()-0.5f)< leastBoringness){
					leastBoringness = val;
					leastBoringAct = i;
				}
			}
        	action = leastBoringAct;
        }
        
        //Calculate generalDeadliness (sum(deathactions)) and find least deadly action
        float generalDeadliness = 0;
        float leastDeadly = Float.MAX_VALUE;
        int leastDeadlyAct = -1;
        boolean spooked = false;
        for (int i = 0; i < actions.length; i++) {
        	        	
        	generalDeadliness += deathActions[i];
        	if (deathActions[i] < leastDeadly){
				leastDeadlyAct = i;
				leastDeadly = deathActions[i];
        	}
        }
        
        //Calculate thresholds for when to be spooked (pick the least deadly action). Values are dependant on the borinngess (increases thresholds),
        //and the number of iterations performed in the loop (decreases thresholds if lower than 300, otherwise increases)
        //In other words: DontDie becomes more and more fearless the more it is bored. 
        //With a low amount of iterations it becomes more easily spooked
//        f(not bored at all) --> very easily spooked --> thresholds low
//        f(very bored) --> very hard to spook --> thresholds high
//        few iterations --> more spooky --> thresholds lower
//        many iterations --> less spooky --> thresholds higer
        
        float deadwayThreshold = -1;
        float deadGeneralThreshold = -1;
        if (generalDeadliness > 0){
//	        deadwayThreshold = 100f*positionBoringness*positionBoringness + 0.001f;		//1-1000
	        deadwayThreshold = (float) Math.pow(positionBoringness, actions.length) + 0.01f;		//1-1000
//	        deadGeneralThreshold = actions.length*100f*positionBoringness*positionBoringness + 0.01f*actions.length;	//5-5000
	        deadGeneralThreshold = actions.length*(float) Math.pow(positionBoringness, actions.length)  + 0.01f*actions.length;	//5-5000
	        deadwayThreshold *= numIters/300f;
	        deadGeneralThreshold *= numIters/300f;
	       
	        if (action == -1)  action = r.nextInt(actions.length);
	        if (generalDeadliness > deadGeneralThreshold || deathActions[action] > deadwayThreshold){
	        	action = leastDeadlyAct;
	        	spooked = true;
	        }
		}
        
        //Check if taking action kills us
        boolean allActionsDeadly = true;
        for (int i = 0; i < actions.length; i++) {
			if (!directDeathActions[i]){
				allActionsDeadly = false;
				break;
			}
		}
        
        //Maybe moving into a wall can make us survive?
        if (action == -1){
	        for (int i = 0; i < actions.length; i++) {
	        	if (directDeathActions[i]) continue;
	        	action = i;
	        	break;
	        }
        }
        
        

        if (generalDeadliness > feelingOfDeadliness){
        	feelingOfDeadliness = generalDeadliness;
        }else{
        	feelingOfDeadliness *= 0.9f;
        }
        
        //Is returning nil the best thing to do? 
        if (allActionsDeadly || action == -1 || numIters == 1){
        	if (VERBOSE || (MINOR_VERBOSE && (directDeathActions[action] == true))){
        		System.out.println("COULD NOT FIND ANYTHING INTERESTING TO DO - RETURNING NIL");
        	}
        	return ACTIONS.ACTION_NIL;
        }
        
       if (VERBOSE || (MINOR_VERBOSE && generalDeadliness > 0.00001)){
    	   System.out.println("Q size: " + q.size());
    	   System.out.println("deadwayThreshold: " + deadwayThreshold + " deadGeneralThreshold: " + deadGeneralThreshold + " - pos boring: " + positionBoringness);
    	   if (noActualBest) System.out.println("NO ACTUAL BEST MOVE"); else System.out.println();
    	   if (spooked) System.out.println("SPOOOOOOOOOOOOOOOKED"); else System.out.println();
	        System.out.println("---ACTUAL ACTION CHOSEN: " + actions[action] + " , iterations: " +  numIters + ", reexpands: " + reexpands);
	        System.out.println("BEST ACTION: " + (bestNode.list.peekFirst() != null ? actions[bestNode.list.peekFirst()] : "ERR"));
	        System.out.println("\t\t\t" + Arrays.toString(actions));
	        System.out.println("BEST NODES: \t\t" + Arrays.toString(bestVals));

	        System.out.println("Death actions:\t\t "+Arrays.toString(deathActions));
	        System.out.println("Direct death actions:\t\t "+Arrays.toString(directDeathActions));
	        System.out.println("Wall actions:\t\t "+Arrays.toString(wallActions));
	        System.out.println("Least boring actions:\t" + Arrays.toString(leastBoringActions));
	        System.out.println("Current avatar pos: " + so.getAvatarPosition());
//	        System.out.println("Boring list: " + convertBoringList(boringPlaces));
	        System.out.println("Current pos boringness: " + boringPlaces[getPositionKey(currentPos)]);        
	        System.out.println("Planned move boringness: " + boringPlaces[getPositionKey(changePosByAction(currentPos, action))]);
	        System.out.println("Expected next pos: " + changePosByAction(currentPos, action));
	        System.out.println("General deadliness: " +generalDeadliness + " - deadly feeling: " + feelingOfDeadliness);
	        System.out.println("Action iterations: " + Arrays.toString(actionIterations));
       }
       
       return actions[action];
	}
	
	
    private double value(StateObservation a_gameState) {
        boolean gameOver = a_gameState.isGameOver();
        Types.WINNER win = a_gameState.getGameWinner();
        double rawScore = a_gameState.getGameScore();

        if(gameOver && win == Types.WINNER.PLAYER_LOSES)
            return loseValue;

        if(gameOver && win == Types.WINNER.PLAYER_WINS){
            return winValue + rawScore;
        }
            
        return rawScore;
    }

    private int getPositionKey(Vector2d vec){
    	if (vec == null) return 0;
    	if (vec.x < 0 || vec.y < 0 || vec.x > blockSize*widthOfLevel || vec.y > blockSize*heightOfLevel) return widthOfLevel *heightOfLevel + 5;
		return (int)((vec.x/blockSize) + (vec.y/blockSize) * widthOfLevel);
    	
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
    
//    private HashMap<Vector2d, Float> convertBoringList(HashMap<Integer, Float> boringPlaces){
//    	HashMap<Vector2d, Float> result = new HashMap<Vector2d, Float>();
//  
//    	for (Integer key : boringPlaces.keySet()) {
//    		Float val = boringPlaces.get(key);
//    		Vector2d pos = getPositionFromKey(key);
//    		result.put(pos, val);
//		}
//		return result;
//    }
    
    
    ArrayList<ACTIONS> getActionList(LinkedList<Integer> list){
    	ArrayList<ACTIONS> result = new ArrayList<ACTIONS>();
    	
    	for (Integer integer : list){
    		result.add(actions[integer]);
		}
    	return result;
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
}
