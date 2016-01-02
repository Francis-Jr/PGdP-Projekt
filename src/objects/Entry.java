package objects;

import com.googlecode.lanterna.terminal.Terminal.Color;

public class Entry extends GameObject {

	@Override
	public char getDisplayChar() {
		return ' ';
	}

	@Override
	public Color getBgColor() {
		return Color.BLACK;
	}

	@Override
	public Color getFgColor() {
		return Color.WHITE;
	}

	@Override
	public int getInteractionCode() {
		return 2;
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
