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

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import xyz.cutipro.bookmarkorganizer.GlobalVars.Companion.needDataWrite
import xyz.cutipro.bookmarkorganizer.GlobalVars.Companion.prevWrittenContentChanged
import xyz.cutipro.bookmarkorganizer.GlobalVars.Companion.sentUrl
import xyz.cutipro.bookmarkorganizer.ui.theme.BookmarkOrganizerTheme

class MainActivity : ComponentActivity() {

    private val bmoViewModel: BmoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bmoViewModel.initAppData(this)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                bmoViewModel.appData.collect() {

                }

            }
        }

        val intent = intent
        val action = intent.action
        val type = intent.type

        if (Intent.ACTION_SEND == action && type != null) {

            if ("text/plain" == type) {
                sentUrl = intent.getStringExtra(Intent.EXTRA_TEXT)

            }
        }
        setContent {

            BookmarkOrganizerTheme() {

                Nav()

            }


        }
    }

    override fun onPause() {
        super.onPause()

        if (needDataWrite) {
            bmoViewModel.saveData(this)

        }
        prevWrittenContentChanged = false
        needDataWrite = false

    }

    override fun onStop() {
        super.onStop()

        if (needDataWrite) {
            bmoViewModel.saveData(this)

        }
        prevWrittenContentChanged = false
        needDataWrite = false

    }

    fun restartMain() {
        startActivity(Intent.makeRestartActivityTask(this.intent.component))
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TopBar() {

        CenterAlignedTopAppBar(
            title = { Text(text = stringResource(id = R.string.app_name)) },
            modifier = Modifier.fillMaxWidth(),
            navigationIcon = {},
            actions = {},
            windowInsets = TopAppBarDefaults.windowInsets,
            TopAppBarDefaults.centerAlignedTopAppBarColors(),
            null
        )

    }

    @Composable
    fun Nav () {
        val navCon = rememberNavController()
        NavHost(navController = navCon, startDestination = Screens.FolderListViewScreen.route) {
            composable(route = Screens.FolderListViewScreen.route) {
                FolderListViewScreen(
                    list = bmoViewModel.appData.collectAsState().value.folderObjArrayData,
                    navCon = navCon
                )
            }
            composable(route = Screens.AddFolderScreen.route) {
                AddFolderScreen(navCon = navCon)
            }
            composable(route = Screens.FolderContentScreen.route) {
                FolderContentScreen(
                    navCon = navCon,
                    fldrObj = bmoViewModel.appData.collectAsState().value.folderObjArrayData[bmoViewModel.getFldrToView()]
                )
            }
            composable(route = Screens.AddBookmarkScreen.route) {
                AddBookmarkScreen(
                    navCon = navCon,
                    fldrObj = bmoViewModel.appData.collectAsState().value.folderObjArrayData[bmoViewModel.getFldrToView()]
                )
            }
            composable(route = Screens.DelTagScreen.route) {
                DeleteTagScreen(
                    navCon = navCon,
                    fldrObj = bmoViewModel.appData.collectAsState().value.folderObjArrayData[bmoViewModel.getFldrToView()]                )
            }
            composable(route = Screens.SettingsScreen.route) {
                SettingsScreen(navCon = navCon)
            }
        }
    }

    //Begin FolderListViewScreen Composables.
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun FolderListViewScreen(list: MutableList<FolderObj>, navCon: NavController) {
//        BookmarkOrganizerTheme {
//            // A surface container using the 'background' color from the theme
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {

                Column() {
                    CenterAlignedTopAppBar(
                        title = { Text(text = stringResource(id = R.string.app_name)) },
                        modifier = Modifier.fillMaxWidth(),
                        navigationIcon = {},
                        actions = { IconButton(onClick = { navCon.navigate(Screens.SettingsScreen.route) }) {
                            Icon(imageVector = Icons.Filled.Settings, contentDescription = "")
                        }},
                        windowInsets = TopAppBarDefaults.windowInsets,
                        TopAppBarDefaults.centerAlignedTopAppBarColors(),
                        null
                    )

                    FolderListViewScreenTabRow()


                        FolderList(list = list, navCon = navCon)
                }

            }
//        }

    }

    @Composable
    fun FolderListViewScreenTabRow() {

//        var topRowState by remember {
//            mutableStateOf(0)
//
//        }

        var bottomRowState by remember {
            mutableStateOf(0)

        }

        var hiddenCount by remember {
            mutableStateOf(0)
        }

//        TabRow(selectedTabIndex = topRowState) {
//
//            Tab(
//                selected = topRowState == 0, onClick = { topRowState = 0; viewSettings = false }, enabled = true,
//                icon = {
//                    Icon(
//                        imageVector = Icons.Outlined.Home,
//                        contentDescription = "Home icon"
//                    )
//                })
//
//            Tab(
//                selected = topRowState == 1, onClick = { topRowState = 1; viewSettings = true }, enabled = true,
//                icon = {
//                    Icon(
//                        imageVector = Icons.Outlined.Settings,
//                        contentDescription = "Settings icon"
//                    )
//                })
//
//
//        }

        TabRow(selectedTabIndex = bottomRowState) {

            Tab(
                selected = bottomRowState == 0,
                onClick = { bottomRowState = 0; if (hiddenCount < 4) hiddenCount++ else bmoViewModel.isHiddenFalse() },
                enabled = true,
                text =  {Text(
                    style = MaterialTheme.typography.titleLarge,
                    text = stringResource(id = R.string.folders))}
            )


            if (hiddenCount == 4) {
                Tab(
                    selected = bottomRowState == 1, onClick = { bottomRowState = 1; bmoViewModel.isHiddenTrue() },
                    enabled = true, text = { Text(
                        style = MaterialTheme.typography.titleLarge,
                        text = stringResource(id = R.string.hidden))})


            }
        }


    }

    @Composable
    fun FolderList(list: MutableList<FolderObj>, navCon: NavController) {

        val filteredList = bmoViewModel.filterObjList(list = list)
        var hasSentUrl by remember {
            mutableStateOf(false)
        }

        if (sentUrl != null) {
            hasSentUrl = true
        }

        Column() {
            LazyVerticalGrid(
                modifier = Modifier.weight(1f, true),
                columns = GridCells.Fixed(2),
//        verticalArrangement = Arrangement.spacedBy(8.dp),
//        horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                items(filteredList.size) { obj ->

                    FolderItem(obj = filteredList[obj], navCon = navCon)

                }

            }

            FilledIconButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                onClick = {

                    bmoViewModel.setIsAddingTag(false)
                    navCon.navigate(Screens.AddFolderScreen.route)

                },
                enabled = !hasSentUrl) {

                Text(text = stringResource(id = R.string.bttn_add_folder))

            }

        }

    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun FolderItem(obj: FolderObj, navCon: NavController) {

        ElevatedCard(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight()
                .padding(8.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            onClick = {
                if (sentUrl != null) {
                    bmoViewModel.setFldrToView(obj._id)
                    navCon.navigate(Screens.AddBookmarkScreen.route)
                } else {
                    bmoViewModel.setFldrToView(obj._id)
                    navCon.navigate(Screens.FolderContentScreen.route)
                }

            }
        ) {

            Image(
                modifier = Modifier.size(156.dp, 156.dp),
                painter = painterResource(
                    id = R.drawable.baseline_folder_24
                ),
                contentDescription = "Folder icon",
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary, BlendMode.SrcIn)
            )

            Text(
                modifier = Modifier
                    .width(156.dp)
                    .padding(10.dp, 0.dp, 10.dp, 8.dp),
                text = obj.name,
                maxLines = 1,
                style = MaterialTheme.typography.titleLarge
            )

        }

    }
// End FolderListViewScreen Composables.

    //Begin AddFolderScreen Composables.
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AddFolderScreen(navCon: NavController) {


            var fldrName by remember {
                mutableStateOf("")
            }

            var isDuplicate by remember {
                mutableStateOf(false)
            }

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {

                Column() {

                    TopBar()

                }

                Column(
                    modifier = Modifier.padding(8.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {


                    OutlinedTextField(
                        value = fldrName,
                        modifier = Modifier.fillMaxWidth(),
                        onValueChange = { fldrName = it; if (!bmoViewModel.getIsAddingTag()) {isDuplicate = bmoViewModel.duplicateFldrCheck(fldrName)} else {isDuplicate = bmoViewModel.appData.value.folderObjArrayData[bmoViewModel.getFldrToView()].checkForDuplicateTags(fldrName)} },
                        label = {
                            Text(
                                text = if (!isDuplicate) {
                                    if (!bmoViewModel.getIsAddingTag()) {
                                        "Folder name"
                                    } else {
                                        "Tag name"
                                    }
                                } else {
                                    "Already Exists"
                                }
                            )
                        },
                    isError = isDuplicate
                    )

                }

                Column(
                    Modifier.padding(8.dp),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    FilledIconButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {


                            if (!isDuplicate) {
                                if (!bmoViewModel.getIsAddingTag()) {
                                    bmoViewModel.addFolderObject(
                                        FolderObj(
                                            bmoViewModel.appData.value.folderObjArrayData.size + 1,
                                            fldrName,
                                            bmoViewModel.getIsHidden(),
                                            mutableListOf(),
                                            mutableListOf()
                                        )
                                    )

                                    navCon.navigate(Screens.FolderListViewScreen.route) {
                                        popUpTo(Screens.FolderListViewScreen.route)
                                    }
                                } else {

                                    bmoViewModel.appData.value.folderObjArrayData[bmoViewModel.getFldrToView()].AddTag(
                                        TagObj(
                                            _id = bmoViewModel.appData.value.folderObjArrayData[bmoViewModel.getFldrToView()].tags.size + 1,
                                            name = fldrName
                                        )
                                    )
                                    bmoViewModel.setIsAddingTag(false)
                                    navCon.popBackStack()
                                }
                            }

                        },
                        enabled = !isDuplicate

                    ) {

                        Text(text = stringResource(id = R.string.bttn_confirm))

                    }
                }


        }


    }
    //End AddFolderScreen Composables.

    //Begin FolderContentScreen Composables.
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
    @Composable
    fun FolderContentScreen(navCon: NavController, fldrObj: FolderObj) {

            var selectedTag: Int by remember {
                mutableStateOf(-1)
            }

            var selectingFilter by remember {
                mutableStateOf(false)
            }

            var confirmFldrDel by remember {
                mutableStateOf(false)
            }

            var isFirstIndex by remember {
                mutableStateOf(false)
            }

            if (bmoViewModel.getFldrToView() == 0 || bmoViewModel.getFldrToView() == 1) {
                isFirstIndex = true
            }

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {

            }
            Column() {

                if (confirmFldrDel) {
                    Column {

                        TopBar()

                    }


                    ElevatedCard(
                        modifier = Modifier
                            .wrapContentWidth()
                            .wrapContentHeight()
                            .padding(8.dp),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(8.dp),
                        onClick = {}
                    ) {

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(modifier = Modifier.padding(8.dp), text = stringResource(id = R.string.confirm_delete_fldr))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {

                                FilledTonalButton(modifier = Modifier.padding(8.dp), onClick = {confirmFldrDel = false}) {

                                    Text(text = stringResource(id = R.string.no_cancel))

                                }

                                FilledTonalButton(modifier = Modifier.padding(8.dp), onClick = {

                                    bmoViewModel.delFolderObject(fldrObj)

                                    confirmFldrDel = false

                                    if (bmoViewModel.appData.value.folderObjArrayData.size == 0){
                                        restartMain()

                                    } else {
                                        navCon.navigate(Screens.FolderListViewScreen.route)
                                    }
                                }

                                ) {

                                    Text(text = stringResource(id = R.string.yes_delete_fldr))

                                }

                            }

                        }

                    }

                } else {

                    Column {

                        if (!selectingFilter) {
                            CenterAlignedTopAppBar(
                                title = { Text(modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .basicMarquee(), text = fldrObj.name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                                modifier = Modifier.fillMaxWidth(),
                                navigationIcon = {},
                                actions = {
                                    IconButton(onClick = {confirmFldrDel = true}, enabled = !isFirstIndex) {
                                        Icon(
                                            imageVector = Icons.Filled.Delete,
                                            contentDescription = stringResource(id = R.string.del_folder_content_desc)
                                        )
                                    }
                                },
                                windowInsets = TopAppBarDefaults.windowInsets,
                                TopAppBarDefaults.centerAlignedTopAppBarColors(),
                                null
                            )
                        } else {
                            CenterAlignedTopAppBar(
                                title = { Text(modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .basicMarquee(), text = fldrObj.name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                                modifier = Modifier.fillMaxWidth(),
                                navigationIcon = {},
                                actions = {},
                                windowInsets = TopAppBarDefaults.windowInsets,
                                TopAppBarDefaults.centerAlignedTopAppBarColors(),
                                null
                            )
                        }


                    }

                    if (!selectingFilter) {



                        Column (
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()){
                            Row(
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedButton(
                                    modifier = Modifier.padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 0.dp),
                                    onClick = {bmoViewModel.setIsAddingTag(true); navCon.navigate(Screens.AddFolderScreen.route)}
                                ) {
                                    Text(text = stringResource(id = R.string.add_tag))
                                }

                                OutlinedButton(
                                    modifier = Modifier.padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 0.dp),
                                    onClick = {navCon.navigate(Screens.DelTagScreen.route)}
                                ) {
                                    Text(text = stringResource(id = R.string.del_tag))
                                }
                            }

                            Row() {
                                OutlinedButton(
                                    onClick = { selectingFilter = true},
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                ) {

                                        Text(text = if (selectedTag == -1) {stringResource(id = R.string.filter)} else { if (selectedTag > 0) {fldrObj.tags[selectedTag - 1].name} else {fldrObj.tags[selectedTag].name}})

                                }
                            }
                        }

                        LazyColumn(
                            modifier = Modifier.weight(1f, true),

                            ) {


                            if (selectedTag != -1) {
                                items(fldrObj.bookmarks.size) { bmObj ->

                                    if (fldrObj.bookmarks[bmObj].tags.contains(fldrObj.tags[if (selectedTag == 0) {selectedTag} else {selectedTag -1}])) {

                                        BookmarkItem(fldrObj = fldrObj, _id = bmObj, navCon = navCon)


                                    }
                                }
                            } else {
                                items(fldrObj.bookmarks.size) { bmObj ->

                                        BookmarkItem(fldrObj = fldrObj, _id = bmObj, navCon = navCon)

                                }
                            }



//                                items(fldrObj.filterBookmarksbyTag(null).size) { bmObj ->
//
//                                    BookmarkItem(fldrObj = fldrObj, _id = bmObj, navCon = navCon)
//                                }


                        }

                        FilledIconButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            onClick = {navCon.navigate(Screens.AddBookmarkScreen.route)}
                        ) {
                            Text(text = stringResource(id = R.string.add_bookmark))
                        }

                    } else {

                        Column(modifier = Modifier.fillMaxWidth()) {
                            OutlinedButton(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                onClick = {selectedTag = -1; selectingFilter = false}
                            ) {
                                Text(text = "Remove")
                            }

                            LazyColumn() {
                                items(fldrObj.tags.size) { tag ->
                                    OutlinedButton(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        onClick = {selectedTag = fldrObj.tags[tag]._id; selectingFilter = false }
                                    ) {
                                        Text(text = fldrObj.tags[tag].name)
                                    }
                                }
                            }

                            OutlinedButton(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                onClick = {selectingFilter = false}
                            ) {
                                Text(text = "Cancel")
                            }

                        }

                    }

                }

            }

        }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DeleteTagScreen(navCon: NavController, fldrObj: FolderObj) {


            var tagToDel: TagObj? = null
            var hasSelectedTag by remember {
                mutableStateOf(false)
            }



            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {

                Column {

                    TopBar()

                    if (!hasSelectedTag) {

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            LazyColumn() {
                                items(fldrObj.tags.size) { tag ->
                                    OutlinedButton(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        onClick = {
                                            tagToDel = fldrObj.tags[tag]; hasSelectedTag = true
                                        }) {
                                        Text(text = fldrObj.tags[tag].name)
                                    }
                                }
                            }

                            OutlinedButton(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                onClick = {
                                    hasSelectedTag =
                                        false; navCon.navigate(Screens.FolderContentScreen.route)
                                }) {
                                Text(text = stringResource(id = R.string.cancel))
                            }
                        }

                    } else {


                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            ElevatedCard(
                                modifier = Modifier
                                    .wrapContentWidth()
                                    .wrapContentHeight()
                                    .padding(8.dp),
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(8.dp),
                                onClick = {}
                            ) {
                                Text(
                                    modifier = Modifier.padding(8.dp),
                                    text = stringResource(id = R.string.confirm_delete_tag)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {

                                    FilledTonalButton(
                                        modifier = Modifier.padding(8.dp),
                                        onClick = {
                                            tagToDel =
                                                null; navCon.navigate(Screens.FolderContentScreen.route) {
                                            popUpTo(
                                                Screens.FolderListViewScreen.route
                                            )
                                        }
                                        }) {

                                        Text(text = stringResource(id = R.string.no_cancel))

                                    }

                                    FilledTonalButton(modifier = Modifier.padding(8.dp), onClick = {
                                        if (tagToDel != null) {
                                            fldrObj.DeleteTag(tagToDel!!)
                                        }

//                                    hasSelectedTag = false

                                        navCon.navigate(Screens.FolderContentScreen.route) {
                                            popUpTo(Screens.FolderListViewScreen.route)
                                        }
                                    }

                                    ) {

                                        Text(text = stringResource(id = R.string.yes_delete_tag))

                                    }

                                }

                            }
                        }
                    }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
    @Composable
    fun BookmarkItem(fldrObj: FolderObj, _id:Int, navCon: NavController) {
        var expanded by remember {
            mutableStateOf(false)
        }

        var cnfrmDel by remember {
            mutableStateOf(false)
        }

        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(8.dp)
                .animateContentSize(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            onClick = {expanded = !expanded}
        ) {



            if (!expanded) {

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(modifier = Modifier.padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 0.dp), text = stringResource(id = R.string.title), style = MaterialTheme.typography.titleLarge)
                    Divider(modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp))
                }

                if (fldrObj.bookmarks[_id].title != null) {
                    Text(modifier = Modifier.padding(8.dp), text = fldrObj.bookmarks[_id].title!!, maxLines = 1)
                } else {
                    Text(modifier = Modifier.padding(8.dp), text = "", maxLines = 1)
                }

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(modifier = Modifier.padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 0.dp), text = stringResource(id = R.string.url), style = MaterialTheme.typography.titleLarge)
                    Divider(modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp))
                }

                val annotatedString = buildAnnotatedString {
                    val string = fldrObj.bookmarks[_id].url
                    append(string)
                    addStyle(SpanStyle(color = MaterialTheme.colorScheme.primary, textDecoration = TextDecoration.Underline), 0, string.length)
                    addStringAnnotation(tag = "url", annotation = string, start = 0, end = string.length)
                }
                val uriHandler = LocalUriHandler.current
                ClickableText(modifier = Modifier.padding(8.dp), text = annotatedString, onClick = { val uri = annotatedString.getStringAnnotations(tag = "url", start = it, end = it)?.firstOrNull()?.item; if (uri != null) {uriHandler.openUri(uri)} })

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(modifier = Modifier.padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 0.dp), text = stringResource(id = R.string.tags), style = MaterialTheme.typography.titleLarge)
                    Divider(modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp))
                }
                Text(modifier = Modifier.padding(8.dp), text = fldrObj.bookmarks[_id].getTagsString(), maxLines = 1)

            }




            if (expanded) {

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(modifier = Modifier.padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 0.dp), text = stringResource(id = R.string.title), style = MaterialTheme.typography.titleLarge)
                    Divider(modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp))
                }

                if (fldrObj.bookmarks[_id].title != null) {
                    Text(modifier = Modifier.padding(8.dp), text = fldrObj.bookmarks[_id].title!!)
                } else {
                    Text(modifier = Modifier.padding(8.dp), text = "")
                }

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(modifier = Modifier.padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 0.dp), text = stringResource(id = R.string.url), style = MaterialTheme.typography.titleLarge)
                    Divider(modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp))
                }

                val annotatedString = buildAnnotatedString {
                    val string = fldrObj.bookmarks[_id].url
                    append(string)
                    addStyle(SpanStyle(color = MaterialTheme.colorScheme.primary, textDecoration = TextDecoration.Underline), 0, string.length)
                    addStringAnnotation(tag = "url", annotation = string, start = 0, end = string.length)
                }
                val uriHandler = LocalUriHandler.current
                ClickableText(modifier = Modifier.padding(8.dp), text = annotatedString, onClick = { val uri = annotatedString.getStringAnnotations(tag = "url", start = it, end = it)?.firstOrNull()?.item; if (uri != null) {uriHandler.openUri(uri)} })

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(modifier = Modifier.padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 0.dp), text = stringResource(id = R.string.tags), style = MaterialTheme.typography.titleLarge)
                    Divider(modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp))
                }

                Text(modifier = Modifier.padding(8.dp), text = fldrObj.bookmarks[_id].getTagsString())


                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(modifier = Modifier.padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 0.dp), text = stringResource(id = R.string.notes), style = MaterialTheme.typography.titleLarge)
                    Divider(modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp))
                }

                if (fldrObj.bookmarks[_id].note != null) {
                    Text(modifier = Modifier.padding(8.dp), text = fldrObj.bookmarks[_id].note!!)
                } else {
                    Text(modifier = Modifier.padding(8.dp), text = "")
                }

                if (!cnfrmDel) {
                    Row(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilledIconButton(
                            modifier = Modifier.padding(8.dp),
                            onClick = {
                                var textToSend = "Hey check out this link! \n \n".plus("${fldrObj.bookmarks[_id].title} \n \n").plus("${fldrObj.bookmarks[_id].url} \n \n").plus("~sent via Bookmark Organizer.")
                                val sendIntent: Intent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, textToSend)
                                    type = "text/plain"
                                }

                                val shareIntent = Intent.createChooser(sendIntent, null)
                                startActivity(shareIntent)
                            },
                        enabled = !bmoViewModel.getIsHidden()) {
                            Icon(imageVector = Icons.Filled.Share, contentDescription = "")
                        }
                        FilledIconButton(modifier = Modifier.padding(8.dp),
                            onClick = { bmoViewModel.setBookmarkToEdit(fldrObj.bookmarks[_id]._id); navCon.navigate(Screens.AddBookmarkScreen.route)}) {
                            Icon(imageVector = Icons.Filled.Edit, contentDescription = "")
                        }
                        FilledIconButton(modifier = Modifier.padding(8.dp),
                            onClick = { cnfrmDel = true }) {
                            Icon(imageVector = Icons.Filled.Delete, contentDescription = "")
                        }
                        FilledIconButton(modifier = Modifier.padding(8.dp),
                            onClick = { /*TODO*/ }, enabled = false) {
                            Icon(imageVector = Icons.Filled.List, contentDescription = "")
                        }
                }


                } else {
                    ElevatedCard(
                        modifier = Modifier
                            .wrapContentWidth()
                            .wrapContentHeight()
                            .padding(8.dp),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(8.dp),
                        onClick = {}
                    ) {

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(modifier = Modifier.padding(8.dp), text = stringResource(id = R.string.confirm_delete_bookmark))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {

                                FilledTonalButton(modifier = Modifier.padding(8.dp), onClick = {cnfrmDel = false}) {

                                    Text(text = stringResource(id = R.string.no_cancel))

                                }

                                FilledTonalButton(modifier = Modifier.padding(8.dp), onClick = {

                                    fldrObj.DeleteBookmark(fldrObj.bookmarks[_id])
                                    cnfrmDel = false
                                    navCon.popBackStack()

                                }

                                ) {

                                    Text(modifier = Modifier.basicMarquee(), text = stringResource(id = R.string.yes_delete_bookmark))

                                }

                            }

                        }

                    }
                }

            }

        }

    }

//End folder content screen composables.

    //Begin Add bookmark screen composables.
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AddBookmarkScreen (navCon: NavController, fldrObj: FolderObj) {


            val scope = rememberCoroutineScope()

            var title: String by remember {
                mutableStateOf("")
            }
            var url: String by remember {
                mutableStateOf("")
            }
            var note: String by remember {
                mutableStateOf("")
            }

            var isDuplicate by remember {
                mutableStateOf(false)
            }

        if (sentUrl != null) {
            url = sentUrl!!
            sentUrl = null
        }

        if (bmoViewModel.getBookmarkToEdit() != -1) {
            if (fldrObj.bookmarks[bmoViewModel.getBookmarkToEdit()].title != null) {

                title = fldrObj.bookmarks[bmoViewModel.getBookmarkToEdit()].title.toString()

            }

            url = fldrObj.bookmarks[bmoViewModel.getBookmarkToEdit()].url

            if (fldrObj.bookmarks[bmoViewModel.getBookmarkToEdit()].note != null) {

                note = fldrObj.bookmarks[bmoViewModel.getBookmarkToEdit()].note.toString()

            }

        }

            var tags: MutableList<TagObj> = mutableListOf()

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {

                Column(modifier = Modifier.fillMaxWidth()) {

                    OutlinedTextField(
                        value = title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        onValueChange = { title = it },
                        maxLines = 1,
                        label = { Text(text = "Title") }
                    )

                    FilledTonalButton(modifier = Modifier
                        .wrapContentWidth()
                        .padding(8.dp),
                        enabled = !isDuplicate,
                        onClick = {
                            GlobalVars.isParsingTitle = true; if (url != "" && url.startsWith(
                                "https://"
                            )
                        ) {
                            scope.launch(Dispatchers.IO) { title = HtmlParser.ParseForTitle(url) }
                        }
                        }) {
                        Text(text = stringResource(id = R.string.detect_page_title))
                    }

                    OutlinedTextField(
                        value = url,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        onValueChange = {
                            url = it; isDuplicate = fldrObj.checkForDuplicateUrl(url)
                        },
                        maxLines = 1,
                        isError = isDuplicate,
                        label = {
                            Text(
                                text = if (isDuplicate) {
                                    "Already exists"
                                } else {
                                    "Url"
                                }
                            )
                        }
                    )

                    OutlinedTextField(
                        value = note,
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(8.dp),
                        onValueChange = { note = it },
                        maxLines = 4,
                        label = { Text(text = "Notes") }
                    )


                    LazyColumn(modifier = Modifier.weight(1f)) {


                        items(fldrObj.tags.size) { tag ->

                            var isChecked by remember {
                                mutableStateOf(false)
                            }

                            Row() {

                                Checkbox(
                                    modifier = Modifier.padding(8.dp),
                                    checked = isChecked,
                                    onCheckedChange = {
                                        isChecked = it; if (isChecked) {
                                        tags.add(fldrObj.tags[tag])
                                    }; if (!isChecked) {
                                        tags.remove(fldrObj.tags[tag])
                                    }
                                    },
                                    colors = CheckboxDefaults.colors()
                                )
                                Text(
                                    modifier = Modifier.padding(8.dp),
                                    text = fldrObj.tags[tag].name
                                )

                            }

                        }

                    }




                    FilledIconButton(modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                        enabled = !isDuplicate, onClick = {
                            if (bmoViewModel.getBookmarkToEdit() == -1) {

                                if (title == "" && note != "") {

                                    fldrObj.AddBookmark(
                                        BookmarkObj(
                                            _id = fldrObj.bookmarks.size + 1,
                                            title = null,
                                            url = url,
                                            note = note,
                                            tags = tags
                                        )
                                    )
                                    navCon.popBackStack()

                                } else if (title != "" && note == "") {

                                    fldrObj.AddBookmark(
                                        BookmarkObj(
                                            _id = fldrObj.bookmarks.size + 1,
                                            title = title,
                                            url = url,
                                            note = null,
                                            tags = tags
                                        )
                                    )
                                    navCon.popBackStack()

                                } else if (title == "" && note == "") {

                                    fldrObj.AddBookmark(
                                        BookmarkObj(
                                            _id = fldrObj.bookmarks.size + 1,
                                            title = null,
                                            url = url,
                                            note = null,
                                            tags = tags
                                        )
                                    )
                                    navCon.popBackStack()

                                } else {

                                    fldrObj.AddBookmark(
                                        BookmarkObj(
                                            _id = fldrObj.bookmarks.size + 1,
                                            title = title,
                                            url = url,
                                            note = note,
                                            tags = tags
                                        )
                                    )
                                    navCon.popBackStack()

                                }

                            } else {

                                if (title == "" && note != "") {

                                    fldrObj.EditBookmark(
                                        BookmarkObj(
                                            _id = bmoViewModel.getBookmarkToEdit(),
                                            title = null,
                                            url = url,
                                            note = note,
                                            tags = tags
                                        )
                                    )
                                    navCon.popBackStack()

                                } else if (title != "" && note == "") {

                                    fldrObj.EditBookmark(
                                        BookmarkObj(
                                            _id = bmoViewModel.getBookmarkToEdit(),
                                            title = title,
                                            url = url,
                                            note = null,
                                            tags = tags
                                        )
                                    )
                                    navCon.popBackStack()


                                } else if (title == "" && note == "") {

                                    fldrObj.EditBookmark(
                                        BookmarkObj(
                                            _id = bmoViewModel.getBookmarkToEdit(),
                                            title = null,
                                            url = url,
                                            note = null,
                                            tags = tags
                                        )
                                    )
                                    navCon.popBackStack()

                                } else {

                                    fldrObj.EditBookmark(
                                        BookmarkObj(
                                            _id = bmoViewModel.getBookmarkToEdit(),
                                            title = title,
                                            url = url,
                                            note = note,
                                            tags = tags
                                        )
                                    )
                                    navCon.popBackStack()

                                }

                            }
                        }) {
                        Text(text = stringResource(id = R.string.bttn_confirm))
                    }
                }
            }
    }

    @Composable
    fun SettingsScreen(navCon: NavController) {

        val context = LocalContext.current

        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {


            Column(modifier = Modifier.fillMaxWidth()) {

                TopBar()

                FilledTonalButton(modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp), onClick = { bmoViewModel.importData(context); restartMain() }) {
                    Text(text = stringResource(id = R.string.imp_data))
                }
                FilledTonalButton(modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp), onClick = { bmoViewModel.exportData(context); navCon.popBackStack() }) {
                    Text(text = stringResource(id = R.string.exp_data))
                }
            }
        }
    }
}