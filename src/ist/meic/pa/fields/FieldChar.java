package ist.meic.pa.fields;

import java.lang.reflect.Field;

public class FieldChar extends FieldType {

	public FieldChar(Object o, Field f){
		this.o = o;
		this.f = f;
	}

	@Override
	public void modify(String value){
		try {
			f.set(o, value.charAt(0));
		} catch (IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	

}
