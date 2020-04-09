package ids.employeeat.fragment.menu.modules.device.wifiSetting;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ids.employeeat.AppConstant;
import ids.employeeat.R;
import ids.employeeat.Theme;
import ids.employeeat.fragment.AppFragment;
import ids.employeeat.wifi.WifiUtils;

public class WifiSettingFragment extends AppFragment {
    private Spinner wifiSpinner;
    private EditText wifiPassword;
    private Button connectWifi;
    private WifiManager mWifiManager;
    private static final String TEST_CONNECT_WIFI ="xcheng.action.connect.wifi.test";
    private Map<String,ScanResult> scanMap = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.adapter_wifi_setting, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        wifiPassword = view.findViewById(R.id.wifi_password);
        connectWifi = view.findViewById(R.id.connect_wifi);

        connectWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectWifi();
            }
        });

        mWifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        List<ScanResult> wifiScanList = mWifiManager.getScanResults();

        List<String> wifis  =  new ArrayList<String>();
        for (int i = 0; i < wifiScanList.size(); i++) {
            wifis.add(((wifiScanList.get(i).SSID)));
            scanMap.put(wifiScanList.get(i).SSID,wifiScanList.get(i));
        }
        wifiSpinner = view.findViewById(R.id.wifi_list);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, wifis);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        wifiSpinner.setAdapter(adapter);

        setHeaderTitle(R.string.wifi_setup);
    }

    private void connectWifi() {
        String SSID = wifiSpinner.getSelectedItem().toString();
        String password = wifiPassword.getText().toString();

        WifiConfiguration config = WifiUtils.createWifiInfo(scanMap.get(SSID), password);
        WifiUtils.connectWifi(mWifiManager, config);
        Log.d(TAG, "connectWifi, mScanResult = " +SSID);
        Intent intent = new Intent(TEST_CONNECT_WIFI);
        intent.putExtra("ssid",SSID);
        intent.putExtra("password", password);
        getActivity().sendBroadcast(intent);
    }
}
