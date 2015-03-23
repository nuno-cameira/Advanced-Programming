package ist.meic.pa.fields;

import java.lang.reflect.Field;

public class FieldInt extends FieldType {

	public FieldInt(Object o, Field f){
		this.o = o;
		this.f = f;
	}
	
	@Override
	public void modify(String value){
		// TODO Auto-generated method stub
		try {
			f.set(o, Integer.parseInt(value));
		} catch (IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
