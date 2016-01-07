package objects;

import level.Level;

import com.googlecode.lanterna.terminal.Terminal;


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
		if(mov.tryGiveKey()){
			take();
		}
	}
	
	public void take(){ //"untake" not possible.
		isTaken = true;
		charRepresentation = emptyChar;
		color = defaultBgColor;
	}
}