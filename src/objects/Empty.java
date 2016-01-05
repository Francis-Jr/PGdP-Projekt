package objects;

import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.Terminal.Color;

import level.Level;

public class Empty extends StaticGameObject {
	
	public Empty(int posX, int posY, Terminal term, Level lv){
		
		x = posX;
		y = posY;
		terminal = term;
		level = lv;
		color = defaultBgColor; //Die Farbe von Empty wird hoechstens als Bg 
								//fuer MovingGameObjects verwendet
		
		charRepresentation = emptyChar;
	}

	@Override
	public void onContact(MovingGameObject mov) {
		//do nothing
	}
}
