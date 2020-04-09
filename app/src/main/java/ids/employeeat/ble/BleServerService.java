package ids.employeeat.ble;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.Toast;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiManager;
import java.util.List;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.io.IOException;
import java.net.UnknownHostException;
import java.net.SocketException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;

import ids.employeeat.wifi.WifiUtils;

public class BleServerService extends Service {

    public static final boolean DEBUG = true;
    public static final String TAG = "BleServerService";

    public static final int COMMAND_ADD_SERVICE = 0;
    public static final int COMMAND_WRITE_CHARACTERISTIC = 1;
    public static final int COMMAND_WRITE_DESCRIPTOR = 2;

    public static final String BLE_SERVER_CONNECTED =
            "com.android.cts.verifier.bluetooth.BLE_SERVER_CONNECTED";
    public static final String BLE_SERVER_DISCONNECTED =
            "com.android.cts.verifier.bluetooth.BLE_SERVER_DISCONNECTED";
    public static final String BLE_SERVICE_ADDED =
            "com.android.cts.verifier.bluetooth.BLE_SERVICE_ADDED";
    public static final String BLE_CHARACTERISTIC_READ_REQUEST =
            "com.android.cts.verifier.bluetooth.BLE_CHARACTERISTIC_READ_REQUEST";
    public static final String BLE_CHARACTERISTIC_WRITE_REQUEST =
            "com.android.cts.verifier.bluetooth.BLE_CHARACTERISTIC_WRITE_REQUEST";
    public static final String BLE_DESCRIPTOR_READ_REQUEST =
            "com.android.cts.verifier.bluetooth.BLE_DESCRIPTOR_READ_REQUEST";
    public static final String BLE_DESCRIPTOR_WRITE_REQUEST =
            "com.android.cts.verifier.bluetooth.BLE_DESCRIPTOR_WRITE_REQUEST";
    public static final String BLE_EXECUTE_WRITE =
            "com.android.cts.verifier.bluetooth.BLE_EXECUTE_WRITE";
    public static final String BLE_OPEN_FAIL =
            "com.android.cts.verifier.bluetooth.BLE_OPEN_FAIL";

    private static final UUID SERVICE_UUID =
            UUID.fromString("0000ff00-0000-1000-8000-00805f9b34fb");
    private static final UUID CHARACTERISTIC_UUID =
            UUID.fromString("0000ff02-0000-1000-8000-00805f9b34fb");
    private static final UUID UPDATE_CHARACTERISTIC_UUID =
            UUID.fromString("00009997-0000-1000-8000-00805f9b34fb");
    private static final UUID DESCRIPTOR_UUID =
            UUID.fromString("00009996-0000-1000-8000-00805f9b34fb");


    private BluetoothManager mBluetoothManager;
    private BluetoothGattServer mGattServer;
    private BluetoothGattService mService;
    private BluetoothDevice mDevice;
    private Timer mNotificationTimer;
    private Handler mHandler;
    private String mReliableWriteValue;
    private BluetoothLeAdvertiser mAdvertiser;
    private String dataAll = "";
    private int time = 0;
    private int sum_time = 0;



    @Override
    public void onCreate() {
        super.onCreate();

        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mAdvertiser = mBluetoothManager.getAdapter().getBluetoothLeAdvertiser();
        mGattServer = mBluetoothManager.openGattServer(this, mCallbacks);
        mService = createService();
        if (mGattServer != null) {
            mGattServer.addService(mService);
        }
        mDevice = null;
        mReliableWriteValue = null;

        mHandler = new Handler();
        if (mGattServer == null) {
            notifyOpenFail();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startAdvertise();
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopAdvertise();
        if (mGattServer == null) {
            return;
        }
        if (mDevice != null) mGattServer.cancelConnection(mDevice);
        mGattServer.close();
    }

    private void writeCharacteristic(String writeValue) {
        BluetoothGattCharacteristic characteristic = getCharacteristic(CHARACTERISTIC_UUID);
        if (characteristic != null) return;
        characteristic.setValue(writeValue);
    }

    private void writeDescriptor(String writeValue) {
        BluetoothGattDescriptor descriptor = getDescriptor();
        if (descriptor == null) return;
        descriptor.setValue(writeValue.getBytes());
    }

    private void notifyOpenFail() {
        if (DEBUG) Log.d(TAG, "notifyOpenFail");
        Intent intent = new Intent(BLE_OPEN_FAIL);
        sendBroadcast(intent);
    }

    private void notifyConnected() {
        if (DEBUG) Log.d(TAG, "notifyConnected");
        Intent intent = new Intent(BLE_SERVER_CONNECTED);
        sendBroadcast(intent);
    }

    private void notifyDisconnected() {
        if (DEBUG) Log.d(TAG, "notifyDisconnected");
        Intent intent = new Intent(BLE_SERVER_DISCONNECTED);
        sendBroadcast(intent);
    }

    private void notifyServiceAdded() {
        if (DEBUG) Log.d(TAG, "notifyServiceAdded");
        Intent intent = new Intent(BLE_SERVICE_ADDED);
        sendBroadcast(intent);
    }

    private void notifyCharacteristicReadRequest() {
        if (DEBUG) Log.d(TAG, "notifyCharacteristicReadRequest");
        Intent intent = new Intent(BLE_CHARACTERISTIC_READ_REQUEST);
        sendBroadcast(intent);
    }

    private void notifyCharacteristicWriteRequest() {
        if (DEBUG) Log.d(TAG, "notifyCharacteristicWriteRequest");
        Intent intent = new Intent(BLE_CHARACTERISTIC_WRITE_REQUEST);
        sendBroadcast(intent);
    }

    private void notifyDescriptorReadRequest() {
        if (DEBUG) Log.d(TAG, "notifyDescriptorReadRequest");
        Intent intent = new Intent(BLE_DESCRIPTOR_READ_REQUEST);
        sendBroadcast(intent);
    }

    private void notifyDescriptorWriteRequest() {
        if (DEBUG) Log.d(TAG, "notifyDescriptorWriteRequest");
        Intent intent = new Intent(BLE_DESCRIPTOR_WRITE_REQUEST);
        sendBroadcast(intent);
    }

    private void notifyExecuteWrite() {
        if (DEBUG) Log.d(TAG, "notifyExecuteWrite");
        Intent intent = new Intent(BLE_EXECUTE_WRITE);
        sendBroadcast(intent);
    }

    private BluetoothGattCharacteristic getCharacteristic(UUID uuid) {
        BluetoothGattCharacteristic characteristic =
                mService.getCharacteristic(uuid);
        if (characteristic == null) {
            //showMessage("Characteristic not found");
            return null;
        }
        return characteristic;
    }

    private BluetoothGattDescriptor getDescriptor() {
        BluetoothGattCharacteristic characteristic = getCharacteristic(CHARACTERISTIC_UUID);
        if (characteristic == null) return null;

        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(DESCRIPTOR_UUID);
        if (descriptor == null) {
            //showMessage("Descriptor not found");
            return null;
        }
        return descriptor;
    }

    private BluetoothGattService createService() {
        BluetoothGattService service =
                new BluetoothGattService(SERVICE_UUID, BluetoothGattService.SERVICE_TYPE_PRIMARY);
        BluetoothGattCharacteristic characteristic =
                new BluetoothGattCharacteristic(CHARACTERISTIC_UUID, 0x0A, 0x11);
        BluetoothGattDescriptor descriptor = new BluetoothGattDescriptor(DESCRIPTOR_UUID, 0x11);
        characteristic.addDescriptor(descriptor);
        service.addCharacteristic(characteristic);

        BluetoothGattCharacteristic notiCharacteristic =
                new BluetoothGattCharacteristic(UPDATE_CHARACTERISTIC_UUID, 0x10, 0x00);
        service.addCharacteristic(notiCharacteristic);

        return service;
    }

    private void beginNotification() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (mGattServer == null) {
                    if (DEBUG) Log.d(TAG, "GattServer is null, return");
                    return;
                }
                BluetoothGattCharacteristic characteristic =
                        mService.getCharacteristic(UPDATE_CHARACTERISTIC_UUID);
                if (characteristic == null) return;

                String date = (new Date()).toString();
                characteristic.setValue(date);
                mGattServer.notifyCharacteristicChanged(mDevice, characteristic, false);
            }
        };
        mNotificationTimer = new Timer();
        mNotificationTimer.schedule(task, 0, 1000);
    }

    private void stopNotification() {
        if (mNotificationTimer == null) return;
        mNotificationTimer.cancel();
        mNotificationTimer = null;
    }

    private void showMessage(final String msg) {
        mHandler.post(new Runnable() {
            public void run() {
                Toast.makeText(BleServerService.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private final BluetoothGattServerCallback mCallbacks = new BluetoothGattServerCallback() {
        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            if (DEBUG) Log.d(TAG, "onConnectionStateChange: newState=" + newState);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    mDevice = device;
                    notifyConnected();
                    //beginNotification();
                } else if (status == BluetoothProfile.STATE_DISCONNECTED) {
                    stopNotification();
                    notifyDisconnected();
                    dataAll = "";
                    time = 0;
                    sum_time = 0;
                    mDevice = null;
                }
            }
        }

        @Override
        public void onServiceAdded(int status, BluetoothGattService service) {
            if (DEBUG) Log.d(TAG, "onServiceAdded()");
            if (status == BluetoothGatt.GATT_SUCCESS) notifyServiceAdded();
        }

        @Override
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId,
                                                int offset, BluetoothGattCharacteristic characteristic) {
            if (mGattServer == null) {
                if (DEBUG) Log.d(TAG, "GattServer is null, return");
                return;
            }
            if (DEBUG) Log.d(TAG, "onCharacteristicReadRequest()");

            notifyCharacteristicReadRequest();
            mGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0,
                    characteristic.getValue());
        }

        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId,
                                                 BluetoothGattCharacteristic characteristic,
                                                 boolean preparedWrite, boolean responseNeeded,
                                                 int offset, byte[] value) {
            if (mGattServer == null) {
                if (DEBUG) Log.d(TAG, "GattServer is null, return");
                return;
            }
            if (DEBUG) Log.d(TAG, "onCharacteristicWriteRequest: preparedWrite=" + preparedWrite);

            notifyCharacteristicWriteRequest();
            if (preparedWrite) mReliableWriteValue = new String(value);
            else characteristic.setValue(value);

            String wifi_sid;
            String wifi_password_encode;
            String wifi_password;
            dataAll = dataAll + bytes2HexStr(value);
            int totalLength = 14 + Integer.parseInt(dataAll.substring(12,14),16)*2 + 2;

            if (totalLength%40 == 0){
                time = totalLength/40;
            }else{
                time = totalLength/40 + 1;
            }
            sum_time = sum_time + 1;
            if (sum_time == time){
                if (DEBUG) Log.d(TAG, "dataAll=" + dataAll);
                String key ="0sDx7QUIjEYUOvpLKCM3wzxs";
                byte[] keyBytes = key.getBytes();
                String data = dataAll.substring(14);
                if ("40444CFA03".equals(dataAll.substring(0,10))){
                    Log.d(TAG, "Integer.parseInt(data.substring(0,2),16)="+Integer.parseInt(data.substring(0,2),16));
                    wifi_sid = hexStringToString(data.substring(2,Integer.parseInt(data.substring(0,2),16)*2 + 2));
                    if (DEBUG) Log.d(TAG, "wifi_sid="+wifi_sid);
                    wifi_password_encode = data.substring(Integer.parseInt(data.substring(0,2),16)*2 + 4,Integer.parseInt(data.substring(Integer.parseInt(data.substring(0,2),16)*2 + 2,Integer.parseInt(data.substring(0,2),16)*2 + 4),16)*2 + Integer.parseInt(data.substring(0,2),16)*2 + 4);
                    byte[] srcBytes = hexStringToBytes(wifi_password_encode);
                    byte[] encodeBytes = srcBytes;
                    for (int i = 0; i < srcBytes.length; i++) {
                        for (byte keyByte : keyBytes) {
                            srcBytes[i] = (byte) (encodeBytes[i] ^ keyByte);
                        }
                    }
                    wifi_password = hexStringToString(bytes2HexStr(srcBytes));
                    if ("DELI@".equals(wifi_password.substring(0,5))){
                        wifi_password = wifi_password.substring(5);
                    }
                    if (DEBUG) Log.d(TAG, "wifi_password="+wifi_password);
                    WifiManager mWifiManager;
                    mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    mWifiManager.startScan();
                    List<ScanResult> scanResults = mWifiManager.getScanResults();
                    ScanResult scanResult;
                    if (scanResults == null) {
                        return;
                    }
                    for (int i = 0; i < scanResults.size(); i++) {
                        if(scanResults.get(i).SSID.equals(wifi_sid)){
                            scanResult = scanResults.get(i);
                            WifiConfiguration config = WifiUtils.createWifiInfo(scanResult, wifi_password);
                            WifiUtils.connectWifi(mWifiManager, config);
                            break;
                        }
                    }

                    String udpData = "40444CFA0300";
                    String deviceId = "DL-D5_"+ android.os.Build.SERIAL;
                    String deviceIdLength = intToHex(deviceId.length());

                    String productModel = "DL-D5";
                    String productModelLength = intToHex(productModel.length());
                    String loadDataLength = intToHex(deviceId.length() + productModel.length() + 4);
                    if(deviceId.length() < 10){
                        deviceIdLength = "0"+deviceIdLength;
                    }
                    if(productModel.length() < 10){
                        productModelLength = "0" + productModelLength;
                    }
                    udpData = udpData + loadDataLength + deviceIdLength + str2HexStr(deviceId) + productModelLength + str2HexStr(productModel) + "0000";
                    udpData = udpData + makeChecksum(udpData).toUpperCase();
                    if (DEBUG) Log.d(TAG,"udpData="+udpData);

                    for (int i = 0; i < 3; i++) {
                        send(hexStringToBytes(udpData));
                        if( i != 2){
                            SystemClock.sleep(1000);
                        }
                    }
                }
            }
            if (responseNeeded){
                mGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, null);
            }
        }

        @Override
        public void onDescriptorReadRequest(BluetoothDevice device, int requestId,
                                            int offset, BluetoothGattDescriptor descriptor) {
            if (mGattServer == null) {
                if (DEBUG) Log.d(TAG, "GattServer is null, return");
                return;
            }
            if (DEBUG) Log.d(TAG, "onDescriptorReadRequest(): (descriptor == getDescriptor())="
                    + (descriptor == getDescriptor()));

            notifyDescriptorReadRequest();
            mGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0,
                    descriptor.getValue());
        }

        @Override
        public void onDescriptorWriteRequest(BluetoothDevice device, int requestId,
                                             BluetoothGattDescriptor descriptor,
                                             boolean preparedWrite, boolean responseNeeded,
                                             int offset,  byte[] value) {
            if (mGattServer == null) {
                if (DEBUG) Log.d(TAG, "GattServer is null, return");
                return;
            }
            if (DEBUG) Log.d(TAG, "onDescriptorWriteRequest(): (descriptor == getDescriptor())="
                    + (descriptor == getDescriptor()));

            notifyDescriptorWriteRequest();
            descriptor.setValue(value);
            if (responseNeeded)
                mGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, null);
        }

        @Override
        public void onExecuteWrite(BluetoothDevice device, int requestId, boolean execute) {
            if (mGattServer == null) {
                if (DEBUG) Log.d(TAG, "GattServer is null, return");
                return;
            }
            if (DEBUG) Log.d(TAG, "onExecuteWrite, execute = " + execute);
            if (DEBUG) Log.d(TAG, "onExecuteWrite, mReliableWriteValue = " + mReliableWriteValue);
            if (execute) {
                notifyExecuteWrite();
                getCharacteristic(CHARACTERISTIC_UUID).setValue(mReliableWriteValue);
                mGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, null);
            }
        }
    };


    private boolean isNetworkConnected() {
        ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }



    public void send(byte[] messageByte) {
        int server_port = 24333;
        DatagramSocket s = null;
        try {
            s = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        InetAddress local = null;
        try {
            local = InetAddress.getByName("255.255.255.255");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        if (DEBUG) Log.d(TAG, "messageByte before:"+bytes2HexStr(messageByte));
        DatagramPacket p = new DatagramPacket(messageByte, messageByte.length, local,server_port);
        if (DEBUG) Log.d(TAG, "messageByte after:"+bytes2HexStr(messageByte));
        try {
            s.send(p);
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public String makeChecksum(String data)
    {
        if(data == null || data.equals(""))
        {
            return "";
        }
        int iTotal = 0;
        int iLen = data.length();
        int iNum = 0;

        while (iNum < iLen)
        {
            String s = data.substring(iNum, iNum + 2);
            System.out.println(s);
            iTotal += Integer.parseInt(s, 16);
            iNum = iNum + 2;
        }

        /**
         * 用256求余最大是255，即16进制的FF
         */
        int iMod = iTotal % 256;
        String sHex = Integer.toHexString(iMod);
        iLen = sHex.length();
        //如果不够校验位的长度，补0,这里用的是两位校验
        if (iLen < 2)
        {
            sHex = "0" + sHex;
        }
        return sHex;
    }


    public  String intToHex(int n) {
        StringBuffer s = new StringBuffer();
        String a;
        char []b = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        while(n != 0){
            s = s.append(b[n%16]);
            n = n/16;
        }
        a = s.reverse().toString();
        return a;
    }


    public String str2HexStr(String str) {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
        }
        return sb.toString();
    }

    public String hexStringToString(String s) {
        if (s == null || s.equals("")) {
            return null;
        }
        s = s.replace(" ", "");
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "UTF-8");
            new String();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }


    public String bytes2HexStr(byte[] byteArr) {
        String hexString = "0123456789ABCDEF";
        StringBuilder sb = new StringBuilder(byteArr.length*2);
        for(int i=0;i<byteArr.length;i++)
        {
            sb.append(hexString.charAt((byteArr[i]&0xf0)>>4));
            sb.append(hexString.charAt((byteArr[i]&0x0f)>>0));
        }
        return sb.toString();
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private void startAdvertise() {
        if (DEBUG) Log.d(TAG, "startAdvertise");
        AdvertiseData data = new AdvertiseData.Builder()
                .addServiceUuid(new ParcelUuid(SERVICE_UUID))
                .setIncludeDeviceName(true)
                .build();
        AdvertiseSettings setting = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                .setConnectable(true)
                .build();
        mAdvertiser.startAdvertising(setting, data, mAdvertiseCallback);
    }

    private void stopAdvertise() {
        if (DEBUG) Log.d(TAG, "stopAdvertise");
        mAdvertiser.stopAdvertising(mAdvertiseCallback);
    }

    private final AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback(){
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            if (DEBUG) Log.d(TAG, "onStartSuccess");
        }

        @Override
        public void onStartFailure(int errorCode) {
            if (DEBUG) Log.d(TAG, "onStartFailure, errorCode = " + errorCode);
        }
    };
}

