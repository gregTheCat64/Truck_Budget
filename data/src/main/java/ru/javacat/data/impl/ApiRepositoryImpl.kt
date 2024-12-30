package ru.javacat.data.impl

import android.content.Context
import android.util.Log
import androidx.room.Room
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import ru.javacat.data.apiRequest
import ru.javacat.data.db.AppDb
import ru.javacat.data.network.ApiService
import ru.javacat.data.switchDatabaseModified
import ru.javacat.domain.models.User
import ru.javacat.domain.repo.ApiRepository
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiService: ApiService,
    private val db: AppDb,
) : ApiRepository {
    private val TAG = "ApiRepoImpl"
    private val dbName = "app.db"
    private val dbPath = "app:/backup.db"

    private val localDbFilePath = "${context.getDatabasePath("app.db")?.parent}/app.db"
    private val localWalFilePath = "${context.getDatabasePath("app.db")?.parent}/app.db-wal"
    private val localShmFilePath = "${context.getDatabasePath("app.db")?.parent}/app.db-shm"

    override suspend fun getUserInfo(token: String): User {
        val result = apiRequest {
            apiService.getUserInfo("Bearer $token")
        }
        Log.i(TAG, "user is gotten. It is: $result")
        return result
    }

    override suspend fun uploadDatabaseFiles(
        token: String
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val dbUploadUrl = getUploadUrl(token, dbPath)
                val walUploadUrl = getUploadUrl(token, "$dbPath-wal")
                val shmUploadUrl = getUploadUrl(token, "$dbPath-shm")

                val dbUploadSuccess = uploadFile(dbUploadUrl, localDbFilePath)
                val walUploadSuccess = uploadFile(walUploadUrl, localWalFilePath)
                val shmUploadSuccess = uploadFile(shmUploadUrl, localShmFilePath)

                if (dbUploadSuccess && walUploadSuccess && shmUploadSuccess) {
                    switchDatabaseModified(context, false)
                }
                dbUploadSuccess && walUploadSuccess && shmUploadSuccess
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    override suspend fun downLoadDatabaseFiles(
        token: String
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val dbDownloadUrl = getDownloadUrl(token, dbPath)
                val walDownloadUrl = getDownloadUrl(token, "$dbPath-wal")
                val shmDownloadUrl = getDownloadUrl(token, "$dbPath-shm")

                //если бекапы сделаны, качаем и заменяем файлы
                if (makeBackupFiles()){
                    val dbDownloadSuccess = downloadFile(dbDownloadUrl, localDbFilePath)
                    val walDownloadSuccess = downloadFile(walDownloadUrl, localWalFilePath)
                    val shmDownloadSuccess = downloadFile(shmDownloadUrl, localShmFilePath)
                    return@withContext  dbDownloadSuccess && walDownloadSuccess && shmDownloadSuccess
                } else {
                    false
                }

            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    private suspend fun getUploadUrl(token: String, path: String): String {
        val response =
            apiService.getUploadUrl("OAuth $token", path)

        Log.i(TAG, "getting uploadUrl")
        Log.i(TAG, "response code: ${response.code()}")
        if (response.isSuccessful) {
            return response.body()?.href ?: throw Exception("Empty upload URL")
        } else {
            throw Exception("Failed to get upload URL")
        }
    }


    private suspend fun uploadFile(uploadUrl: String, filePath: String): Boolean {
        Log.i(TAG, "uploading")
        val file = File(filePath)
        val requestBody = file.asRequestBody("application/octet-stream".toMediaTypeOrNull())
        val response = apiService.uploadFile(uploadUrl, requestBody)
        return response.isSuccessful
    }


    private suspend fun getDownloadUrl(token: String, path: String): String {
        val response =
            apiService.getDownloadUrl("OAuth $token", path)
        Log.i(TAG, "getting uploadUrl")
        Log.i(TAG, "response code: ${response.code()}")
        if (response.isSuccessful) {
            return response.body()?.href ?: throw Exception("Empty download URL")
        } else {
            throw Exception("Failed to get download URL")
        }
    }

    private suspend fun downloadFile(downLoadUrl: String, localFilePath: String): Boolean {
        Log.i(TAG, "uploading")

        val response = apiService.downloadFile(downLoadUrl)
        return if (response.isSuccessful) {
            response.body()?.let { responseBody ->
                saveToFile(responseBody, localFilePath)
                true
            } ?: false
        } else {
            false
        }
    }

    private fun saveToFile(responseBody: ResponseBody, filePath: String) {
        val file = File(filePath)
        FileOutputStream(file).use { output ->
            output.write(responseBody.bytes())
        }
    }

    private fun makeBackupFiles(
    ): Boolean{
        Log.i(TAG, "создаем бекап файлы на всякий случай")
        val dbFile = context.getDatabasePath("app.db")
        val walFile = File(dbFile.parentFile, "app.db-wal")
        val shmFile = File(dbFile.parentFile, "app.db-shm")

        val backupLocalDbFilePath = "${context.getDatabasePath("app.db")?.parent}/app.db-old"
        val backupLocalWalFilePath = "${context.getDatabasePath("app.db")?.parent}/app.db-wal-old"
        val backupLocalShmFilePath = "${context.getDatabasePath("app.db")?.parent}/app.db-shm-old"

        val dbFileResult = dbFile.copyTo(File(backupLocalDbFilePath), overwrite = true)
        val walFileResult = walFile.copyTo(File(backupLocalWalFilePath), overwrite = true)
        val shmFileResult = shmFile.copyTo(File(backupLocalShmFilePath), overwrite = true)

        if (dbFileResult.exists() && walFileResult.exists() && shmFileResult.exists()) {
            println("Backup files were made successfully")
            return true
        } else return false
    }

        //перенес функционал
    suspend fun replaceDatabaseFile(localDbPath: String, localWalPath: String, localShmPath: String) {
        //закрываем текущую БД

        db.close()
        Log.i(TAG, "db is open: ${db.isOpen}")
        //deleteOldDatabaseFiles()
        //copyNewDatabaseFiles(localDbPath, localWalPath, localShmPath)
        //reopenDatabase()
    }

    private fun deleteOldDatabaseFiles() {
        val dbFile = context.getDatabasePath(dbName)
        val walFile = File(dbFile.parentFile, "$dbName-wal")
        val shmFile = File(dbFile.parentFile, "$dbName-shm")
        Log.i(TAG, "current db path is: $dbFile")

        // Удаляем файлы WAL и SHM
        dbFile.delete()
        walFile.delete()
        shmFile.delete()
    }

    private fun copyNewDatabaseFiles(
        backupDbPath: String,
        backupWalPath: String,
        backupShmPath: String
    ): Boolean {
        val dbFile = context.getDatabasePath("app.db")
        val walFile = File(dbFile.parentFile, "app.db-wal")
        val shmFile = File(dbFile.parentFile, "app.db-shm")

        val dbFileResult = File(backupDbPath).copyTo(dbFile, overwrite = true)
        val walFileResult = File(backupWalPath).copyTo(walFile, overwrite = true)
        val shmFileResult = File(backupShmPath).copyTo(shmFile, overwrite = true)

        if (dbFileResult.exists() && walFileResult.exists() && shmFileResult.exists()) {
            println("New database files copied successfully")
            return true
        } else return false
    }



    private fun reopenDatabase() {
        Room.databaseBuilder(
            context,
            AppDb::class.java, "app.db"
        ).build()

        Log.i(TAG, "db is opened: ${db.isOpen}")
        println("Database reopened successfully")

    }
}