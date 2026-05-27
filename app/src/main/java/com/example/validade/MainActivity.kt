package com.example.validade

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.validade.ui.theme.ValidadeTheme
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.ExperimentalMaterial3Api

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ValidadeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TelaCadastroProduto()
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaCadastroProduto() {
    val contexto = LocalContext.current
    val scope = rememberCoroutineScope()
    val db = AppDatabase.getDatabase(contexto)

    var nomeProduto by remember { mutableStateOf("") }
    var dataValidade by remember { mutableStateOf("") }
    val listaProdutos by db.produtoDao().listarTodos().collectAsState(initial = emptyList())
    var mostrarCalendario by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Cadastrar Produto", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = nomeProduto,
            onValueChange = { nomeProduto = it },
            label = { Text("Nome do Produto") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = dataValidade,
            onValueChange = { },
            label = { Text("Data de Vencimento") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { mostrarCalendario = true },
            readOnly = true,
            enabled = false,
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        Button(
            onClick = {
                if (nomeProduto.isNotEmpty() && dataValidade.isNotEmpty()) {
                    val novoProduto = Produto(nome = nomeProduto, dataValidade = dataValidade)

                    scope.launch {
                        db.produtoDao().inserir(novoProduto)
                    }

                    Toast.makeText(contexto, "Produto $nomeProduto salvo!", Toast.LENGTH_SHORT).show()
                    nomeProduto = ""
                    dataValidade = ""
                } else {
                    Toast.makeText(contexto, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            if (mostrarCalendario) {
                DatePickerDialog(
                    onDismissRequest = { mostrarCalendario = false }, // Fecha se clicar fora do calendário
                    confirmButton = {
                        TextButton(onClick = {
                            // Pega o dia selecionado em milissegundos
                            val dataSelecionada = datePickerState.selectedDateMillis
                            if (dataSelecionada != null) {
                                // Transforma o número gigante de milissegundos em formato brasileiro de data
                                val formato = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                                dataValidade = formato.format(java.util.Date(dataSelecionada))
                            }
                            mostrarCalendario = false // Desliga o interruptor e fecha a janela
                        }) {
                            Text("Confirmar")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { mostrarCalendario = false }) {
                            Text("Cancelar")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }
            Text("Salvar Produto")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Produtos Cadastrados",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(listaProdutos) { produto ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = produto.nome,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Validade: ${produto.dataValidade}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = {
                                scope.launch {
                                    db.produtoDao().deletar(produto)
                                }
                                Toast.makeText(contexto, "${produto.nome} removido!", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(
                                painter = androidx.compose.ui.res.painterResource(id = android.R.drawable.ic_menu_delete),
                                contentDescription = "Deletar Produto",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TelaCadastroPreview() {
    ValidadeTheme {
        TelaCadastroProduto()
    }
}