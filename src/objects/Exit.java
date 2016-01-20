package objects;

import level.Level;

import com.googlecode.lanterna.terminal.Terminal;

public class Exit extends StaticGameObject{
	
	/**
	 * Project Labyrinth (PGdP 1)
	 * WS15/16 TUM
	 * <p>
	 * The Exit is the goal of the level. If the player reaches an exit with a key, he wins the level.
	 * @version 19.01.2016
	 * @author junfried
	 */
	public Exit(int posX, int posY, Terminal term, Level lv){
		x = posX;
		y = posY;
		terminal = term;
		level = lv;
		charRepresentation = exitChar;
		color = exitColor;
	}

	@Override
	public void onContact(MovingGameObject mov) {
		if(mov.hasKey()){
			mov.win();
		}
	}
}
