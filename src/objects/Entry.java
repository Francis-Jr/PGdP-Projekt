package objects;

import level.Level;

import com.googlecode.lanterna.terminal.Terminal;

public class Entry extends StaticGameObject {
	
	/**
	 * Project Labyrinth (PGdP 1)
	 * WS15/16 TUM
	 * <p>
	 * An Entry to the labyrinth. It acts like an Empty object, but is important for
	 * player placement when the level resets
	 * @version 19.01.2016
	 * @author junfried
	 */
	public Entry(int posX, int posY, Terminal term, Level lv){
		x = posX;
		y = posY;
		terminal = term;
		level = lv;
		
		color = defaultBgColor; //Die Farbe von Entry wird hoechstens als Bg 
								//fuer MovingGameObjects verwendet
		
		charRepresentation = emptyChar;
	}

	@Override
	public void onContact(MovingGameObject mov) {
		//TODO maybe an alert "this is where you came from"
	}

}
