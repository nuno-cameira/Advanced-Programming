package ist.meic.pa.fields;

import java.lang.reflect.Field;

public class FieldString extends FieldType {

	
	public FieldString(Object o, Field f) {
		this.o = o;
		this.f = f;
	}

	@Override
	public void modify(String value){
		try {
			f.set(o, value);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



}
