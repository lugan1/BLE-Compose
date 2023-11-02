package com.example.blecomposestudy.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun MainScreen(
    bindService: () -> Unit = {},
    connect: () -> Unit = {},
    disconnect: () -> Unit = {},
    write: () -> Unit = {}
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(30.dp)
    ) {
        Spacer(modifier = Modifier.height(30.dp))
        Button(onClick = bindService) {
            Text(text = "Bind Service")
        }


        Button(onClick = connect) {
            Text(text = "Connect")
        }

        Button(onClick = disconnect) {
            Text(text = "Disconnect")
        }

        Button(onClick = write) {
            Text(text = "Write")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen()
}