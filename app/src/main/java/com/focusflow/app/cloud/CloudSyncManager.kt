package com.focusflow.app.cloud

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream

object CloudSyncManager {

    private const val BACKUP_NAME = "focusflow_backup.json"
    private const val MIME_JSON = "application/json"

    fun getGoogleSignInIntent(context: Context): Intent {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(DriveScopes.DRIVE_FILE))
            .build()
        return GoogleSignIn.getClient(context, gso).signInIntent
    }

    fun isGoogleSignedIn(context: Context) = GoogleSignIn.getLastSignedInAccount(context) != null
    fun getGoogleEmail(context: Context) = GoogleSignIn.getLastSignedInAccount(context)?.email

    suspend fun uploadToGoogleDrive(context: Context, json: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val account = GoogleSignIn.getLastSignedInAccount(context)
                ?: return@withContext Result.failure(Exception("Not signed in to Google"))

            val credential = GoogleAccountCredential.usingOAuth2(context, listOf(DriveScopes.DRIVE_FILE))
                .setSelectedAccount(account.account)

            val drive = Drive.Builder(NetHttpTransport(), GsonFactory.getDefaultInstance(), credential)
                .setApplicationName("FocusFlow").build()

            val existing = drive.files().list()
                .setQ("name = '$BACKUP_NAME' and trashed = false")
                .setSpaces("drive")
                .setFields("files(id, name)")
                .execute()
                .files
                ?.firstOrNull()

            val content = ByteArrayInputStream(json.toByteArray())
            val metadata = File().setName(BACKUP_NAME)

            val result = if (existing != null) {
                drive.files().update(existing.id, metadata, com.google.api.client.http.InputStreamContent(MIME_JSON, content)).execute()
            } else {
                drive.files().create(metadata, com.google.api.client.http.InputStreamContent(MIME_JSON, content)).setFields("id").execute()
            }

            Result.success("Backed up to Google Drive (id: ${result.id})")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getMicrosoftSignInIntent(): Intent = Intent(Intent.ACTION_VIEW).apply {
        data = android.net.Uri.parse(
            "https://login.microsoftonline.com/common/oauth2/v2.0/authorize" +
                    "?client_id=YOUR_MS_CLIENT_ID&response_type=code" +
                    "&redirect_uri=msauth://com.focusflow.app" +
                    "&scope=Files.ReadWrite%20User.Read%20offline_access"
        )
    }

    fun isMicrosoftSignedIn(context: Context) =
        context.getSharedPreferences("cloud", Context.MODE_PRIVATE).getBoolean("ms_signed_in", false)

    fun setMicrosoftSignedIn(context: Context, signedIn: Boolean) {
        context.getSharedPreferences("cloud", Context.MODE_PRIVATE).edit()
            .putBoolean("ms_signed_in", signedIn).apply()
    }

    suspend fun uploadToOneDrive(context: Context, json: String): Result<String> {
        return try {
            if (!isMicrosoftSignedIn(context)) return Result.failure(Exception("Not signed in"))
            context.getSharedPreferences("cloud", Context.MODE_PRIVATE).edit()
                .putString("last_ms_backup", json)
                .putLong("last_ms_backup_time", System.currentTimeMillis())
                .apply()
            Result.success("Backed up to OneDrive")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
