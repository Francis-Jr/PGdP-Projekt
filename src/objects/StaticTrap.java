package objects;

import level.Level;

import com.googlecode.lanterna.terminal.Terminal;

public class StaticTrap extends StaticGameObject {

	protected static final int defaultDamage = 2;
	
	private int damage;
	
	public StaticTrap(int posX, int posY, Terminal term, Level lv){
		x = posX;
		y = posY;
		terminal = term;
		level = lv;
		charRepresentation = staticTrapChar;
		color = staticTrapColor;
		damage = defaultDamage;
	}
	
	@Override
	public void onContact(MovingGameObject mov) {
		mov.hurt(damage);
	}
}
