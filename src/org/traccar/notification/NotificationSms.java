/*
 * Copyright 2017 - 2018 Anton Tananaev (anton@traccar.org)
 * Copyright 2017 - 2018 Andrey Kunitsyn (andrey@traccar.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.traccar.notification;

import org.traccar.Context;
import org.traccar.model.Event;
import org.traccar.model.Position;
import org.traccar.model.User;
import org.traccar.sms.SmsManager;

public final class NotificationSms extends Notificator {

    private final SmsManager smsManager;

    public NotificationSms() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        final String smsClass = Context.getConfig().getString("notificator.sms.manager.class", "");
        if (smsClass.length() > 0) {
            smsManager = (SmsManager) Class.forName(smsClass).newInstance();
        } else {
            smsManager = Context.getSmsManager();
        }
    }

    @Override
    public void sendAsync(long userId, Event event, Position position) {
        final User user = Context.getPermissionsManager().getUser(userId);
        if (user.getPhone() != null) {
            Context.getStatisticsManager().registerSms();
            smsManager.sendMessageAsync(user.getPhone(),
                    NotificationFormatter.formatShortMessage(userId, event, position), false);
        }
    }

    @Override
    public void sendSync(long userId, Event event, Position position) throws MessageException, InterruptedException {
        final User user = Context.getPermissionsManager().getUser(userId);
        if (user.getPhone() != null) {
            Context.getStatisticsManager().registerSms();
            smsManager.sendMessageSync(user.getPhone(),
                    NotificationFormatter.formatShortMessage(userId, event, position), false);
        }
    }
}
