package controllers.MCTSishPlus;

import java.util.LinkedList;

import tools.Vector2d;
import core.game.StateObservation;

public class Node {
	public StateObservation state;
	public LinkedList<Integer> list = new LinkedList<Integer>();
	public Vector2d lastAvatarPos;
	
	public Node(StateObservation state, LinkedList<Integer> list, Vector2d lastAvatarPos, int act){
		this.state = state;
		this.list = list;
		this.lastAvatarPos = lastAvatarPos;
		
		if (act >= 0) this.list.add(act);
	}
}
