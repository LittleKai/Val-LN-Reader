package com.valvrare.littlekai.valvraretranslation.utils;

/**
 * Created by Kai on 12/28/2016.
 */

import java.util.concurrent.atomic.AtomicInteger;

public class NotificationUtils {

    private final static AtomicInteger c = new AtomicInteger(0);

    public static int getID() {
        return c.incrementAndGet();
    }
}
