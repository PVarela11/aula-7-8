package pt.atp.bobi.presentation.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.common.SignInButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import pt.atp.bobi.EXTRA_USERNAME
import pt.atp.bobi.R
import pt.atp.bobi.presentation.LoginViewModel

private const val REQUEST_SIGN_IN = 12345

class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setup()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val username = FirebaseAuth.getInstance().currentUser
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra(EXTRA_USERNAME, username)

                startActivity(intent)
                finish()
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }

    private fun setup() {
        findViewById<TextInputEditText>(R.id.tet_username).setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                validateCredentialsAndRedirect()
            }
            true
        }

        findViewById<TextInputEditText>(R.id.tet_password).setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                validateCredentialsAndRedirect()
            }
            true
        }

        findViewById<Button>(R.id.btn_authenticate).setOnClickListener {
            validateCredentialsAndRedirect()
        }

        findViewById<SignInButton>(R.id.sign_in).setOnClickListener {
            val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())

            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build(),
                REQUEST_SIGN_IN)
        }

        viewModel.loginResultLiveData.observe(this){ loginResult ->
            if (!loginResult) {
                findViewById<TextView>(R.id.tv_error).text = getString(R.string.error_credentials_mismatch)
            }else{
                val username = findViewById<TextInputEditText>(R.id.tet_username).text.toString()

                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra(EXTRA_USERNAME, username)

                startActivity(intent)
                finish()
            }
        }
    }

    private fun validateCredentialsAndRedirect() {

        val username = findViewById<TextInputEditText>(R.id.tet_username).text.toString()
        if (username.isEmpty()) {
            findViewById<TextView>(R.id.tv_error).text = getString(R.string.error_credentials_empty_username)
            return
        }

        val password = findViewById<TextInputEditText>(R.id.tet_password).text.toString()
        if (password.isEmpty()) {
            findViewById<TextView>(R.id.tv_error).text = getString(R.string.error_credentials_empty_password)
            return
        }

        viewModel.areCredentialsValid(username, password)

    }
}