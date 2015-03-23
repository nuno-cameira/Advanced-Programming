package ist.meic.pa.fields;

import java.lang.reflect.Field;

public class FieldLong extends FieldType {

	public FieldLong(Object o, Field f){
		this.o = o;
		this.f = f;
	}
	
	@Override
	public void modify(String value){
		// TODO Auto-generated method stub
		try {
			f.set(o, Long.parseLong(value));
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
