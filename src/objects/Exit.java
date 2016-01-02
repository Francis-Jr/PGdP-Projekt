package objects;

import com.googlecode.lanterna.terminal.Terminal.Color;

public class Exit extends GameObject {

	@Override
	public char getDisplayChar() {
		return 'E';
	}

	@Override
	public Color getBgColor() {
		return Color.BLACK;
	}

	@Override
	public Color getFgColor() {
		return Color.BLUE;
	}

	@Override
	public int getInteractionCode() {
		return 3;
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
