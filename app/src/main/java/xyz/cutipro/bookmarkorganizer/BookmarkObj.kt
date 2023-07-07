package xyz.cutipro.bookmarkorganizer

import java.io.Serializable

@kotlinx.serialization.Serializable
data class BookmarkObj(var _id:Int, var title: String?, var url: String, var note: String?,
                       var tags: MutableList<TagObj>) {


}
