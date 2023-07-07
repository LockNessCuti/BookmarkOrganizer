package xyz.cutipro.bookmarkorganizer

import android.content.Context
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.FileWriter

data class AppData(var folderObjArrayData: MutableList<FolderObj>)



class BmoViewModel: ViewModel() {

    private var _appData = MutableStateFlow(AppData(mutableStateListOf<FolderObj>()))
    var appData: StateFlow<AppData> = _appData.asStateFlow()
    private var isHidden = mutableStateOf(false)
    private var isAddScreen = mutableStateOf(false)


    fun initAppData(context: Context) {

        val file = File(context.filesDir, "bmo_data.json")

        if (file.exists()) {
        val bufferedReader = BufferedReader(FileReader(file))

        bufferedReader.forEachLine {
            _appData.value.folderObjArrayData.add(Json.decodeFromString(it))
        }
    }

//        println(_appData.value.folderObjArrayData.get(0).name)

//        _appData.value.folderObjArrayData.add(FolderObj(
//            _appData.value.folderObjArrayData.size + 1, "Folder 1",
//            false, mutableListOf<TagObj>(), mutableListOf<BookmarkObj>())
//        )


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

    fun getIsAddScreen(): Boolean {
        return isAddScreen.value
    }

    fun isAddScreenTrue() {

        isAddScreen.value = true

    }

    fun isAddScreenfalse() {

        isAddScreen.value = false

    }

    fun saveData(context: Context) {


        val file = File(context.filesDir, "bmo_data.json")
        file.createNewFile()

        val fileWriter = FileWriter(file, false)
//        println(_appData.value.folderObjArrayData.get(0).name)
//        println(Json.encodeToString(_appData.value.folderObjArrayData))

            for (obj in _appData.value.folderObjArrayData) {
                fileWriter.write(Json.encodeToString(obj))
//                if (obj != _appData.value.folderObjArrayData.last()) {

                    fileWriter.write("\n")

//                }
            }

        fileWriter.close()

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

    }

    fun delFolderObject(folderObj: FolderObj) {

        _appData.value.folderObjArrayData.remove(folderObj)

    }

    fun replaceFolderObj(folderObj: FolderObj) {

        _appData.value.folderObjArrayData.set(folderObj._id, folderObj)

    }

}