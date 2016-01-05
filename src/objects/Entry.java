package objects;

public class Entry extends StaticGameObject {
	
	public Entry(){
		charRepresentation = emptyChar;
	}

	@Override
	public void onContact(MovingGameObject mov) {
		//TODO maybe an alert "this is where you came from"
	}

}
