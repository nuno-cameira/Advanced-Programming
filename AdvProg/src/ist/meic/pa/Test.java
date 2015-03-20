package ist.meic.pa;

import javassist.*;

public class Test {
	
	static String a = "bla";
	static int b = 0;
	boolean c = true;
	
	public static void main(String[] args){
		System.out.println("Test");
		a = null;
		b = 10;
		//a.equals("ohhh exception");
	}
}
