package com.example.ewalletexample.service.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Build;

import com.example.ewalletexample.Symbol.Service;
import com.example.ewalletexample.data.UserNotifyEntity;
import com.google.gson.Gson;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class NotificationCreator {
    private static Gson gson = new Gson();

    @RequiresApi(api = Build.VERSION_CODES.M)
    private static void createNotificationChannel(Context context, Service service) {
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManager.getNotificationChannel(service.GetName()) == null) {
            CharSequence name = service.GetName();
            String description = service.GetDescription();
            NotificationChannel channel = new NotificationChannel(service.GetName(), name, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void ShowNotification(Context context, UserNotifyEntity notifyEntity){
        Service service = Service.Find(notifyEntity.getServiceType());
        createNotificationChannel(context, service);

        String content = GetContentByService(service, notifyEntity);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, service.GetName())
                .setContentTitle(notifyEntity.getTitle())
                .setContentText(content)
                .setWhen(notifyEntity.getTimemilliseconds())
                .setSmallIcon(service.GetImageId())
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), service.GetImageId()))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(service.GetCode(), builder.build());
    }

    private static String GetContentByService(Service service, UserNotifyEntity notifyEntity){
        if (service == Service.VERIFY_USER){
            SecurityNotifyTemplate template = gson.fromJson(notifyEntity.getContent(), SecurityNotifyTemplate.class);
            return template.getComment();
        } else if (service == Service.LINK_CARD || service == Service.UN_LINK_CARD){
            BankServiceTemplate template = gson.fromJson(notifyEntity.getContent(), BankServiceTemplate.class);
            return notifyEntity.getTitle() + " voi the " + template.getFirst6CardNo() + "******" + template.getLast4CardNo();
        } else if (service == Service.EXCHANGE_SERVICE_TYPE){
            return notifyEntity.getTitle();
        }
        else {
            return "";
        }
    }
}
