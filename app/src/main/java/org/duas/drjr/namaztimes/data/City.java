package org.duas.drjr.namaztimes.data;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cities")
public final class City {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "id")
    private final int cityId;

    @NonNull
    private final String city;

    @NonNull
    private final double latitude;

    @NonNull
    private final double longitude;

    @NonNull
    private final String province;

    public City(@NonNull int cityId, @NonNull String city, @NonNull double latitude,
                @NonNull double longitude, @NonNull String province) {
        this.cityId = cityId;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        this.province = province;
    }

    @NonNull
    public int getCityId() {
        return  cityId;
    }

    @NonNull
    public String getCity() {
        return  city;
    }

    @NonNull
    public double getLatitude() {
        return  latitude;
    }

    @NonNull
    public  String getProvince() {
        return province;
    }

    @NonNull
    @Override
    public String toString() {
        return city + ", " + province;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof City
                && this.cityId == ((City) obj).cityId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cityId);
    }

    @Override
    protected Object clone() {
        return new City(cityId, city, latitude, longitude, province);
    }
}
