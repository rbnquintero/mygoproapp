package com.rbnquintero.personal.mygoproapp.service.delegate;

import com.rbnquintero.personal.mygoproapp.objects.GoProStatus;

/**
 * Created by rbnquintero on 2/26/16.
 */
public interface GoProWakeUpServiceDelegate {
    void wakeUpSuccess();
    void wakeUpError();
}
