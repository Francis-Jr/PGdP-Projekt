package main;

public class T {

	//TODO remove, this is only a testclass

	public static void p(String a){
		System.out.println("[TEST] " + a);
	}

	public static String oC(int operationCode) {
		switch(operationCode){
		case 0: return "do nothing";
		case 1: return "quit";
		case 2: return "menu";
		case 3: return "nextLevel";
		default: return "unknown";
		}
	}
	
}
