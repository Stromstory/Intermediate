package com.dicoding.mysubmissionintermediate.ui.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.mysubmissionintermediate.data.result.ResultCode
import com.dicoding.mysubmissionintermediate.ui.FactoryViewModel
import com.dicoding.mysubmissionintermediate.R
import com.dicoding.mysubmissionintermediate.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModelRegister by viewModels<ViewModelRegister> {
        FactoryViewModel.getInstance(application)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()
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
        binding.btnRegister.setOnClickListener {
            val name = binding.edtName.text.toString()
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()

            if (isFormValid()) {
                viewModelRegister.register(name, email, password).observe(this) { result ->
                    when (result) {
                        is ResultCode.Error -> showErrorDialog(result.error)
                        ResultCode.Loading -> showLoading(true)
                        is ResultCode.Success -> showSuccessDialog(email)
                    }
                }
            } else {
                Toast.makeText(this, R.string.form_message, Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun isFormValid(): Boolean {
        return !(binding.edtEmail.error != null || binding.edtPassword.error != null)
    }
    private fun showSuccessDialog(email: String) {
        showLoading(false)
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.register_success))
            setMessage(getString(R.string.account_created, email))
            setPositiveButton(getString(R.string.next)) { _, _ ->
                finish()
            }
            create()
            show()
        }
    }

    private fun showErrorDialog(errorMessage: String) {
        showLoading(false)
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.account_failed))
            setMessage(errorMessage)
            setPositiveButton(R.string.done) { _, _ ->
            }
            create()
            show()
        }
    }


    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnRegister.isEnabled = !isLoading
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()


        val nameEdit = ObjectAnimator.ofFloat(binding.edtName, View.ALPHA, 1f).setDuration(100)
        val emailEdit = ObjectAnimator.ofFloat(binding.edtEmail, View.ALPHA, 1f).setDuration(100)
        val passwordEdit = ObjectAnimator.ofFloat(binding.edtPassword, View.ALPHA, 1f).setDuration(100)
        val register = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(nameEdit, emailEdit, passwordEdit, register)
            start()
        }
    }

}