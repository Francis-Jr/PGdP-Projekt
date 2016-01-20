package objects;

import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.Terminal.Color;

import level.Level;

/**
 * Project Labyrinth (PGdP 1)
 * WS15/16 TUM
 * <p>
 * An empty field in the labyrinth that does not interact with any entities
 * @version 19.01.2016
 * @author junfried
 */
public class Empty extends StaticGameObject {
	
	public Empty(int posX, int posY, Terminal term, Level lv){
		
		x = posX;
		y = posY;
		terminal = term;
		level = lv;
		color = defaultBgColor; //The color of an Empty object will only be used
								//as backgroundcolor for Players/DynamicTraps walking over it
		
		charRepresentation = emptyChar;
	}

	@Override
	public void onContact(MovingGameObject mov) {
		//do nothing
	}
}
