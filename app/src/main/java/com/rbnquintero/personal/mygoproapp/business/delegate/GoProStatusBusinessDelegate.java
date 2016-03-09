package com.rbnquintero.personal.mygoproapp.business.delegate;

import com.rbnquintero.personal.mygoproapp.objects.GoProStatus;

import java.util.List;

/**
 * Created by rbnquintero on 2/29/16.
 */
public interface GoProStatusBusinessDelegate {
    void updateStatus(GoProStatus response);
    void updateMediaList(List<String> media);
}
