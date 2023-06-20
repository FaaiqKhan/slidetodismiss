package com.library.slidetodismiss

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
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
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
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
                modifier = Modifier.fillMaxWidth(),
                onDismiss = { value -> localUser = localUser.filter { it.name != value?.name } },
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = dimensionResource(id = R.dimen.card_minimum_height))
                        .align(Alignment.CenterVertically),
                ) {
                    Text(
                        text = "Name: ${localUser[index].name}, Age: ${localUser[index].age}",
                        modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
                    )
                }
            }
        }
    }
}