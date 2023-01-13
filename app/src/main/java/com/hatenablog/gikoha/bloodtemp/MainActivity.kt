package com.hatenablog.gikoha.bloodtemp

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.*
import com.hatenablog.gikoha.bloodtemp.ui.theme.BloodTempTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class MainActivity : ComponentActivity()
{

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        setContent {
            MainScreen()
        }
    }

}

// Create one line row in Compose LazyColumn

@Composable
fun BTOneline(data: BloodTemp)
{
    Column {
        Row {
            Text(
                text = data.date,
                modifier = Modifier.padding(all = 4.dp)
            )
            Text(
                text = data.temp,
                modifier = Modifier.padding(
                    vertical = 4.dp,
                    horizontal = 12.dp
                )
            )
        }
        Text(
            text = data.memo ?: "",
            fontSize = 10.sp,
            modifier = Modifier.padding(all = 4.dp)
        )
    }
}

@Composable
fun MainScreen()
{
    val focusManager = LocalFocusManager.current

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val viewModel: BloodTempViewModel = hiltViewModel()

    val buttontitle by viewModel.buttontitle.observeAsState()

    val viewState: BloodTempViewState by viewModel.state.collectAsState(initial = BloodTempViewState.EMPTY)
    val items: List<BloodTemp> = viewState.items ?: emptyList()

    val showDialog by viewModel.showDialog.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val activity = (LocalContext.current as? Activity)

    if (items.isEmpty()) {
        viewModel.loadData {
            coroutineScope.launch {
                // Animate scroll to the end of item

                listState.animateScrollToItem(viewState.items?.count() ?: 0)
            }
        }
    }

    BTMainScreen(items, buttontitle) { temp, memo ->
        focusManager.clearFocus()
        viewModel.postData(temp, memo)
        {
            viewModel.changeTitle("POST OK")
            viewModel.clearData()
            // 削除すると recompose 行われ、loadData が行われ、再度 recomposeされることで recompose保証
            // updateだけでは recomposeすら行われない「ことがある」
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = {
                Text("Error " + errorMessage)
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.closeAlert()
                    activity?.finishAndRemoveTask()
                }) {
                    Text("Exit")
                }
            }
        )
    }
}

// Provide sample data for preview
class BTMainScreenParameterProvider : PreviewParameterProvider<List<BloodTemp>>
{
    override val values = sequenceOf(
        listOf(
            BloodTemp("22-10-9", "37.0", "やと　ねつ　ひいた　も　とてもかゆい"),
            BloodTemp("22-10-15", "36.0", "かゆい　かゆい　スコットーきた")
        ),
    )
}

@Preview(name = "MainScreen")
@Composable
fun BTMainScreen(
    @PreviewParameter(BTMainScreenParameterProvider::class) items: List<BloodTemp>,
    buttontitle: String? = "SUBMIT",
    onClick: (temp: String, memo: String) -> Unit = { _, _ -> },
)
{
    var temp by remember { mutableStateOf("") }
    var memo by remember { mutableStateOf("") }

    BloodTempTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {

            Column {
                TopAppBar(
                    title = { Text("BloodTemp") },
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextField(
                        value = temp,
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        onValueChange = { temp = it },
                        label = { Text("input blood temperature (eg. 36.3)") },
                        modifier = Modifier.padding(all = 4.dp),
                        maxLines = 1,
                        singleLine = true,
                    )

                    Button(
                        onClick = {
                            onClick(temp, memo)
                        },
                        modifier = Modifier.padding(all = 4.dp)
                    ) {
                        Text(
                            text = buttontitle ?: "",
                            fontSize = 10.sp,
                            maxLines = 1,
                        )
                    }
                }

                TextField(
                    value = memo,
                    onValueChange = { memo = it },
                    label = { Text("memo") },
                    modifier = Modifier.padding(all = 4.dp).fillMaxWidth(),
                    maxLines = 1,
                    singleLine = true,
                )

                BTLists(items)
            }
        }


    }
}

@Composable
fun BTLists(items: List<BloodTemp>)
{
    LazyColumn(
        //state = listState,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        itemsIndexed(items) { _, item ->
            BTOneline(item)
            Divider(color = Color.Gray, thickness = 1.dp)
        }

    }

}


