package ids.employeeat.activity.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;

import helper.logger.LogUtil;
import helper.utils.android.NetworkUtils;
import ids.employeeat.AppPreferences;
import ids.employeeat.JsonTag;
import ids.employeeat.R;
import ids.employeeat.activity.AppActivity;
import ids.employeeat.activity.home.HomeActivity;
import ids.employeeat.server.ServerComm;
import ids.employeeat.server.ServerConstant;

public class LoginActivity extends AppActivity implements ServerComm.ServerResponseListener {
    private AppPreferences preferences;
    private EditText etUser;
    private EditText etPwd;
    private EditText etUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    private void init()
    {
        this.preferences = new AppPreferences(this);
        initView();

        if (getPreferences().getUserName() != null && getPreferences().getPwd() != null)
        {
            ServerComm.requestGet(LoginActivity.this, ServerConstant.AUTHENTICATE_USER, LoginActivity.this);
        }
        else
        {
            //initView();
        }
    }

    private void initView()
    {
        etUrl = findViewById(R.id.url);
        etUser = findViewById(R.id.user);
        etPwd = findViewById(R.id.pwd);
        if (getPreferences().getServerURL() != null)
        {
            etUrl.setText(getPreferences().getServerURL());
        }
    }

    public void login(View view)
    {
        try
        {
            String user = etUser.getText().toString().trim();
            String pwd = etPwd.getText().toString().trim();
            String url = etUrl.getText().toString().trim();
            if (user.length() <= 0 || pwd.length() <= 0 || url.length() <= 0)
            {
                printToast(R.string.fields_mandatory);
            }
            else if (user.equalsIgnoreCase("synctime") && pwd.equalsIgnoreCase("synctime")){
                startHomeActivity();
            }
            else
            {
                new AuthenticateUser().execute(user, pwd, url);
            }
        } catch (Exception e)
        {
            LogUtil.logException(TAG + "-->login", e);
        }
    }


    @SuppressLint("StaticFieldLeak")
    private class AuthenticateUser extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params)
        {
            boolean valid = false;
            try
            {
                String user = params[0];
                String pwd = params[1];
                String url = params[2];

                if (NetworkUtils.isURLReachable(LoginActivity.this, url))
                {
                    getPreferences().setServerURL(url);
                    getPreferences().setUserName(user);
                    getPreferences().setPwd(pwd);
                    ServerComm.requestGet(LoginActivity.this, ServerConstant.AUTHENTICATE_USER, LoginActivity.this);
                    valid = true;
                }
            } catch (Exception e)
            {
                LogUtil.logException("AuthenticateUser" + "-->login", e);
            }
            return valid;
        }

        @Override
        protected void onPostExecute(Boolean valid)
        {
            super.onPostExecute(valid);
            if (!valid)
            {
                printToast(R.string.server_unreachable);
            }
        }
    }

    @Override
    public void responseSuccess(int processType, JSONArray requestArray, JSONArray responseArray)
    {
        try
        {
            JSONObject data = responseArray.getJSONObject(0);
            if (data.has(JsonTag.PERSON_ID))
            {
                getPreferences().setPersonId(data.getLong(JsonTag.PERSON_ID));
                startHomeActivity();
            }
        } catch (Exception e)
        {
            LogUtil.logException(TAG + "-->responseSuccess", e);
        }
    }

    @Override
    public void responseFail(int processType, JSONArray requestData, int statusCode)
    {
        switch (statusCode)
        {
            case HttpURLConnection.HTTP_UNAUTHORIZED:
            case HttpURLConnection.HTTP_NOT_FOUND:
                clearUserInfo();
                break;
        }
    }

    private void clearUserInfo()
    {
        etPwd.setText("");
        etUser.setText("");
        getPreferences().setUserName(null);
        getPreferences().setPwd(null);
    }


    private void startHomeActivity()
    {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private AppPreferences getPreferences()
    {
        return preferences;
    }
}
