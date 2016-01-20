package objects;

import level.Level;

import com.googlecode.lanterna.terminal.Terminal;

/**
 * Project Labyrinth (PGdP 1)
 * WS15/16 TUM
 * <p>
 * This object is a Key to the Exit. It will be removed once taken
 * changing its appearance to the same as an Empty object
 * @version 19.01.2016
 * @author junfried
 */
public class ExitKey extends StaticGameObject {

	
	boolean isTaken = false;
	
	public ExitKey(int posX, int posY, Terminal term, Level lv){
		x = posX;
		y = posY;
		terminal = term;
		level = lv;
		charRepresentation = keyChar;
		color = keyColor;
	}

	@Override
	public void onContact(MovingGameObject mov) {
		if(!isTaken){
			if(mov.tryGiveKey()){
				take();
			}
		}
	}
	
	/**
	 * changes the ExitKeys appearance to the same as an Empty object
	 */
	public void take(){ //"untake" not possible.
		isTaken = true;
		charRepresentation = emptyChar;
		color = defaultBgColor;
	}
}