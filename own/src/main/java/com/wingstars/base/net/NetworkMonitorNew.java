package com.wingstars.base.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.util.Log;

public class NetworkMonitorNew {
    private static final String TAG = "NetworkMonitor";
    private static NetworkMonitorNew instance;

    private final ConnectivityManager connectivityManager;
    private NetworkCallback networkCallback;
    private NetworkState currentState;
    private NetworkStateListener listener;

    private NetworkMonitorNew(Context context) {
        connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        currentState = getCurrentNetworkState();
    }

    public static synchronized NetworkMonitorNew getInstance(Context context) {
        if (instance == null) {
            instance = new NetworkMonitorNew(context.getApplicationContext());
        }
        return instance;
    }

    // 注册网络监听
    public void registerListener(NetworkStateListener listener) {
        this.listener = listener;
        if (networkCallback == null) {
            networkCallback = new NetworkCallback();
            NetworkRequest request = new NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build();

            try {
                connectivityManager.registerNetworkCallback(request, networkCallback);
            } catch (SecurityException e) {
                Log.e(TAG, "Network permission not granted", e);
            }
        }
    }

    // 取消注册
    public void unregisterListener() {
        if (networkCallback != null) {
            try {
                connectivityManager.unregisterNetworkCallback(networkCallback);
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "Network callback already unregistered", e);
            }
            networkCallback = null;
        }
        listener = null;
    }

    // 获取当前网络状态
    public NetworkState getCurrentNetworkState() {
        if (connectivityManager == null) {
            return new NetworkState(false, NetworkState.TYPE_UNKNOWN);
        }

        Network activeNetwork = connectivityManager.getActiveNetwork();
        if (activeNetwork == null) {
            return new NetworkState(false, NetworkState.TYPE_UNKNOWN);
        }

        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
        return parseNetworkState(capabilities);
    }

    // 解析网络能力为状态对象
    private NetworkState parseNetworkState(NetworkCapabilities capabilities) {
        if (capabilities == null) {
            return new NetworkState(false, NetworkState.TYPE_UNKNOWN);
        }

        // 检查网络是否真正可用
        boolean isConnected = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);

        int networkType = NetworkState.TYPE_UNKNOWN;

        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            networkType = NetworkState.TYPE_WIFI;
        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
            networkType = NetworkState.TYPE_CELLULAR;
        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
            networkType = NetworkState.TYPE_VPN;
        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
            networkType = NetworkState.TYPE_ETHERNET;
        }

        return new NetworkState(isConnected, networkType);
    }

    // 网络状态数据类
    public static class NetworkState {
        public static final int TYPE_UNKNOWN = 0;
        public static final int TYPE_WIFI = 1;
        public static final int TYPE_CELLULAR = 2;
        public static final int TYPE_VPN = 3;
        public static final int TYPE_ETHERNET = 4;

        public final boolean isConnected;
        public final int networkType;

        public NetworkState(boolean isConnected, int networkType) {
            this.isConnected = isConnected;
            this.networkType = networkType;
        }

        @Override
        public String toString() {
            String status = isConnected ? "Connected" : "Disconnected";
            String type = "Unknown";
            switch (networkType) {
                case TYPE_WIFI: type = "WiFi"; break;
                case TYPE_CELLULAR: type = "Cellular"; break;
                case TYPE_VPN: type = "VPN"; break;
                case TYPE_ETHERNET: type = "Ethernet"; break;
            }
            return status + " (" + type + ")";
        }
    }

    // 网络状态监听接口
    public interface NetworkStateListener {
        void onNetworkDisconnected();
        void onNetworkChanged(NetworkState state);
    }

    // 网络回调实现
    private class NetworkCallback extends ConnectivityManager.NetworkCallback {
        @Override
        public void onAvailable(Network network) {
            updateNetworkState();
        }

        @Override
        public void onLost(Network network) {
            // 网络断开事件
            currentState = new NetworkState(false, NetworkState.TYPE_UNKNOWN);
            if (listener != null) {
                listener.onNetworkDisconnected();
            }
            Log.d(TAG, "Network disconnected");
        }

        @Override
        public void onCapabilitiesChanged(Network network, NetworkCapabilities capabilities) {
            updateNetworkState();
        }

        private void updateNetworkState() {
            NetworkState newState = getCurrentNetworkState();

            // 仅当状态变化时通知
            if (currentState == null ||
                    currentState.isConnected != newState.isConnected ||
                    currentState.networkType != newState.networkType) {

                currentState = newState;
                if (listener != null) {
                    listener.onNetworkChanged(newState);
                }
                Log.d(TAG, "Network changed: " + newState);
            }
        }
    }
}
