package me.alex.pet.apps.zhishi.presentation.rules

import android.os.Parcel
import android.os.Parcelable

data class RulesToDisplay(val ids: List<Long>, val selectionIndex: Int) : Parcelable {

    init {
        require(ids.isNotEmpty())
        require(selectionIndex in ids.indices)
    }

    constructor(ruleId: Long) : this(listOf(ruleId), 0)

    private constructor(parcel: Parcel) : this(
            LongArray(parcel.readInt()).toList(),
            parcel.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        ids.toLongArray().let { arr ->
            dest.writeInt(arr.size)
            dest.writeLongArray(arr)
        }
        dest.writeInt(selectionIndex)
    }

    companion object CREATOR : Parcelable.Creator<RulesToDisplay> {
        override fun createFromParcel(parcel: Parcel): RulesToDisplay {
            return RulesToDisplay(parcel)
        }

        override fun newArray(size: Int): Array<RulesToDisplay?> {
            return arrayOfNulls(size)
        }
    }
}