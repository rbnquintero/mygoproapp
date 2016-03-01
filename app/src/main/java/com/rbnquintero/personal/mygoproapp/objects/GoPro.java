package com.rbnquintero.personal.mygoproapp.objects;

import android.net.wifi.ScanResult;
import android.os.Parcel;
import android.os.Parcelable;

import com.rbnquintero.personal.mygoproapp.service.GoProDiscoveryService;

/**
 * Created by rbnquintero on 2/23/16.
 */
public class GoPro implements Parcelable {
    public int icon;
    public int netId;
    public String title;
    public String bssid;
    public String status;
    public ScanResult scanResult;

    public GoPro() { super(); }
    public GoPro(String title, String bssid) {
        super();
        this.title = title;
        this.bssid = bssid;
        this.status = GoProDiscoveryService.wifi_not_saved;
    }

    /* everything below here is for implementing Parcelable */

    // 99.9% of the time you can just ignore this
    @Override
    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(icon);
        out.writeInt(netId);
        out.writeString(title);
        out.writeString(bssid);
        out.writeString(status);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<GoPro> CREATOR = new Parcelable.Creator<GoPro>() {
        public GoPro createFromParcel(Parcel in) {
            return new GoPro(in);
        }

        public GoPro[] newArray(int size) {
            return new GoPro[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private GoPro(Parcel in) {
        icon = in.readInt();
        netId = in.readInt();
        title = in.readString();
        bssid = in.readString();
        status = in.readString();
    }
}
