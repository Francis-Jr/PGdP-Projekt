package objects;

public class Exit extends StaticGameObject{
	
	public Exit(){
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
