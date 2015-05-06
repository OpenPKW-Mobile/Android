package pl.openpkw.openpkwmobile.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by kuczmysz on 02.05.2015.
 */
public class Commission implements Parcelable {

    private int id;
    private String pkwId;
    private String name;
    private String address;
    private int protokolCount;

    public Commission(){}

    public Commission(Parcel parcel){
        this.setId(parcel.readInt());
        this.setPkwId(parcel.readString());
        this.setName(parcel.readString());
        this.setAddress(parcel.readString());
        this.setProtokolCount(parcel.readInt());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(getId());
        dest.writeString(getPkwId());
        dest.writeString(getName());
        dest.writeString(getAddress());
        dest.writeInt(getProtokolCount());
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Commission createFromParcel(Parcel in) {
            return new Commission(in);
        }

        public Commission[] newArray(int size) {
            return new Commission[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPkwId() {
        return pkwId;
    }

    public void setPkwId(String pkwId) {
        this.pkwId = pkwId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getProtokolCount() {
        return protokolCount;
    }

    public void setProtokolCount(int protokolCount) {
        this.protokolCount = protokolCount;
    }

}