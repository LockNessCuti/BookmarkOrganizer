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

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import xyz.cutipro.bookmarkorganizer.GlobalVars.Companion.needDataWrite
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.FileWriter

data class AppData(var folderObjArrayData: MutableList<FolderObj>)



class BmoViewModel: ViewModel() {

    private var _appData = MutableStateFlow(AppData(mutableStateListOf<FolderObj>()))
    var appData: StateFlow<AppData> = _appData.asStateFlow()
    private var isHidden = mutableStateOf(false)
    private var fldrToView = -1
    private var parsingTitle = false
    private var isEdit = false
    private var bookmarkToEdit = -1
    private var isAddingTag = false


    fun initAppData(context: Context) {

        val file = File(context.filesDir, "bmo_data.json")

        _appData.value.folderObjArrayData.add(FolderObj(_appData.value.folderObjArrayData.size + 1, name = "All", isPrivate = false, mutableListOf(), mutableListOf()))
        _appData.value.folderObjArrayData.add(FolderObj(_appData.value.folderObjArrayData.size + 1, name = "All", isPrivate = true, mutableListOf(), mutableListOf()))

        if (file.exists()) {
        val bufferedReader = BufferedReader(FileReader(file))

        bufferedReader.forEachLine {
            _appData.value.folderObjArrayData.add(Json.decodeFromString(it))
        }
    }


    }
    fun getIsHidden(): Boolean {
        return isHidden.value
    }

    fun isHiddenTrue() {

        isHidden.value = true

    }

    fun isHiddenFalse() {

        isHidden.value = false

    }

    fun getFldrToView(): Int {

        return fldrToView

    }

    fun setFldrToView(int: Int) {

        if (int == 1) {
            fldrToView = 0
        } else {
            fldrToView = int - 1
        }


    }

    fun getParsingTitle(): Boolean {

        return parsingTitle

    }

    fun setParsingTitle(boolean: Boolean) {

        parsingTitle = boolean

    }

    fun getIsEdit(): Boolean {

        return isEdit

    }

    fun setIsEdit(boolean: Boolean) {

        isEdit = boolean

    }

    fun getIsAddingTag() : Boolean {

        return isAddingTag

    }

    fun setIsAddingTag(boolean: Boolean) {

        isAddingTag = boolean

    }

    fun getBookmarkToEdit() : Int {

        return bookmarkToEdit

    }

    fun setBookmarkToEdit(int: Int) {

        if (int ==1) {
            bookmarkToEdit = 0
        } else {
            bookmarkToEdit = int - 1
        }


    }

    fun saveData(context: Context) {


        val file = File(context.filesDir, "bmo_data.json")
//        if (prevWrittenContentChanged) {
        if (file.exists()) {
                file.delete()
            }
            file.createNewFile()
//        }


        val fileWriter = FileWriter(file, true)
//        println(_appData.value.folderObjArrayData.get(0).name)
//        println(Json.encodeToString(_appData.value.folderObjArrayData))

            for (obj in _appData.value.folderObjArrayData) {

                if (obj._id != 1 && obj._id != 2) {
                    fileWriter.write(Json.encodeToString(obj))
                    fileWriter.write("\n")
                }




            }

        fileWriter.close()

    }

    fun importData(context: Context) {
        val inputFile = File(context.getExternalFilesDir(null), "BMO-backup.json")
        val file = File(context.filesDir, "bmo_data.json")
        inputFile.copyTo(file, true)
    }

    fun exportData(context: Context) {

//        val i: Intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        val path = "BMO-backup.json"
//        i.addCategory(Intent.CATEGORY_OPENABLE)
//        i.setType("text/*")
//        i.putExtra(Intent.EXTRA_TITLE, path)
//        context.startActivity(i)
        val fileInput = File(context.filesDir, "bmo_data.json")
        val file = File(context.getExternalFilesDir(null), path)
        fileInput.copyTo(file, true)

    }

    fun filterObjList(list: MutableList<FolderObj>): MutableList<FolderObj> {

        val filteredList = mutableStateListOf<FolderObj>()
        for (obj in list) {
            if (obj.isPrivate == isHidden.value) {
                filteredList.add(obj)
            }
        }

        return filteredList
    }

    fun addFolderObject(folderObj: FolderObj) {

        _appData.value.folderObjArrayData.add(folderObj)
        needDataWrite = true

    }

    fun delFolderObject(folderObj: FolderObj) {

        if (_appData.value.folderObjArrayData.size > 0) {
            _appData.value.folderObjArrayData.remove(folderObj)
            decFldrObjArray(folderObj._id)
            fldrToView -= 1
        } else if (_appData.value.folderObjArrayData.size == 0) {
            _appData.value.folderObjArrayData.remove(folderObj)
        }

//        prevWrittenContentChanged = true
        needDataWrite = true

    }

    fun replaceFolderObj(folderObj: FolderObj) {

        _appData.value.folderObjArrayData.set(folderObj._id, folderObj)
//        prevWrittenContentChanged = true
        needDataWrite = true


    }

    fun decFldrObjArray(int: Int) {
        for (fldrObj in _appData.value.folderObjArrayData) {

            if (fldrObj._id > int) {
                fldrObj._id = fldrObj._id - 1
            }
        }
    }

    fun duplicateFldrCheck(name: String) : Boolean {
        for (fldr in _appData.value.folderObjArrayData) {
            if (fldr.name.equals(name, true)) {
                return true
            }
        }
        return false
    }

}