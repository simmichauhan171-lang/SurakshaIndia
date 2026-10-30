package com.suraksha.india.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.suraksha.india.data.ContactEntity
import com.suraksha.india.ui.theme.*
import com.suraksha.india.viewmodel.MainViewModel

@Composable
fun ContactsScreen(navController: NavController, viewModel: MainViewModel) {
    val contacts by viewModel.contacts.collectAsState()
    val context = LocalContext.current

    var showDialog by remember { mutableStateOf(false) }
    var editingContact by remember { mutableStateOf<ContactEntity?>(null) }
    var nameState by remember { mutableStateOf("") }
    var phoneState by remember { mutableStateOf("") }
    var primaryState by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = NavyBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingContact = null
                    nameState = ""
                    phoneState = ""
                    primaryState = false
                    showDialog = true
                },
                containerColor = AccentRed,
                contentColor = PureWhite
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Contact")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Trusted SOS Contacts", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PureWhite)
            Text(
                "These phone numbers receive instant SMS coordinates when SOS is triggered.",
                fontSize = 12.sp,
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (contacts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.PersonOff,
                            contentDescription = "Empty",
                            tint = TextMuted,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No contacts registered yet.", color = TextMuted)
                        Text("Tap + to add emergency family or friends.", fontSize = 12.sp, color = TextMuted)
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(contacts, key = { it.id }) { contact ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = NavySurface),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(contact.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = PureWhite)
                                        if (contact.isPrimary) {
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Surface(
                                                color = EmeraldSafe.copy(alpha = 0.2f),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    "PRIMARY",
                                                    color = EmeraldSafe,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(contact.phoneNumber, fontSize = 13.sp, color = TextMuted)
                                }
                                Row {
                                    IconButton(
                                        onClick = {
                                            editingContact = contact
                                            nameState = contact.name
                                            phoneState = contact.phoneNumber
                                            primaryState = contact.isPrimary
                                            showDialog = true
                                        }
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = TextMuted)
                                    }
                                    IconButton(onClick = { viewModel.deleteContact(contact) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextMuted)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                containerColor = NavyCard,
                titleColor = PureWhite,
                textContentColor = TextMuted,
                title = { Text(if (editingContact == null) "Add Trusted Contact" else "Edit Contact") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = nameState,
                            onValueChange = { nameState = it },
                            label = { Text("Contact Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = phoneState,
                            onValueChange = { phoneState = it },
                            label = { Text("Mobile Number (10 digits)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = primaryState,
                                onCheckedChange = { primaryState = it }
                            )
                            Text("Mark as Primary Contact", fontSize = 13.sp, color = PureWhite)
                        }
                    }
                },
                confirmButton = {
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
                        onClick = {
                            val cleanNumber = phoneState.replace("[^0-9+]".toRegex(), "")
                            if (nameState.isNotBlank() && cleanNumber.length >= 10) {
                                if (editingContact == null) {
                                    viewModel.addContact(nameState, cleanNumber, primaryState)
                                } else {
                                    viewModel.updateContact(
                                        editingContact!!.copy(
                                            name = nameState.trim(),
                                            phoneNumber = cleanNumber,
                                            isPrimary = primaryState
                                        )
                                    )
                                    if (primaryState) {
                                        viewModel.setPrimaryContact(editingContact!!.id)
                                    }
                                }
                                showDialog = false
                            } else {
                                Toast.makeText(context, "Enter a valid Name and 10-digit number", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancel", color = TextMuted)
                    }
                }
            )
        }
    }
}
