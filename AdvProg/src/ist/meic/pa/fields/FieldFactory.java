package ist.meic.pa.fields;

import java.lang.reflect.Field;

public class FieldFactory {
    public static FieldType getFieldType(Object o, Field f){
    	String type = f.getType().getSimpleName();
        if("String".equalsIgnoreCase(type)) return new FieldString(o, f);
        else if("int".equalsIgnoreCase(type)) return new FieldInt(o, f);
        else if("boolean".equalsIgnoreCase(type)) return new FieldBoolean(o, f);
        else if("long".equalsIgnoreCase(type)) return new FieldLong(o, f);
        return null;
    }
}
