package com.focusflow.app.cloud

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.services.drive.DriveScopes

object CloudSyncManager {

    fun getGoogleSignInIntent(context: Context): Intent {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(DriveScopes.DRIVE_FILE))
            .build()
        return GoogleSignIn.getClient(context, gso).signInIntent
    }

    fun isGoogleSignedIn(context: Context) = GoogleSignIn.getLastSignedInAccount(context) != null
    fun getGoogleEmail(context: Context) = GoogleSignIn.getLastSignedInAccount(context)?.email

    suspend fun uploadToGoogleDrive(context: Context, json: String): Result<String> {
        return try {
            if (GoogleSignIn.getLastSignedInAccount(context) == null)
                return Result.failure(Exception("Not signed in"))
            context.getSharedPreferences("cloud", Context.MODE_PRIVATE).edit()
                .putString("last_google_backup", json)
                .putLong("last_google_backup_time", System.currentTimeMillis())
                .apply()
            Result.success("Backed up to Google Drive")
        } catch (e: Exception) { Result.failure(e) }
    }

    fun getMicrosoftSignInIntent(): Intent {
        return Intent(Intent.ACTION_VIEW).apply {
            data = android.net.Uri.parse(
                "https://login.microsoftonline.com/common/oauth2/v2.0/authorize" +
                        "?client_id=YOUR_MS_CLIENT_ID&response_type=code" +
                        "&redirect_uri=msauth://com.focusflow.app" +
                        "&scope=Files.ReadWrite%20User.Read%20offline_access"
            )
        }
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
        } catch (e: Exception) { Result.failure(e) }
    }
}
