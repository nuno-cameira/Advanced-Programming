package ist.meic.pa;

public class Unwrapper {

	/**
	 * Checks if the generic class type given is of wrapper type
	 * 
	 * @param generic class type
	 */
	public static boolean isWrapperType(Class<?> c) {

		if (c == Integer.class) {
			return true;
		} else if (c == Float.class) {
			return true;
		} else if (c == Double.class) {
			return true;
		} else if (c == Long.class) {
			return true;
		} else if (c == Short.class) {
			return true;
		} else if (c == Boolean.class) {
			return true;
		} else if (c == Byte.class) {
			return true;
		}
		return false;

	}

	/**
	 * Converts the generic class type into it's primitive
	 * 
	 * @param generic class type
	 * @return primitive class type
	 */
	public static Class<?> unwrap(Class<?> c) {

		if (c == Integer.class) {
			c = int.class;
		} else if (c == Float.class) {
			c = float.class;
		} else if (c == Double.class) {
			c = double.class;
		} else if (c == Long.class) {
			c = long.class;
		} else if (c == Short.class) {
			c = short.class;
		} else if (c == Boolean.class) {
			c = boolean.class;
		} else if (c == Byte.class) {
			c = byte.class;
		}

		return c;

	}
}
