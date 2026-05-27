package com.example.validade


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProdutoDao {
    @Insert
    suspend fun inserir(produto: Produto)
    @Delete
    suspend fun deletar(produto: Produto)

    @Query("SELECT * FROM produtos ORDER BY id DESC")
    fun listarTodos(): Flow<List<Produto>>
}