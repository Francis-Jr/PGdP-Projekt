package objects;


public class ExitKey extends StaticGameObject {

	boolean isTaken = false;
	
	public ExitKey(){
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
	}
}