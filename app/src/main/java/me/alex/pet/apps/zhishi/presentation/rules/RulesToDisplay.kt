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
            parcel.readListOfLong(),
            parcel.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeListOfLong(ids)
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

private fun Parcel.readListOfLong(): List<Long> {
    val size = readInt()
    val arr = LongArray(size)
    readLongArray(arr)
    return arr.toList()
}

private fun Parcel.writeListOfLong(list: List<Long>) {
    writeInt(list.size)
    writeLongArray(list.toLongArray())
}