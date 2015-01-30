package controllers.approx;

import core.game.StateObservation;

public class Action {
	public int act;
	public StateObservation so;
	
	public Action(int act, StateObservation so){
		this.act = act;
		this.so = so;
	}
}
