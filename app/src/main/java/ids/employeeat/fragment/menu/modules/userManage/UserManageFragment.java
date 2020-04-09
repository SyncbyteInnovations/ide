package ids.employeeat.fragment.menu.modules.userManage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import ids.employeeat.AppConstant;
import ids.employeeat.R;
import ids.employeeat.Theme;
import ids.employeeat.ble.BleClientService;
import ids.employeeat.fragment.AppFragment;

public class UserManageFragment extends AppFragment {
    private Button mRegisterUser;
    private EditText mUserInfoText;
    private EditText mUserIdText;
    private static final String TEST_REGISTER ="xcheng.action.register.test";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.adapter_usermanage_setting, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        this.mRegisterUser = view.findViewById(R.id.btn_register_user);
        this.mUserInfoText = view.findViewById(R.id.user_name);
        this.mUserIdText = view.findViewById(R.id.user_id);

        this.mRegisterUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testRegister();
            }
        });
        setHeaderTitle(R.string.register_user);
    }

    private void testRegister() {
        Log.d(TAG, "testRegister");
        String uname = "";
        String uid = "";
        if (mUserInfoText.length() != 0) {
            uname = mUserInfoText.getText().toString();
            uid = mUserIdText.getText().toString();

            Log.d(TAG, "testRegister, uname = " + uname);
            Intent intent = new Intent(TEST_REGISTER);
            intent.putExtra("uname", uname);
            intent.putExtra("uid", uid);

            getActivity().sendBroadcast(intent);
        }
    }
}
