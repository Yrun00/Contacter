package com.github.contacter

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.github.contacter.ui.theme.ContacterTheme

class MainActivity : ComponentActivity() {
    private val app by lazy { applicationContext as MyApplication }

    private val viewModel by lazy {app.viewModel}

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            viewModel.loadContacts()
        } else {
            Toast.makeText(this, "PermissionDenied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ContacterTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ContactsScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding),
                        onRequestPermission = {
                            requestPermission()
                        }
                    )
                }
            }
        }
    }

    private fun requestPermission() {

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_CONTACTS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                viewModel.loadContacts()
            } else {

                requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
            }

    }

    @Composable
    fun ContactsScreen(
        viewModel: ContactViewModel,
        modifier: Modifier = Modifier,
        onRequestPermission: () -> Unit
    ) {
        val contacts = viewModel.contacts.collectAsState()
        val isLoading = viewModel.isLoading.collectAsState()

        LaunchedEffect(Unit) {
            onRequestPermission()
        }

        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when {
                isLoading.value -> {
                    CircularProgressIndicator()
                    Text("Loading contacts...")
                }

                contacts.value.isEmpty() -> {
                    Text("No contacts found")
                }

                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(contacts.value.size) { index ->
                            ContactItem(contacts.value[index])
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun ContactItem(contact: Contact) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = contact.name,
                    fontSize = 16.sp
                )
                Text(
                    text = contact.phoneNumber,
                    fontSize = 12.sp
                )
                Text(
                    text = "ID: ${contact.id}",
                    fontSize = 10.sp
                )
            }
        }
    }

}


