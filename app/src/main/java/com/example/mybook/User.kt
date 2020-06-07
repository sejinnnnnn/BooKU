package com.example.mybook

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

data class User(var no:Int, var eEmail:String, var pass:String, var name:String, var uri:Uri, var catg:String):Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(Uri::class.java.classLoader),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(no)
        parcel.writeString(eEmail)
        parcel.writeString(pass)
        parcel.writeString(name)
        parcel.writeParcelable(uri, flags)
        parcel.writeString(catg)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}


