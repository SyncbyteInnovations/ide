package helper.utils.android;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

import helper.logger.LogUtil;

public class FragmentUtil {

    private static List<Fragment> fragmentList = new ArrayList<>();

    public static Fragment loadFragment(FragmentManager fragmentManager, Class classObject, Bundle bundle, boolean onBackTrigger, int fragmentId)
    {
        Fragment fragment = null;
        try
        {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            fragment = (Fragment) classObject.newInstance();


            if (bundle != null)
            {
                fragment.setArguments(bundle);
            }
            if (onBackTrigger)
            {

                transaction.replace(fragmentId, fragment).addToBackStack(null).commit();
            }
            else
            {
                transaction.replace(fragmentId, fragment).commit();
            }
            fragmentList.add(fragment);
        } catch (Exception e)
        {
            LogUtil.logException("FragmentUtil.loadFragment", e);
        }
        return fragment;
    }
}
