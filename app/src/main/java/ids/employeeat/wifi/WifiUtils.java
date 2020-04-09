package ids.employeeat.wifi;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

public class WifiUtils {
    private static final String TAG = "WifiUtils";
    public static final int SECURITY_NONE = 0;
    public static final int SECURITY_WEP = 1;
    public static final int SECURITY_PSK = 2;
    public static final int SECURITY_PSK2 = 3;
    public static final int SECURITY_EAP = 4;

    private static final int PSK_UNKNOWN = 0;
    private static final int PSK_WPA = 1;
    private static final int PSK_WPA2 = 2;
    private static final int PSK_WPA_WPA2 = 3;

    public static boolean connectWifi(WifiManager wifiManager, WifiConfiguration config){
        Log.d(TAG, "connectWifi, config = " + config);
        if (wifiManager == null || config == null) {
            return false;
        }


        int wcgID = wifiManager.addNetwork(config);
        return wifiManager.enableNetwork(wcgID, true);
    }

    public static WifiConfiguration createWifiInfo(ScanResult scanResult, String password) {
        int type = getSecurity(scanResult);
        // nopass
        if (type == SECURITY_NONE) {
            return createOpenWifiInfo(scanResult);
        }
        // wep
        if (type == SECURITY_WEP) {
            return createWepWifiInfo(scanResult, password);
        }
        // wpa2
        if (type == SECURITY_PSK2) {
            return createPskWifiInfo(scanResult, password, 4);
        }
        // wpa
        if (type == SECURITY_PSK) {
            return createPskWifiInfo(scanResult, password, KeyMgmt.WPA_PSK);
        }

        return createDefaultWifiInfo(scanResult);
    }

    public static WifiConfiguration createDefaultWifiInfo(ScanResult result) {
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = "\"" + result.SSID + "\"";
        return config;
    }

    public static WifiConfiguration createOpenWifiInfo(ScanResult result) {
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = "\"" + result.SSID + "\"";
        config.allowedKeyManagement.set(KeyMgmt.NONE);
        return config;
    }

    public static WifiConfiguration createWepWifiInfo(ScanResult result, String password) {
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = "\"" + result.SSID + "\"";
        if (!TextUtils.isEmpty(password)) {
            config.wepKeys[0] = "\"" + password + "\"";
        }
        config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
        config.allowedAuthAlgorithms.set(AuthAlgorithm.SHARED);
        config.allowedKeyManagement.set(KeyMgmt.NONE);
        config.wepTxKeyIndex = 0;
        return config;
    }

    public static WifiConfiguration createPskWifiInfo(ScanResult result, String password, int key) {
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = "\"" + result.SSID + "\"";
        config.preSharedKey = "\"" + password + "\"";
        config.allowedKeyManagement.set(key);

        config.hiddenSSID = true;
        config.allowedAuthAlgorithms
                .set(AuthAlgorithm.OPEN);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        config.allowedPairwiseCiphers
                .set(WifiConfiguration.PairwiseCipher.TKIP);
        //config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        config.allowedPairwiseCiphers
                .set(WifiConfiguration.PairwiseCipher.CCMP);
        config.status = WifiConfiguration.Status.ENABLED;
        return config;
    }

    public static int getPskType(ScanResult result) {
        Log.d(TAG, "getPskType, result.capabilities: " + result.capabilities);
        boolean wpa = result.capabilities.contains("WPA-PSK");
        boolean wpa2 = result.capabilities.contains("WPA2-PSK");
        if (wpa2 && wpa) {
            return PSK_WPA_WPA2;
        } else if (wpa2) {
            return PSK_WPA2;
        } else if (wpa) {
            return PSK_WPA;
        } else {
            Log.w(TAG, "Received abnormal flag string: " + result.capabilities);
            return PSK_UNKNOWN;
        }
    }

    public static int getSecurity(ScanResult result) {
        Log.d(TAG, "getSecurity, result.capabilities: " + result.capabilities);
        if (result.capabilities.contains("WEP")) {
            return SECURITY_WEP;
        } else if (result.capabilities.contains("PSK2")) {
            return SECURITY_PSK2;
        } else if (result.capabilities.contains("PSK")) {
            return SECURITY_PSK;
        } else if (result.capabilities.contains("EAP")) {
            return SECURITY_EAP;
        }
        return SECURITY_NONE;
    }
}
