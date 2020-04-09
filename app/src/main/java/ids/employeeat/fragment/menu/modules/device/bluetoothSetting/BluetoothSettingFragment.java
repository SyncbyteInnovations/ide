package ids.employeeat.fragment.menu.modules.device.bluetoothSetting;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import ids.employeeat.R;
import ids.employeeat.Theme;
import ids.employeeat.ble.BleClientService;
import ids.employeeat.fragment.AppFragment;

public class BluetoothSettingFragment extends AppFragment {
    private Button mBluetoothConnectButton;
    private TextView mText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.adapter_bluetooth_setting, container, false);
        init(view);
        return view;
    }

    private void init(View view){

        this.mBluetoothConnectButton = view.findViewById(R.id.btn_connect_bluetooth);
        this.mText = view.findViewById(R.id.serialnumber);

        this.mBluetoothConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), BleClientService.class);
                intent.putExtra("SN", mText.getText().toString());
                getActivity().startService(intent);
            }
        });

        setHeaderTitle(R.string.bluetooth_setup);
    }
}
