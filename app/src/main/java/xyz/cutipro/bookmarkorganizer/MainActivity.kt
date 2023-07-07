package xyz.cutipro.bookmarkorganizer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
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
        setContent {

            FolderListViewScreen(list = bmoViewModel.appData.collectAsState().value.folderObjArrayData)

        }
    }

    override fun onPause() {
        super.onPause()

        bmoViewModel.saveData(this)

    }

    override fun onStop() {
        super.onStop()

        bmoViewModel.saveData(this)

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

    //Begin FolderListViewScreen Composables.
    @Composable
    fun FolderListViewScreen(list: MutableList<FolderObj>) {


        BookmarkOrganizerTheme {
            // A surface container using the 'background' color from the theme
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {

                Column() {
                    TopBar()

                    FolderListViewScreenTabRow()

                    FolderList(list = list)

                }

            }
        }

    }

    @Composable
    fun FolderListViewScreenTabRow() {

        var topRowState by remember {
            mutableStateOf(0)

        }

        var bottomRowState by remember {
            mutableStateOf(0)

        }

        var hiddenCount by remember {
            mutableStateOf(0)
        }

        TabRow(selectedTabIndex = topRowState) {

            Tab(
                selected = topRowState == 0, onClick = { topRowState = 0 }, enabled = true,
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Home,
                        contentDescription = "Home icon"
                    )
                })

            Tab(
                selected = topRowState == 1, onClick = { topRowState = 1 }, enabled = true,
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Settings icon"
                    )
                })


        }

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
    fun FolderList(list: MutableList<FolderObj>) {

        val filteredList = bmoViewModel.filterObjList(list = list)

        Column() {
            LazyVerticalGrid(
                modifier = Modifier.weight(1f, true),
                columns = GridCells.Fixed(2),
//        verticalArrangement = Arrangement.spacedBy(8.dp),
//        horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                items(filteredList.size) { obj ->

                    FolderItem(obj = filteredList[obj])

                }

            }

            FilledIconButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                onClick = {
//                    list.add(FolderObj(0, "show 1", false, mutableListOf(), mutableListOf()))
//                    list.add(FolderObj(1, "show 2", false, mutableListOf(), mutableListOf()))
//                    list.add(FolderObj(2, "priv 1", true, mutableListOf(), mutableListOf()))
//                    list.add(FolderObj(3, "priv 2", true, mutableListOf(), mutableListOf()))
//                    list.add(FolderObj(4, "show 3", false, mutableListOf(), mutableListOf()))
//                    list.add(FolderObj(5, "priv 3", true, mutableListOf(), mutableListOf()))
//                    list.add(FolderObj(6, "show 4", false, mutableListOf(), mutableListOf()))
//                    list.add(FolderObj(7, "priv 4", true, mutableListOf(), mutableListOf()))
//                    list.add(FolderObj(8, "show 5", false, mutableListOf(), mutableListOf()))
//                    list.add(FolderObj(9, "priv 5", true, mutableListOf(), mutableListOf()))
//                    list.add(FolderObj(10, "show 6", false, mutableListOf(), mutableListOf()))
                }) {

                Text(text = stringResource(id = R.string.bttn_add_folder))

            }

        }

    }

    @Composable
    fun FolderItem(obj: FolderObj) {

        ElevatedCard(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight()
                .padding(8.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(8.dp)
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
    fun AddFolderScreen() {
        
        var fldrName by remember {
            mutableStateOf("")
        }

        BookmarkOrganizerTheme() {

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                
                Column {
                    TopBar()
                    
                    OutlinedTextField(value = fldrName, onValueChange = { fldrName == it })
                    
                }

            }

        }

    }

    //Begin FolderListViewScreen Composable previews.
    @Preview(showBackground = true)
    @Composable
    fun FolderListViewScreenPreview() {

        val navCon = rememberNavController()
        val list: MutableList<FolderObj> = mutableListOf()
        list.add(FolderObj(0, "Folder 1", false, mutableListOf(), mutableListOf()))
        list.add(FolderObj(1, "Folder 2", false, mutableListOf(), mutableListOf()))

        FolderListViewScreen(list = list)

    }

    @Preview(showBackground = false)
    @Composable
    fun FolderItemPreview() {

        BookmarkOrganizerTheme() {

            FolderItem(
                obj = FolderObj(
                    0, "Title",
                    false, mutableListOf(), mutableListOf()
                )
            )

        }

    }
}