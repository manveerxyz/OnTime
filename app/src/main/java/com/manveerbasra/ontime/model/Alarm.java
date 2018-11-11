package com.manveerbasra.ontime.model;

import java.util.Date;

public interface Alarm {
    int getId();
    Date getTime();
    boolean isActive();
    String[] getActiveDays();
}
