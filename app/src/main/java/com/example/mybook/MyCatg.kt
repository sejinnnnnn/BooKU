package com.example.mybook

import android.os.Parcel
import android.os.Parcelable

data class MyCatg(val code: String, val name: String) : Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun toString(): String {
        return name
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(code)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MyCatg> {
        override fun createFromParcel(parcel: Parcel): MyCatg {
            return MyCatg(parcel)
        }

        override fun newArray(size: Int): Array<MyCatg?> {
            return arrayOfNulls(size)
        }
    }
}