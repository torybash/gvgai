package controllers.approx;

import java.util.Arrays;

public class GameSignature {

	int actCount;
	int[] itypArray = new int[6];

	String gameTitle;
	
	public GameSignature(int actCount , int[] vals) {
		this.actCount = actCount;
		itypArray = vals.clone();
	}

	
	@Override
	public String toString() {
		String result = "Sign=[";
		result += actCount;
		for (int i = 0; i < itypArray.length; i++) {
			result += "," + itypArray[i];
		}
		result += "]";
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		GameSignature other = (GameSignature) obj;
		
		boolean result = true;
		
		for (int i = 0; i < itypArray.length; i++) {
			if (itypArray[i] != other.itypArray[i]){ result = false; break;}
		}
		
		if (actCount != other.actCount) result = false;

		return result;
	}
	
}
