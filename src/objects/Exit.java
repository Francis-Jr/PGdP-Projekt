package objects;

import level.Level;

import com.googlecode.lanterna.terminal.Terminal;

public class Exit extends StaticGameObject{
	
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
