package objects;

import com.googlecode.lanterna.terminal.Terminal.Color;

public class Player extends GameObject {

	//TODO hasKey(), getKey(), useKey() ...
	
	@Override
	public char getDisplayChar() {
		return 'o';
	}

	@Override
	public Color getBgColor() {
		return Color.BLACK;
	}

	@Override
	public Color getFgColor() {
		return Color.GREEN;
	}

	@Override
	public int getInteractionCode() {
		return 0;
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
