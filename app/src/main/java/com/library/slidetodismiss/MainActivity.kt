package com.library.slidetodismiss

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.library.slide_to_dismiss.SlideToDismiss
import com.library.slidetodismiss.data.User
import com.library.slidetodismiss.data.UserDataProvider
import com.library.slidetodismiss.ui.theme.SlidetodismissTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SlidetodismissTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize()) {
                    SwipeToDismissDemo(UserDataProvider.users)
                }
            }
        }
    }
}

@Composable
fun SwipeToDismissDemo(users: List<User>, modifier: Modifier = Modifier) {
    var localUser by remember { mutableStateOf(users) }
    LazyColumn(
        modifier = modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_large)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        items(count = localUser.size, key = { index: Int -> localUser[index].id }) { index ->
            SlideToDismiss(
                data = localUser[index],
                leftAction = { value -> localUser = localUser.filter { it.name != value?.name } },
                rightAction = {},
                leftIcon = Icons.Default.Delete,
                rightIcon = Icons.Default.Share,
                leftIconTint = Color.Red,
                rightIconTint = Color.Red,
            ) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Name: ${localUser[index].name}, Age: ${localUser[index].age}",
                        modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun SlideToDismissStaticIconPreview() {
    SlideToDismiss<String>(
        modifier = Modifier.fillMaxWidth(),
        leftAction = { },
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Slide to dismiss",
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
            )
        }
    }
}