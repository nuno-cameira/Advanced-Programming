package ist.meic.pa.fields;

import java.lang.reflect.Field;

public class FieldFloat extends FieldType {

	public FieldFloat(Object o, Field f){
		this.o = o;
		this.f = f;
	}

	@Override
	public void modify(String value){
		try {
			f.set(o, Float.parseFloat(value));
		} catch (IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	

}
