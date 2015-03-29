package ist.meic.pa.fields;

import java.lang.reflect.Field;

public class FieldFactory {
    public static FieldType getFieldType(Object o, Field f){
    	String type = f.getType().getSimpleName();
    	if("char".equalsIgnoreCase(type)) return new FieldChar(o, f);
    	else if("String".equalsIgnoreCase(type)) return new FieldString(o, f);
        else if("int".equalsIgnoreCase(type)) return new FieldInt(o, f);
        else if("boolean".equalsIgnoreCase(type)) return new FieldBoolean(o, f);
        else if("long".equalsIgnoreCase(type)) return new FieldLong(o, f);
        else if("double".equalsIgnoreCase(type)) return new FieldDouble(o, f);
        else if("float".equalsIgnoreCase(type)) return new FieldFloat(o, f);
        else if("byte".equalsIgnoreCase(type)) return new FieldFloat(o, f);
        return null;
    }
    
    public static Object getType(String value, String type){
        if("char".equalsIgnoreCase(type)) return (value.length()>=1)?new Character(value.charAt(0)):null;
        else if("String".equalsIgnoreCase(type)) return value;
        else if("int".equalsIgnoreCase(type)) return new Integer(Integer.parseInt(value));
        else if("boolean".equalsIgnoreCase(type)) return new Boolean(Boolean.parseBoolean(value));
        else if("long".equalsIgnoreCase(type)) return new Long( Long.parseLong(value));
        else if("double".equalsIgnoreCase(type)){ return new Double( Double.parseDouble(value));}
        else if("float".equalsIgnoreCase(type)) return new Float(Float.parseFloat(value));
        else if("byte".equalsIgnoreCase(type)) return new Byte(Byte.parseByte(value));
        return null;
       }
}
