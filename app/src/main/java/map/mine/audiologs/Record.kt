package map.mine.audiologs

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Record(
    val name: String,
    val path: String
) : Parcelable