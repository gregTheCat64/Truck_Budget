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
import ru.javacat.domain.models.ApiResult
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

 //   private val localDbFilePath = "${context.getDatabasePath("app.db")?.parent}/app.db"
//    private val localWalFilePath = "${context.getDatabasePath("app.db")?.parent}/app.db-wal"
//    private val localShmFilePath = "${context.getDatabasePath("app.db")?.parent}/app.db-shm"

    val dbFile = context.getDatabasePath("app.db")
    val walFile = File(dbFile.parentFile, "app.db-wal")
    val shmFile = File(dbFile.parentFile, "app.db-shm")

    override suspend fun getUserInfo(token: String): User {
        val result = apiRequest {
            apiService.getUserInfo("Bearer $token")
        }
        Log.i(TAG, "user is gotten. It is: $result")
        return result
    }

    override suspend fun uploadDatabaseFiles(
        token: String
    ): ApiResult<String> = withContext(Dispatchers.IO) {

        val dbFiles = listOf(
            dbFile, walFile, shmFile
        )

        dbFiles.forEach { file ->
            val urlResult = getUploadUrl(token, "app:/backup-${file.name}")
            if (urlResult is ApiResult.Error){
                return@withContext urlResult
            }
            if (urlResult is ApiResult.Success) {
                val uploadResult = uploadFile(urlResult.data, file)
                if (uploadResult is ApiResult.Error){
                    return@withContext uploadResult
                }
            }
        }

//        withContext(Dispatchers.IO) {
//            val dbUploadUrlResult = getUploadUrl(token, dbPath)
//            if (dbUploadUrlResult is ApiResult.Error) return@withContext  dbUploadUrlResult
//
//            val walUploadUrlResult = getUploadUrl(token, "$dbPath-wal")
//            if (walUploadUrlResult is ApiResult.Error) return@withContext  walUploadUrlResult
//
//            val shmUploadUrlResult = getUploadUrl(token, "$dbPath-shm")
//            if (shmUploadUrlResult is ApiResult.Error) return@withContext  shmUploadUrlResult
//
//            if (dbUploadUrlResult is ApiResult.Success
//                && walUploadUrlResult is ApiResult.Success
//                && shmUploadUrlResult is ApiResult.Success) {
//                val dbUploadSuccess = uploadFile(dbUploadUrlResult.data, localDbFilePath)
//                val walUploadSuccess = uploadFile(walUploadUrlResult.data, localWalFilePath)
//                val shmUploadSuccess = uploadFile(shmUploadUrlResult.data, localShmFilePath)
//
//                if (dbUploadSuccess && walUploadSuccess && shmUploadSuccess) {
//                    switchDatabaseModified(context, false)
//                } else{
//
//                }
//            } else {
//
//            }

            //try {
//                val dbUploadUrlResult = getUploadUrl(token, dbPath)
//                val walUploadUrlResult = getUploadUrl(token, "$dbPath-wal")
//                val shmUploadUrlResult = getUploadUrl(token, "$dbPath-shm")
//
//                if (dbUploadUrlResult is ApiResult.Success
//                    && walUploadUrlResult is ApiResult.Success
//                    && shmUploadUrlResult is ApiResult.Success){
//                    val dbUploadSuccess = uploadFile(dbUploadUrlResult.data, localDbFilePath)
//                    val walUploadSuccess = uploadFile(walUploadUrlResult.data, localWalFilePath)
//                    val shmUploadSuccess = uploadFile(shmUploadUrlResult.data, localShmFilePath)

//                    if (dbUploadSuccess && walUploadSuccess && shmUploadSuccess) {
//                        switchDatabaseModified(context, false)
//                        return@withContext ApiResult.Success("")
//                    } else return@withContext  dbUploadUrlResult
//                } else{
//                    return@withContext dbUploadUrlResult
//                }

                //dbUploadSuccess && walUploadSuccess && shmUploadSuccess
//            } catch (e: Exception) {
//                e.printStackTrace()
//                false
//            }
        //}
        switchDatabaseModified(context, false)
        return@withContext ApiResult.Success("All files uploaded successfully")
    }

    private suspend fun getUploadUrl(token: String, path: String): ApiResult<String> {
        Log.i(TAG, "getting uploadUrl, token is $token, path is $path")
        return try {
            val response = apiService.getUploadUrl("OAuth $token", path)
            Log.i(TAG, "response code: ${response.code()}")

            if (response.isSuccessful) {
                ApiResult.Success(response.body()?.href ?: throw Exception("Empty upload URL"))
            } else {
                when (response.code()) {
                    401 -> ApiResult.Error.Unauthorized
                    500, 503 -> ApiResult.Error.ServerError
                    507 -> ApiResult.Error.InsufficientStorage
                    else -> ApiResult.Error.UnknownError(response.message())
                }
            }
        } catch (e: Exception){
            ApiResult.Error.UnknownError(e.message?:"Unknown error")
        }
    }

    override suspend fun downLoadDatabaseFiles(
        token: String
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val dbDownloadUrl = getDownloadUrl(token, "app:/backup-app.db")
                val walDownloadUrl = getDownloadUrl(token, "app:/backup-app.db-wal")
                val shmDownloadUrl = getDownloadUrl(token, "app:/backup-app.db-shm")

                //если бекапы сделаны, качаем и заменяем файлы
                if (makeBackupFiles()){
                    val dbDownloadSuccess = downloadFile(dbDownloadUrl, dbFile)
                    val walDownloadSuccess = downloadFile(walDownloadUrl, walFile)
                    val shmDownloadSuccess = downloadFile(shmDownloadUrl, shmFile)
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


    private suspend fun uploadFile(uploadUrl: String, file: File): ApiResult<String> {
        Log.i(TAG, "uploading file")
        return try {
            val requestBody = file.asRequestBody("application/octet-stream".toMediaTypeOrNull())
            val response = apiService.uploadFile(uploadUrl, requestBody)
            if (response.isSuccessful) {
                ApiResult.Success("File uploaded successfully")
            } else {
                when (response.code()) {
                    401 -> ApiResult.Error.Unauthorized
                    500, 503 -> ApiResult.Error.ServerError
                    507 -> ApiResult.Error.InsufficientStorage
                    else -> ApiResult.Error.UnknownError(response.message())
                }
            }
        } catch (e: Exception) {
            ApiResult.Error.UnknownError(e.message ?: "Unknown error")
        }
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

    private suspend fun downloadFile(downLoadUrl: String, localFile: File): Boolean {
        Log.i(TAG, "uploading")

        val response = apiService.downloadFile(downLoadUrl)
        return if (response.isSuccessful) {
            response.body()?.let { responseBody ->
                saveToFile(responseBody, localFile)
                true
            } ?: false
        } else {
            false
        }
    }

    private fun saveToFile(responseBody: ResponseBody, file: File) {
        //val file = File(filePath)
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
        } else {
            println("Error making backup files")
            return false
        }
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