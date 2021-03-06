package controllers.random;

import java.util.Random;

import ontology.Types;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import core.game.StateObservation;
import core.player.AbstractPlayer;

public class Agent extends AbstractPlayer {

    public Agent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {}

    Random r = new Random();
    
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        int index = r.nextInt(stateObs.getAvailableActions().size());
        return stateObs.getAvailableActions().get(index);
    }
}