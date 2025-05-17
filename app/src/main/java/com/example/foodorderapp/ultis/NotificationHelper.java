package com.example.foodorderapp.ultis;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.foodorderapp.R;
import com.example.foodorderapp.ui.OrderActivity;
import com.example.foodorderapp.ui.ShopManager;

public class NotificationHelper {

    public static final String CHANNEL_ID = "cancel_order_channel";
    private static final String CHANNEL_NAME = "Cancel Order Notifications";
    private static final String CHANNEL_DESC = "Thông báo hủy đơn hàng";

    private Context context;
    private NotificationManager notificationManager;

    public NotificationHelper(Context context) {
        this.context = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription(CHANNEL_DESC);
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    public void showCancelOrderNotification(String storeId, String orderId, String reason) {
        String title = "Yêu cầu hủy đơn hàng";
        String message = "Đơn hàng #" + orderId + " vừa được yêu cầu hủy.\nLý do: " + reason;

        // Tạo Intent mở ShopManager khi nhấn vào thông báo
        Intent intent = new Intent(context, ShopManager.class);
        intent.putExtra("shopid", storeId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Nếu cần truyền orderId sang ShopManager để xử lý sau này
//        intent.putExtra("orderId", orderId);
//        intent.putExtra("reason", reason);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Tạo BigTextStyle để hiển thị nội dung mở rộng
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle()
                .setBigContentTitle(title)
                .bigText(message)
                .setSummaryText("Đơn hàng #" + orderId);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification)
                .setContentTitle(title)
                .setContentText("Đơn hàng #" + orderId + " đã yêu cầu hủy")
                .setStyle(bigTextStyle)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        int notificationId = (int) System.currentTimeMillis();
        notificationManager.notify(notificationId, builder.build());
    }
    public void showAcceptNotification(String title, String orderId, String reason) {
        String message = "Đơn hàng #" + orderId + " của ban " + reason;

        // Tạo Intent mở ShopManager khi nhấn vào thông báo
        Intent intent = new Intent(context, OrderActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Tạo BigTextStyle để hiển thị nội dung mở rộng
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle()
                .setBigContentTitle(title)
                .bigText(message)
                .setSummaryText("Đơn hàng #" + orderId);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification)
                .setContentTitle(title)
                .setContentText("Đơn hàng #" + orderId + " đã yêu cầu hủy")
                .setStyle(bigTextStyle)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        int notificationId = (int) System.currentTimeMillis();
        notificationManager.notify(notificationId, builder.build());
    }

}
