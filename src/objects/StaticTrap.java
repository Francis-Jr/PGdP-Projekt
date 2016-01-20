package objects;

import level.Level;

import com.googlecode.lanterna.terminal.Terminal;


/**
 * Project Labyrinth (PGdP 1)
 * WS15/16 TUM
 * <p>
 * a trap that damages the player if he walks on them
 * @version 19.01.2016
 * @author junfried
 */
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
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onContact(MovingGameObject mov) {
		mov.hurt(damage);
	}
}
