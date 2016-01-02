package level;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Level {

	private Properties objects;
	
	public int getObjectAt(int x , int y){
		return Integer.parseInt(objects.getProperty(x + "," + y));
	}
	
	public int getWidth(){
		return Integer.parseInt(objects.getProperty("Width"));
	}
	
	public int getHeight(){
		return Integer.parseInt(objects.getProperty("Height"));
	}
	
	public void moveDynamicTraps(){
		//TODO implement
	}
	
	public static void movePlayerUp(){
		//TODO implement
	}
	
	public static void movePlayerDown(){
		//TODO implement
	}
	
	public static void movePlayerLeft(){
		//TODO implement
	}
	
	public static void movePlayerRight(){
		//TODO implement
	}
	
	public Level(String path){
		objects = new Properties();
		
		//InputStream
			BufferedInputStream stream = null;
			try {
				stream = new BufferedInputStream(new FileInputStream(path));
			} 
			catch (FileNotFoundException e) {
				System.err.println("[ERROR] @ Level() : File " + path + " not found."); 
				e.printStackTrace();
			}
		
		//Load Level
			try {
				objects.load(stream);
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		
		//Close Stream
			try {
				stream.close();
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
	}
}
