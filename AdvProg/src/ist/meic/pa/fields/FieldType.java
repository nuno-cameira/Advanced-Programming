package ist.meic.pa.fields;

import java.lang.reflect.Field;

public abstract class FieldType{
	
	public Object o = null;
	public Field f = null;

	public abstract void modify(String value);
	
}
