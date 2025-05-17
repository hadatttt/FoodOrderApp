package com.example.foodorderapp.websocket;

import android.content.Context;
import android.os.Looper;
import android.util.Log;

import com.example.foodorderapp.ui.ShopManager;
import com.example.foodorderapp.ultis.NotificationHelper;
import com.example.foodorderapp.ultis.ultis;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Looper;


public class WebSocketManager {
    private static WebSocketManager instance;
    private final WebSocketClient webSocketClient;
    private final NotificationHelper notificationHelper;

    private static final String WS_URL = ultis.WEBSOCKET_URL;

    public interface OnMessageListener {
        void onMessage(String message);
    }
    private OnMessageListener onMessageListener;
    private OnCancelRequestListener cancelRequestListener;
    private OnOrderUpdateListener orderUpdateListener;

    public interface OnCancelRequestListener {
        void onCancelRequest();
    }
    public interface OnOrderUpdateListener {
        void onOrderUpdate();
    }

    public void setOnCancelRequestListener(OnCancelRequestListener listener) {
        this.cancelRequestListener = listener;
    }
    public void setOnOrderUpdateListener(OnOrderUpdateListener listener) {
        this.orderUpdateListener = listener;
    }
    private WebSocketManager(Context context) {
        notificationHelper = new NotificationHelper(context.getApplicationContext());
        webSocketClient = new WebSocketClient(WS_URL, new WebSocketClient.WebSocketCallback() {
            @Override
            public void onConnected() {
                Log.d("WebSocketManager", "WebSocket connected");
            }

            @Override
            public void onMessage(String message) {
                Log.d("WebSocketManager", "Received message: " + message);
                if (onMessageListener != null) {
                    onMessageListener.onMessage(message);
                }
                try {
                    JSONObject json = new JSONObject(message);
                    if ("cancel_request".equals(json.optString("type"))) {
                        String storeId = json.optString("shopId");
                        String orderId = json.optString("orderId");
                        String reason  = json.optString("reason");
                        notificationHelper.showCancelOrderNotification(storeId, orderId, reason);

                        if (cancelRequestListener != null) {
                            cancelRequestListener.onCancelRequest();
                        }
                    } else if ("cancelled".equals(json.optString("type")) || "delivery".equals(json.optString("type")) || "complete".equals(json.optString("type"))) {
                        String orderId = json.optString("orderId");
                        String reason  = json.optString("reason");
                        String title;
                        if ("cancelled".equals(json.optString("type"))) {
                            title = "Hủy đơn hàng";
                        } else if ("delivery".equals(json.optString("type"))) {
                            title = "Đơn hàng đang trên đường giao";
                        } else if ("complete".equals(json.optString("type"))) {
                            title = "Hoàn thành đơn hàng";
                        } else {
                            title = "";
                        }

                        if (orderUpdateListener != null) {
                            Log.d("WebSocketManager", "Calling onOrderUpdate listener with delay");
                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                notificationHelper.showAcceptNotification(title, orderId, reason);
                                orderUpdateListener.onOrderUpdate();
                            }, 1500); // delay 1.5 giây
                        }
                    } else if ("reload_orders".equals(json.optString("type"))) {
                        Log.d("WebSocketManager", "Received reload_orders WebSocket message");
                        if (cancelRequestListener != null) {
                            Log.d("WebSocketManager", "Triggering cancelRequestListener from reload_orders");
                            cancelRequestListener.onCancelRequest();
                        } else {
                            Log.d("WebSocketManager", "cancelRequestListener is null when handling reload_orders");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
                Log.e("WebSocketManager", "WebSocket error: " + error);
            }

            @Override
            public void onClosed(String reason) {
                Log.d("WebSocketManager", "WebSocket closed: " + reason);
            }
        });
    }

    /** Gọi một lần duy nhất khi app khởi động */
    public static synchronized void init(Context context) {
        if (instance == null) {
            instance = new WebSocketManager(context);
        }
    }


    public static synchronized WebSocketManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Call init(context) before getInstance().");
        }
        return instance;
    }

    public void registerStore(int storeId) {
        webSocketClient.registerStore(storeId);
    }
    public void registerUser(String userId) {
        webSocketClient.registerUser(userId);
    }

    public void sendCancelOrder(int storeId, String orderId, String reason) {
        webSocketClient.sendCancelOrderMessage(storeId, orderId, reason);
    }
    public void sendAccept(String type, String userId, String orderId, String reason) {
        webSocketClient.sendAccept(type, userId, orderId, reason);
    }
    public void sendMessage(String message, int storeId) {
        webSocketClient.sendMessage(message, storeId);
    }

    public void setOnMessageListener(OnMessageListener listener) {
        this.onMessageListener = listener;
    }

    public void removeOnMessageListener() {
        this.onMessageListener = null;
    }

    public void close() {
        webSocketClient.close();
        instance = null;
    }
}
