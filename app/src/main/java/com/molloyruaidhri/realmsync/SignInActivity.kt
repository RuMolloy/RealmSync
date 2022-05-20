package com.molloyruaidhri.realmsync

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.molloyruaidhri.realmsync.App.Companion.app
import com.molloyruaidhri.realmsync.databinding.ActivitySignInBinding
import io.realm.mongodb.Credentials
import io.realm.mongodb.auth.GoogleAuthType

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding

    private lateinit var client: GoogleSignInClient

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(getString(R.string.server_client_id))
            .build()

        client = GoogleSignIn.getClient(this, googleSignInOptions)

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.getResult(ApiException::class.java)

                handleGoogleSignInResult(account)
            }
        }

        binding.btnSignIn.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        val signIntent = client.signInIntent
        resultLauncher.launch(signIntent)
    }

    private fun handleGoogleSignInResult(account: GoogleSignInAccount?) {
        try{
            Log.d("MainActivity", "${account?.idToken}")
            //1
            val idToken = account?.idToken

            //signed in successfully, forward credentials to MongoDB realm
            //2
            val googleCredentials = Credentials.google(idToken, GoogleAuthType.ID_TOKEN)
            //3
            app.loginAsync(googleCredentials){
                if(it.isSuccess){
                    Log.d("MainActivity", "Successfully authenticated using Google OAuth")
                    //4
                    startActivity(Intent(this, SignedInActivity::class.java))
                } else {
                    Log.d("MainActivity", "Failed to Log in to MongoDB Realm: ${it.error.errorMessage}")
                }
            }
        } catch(exception: ApiException){
            Log.d("MainActivity",  exception.printStackTrace().toString())
        }
    }
}