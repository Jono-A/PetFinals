package com.shorbgy.petsshelter.pojo

import android.os.Parcel
import android.os.Parcelable

data class Pet(
    val id: String?,
    val name: String?,
    val dateOfBirth: String?,
    val gender: String?,
    val age: String?,
    val breed: String?,
    val about: String?,
    val imageUrl: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(dateOfBirth)
        parcel.writeString(gender)
        parcel.writeString(age)
        parcel.writeString(breed)
        parcel.writeString(about)
        parcel.writeString(imageUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Pet> {
        override fun createFromParcel(parcel: Parcel): Pet {
            return Pet(parcel)
        }

        override fun newArray(size: Int): Array<Pet?> {
            return arrayOfNulls(size)
        }
    }
}
