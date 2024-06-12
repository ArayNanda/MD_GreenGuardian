package com.example.greenguardian

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mAuth: FirebaseAuth

    private lateinit var dropzone: ConstraintLayout
    private lateinit var imagePreview: ImageView
    private lateinit var cameraIcon: ImageView
    private lateinit var textView: TextView
    private lateinit var removeImageIcon: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dropzone = findViewById(R.id.dropzone)
        imagePreview = findViewById(R.id.imagePreview)
        cameraIcon = findViewById(R.id.cameraIcon)
        textView = findViewById(R.id.textView)
        removeImageIcon = findViewById(R.id.removeImageIcon)

        dropzone.setOnClickListener {
            selectImageFromGallery()
        }

        removeImageIcon.setOnClickListener {
            removeImage()
        }

        mAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        val textView = findViewById<TextView>(R.id.name)

        val auth = Firebase.auth
        val user = auth.currentUser

        if (user != null) {
            val userName = user.displayName
            textView.text = "Hi, " + userName+ "  \uD83D\uDC4B"
        } else {
            // Handle the case where the user is not signed in
        }

        val sign_out_button = findViewById<Button>(R.id.logout_button)
        sign_out_button.setOnClickListener {
            signOutAndStartSignInActivity()
        }

    }

    private fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            val imageUri: Uri? = data?.data
            imageUri?.let {
                displayImage(it)
            }
        }
    }

    private fun displayImage(imageUri: Uri) {
        imagePreview.setImageURI(imageUri)
        imagePreview.visibility = ImageView.VISIBLE
        cameraIcon.visibility = ImageView.GONE
        textView.visibility = ImageView.GONE
        removeImageIcon.visibility = ImageView.VISIBLE
    }
    private fun removeImage() {
        imagePreview.visibility = ImageView.GONE
        cameraIcon.visibility = ImageView.VISIBLE
        textView.visibility = ImageView.VISIBLE
        removeImageIcon.visibility = ImageView.INVISIBLE
    }


    private fun signOutAndStartSignInActivity() {
        mAuth.signOut()

        mGoogleSignInClient.signOut().addOnCompleteListener(this) {
            // Optional: Update UI or show a message to the user
            val intent = Intent(this@MainActivity, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}