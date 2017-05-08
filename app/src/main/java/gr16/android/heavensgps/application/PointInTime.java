package gr16.android.heavensgps.application;

import java.util.Date;

/**
 * Created by lucas on 08-05-2017.
 */

public class PointInTime {
    private double latitude, longitude;
    private Date date;

    public PointInTime(double latitude, double longitude, Date date)
    {
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
