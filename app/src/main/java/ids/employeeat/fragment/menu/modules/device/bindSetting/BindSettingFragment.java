package ids.employeeat.fragment.menu.modules.device.bindSetting;

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

public class BindSettingFragment extends AppFragment {
    private Button mBindConnectButton;
    private Button msetHostButton;
    private EditText mText;
    private static final String TEST_BIND_ORG ="xcheng.action.org_bind.test";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.adapter_usebind_setting, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        this.mBindConnectButton = view.findViewById(R.id.btn_bind);
        this.msetHostButton = view.findViewById(R.id.btn_set_host);
        this.mText = view.findViewById(R.id.bind_host);

        this.mBindConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TEST_BIND_ORG);
                getActivity().sendBroadcast(intent);
            }
        });

        this.msetHostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setHostConfig();
            }
        });

        setHeaderTitle(R.string.device_bind);

    }

    private void setHostConfig() {
        Log.d("BleClientActivity", "setHostConfig()");
        String mHostAddress = mText.getText().toString();
        if (mHostAddress.length() != 0) {
            Intent intent = new Intent("xcheng.action.host.address");
            intent.putExtra("host_info", mHostAddress);
            getActivity().sendBroadcast(intent);
            return;
        }
        Log.d("BleClientActivity", "setHostConfig() host is null");
    }
}
