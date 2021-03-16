package com.shorbgy.petsshelter.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*
import com.shorbgy.petsshelter.R
import com.shorbgy.petsshelter.databinding.ActivityLoginBinding
import com.shorbgy.petsshelter.ui.home_activity.HomeActivity
import com.shorbgy.petsshelter.ui.register_activity.RegisterActivity


class LoginActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "LoginActivity"
        private const val EMAIL = "email"
        private const val PUBIC_PROFILE = "public_profile"
        private const val RC_SIGN_IN = 123
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var callbackManager: CallbackManager
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var  binding : ActivityLoginBinding


    private lateinit var userDatabaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        userDatabaseReference = FirebaseDatabase.getInstance().getReference("Users")
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        createGoogleRequest()

        if (currentUser!=null){
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        callbackManager = CallbackManager.Factory.create()
        binding.facebookSignInButton.setReadPermissions(listOf(EMAIL, PUBIC_PROFILE))

        binding.buttonSignup.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.buttonSignin.setOnClickListener {
            when {
                TextUtils.isEmpty(binding.etUsername.text) -> {
                    Toast.makeText(this, "Please Enter Your Email", Toast.LENGTH_SHORT).show()
                }
                TextUtils.isEmpty(binding.etPassword.text) -> {
                    Toast.makeText(this, "Please Enter Your Password", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    loginWithEmailAndPassword(binding.etUsername.text.toString(), binding.etPassword.text.toString())
                }
            }
        }

        binding.facebookSignInButton.registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    Log.d(TAG, "facebook:onSuccess:$loginResult")
                    handleFacebookAccessToken(loginResult.accessToken)
                }

                override fun onCancel() {
                    Log.d(TAG, "facebook:onCancel")
                }

                override fun onError(error: FacebookException) {
                    Log.d(TAG, "facebook:onError: ${error.message}")
                }
            })

        binding.googleSignInButton.setOnClickListener {
            googleSignIn()
        }

    }

    private fun createGoogleRequest(){
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun googleSignIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        callbackManager.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                // ...
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    createUser()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    createUser()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                }

            }
    }

    private fun createUser(){
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userMap: HashMap<String, Any?> = HashMap()
        userMap["uid"] = currentUser?.uid
        userMap["email"] = currentUser?.email
        userMap["image_url"] = currentUser?.photoUrl.toString()
        userMap["phone"] = "None"
        userMap["address"] = "None"
        userMap["username"] = currentUser?.email?.replaceAfter("@", "")
            ?.replace("@", "")
        userDatabaseReference.child(currentUser!!.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d(TAG, "onDataChange: ${snapshot.hasChildren()}")
                    if (!snapshot.hasChildren()) {
                        userDatabaseReference.child(currentUser.uid)
                            .setValue(userMap).addOnCompleteListener {
                                if (it.isSuccessful) {
                                    val intent =
                                        Intent(this@LoginActivity, HomeActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                } else {
                                    Log.d(TAG, "onDataChange: ${it.exception?.message}")
                                }
                            }

                    }else{
                        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(TAG, "onCancelled: ${error.message}")
                }
            })


    }

    private fun loginWithEmailAndPassword(email: String, password: String){

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setView(R.layout.progress)
        val dialog: Dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener{

            dialog.dismiss()

            if (it.isSuccessful){
                val intent = Intent(this, HomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }else{
                Toast.makeText(this, it.exception?.message, Toast.LENGTH_LONG).show()
            }
        }
    }
}