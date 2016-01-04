package level;

public class DynamicTrap {

	private int positionX, positionY;
	private int lastDirection = (int) Math.floor( 4 * Math.random());
	//Direction code:    0 = UP   ,   1 = RIGHT   ,   2 = DOWN   ,   3 = LEFT
	
	public DynamicTrap(int x, int y) {
		positionX = x;
		positionY = y;
	}

	public int getPositionX() {
		return positionX;
	}

	public int getPositionY() {
		return positionY;
	}
	
	public void move(boolean upPossible, boolean rightPossible , boolean downPossible , boolean leftPossible) {
		if(!(upPossible || rightPossible || downPossible || leftPossible)) return; //falls keine Bewegung möglich Abbruch 
		
		//TODO KI überdenken...
		//Taktik: 70% Wahrscheinlichkeit Kurs beibehalten; 20% Richtung ändern; 10% umkehren
		double tactics = Math.random();
		
		if(tactics < 0.7){ //Kurs beibehalten
			moveStraight(upPossible, rightPossible, downPossible, leftPossible);
		}
		
		else if(tactics < 0.9){ // Richtung ändern
			moveCurve(upPossible, rightPossible, downPossible, leftPossible);
		}
		
		else{ //umkehren
			moveTurnAround(upPossible, rightPossible, downPossible, leftPossible);
		}
	}
	
	private void moveStraight(boolean upPossible, boolean rightPossible , boolean downPossible , boolean leftPossible) {
		if(move(lastDirection, upPossible, rightPossible, downPossible, leftPossible)) return;
		moveCurve(upPossible, rightPossible, downPossible, leftPossible);
	}

	private void moveCurve(boolean upPossible, boolean rightPossible , boolean downPossible , boolean leftPossible) {
		//50% Rechtskurve ; 50% Linkskurve
		double rightLeft = Math.random();
		if(rightLeft < 0.5){ //Rechtskurve (falls nicht möglich Linkskurve)
			if(move(lastDirection + 1, upPossible, rightPossible, downPossible, leftPossible)) return;
			if(move(lastDirection - 1, upPossible, rightPossible, downPossible, leftPossible)) return;
			
		}
		else{ //Linkskurve (falls nicht möglich Rechtskurve)
			if(move(lastDirection - 1, upPossible, rightPossible, downPossible, leftPossible)) return;
			if(move(lastDirection + 1, upPossible, rightPossible, downPossible, leftPossible)) return;
		}
		moveTurnAround(upPossible, rightPossible, downPossible, leftPossible);
	}

	private void moveTurnAround(boolean upPossible, boolean rightPossible , boolean downPossible , boolean leftPossible) {
		if(move(lastDirection + 2, upPossible, rightPossible, downPossible, leftPossible)) return;
		moveStraight(upPossible, rightPossible, downPossible, leftPossible);
	}

	/**
	 * 
	 * @param direction
	 * @return true wenn erfolgreich bewegt wurde, false falls bewegung nicht moeglich
	 */
	private boolean move(int direction, boolean upPossible, boolean rightPossible , boolean downPossible , boolean leftPossible) {
		if(direction > 3) direction -= 4;
		else if(direction < 0 ) direction += 4;
		switch(direction){
			case 0:
				if(upPossible) { positionY -= 1;	return true;}
				else return false;
			case 1:
				if(rightPossible) { positionX += 1;	return true;}
				else return false;
			case 2:	
				if(downPossible) { positionY += 1;	return true;}
				else return false;
			case 3:
				if(leftPossible) { positionX -= 1;	return true;}
				else return false; 
		}
		return true;
	}
}
