package objects;

import level.Level;

import com.googlecode.lanterna.terminal.Terminal;

public class Entry extends StaticGameObject {
	
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
