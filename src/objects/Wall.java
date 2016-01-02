package objects;

import com.googlecode.lanterna.terminal.Terminal.Color;

public class Wall extends GameObject {

	@Override
	public char getDisplayChar() {
		return '=';
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
		return 1;
	}

	@Override
	public int getX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getY() {
		// TODO Auto-generated method stub
		return 0;
	}

}
