package com.jtk.persistence;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.jtk.util.PropertiesUtil;
import com.jtk.util.StringUtil;

/**
 * <p>
 * Jtk Persistence
 * <p>
 * Provides common methods to auto load properties to an entity class and auto
 * save object of entity class to properties file.
 * <p>
 * We also provide some annotations, such as {@link JtkEntity}, {@link JtkKey},
 * {@link JtkNone}, to annotate the entity class and its keys. When you design
 * an entity class, you should use {@link JtkEntity} on entity class to specify
 * the properties file, and use {@link JtkKey} on member variables to specify
 * the keys, or use {@link JtkNone} on member variables to declare they are not
 * keys in properties. If there is no {@link JtkKey} on a member variable, we
 * use the variable's name as key's name.
 * <p>
 * By now, we support types of properties values as follow: <blockquote><code>
 * <p>
 * byte, short, int, long, float, double, boolean, char.
 * <p>
 * Byte, Short, Integer, Long, Float, Double, Boolean, Character.
 * <p>
 * String. 
 * </code></blockquote>
 * 
 * @author Jason
 * @version 1.0
 * 
 */
public class JtkPersistence {

	/**
	 * Get the instance of an entity class automatically.
	 * 
	 * @param cls
	 *            the Class of an entity class
	 * @return the instance of cls mapped to properties
	 * @throws JtkPersistenceException
	 *             when exceptions thrown during load the properties
	 */
	public static Object getInstance(Class<?> cls)
			throws JtkPersistenceException {
		// Get @JtkEntity of entity class
		JtkEntity entity = getJtkEntity(cls);

		// Load properties by specified name
		PropertiesUtil propertiesUtil = getPropertiesUtil(entity.name());

		// Get instance of Class<T> cls
		Object object = null;
		try {
			object = cls.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new JtkPersistenceException("Get new instance of "
					+ cls.getName() + " failed!", e);
		}

		// Get fields' keys & values and set to instance
		Field[] fields = cls.getDeclaredFields();
		for (Field f : fields) {
			// Jump static fields
			if ((f.getModifiers() & Modifier.STATIC) != 0) {
				continue;
			}

			// Jump @JtkNone fields
			if (f.getAnnotation(JtkNone.class) != null) {
				continue;
			}

			// Is the type of field supported
			if (!isSupported(f.getType())) {
				throw new JtkPersistenceException("Unsupported type "
						+ f.getType() + " of field " + f.getName());
			}

			// Get @JtkKey of field
			JtkKey key = f.getAnnotation(JtkKey.class);
			String keyName;
			String defaultValue;
			if (key != null) {
				keyName = key.name();
				defaultValue = key.defaultValue();
			} else {
				keyName = f.getName();
				defaultValue = "";
			}
			String keyValue = propertiesUtil.getValue(keyName, defaultValue);
			if (StringUtil.isNull(keyValue)) {
				continue;
			}

			// Cast value to type of field
			Object value = null;
			try {
				value = cast(f.getType(), keyValue);
			} catch (NumberFormatException e) {
				throw new JtkPersistenceException(
						"Cast " + keyValue + " to " + f.getType()
								+ " of field " + f.getName() + " failed!", e);
			}

			// Set value to field
			try {
				f.setAccessible(true);
				f.set(object, value);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
				throw new JtkPersistenceException("Set value " + value
						+ " to field " + f.getName() + " failed!", e);
			}
		}

		return object;
	}

	/**
	 * Save the object of an entity class to properties file
	 * 
	 * @param obj
	 *            the object of an entity class
	 * @throws JtkPersistenceException
	 *             when exceptions thrown during save the object
	 */
	public static void save(Object obj) throws JtkPersistenceException {
		// Pre judge NullPointer
		if (obj == null) {
			throw new JtkPersistenceException("Object is null!");
		}

		// Get Class of obj
		Class<?> cls = obj.getClass();

		// Get @JtkEntity of entity class
		JtkEntity entity = getJtkEntity(cls);

		// Load properties by specified name
		PropertiesUtil propertiesUtil = getPropertiesUtil(entity.name());

		// Get fields' keys & values from instance
		Field[] fields = cls.getDeclaredFields();
		for (Field f : fields) {
			// Jump static fields
			if ((f.getModifiers() & Modifier.STATIC) != 0) {
				continue;
			}

			// Jump @JtkNone fields
			if (f.getAnnotation(JtkNone.class) != null) {
				continue;
			}

			// Is the type of field supported
			if (!isSupported(f.getType())) {
				throw new JtkPersistenceException("Unsupported type "
						+ f.getType() + " of field " + f.getName());
			}

			// Get @JtkKey of field
			JtkKey key = f.getAnnotation(JtkKey.class);
			String keyName = key == null ? f.getName() : key.name();
			String keyValue = null;
			try {
				f.setAccessible(true);
				Object value = f.get(obj);
				keyValue = value == null ? "" : value.toString();
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new JtkPersistenceException("Get value of field "
						+ f.getName() + " error!", e);
			}

			// Set key & value to properties
			propertiesUtil.setValue(keyName, keyValue);
		}

		// Save keys & values to properties
		try {
			propertiesUtil.save();
		} catch (IOException e) {
			throw new JtkPersistenceException(
					"Save keys & values to properties file error!", e);
		}
	}

	/**
	 * Get the JtkEntity annotation of the entity class
	 * 
	 * @param cls
	 *            Class of the entity class
	 * @return
	 * @throws JtkPersistenceException
	 */
	private static JtkEntity getJtkEntity(Class<?> cls)
			throws JtkPersistenceException {
		JtkEntity entity = cls.getAnnotation(JtkEntity.class);
		if (entity == null) {
			throw new JtkPersistenceException("Class " + cls.getName()
					+ " is not a legal properties entity class!");
		}
		return entity;
	}

	/**
	 * Get the PropertiesUtil by the name of properties file
	 * 
	 * @param propertiesName
	 *            the name of properties file
	 * @return
	 * @throws JtkPersistenceException
	 */
	private static PropertiesUtil getPropertiesUtil(String propertiesName)
			throws JtkPersistenceException {
		try {
			return PropertiesUtil.getInstance(propertiesName);
		} catch (IOException e) {
			throw new JtkPersistenceException("Load properties resource "
					+ propertiesName + " error!", e);
		}
	}

	/**
	 * Return if the type of a field is supported
	 * 
	 * @param cls
	 *            the type of a field
	 * @return {@code true} if the type is supported, otherwise {@code false}
	 */
	private static boolean isSupported(Class<?> cls) {
		return cls.isPrimitive() || cls == Byte.class || cls == Short.class
				|| cls == Integer.class || cls == Long.class
				|| cls == Float.class || cls == Double.class
				|| cls == Character.class || cls == Boolean.class
				|| cls == String.class;
	}

	/**
	 * Cast a string value to the type of cls
	 * 
	 * @param cls
	 *            the type of value to cast
	 * @param s
	 *            the value to cast
	 * @return
	 * @throws NumberFormatException
	 */
	private static Object cast(Class<?> cls, String s)
			throws NumberFormatException {
		if (cls == byte.class) {
			return Byte.parseByte(s);
		} else if (cls == short.class) {
			return Short.parseShort(s);
		} else if (cls == int.class) {
			return Integer.parseInt(s);
		} else if (cls == long.class) {
			return Long.parseLong(s);
		} else if (cls == float.class) {
			return Float.parseFloat(s);
		} else if (cls == double.class) {
			return Double.parseDouble(s);
		} else if (cls == boolean.class) {
			return Boolean.parseBoolean(s);
		} else if (cls == char.class) {
			return s.charAt(0);
		} else if (cls == Byte.class) {
			return Byte.valueOf(s);
		} else if (cls == Short.class) {
			return Short.valueOf(s);
		} else if (cls == Integer.class) {
			return Integer.valueOf(s);
		} else if (cls == Long.class) {
			return Long.valueOf(s);
		} else if (cls == Float.class) {
			return Float.valueOf(s);
		} else if (cls == Double.class) {
			return Double.valueOf(s);
		} else if (cls == Boolean.class) {
			return Boolean.valueOf(s);
		} else if (cls == Character.class) {
			return Character.valueOf(s.charAt(0));
		} else {
			return s;
		}
	}

}
