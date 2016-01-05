package objects;

public class Empty extends StaticGameObject {
	
	public Empty(){
		charRepresentation = emptyChar;
	}

	@Override
	public void onContact(MovingGameObject mov) {
		//do nothing
	}
}
