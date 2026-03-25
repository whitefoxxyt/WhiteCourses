package fr.white.appcourse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import fr.white.appcourse.models.ProduitItem
import fr.white.appcourse.repository.ShoppingRepository
import kotlinx.coroutines.launch

@Composable
fun App() {
    val repository = remember { ShoppingRepository() }
    val scope = rememberCoroutineScope()

    var listIdInput by remember { mutableStateOf("1") }
    var magasinIdInput by remember { mutableStateOf("1") }
    var isLoading by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var items by remember { mutableStateOf<List<ProduitItem>>(emptyList()) }

    fun parsePositiveIntOrNull(value: String): Int? = value.toIntOrNull()?.takeIf { it > 0 }

    fun loadList() {
        val listId = parsePositiveIntOrNull(listIdInput)
        if (listId == null) {
            statusMessage = "listId invalide"
            return
        }

        val magasinId = if (magasinIdInput.isBlank()) {
            null
        } else {
            parsePositiveIntOrNull(magasinIdInput).also {
                if (it == null) statusMessage = "magasinId invalide"
            }
        } ?: if (magasinIdInput.isBlank()) null else return

        scope.launch {
            isLoading = true
            statusMessage = null

            repository.getListeTriee(listId, magasinId)
                .onSuccess {
                    items = it
                    statusMessage = "${it.size} article(s) charge(s)"
                }
                .onFailure {
                    statusMessage = it.message ?: "Erreur reseau"
                }

            isLoading = false
        }
    }

    fun toggleItem(item: ProduitItem) {
        val nextState = !item.estAchete
        scope.launch {
            repository.toggleAchat(item.id, nextState)
                .onSuccess {
                    items = items.map {
                        if (it.id == item.id) it.copy(estAchete = nextState) else it
                    }
                }
                .onFailure {
                    statusMessage = it.message ?: "Erreur mise a jour"
                }
        }
    }

    MaterialTheme {
        Column(
            modifier = Modifier
                .safeContentPadding()
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Test fonctionnel API mobile", style = MaterialTheme.typography.titleLarge)

            OutlinedTextField(
                value = listIdInput,
                onValueChange = { listIdInput = it },
                label = { Text("List ID") },
                singleLine = true,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = magasinIdInput,
                onValueChange = { magasinIdInput = it },
                label = { Text("Magasin ID (optionnel)") },
                singleLine = true,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { loadList() },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.padding(2.dp))
                } else {
                    Text("Charger la liste")
                }
            }

            statusMessage?.let {
                Text(it, style = MaterialTheme.typography.bodyMedium)
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(items, key = { it.id }) { item ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.nom, style = MaterialTheme.typography.titleMedium)
                                Text("Categorie: ${item.categorieNom}")
                                Text("Quantite: ${item.quantite}")
                                Text("Position rayon: ${item.PositionEnRayon ?: "N/A"}")
                            }
                            Checkbox(
                                checked = item.estAchete,
                                onCheckedChange = { toggleItem(item) }
                            )
                        }
                    }
                }
            }
        }
    }
}