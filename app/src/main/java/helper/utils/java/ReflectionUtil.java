package helper.utils.java;

import java.lang.reflect.Constructor;

public class ReflectionUtil
{
    public static String getClassName(Class<?> classObject)
    {
        String className = null;
        if (classObject != null)
        {
            className = classObject.getName().trim();
            if (!className.equals("") && className.lastIndexOf('.') > 0)
            {
                className = className.substring(className.lastIndexOf('.') + 1);
            }
        }
        return className;
    }

    public static Object getInstance(Class<?> classObject) throws Exception
    {
        Object object = null;
        if (classObject != null)
        {
            Constructor<?> constructor = classObject.getConstructor();
            if (constructor != null)
            {
                object = constructor.newInstance();
            }
        }

        return object;
    }
}
