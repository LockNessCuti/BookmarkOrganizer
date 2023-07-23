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

sealed class Screens(val route:String) {
    object FolderListViewScreen: Screens(route = "folder_list_view_screen")
    object AddFolderScreen: Screens(route = "add_folder_screen")
    object FolderContentScreen: Screens(route = "folder_content_screen")
    object DelTagScreen: Screens(route = "del_tag_screen")
    object AddBookmarkScreen: Screens(route = "add_bookmark_screen")
    object SettingsScreen: Screens(route = "settings_screen")
}
