package controllers.dontDie;

import java.util.LinkedList;

import tools.Vector2d;
import core.game.StateObservation;

public class Node {
	public StateObservation state;
	public Vector2d lastAvatarPos;
	public LinkedList<Integer> list = new LinkedList<Integer>();
	
	public Node(StateObservation state, Vector2d lastAvatarPos, LinkedList<Integer> list){
		this.state = state;
		this.lastAvatarPos = lastAvatarPos;
		this.list = list;
	}
	
	public void addAction(int act){
		list.add(act);
	}

}
