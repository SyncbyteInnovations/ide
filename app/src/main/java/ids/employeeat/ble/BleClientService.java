package ids.employeeat.ble;

import java.util.Arrays;
import java.util.UUID;
import java.util.List;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.Toast;

public class BleClientService extends Service {

    private static final boolean DEBUG = true;
    private static final String TAG = "BleClientService";
    private static final String TEST_REGISTER ="xcheng.action.register.test";
    private static final String TEST_REGISTER_CANCEL ="xcheng.action.cancel_register.test";
    private static final String TEST_BIND_ORG ="xcheng.action.org_bind.test";
    private static final String TEST_UNBIND_ORG ="xcheng.action.org_unbind.test";
    private static final String TEST_CONNECT_WIFI ="xcheng.action.connect.wifi.test";
    private static final String TEST_HOST_ADDRESS = "xcheng.action.host.address";

    private static final UUID SERVICE_UUID =
            UUID.fromString("0000ff00-0000-1000-8000-00805f9b34fb");
    private static final UUID CHARACTERISTIC_UUID =
            UUID.fromString("0000ff02-0000-1000-8000-00805f9b34fb");
    private static final UUID UPDATE_CHARACTERISTIC_UUID =
            UUID.fromString("0000ff01-0000-1000-8000-00805f9b34fb");
    private static final UUID DESCRIPTOR_UUID =
            UUID.fromString("00009996-0000-1000-8000-00805f9b34fb");

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mDevice;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothLeScanner mScanner;
    private Handler mHandler;
    private Context mContext;
    private String mSn;

    private static final String WRITE_VALUE = "test_";

    @Override
    public void onCreate() {
        super.onCreate();

        registerReceivers();
        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        mScanner = mBluetoothAdapter.getBluetoothLeScanner();
        mHandler = new Handler();
        mContext = this;
        startScan();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mSn = intent.getStringExtra("SN");
        Log.d(TAG, "onStartCommand, mSn = " + mSn);
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceivers();
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
        stopScan();
    }

    private void registerReceivers() {
        registerBleReceiver();
    }

    private void unregisterReceivers() {
        unregisterReceiver(mBleReceiver);
    }

    private void registerBleReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(TEST_REGISTER);
        filter.addAction(TEST_REGISTER_CANCEL);
        filter.addAction(TEST_BIND_ORG);
        filter.addAction(TEST_UNBIND_ORG);
        filter.addAction(TEST_CONNECT_WIFI);
        filter.addAction(TEST_HOST_ADDRESS);
        registerReceiver(mBleReceiver, filter);
    }

    private BroadcastReceiver mBleReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(TEST_REGISTER)) {
                String uid = intent.getStringExtra("uid");
                String uname = intent.getStringExtra("uname");
                String userInfo = "start/" + uid + "/" + uname + "/end";
                Log.d(TAG, "TEST_REGISTER, id = " + uid);
                Log.d(TAG, "TEST_REGISTER, name = " + uname);
                Log.d(TAG, "TEST_REGISTER, userInfo = " + userInfo);
                writeUserInfoCharacteristic(userInfo);
                //writeCharacteristic(WRITE_VALUE + "reg_" + uid);
            } else if (action.equals(TEST_REGISTER_CANCEL)) {
                Log.d(TAG, "TEST_REGISTER_CANCEL");
                writeCharacteristic(WRITE_VALUE + "cancel");
            } else if (action.equals(TEST_BIND_ORG)) {
                Log.d(TAG, "TEST_BIND_ORG");
                writeCharacteristic(WRITE_VALUE + "bind");
            } else if (action.equals(TEST_UNBIND_ORG)) {
                Log.d(TAG, "TEST_UNBIND_ORG");
                writeCharacteristic(WRITE_VALUE + "unbind");
            } else if (action.equals(TEST_CONNECT_WIFI)) {
                Log.d(TAG, "TEST_CONNECT_WIFI");
                String ssid = intent.getStringExtra("ssid");
                String password = intent.getStringExtra("password");
                String wifi = "start/" + ssid + "/" + password + "/end";
                Log.d(TAG, "TEST_CONNECT_WIFI ssid = " + ssid);
                Log.d(TAG, "TEST_CONNECT_WIFI password = " + password);
                Log.d(TAG, "TEST_CONNECT_WIFI wifi = " + wifi);
                writeWifiCharacteristic(wifi);
            } else if (action.equals(TEST_HOST_ADDRESS)) {
                String host_info = intent.getStringExtra("host_info");
                String host_info_str = "start/" + host_info + "/end";

                Log.d(TAG, "TEST_HOST_ADDRESS host_info_str = " + host_info_str);
                writeHostInfo(host_info_str);
            }
        }
    };

    //device.delicloud.com:1883
    //t.device.delicloud.com:1883
    private void writeHostInfo(String host_info) {
        if(host_info.length() > 8) {
            Log.d(TAG, "writeHostInfo1 = " + host_info);
            writeCharacteristic(WRITE_VALUE + "host:" + host_info.substring(0, 8));
            sleep(200);
            writeHostInfo(host_info.substring(8));
        } else {
            Log.d(TAG, "writeHostInfo2 = " + host_info);
            writeCharacteristic(WRITE_VALUE + "host:" + host_info);
        }
    }

    private void writeWifiCharacteristic(String wifi) {
        if (wifi.length() > 9) {
            Log.d(TAG, "writeWifiCharacteristic1 = " + wifi.substring(0, 9));
            writeCharacteristic(WRITE_VALUE + "net_" + wifi.substring(0, 9));
            sleep(200);
            writeWifiCharacteristic(wifi.substring(9));
        } else {
            Log.d(TAG, "writeWifiCharacteristic2 = " + wifi);
            writeCharacteristic(WRITE_VALUE + "net_" + wifi);
        }
    }

    private void writeUserInfoCharacteristic(String userInfo) {
        if (userInfo.length() > 9) {
            Log.d(TAG, "writeUserInfoCharacteristic = " + userInfo.substring(0, 9));
            writeCharacteristic(WRITE_VALUE + "usr_" + userInfo.substring(0, 9));
            sleep(200);
            writeUserInfoCharacteristic(userInfo.substring(9));
        } else {
            Log.d(TAG, "writeUserInfoCharacteristic2 = " + userInfo);
            writeCharacteristic(WRITE_VALUE + "usr_" + userInfo);
        }
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Log.e(TAG, "Error in thread sleep", e);
        }
    }

    private void writeCharacteristic(String writeValue) {
        BluetoothGattCharacteristic characteristic = getCharacteristic(CHARACTERISTIC_UUID);
        if (characteristic == null) return;
        characteristic.setValue(writeValue);
        mBluetoothGatt.writeCharacteristic(characteristic);
    }

    private void readCharacteristic() {
        BluetoothGattCharacteristic characteristic = getCharacteristic(CHARACTERISTIC_UUID);
        if (characteristic != null) mBluetoothGatt.readCharacteristic(characteristic);
    }

    private void writeDescriptor(String writeValue) {
        BluetoothGattDescriptor descriptor = getDescriptor();
        if (descriptor == null) return;
        descriptor.setValue(writeValue.getBytes());
        mBluetoothGatt.writeDescriptor(descriptor);
    }

    private void readDescriptor() {
        BluetoothGattDescriptor descriptor = getDescriptor();
        if (descriptor != null) mBluetoothGatt.readDescriptor(descriptor);
    }

    private void setNotification(boolean enable) {
        BluetoothGattCharacteristic characteristic = getCharacteristic(UPDATE_CHARACTERISTIC_UUID);
        if (characteristic != null)
            mBluetoothGatt.setCharacteristicNotification(characteristic, enable);
    }

    private void notifyError(String message) {
        showMessage(message);
    }

    private BluetoothGattService getService() {
        if (mBluetoothGatt == null) return null;

        BluetoothGattService service = mBluetoothGatt.getService(SERVICE_UUID);
        if (service == null) {
            showMessage("Service not found");
            return null;
        }
        return service;
    }

    private BluetoothGattCharacteristic getCharacteristic(UUID uuid) {
        BluetoothGattService service = getService();
        if (service == null) return null;

        BluetoothGattCharacteristic characteristic = service.getCharacteristic(uuid);
        if (characteristic == null) {
            showMessage("Characteristic not found");
            return null;
        }
        return characteristic;
    }

    private BluetoothGattDescriptor getDescriptor() {
        BluetoothGattCharacteristic characteristic = getCharacteristic(CHARACTERISTIC_UUID);
        if (characteristic == null) return null;

        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(DESCRIPTOR_UUID);
        if (descriptor == null) {
            showMessage("Descriptor not found");
            return null;
        }
        return descriptor;
    }

    private void showMessage(final String msg) {
        mHandler.post(new Runnable() {
            public void run() {
                Toast.makeText(BleClientService.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void reliableWrite() {
        mBluetoothGatt.beginReliableWrite();
        writeCharacteristic(WRITE_VALUE);
        if (!mBluetoothGatt.executeReliableWrite()) {
            Log.w(TAG, "reliable write failed");
        }
        mBluetoothGatt.abortReliableWrite();
    }

    private final BluetoothGattCallback mGattCallbacks = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (DEBUG) Log.d(TAG, "onConnectionStateChange");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    stopScan();
                    mBluetoothGatt.discoverServices();
                } else if (status == BluetoothProfile.STATE_DISCONNECTED) {
                }
            } else {
                showMessage("Failed to connect");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (DEBUG) Log.d(TAG, "onServiceDiscovered");
            if ((status == BluetoothGatt.GATT_SUCCESS) &&
                    (mBluetoothGatt.getService(SERVICE_UUID) != null)) {
                //writeCharacteristic(WRITE_VALUE);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic, int status) {
            String value = characteristic.getStringValue(0);
            if (DEBUG) Log.d(TAG, "onCharacteristicWrite: characteristic.val="
                    + value + " status=" + status);
            BluetoothGattCharacteristic mCharacteristic = getCharacteristic(CHARACTERISTIC_UUID);
            if ((status == BluetoothGatt.GATT_SUCCESS) &&
                    (value.equals(mCharacteristic.getStringValue(0)))) {
                //readCharacteristic();
                //notifyError("onCharacteristicWrite");
            } else {
                notifyError("Failed to write characteristic: " + value);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            if (DEBUG) Log.d(TAG, "onCharacteristicRead");
            if ((status == BluetoothGatt.GATT_SUCCESS) &&
                    (characteristic.getUuid().equals(CHARACTERISTIC_UUID))) {
                writeDescriptor(WRITE_VALUE);
                notifyError("onCharacteristicWrite");
            } else {
                notifyError("Failed to read characteristic");
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor,
                                      int status) {
            if (DEBUG) Log.d(TAG, "onDescriptorWrite");
            if ((status == BluetoothGatt.GATT_SUCCESS) &&
                    (descriptor.getUuid().equals(DESCRIPTOR_UUID))) {
                readDescriptor();
            } else {
                notifyError("Failed to write descriptor");
            }
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor,
                                     int status) {
            if (DEBUG) Log.d(TAG, "onDescriptorRead");
            if ((status == BluetoothGatt.GATT_SUCCESS) &&
                    (descriptor.getUuid() != null) &&
                    (descriptor.getUuid().equals(DESCRIPTOR_UUID))) {
                setNotification(true);
            } else {
                notifyError("Failed to read descriptor");
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            if (DEBUG) Log.d(TAG, "onCharacteristicChanged");
            if ((characteristic.getUuid() != null) &&
                    (characteristic.getUuid().equals(UPDATE_CHARACTERISTIC_UUID))) {
                setNotification(false);
                mBluetoothGatt.readRemoteRssi();
            }
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            if (DEBUG) Log.d(TAG, "onReliableWriteComplete: " + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
            } else {
            }
            mBluetoothGatt.disconnect();
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            if (DEBUG) Log.d(TAG, "onReadRemoteRssi");
            if (status == BluetoothGatt.GATT_SUCCESS) {
            } else {
            }
            reliableWrite();
        }
    };

    private final ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            if (mBluetoothGatt == null) {
                String name = result.getDevice().getName();
                Log.d(TAG, "result.getDevice().getName(): " + name);
                if (name != null && name.endsWith(mSn)) {
                    mBluetoothGatt = result.getDevice().connectGatt(mContext, false, mGattCallbacks, BluetoothDevice.TRANSPORT_LE);
                }
            }
        }
    };

    private void startScan() {
        if (DEBUG) Log.d(TAG, "startScan");
        List<ScanFilter> filter = Arrays.asList(new ScanFilter.Builder().build());
        ScanSettings setting = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
        mScanner.startScan(filter, setting, mScanCallback);
    }

    private void stopScan() {
        if (DEBUG) Log.d(TAG, "stopScan");
        mScanner.stopScan(mScanCallback);
    }
}
