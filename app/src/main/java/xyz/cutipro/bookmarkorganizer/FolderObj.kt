/*
Copyright 2023 LockNessCuti

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package xyz.cutipro.bookmarkorganizer

import androidx.compose.ui.res.booleanResource

@kotlinx.serialization.Serializable
data class FolderObj(var _id: Int, var name: String, var isPrivate: Boolean, var tags: MutableList<TagObj>,
                     var bookmarks: MutableList<BookmarkObj>) {

    fun filterBookmarksbyTag(tagObj: TagObj?): MutableList<BookmarkObj> {
        val filteredList = mutableListOf<BookmarkObj>()

        if (tagObj != null) {
            for (bookmark in bookmarks) {
                for (tag in bookmark.tags) {
                    if (tag._id == tagObj._id) {
                        filteredList.add(bookmark)
                    }
                }
            }

            return filteredList
        } else {

            return bookmarks
        }
    }

    fun AddTag(tagObj: TagObj) {

        tags.add(tagObj)

        GlobalVars.needDataWrite = true
    }

    fun DeleteTag(tagObj: TagObj) {

        tags.remove(tagObj)
        for (tag in tags) {
            if (tag._id > tagObj._id) {
                tag._id -=1
            }
        }

//        GlobalVars.prevWrittenContentChanged = true
        GlobalVars.needDataWrite = true

    }

    fun checkForDuplicateTags(name: String) : Boolean {
        for (tag in tags) {
            if (tag.name.equals(name, true))
                return true
        }
        return false
    }

    fun AddBookmark(bookmarkObj: BookmarkObj) {

        bookmarks.add(bookmarkObj)
        GlobalVars.needDataWrite = true

    }

    fun EditBookmark(bookmarkObj: BookmarkObj) {

        bookmarks[bookmarkObj._id].title = bookmarkObj.title
        bookmarks[bookmarkObj._id].url = bookmarkObj.url
        bookmarks[bookmarkObj._id].note = bookmarkObj.note
        bookmarks[bookmarkObj._id].tags = bookmarkObj.tags




//        if (bookmarks.size > 0) {
//            bookmarks.remove(bookmarkObj)
//            bookmarks.add(bookmarkObj._id, bookmarkObj)
//            bookmarks.
//        } else {
//            bookmarks.clear()
//            bookmarks.add(bookmarkObj._id, bookmarkObj)
//        }


//        GlobalVars.prevWrittenContentChanged = true
        GlobalVars.needDataWrite = true


    }

    fun DeleteBookmark(bookmarkObj: BookmarkObj) {

        bookmarks.remove(bookmarkObj)
//        GlobalVars.prevWrittenContentChanged = true
        GlobalVars.needDataWrite = true

    }

    fun checkForDuplicateUrl(url: String) : Boolean{
        for (bookmark in bookmarks) {
            if (bookmark.url.equals(url, ignoreCase = true)) {
                return true
            }
        }

        return false
    }

}
