package ist.meic.pa;

import javassist.*;

public class Test {
	
	static String a = "bla";
	static int b = 0;
	boolean c = true;
	
	public static int add(int a, int b){
		System.out.println("in method add");
		return a+b;
	}
	
	public static void main(String[] args){
		System.out.println("in main of Test");
		a = null;
		b = 10;
		add(2,3);
		//a.equals("ohhh exception");
	}
}
