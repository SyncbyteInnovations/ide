package helper.utils.android;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;

import androidx.core.content.ContextCompat;

public class ResourceUtil
{
    public static int getColor(Context context, int colorId)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            return context.getColor(colorId);
        }
        else
        {
            return ContextCompat.getColor(context, colorId);
        }
    }

    public static Drawable getDrawable(Context context, int imageId)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            return context.getDrawable(imageId);
        }
        else
        {
            return ContextCompat.getDrawable(context, imageId);
        }
    }

    public static Spanned fromHtml(String info)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            return Html.fromHtml(info, Html.FROM_HTML_MODE_LEGACY);
        }
        else
        {
            return Html.fromHtml(info);
        }
    }
}
