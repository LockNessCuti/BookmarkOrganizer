package xyz.cutipro.bookmarkorganizer

import java.io.Serializable

@kotlinx.serialization.Serializable
data class FolderObj(var _id: Int, var name: String, var isPrivate: Boolean, var tags: MutableList<TagObj>,
                     var bookmarks: MutableList<BookmarkObj>) {


}
