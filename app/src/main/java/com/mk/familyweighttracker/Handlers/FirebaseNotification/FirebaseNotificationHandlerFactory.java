package com.mk.familyweighttracker.Handlers.FirebaseNotification;

import java.util.Arrays;
import java.util.List;

/**
 * Created by mvalhekar on 19-01-2017.
 */

public class FirebaseNotificationHandlerFactory {
    private static final List<IFirebaseNotificationHandler> handlers = Arrays.asList(
            new UserBirthdayNotificationHandler(),
            new InfantBirthdayNotificationHandler(),
            new DeliveryDueDateNotificationHandler()
    );

    public static IFirebaseNotificationHandler getHandlerFor(String notificationType) {
        for (IFirebaseNotificationHandler handler : handlers) {
            if(handler.canHandle(notificationType))
                return handler;
        }
        return new DefaultFcmNotificationHandler();
    }
}
