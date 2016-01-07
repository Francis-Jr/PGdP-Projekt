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
		case 2: return "enter menu";
		case 3: return "leave menu";
		case 4: return "next level";
		case 5: return "retry level";
		case 6: return "save level";
		case 7: return "load level";
		case 101: return "saving to 1";
		case 102: return "saving to 2";
		case 103: return "saving to 3";
		case 104: return "saving to 4";
		case 201: return "loading from 1";
		case 202: return "loading from 2";
		case 203: return "loading from 3";
		case 204: return "loading from 4";
		default: return "unknown";
		}
	}
	
}
