package com.dicoding.mysubmissionintermediate.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.dicoding.mysubmissionintermediate.ui.FactoryViewModel
import com.dicoding.mysubmissionintermediate.databinding.ActivityLoginBinding
import com.dicoding.mysubmissionintermediate.data.result.ResultCode
import com.dicoding.mysubmissionintermediate.ui.main.MainActivity
import com.dicoding.mysubmissionintermediate.ui.register.RegisterActivity
import com.dicoding.mysubmissionintermediate.R
import com.dicoding.mysubmissionintermediate.data.preference.UserModel

class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<ViewModelLogin> {
        FactoryViewModel.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()
        val createAccountTextView: TextView = findViewById(R.id.tvCreateAccount)
        val text = "Belum punya akun? Buat akun"

        val spannableString = SpannableString(text)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true
                ds.color = ContextCompat.getColor(this@LoginActivity, R.color.teal_200) // Warna teks
            }
        }

        val startIndex = text.indexOf("Buat akun")
        val endIndex = startIndex + "Buat akun".length
        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        createAccountTextView.text = spannableString
        createAccountTextView.movementMethod = LinkMovementMethod.getInstance()
    }
    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }
    private fun setupAction() {
        binding.btnLogin.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()

            viewModel.login(email, password).observe(this) { loginResponse ->
                when (loginResponse) {
                    is ResultCode.Success -> {
                        val loginResult = loginResponse.data.loginResult
                        showSuccessDialog(loginResult.name)
                        val userModel = UserModel(
                            email = email,
                            token = loginResult.token,
                            isLogin = true
                        )
                        viewModel.saveSession(userModel)
                    }
                    is ResultCode.Error -> {
                        showErrorDialog(loginResponse.error)
                    }
                    is ResultCode.Loading -> {
                        showLoading(true)
                    }
                }
            }
        }
    }
    private fun showSuccessDialog(name: String) {
        showLoading(false)
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.success))
            setMessage(getString(R.string.pop_up_success,name))
            setPositiveButton(getString(R.string.next)) { _, _ ->
                val intent = Intent(context, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            create()
            show()
        }
    }
    private fun showErrorDialog(errorMessage: String) {
        showLoading(false)
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.login_error))
            setMessage(errorMessage)
            setPositiveButton(R.string.done) { _, _ ->
            }
            create()
            show()
        }
    }
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !isLoading
    }
    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()


        val email = ObjectAnimator.ofFloat(binding.LoginTextView, View.ALPHA, 1f).setDuration(100)
        val login = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially( email,  login)
            start()
        }
    }
}