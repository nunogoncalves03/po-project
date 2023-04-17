package prr.clients;

import java.io.Serializable;

public interface NotificationDeliveryMethod extends Serializable {
    public void deliver(Notification notification);
}
