package helper.utils.java;

public class MathUtil
{
    public static int getInteger(Object object)
    {
        if (object instanceof String)
        {
            return getInteger((String) object);
        }
        else if (object instanceof Integer)
        {
            return (Integer) object;
        }
        else
        {
            throw new IllegalArgumentException("This Object doesn't represent an int");
        }
    }

    public static int getInteger(String value)
    {
        return Integer.parseInt(value);
    }

    public static double getDouble(Object objValue)
    {
        if (objValue instanceof String)
        {
            return getDouble((String) objValue);
        }
        else if (objValue instanceof Double)
        {
            return (Double) objValue;
        }
        else
        {
            throw new IllegalArgumentException("This Object doesn't represent an double");
        }
    }

    public static double getDouble(String value)
    {
        return Double.parseDouble(value);
    }

    public static long getLong(Object object)
    {
        if (object instanceof String)
        {
            return getLong((String) object);
        }
        else if (object instanceof Long)
        {
            return (Long) object;
        }
        else
        {
            throw new IllegalArgumentException("This Object doesn't represent an double");
        }
    }

    public static long getLong(String value)
    {
        return Long.parseLong(value);
    }

    public static String getString(Object object)
    {
        return String.valueOf(object);
    }
}
