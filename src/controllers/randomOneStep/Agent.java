package controllers.randomOneStep;

import java.util.Random;

import ontology.Types;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import controllers.Heuristics.SimpleStateHeuristic;
import core.game.StateObservation;
import core.player.AbstractPlayer;

public class Agent extends AbstractPlayer{

	Random r = new Random();
	
	
    public Agent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
    }
	
	public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) 
	{
		boolean allActionsHaveEqualValue = true;
        Types.ACTIONS bestAction = null;
        double maxQ = Double.NEGATIVE_INFINITY;
        for (Types.ACTIONS action : stateObs.getAvailableActions()) {
            StateObservation stCopy = stateObs.copy();
            stCopy.advance(action);
            double Q = value(stCopy);

            //System.out.println("Action:" + action + " score:" + Q);
            if (Q > maxQ) {
                maxQ = Q;
                bestAction = action;
            }
            
            if (Q != maxQ) allActionsHaveEqualValue = false;
        }

        //Choose a random action if all available actions have same value
        if (allActionsHaveEqualValue){
        	int idx = r.nextInt(stateObs.getAvailableActions().size());
        	bestAction = stateObs.getAvailableActions().get(idx);
        }
        return bestAction;
	}
	
    private double value(StateObservation a_gameState) 
    {
        boolean gameOver = a_gameState.isGameOver();
        Types.WINNER win = a_gameState.getGameWinner();
        double rawScore = a_gameState.getGameScore();

        if(gameOver && win == Types.WINNER.PLAYER_LOSES)
            return -100000;

        if(gameOver && win == Types.WINNER.PLAYER_WINS){
            return 1000 + rawScore;
        }
            
        return rawScore;
    }

}
