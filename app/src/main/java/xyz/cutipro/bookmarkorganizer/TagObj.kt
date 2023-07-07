package xyz.cutipro.bookmarkorganizer

import java.io.Serializable

@kotlinx.serialization.Serializable
data class TagObj(var _id:Int, var name: String, var isSelected: Boolean = false, var isChecked: Boolean = false) {


}
