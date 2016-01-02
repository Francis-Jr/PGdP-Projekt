package objects;

import com.googlecode.lanterna.terminal.Terminal.Color;

public class StaticTrap extends GameObject {

	private int damage = 1;
	
	@Override
	public char getDisplayChar() {
		return '#';
	}

	@Override
	public Color getBgColor() {
		return Color.BLACK;
	}

	@Override
	public Color getFgColor() {
		return Color.RED;
	}

	@Override
	public int getInteractionCode() {
		switch(damage){
		case 1:	return 4;
		case 2: return 5;
		case 3: return 6;
		default: return 0;
		}
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

}
