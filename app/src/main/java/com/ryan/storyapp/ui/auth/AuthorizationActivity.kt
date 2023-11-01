package com.ryan.storyapp.ui.auth

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ryan.storyapp.R
import com.ryan.storyapp.data.model.ResultViewModel
import com.ryan.storyapp.databinding.ActivityAuthorizationBinding
import com.ryan.storyapp.repository.LoginRepository
import com.ryan.storyapp.repository.RegisterRepository
import com.ryan.storyapp.ui.main.MainActivity
import com.ryan.storyapp.viewmodel.LoginViewModel
import com.ryan.storyapp.viewmodel.LoginViewModelFactory
import com.ryan.storyapp.viewmodel.RegisterViewModel
import com.ryan.storyapp.viewmodel.RegisterViewModelFactory

class AuthorizationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthorizationBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthorizationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        vieModelLogin()
        viewModelRegister()

        val edRegisterName = binding.edRegisterName
        val edRegisterEmail = binding.edRegisterEmail
        val edRegisterPassword = binding.edRegisterPassword

        binding.buttonLogin.setOnClickListener {
            hideSoftKeyboard(it)
            binding.buttonLogin.visibility = View.INVISIBLE
            binding.loadingProgressBar1.visibility = View.VISIBLE

            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()
            val valid = checkValidation(email, password)

            if (valid) {
                loginUser(email, password)
            } else {
                resetLoginButton()
            }
        }

        binding.register.setOnClickListener {
            hideSoftKeyboard(it)
            binding.register.visibility = View.INVISIBLE
            binding.loadingProgressBar.visibility = View.VISIBLE

            val name = edRegisterName.text.toString()
            val email = edRegisterEmail.text.toString()
            val password = edRegisterPassword.text.toString()
            val valid = checkValidation(email, password)

            if (valid) {
                registerUser(name, email, password)
            } else {
                resetRegisterButton()
            }
        }

        binding.linkRegister.setOnClickListener {
            flipLayout(binding.loginlayout, binding.registerlayout)
        }

        binding.linkToLogin.setOnClickListener {
            flipLayout(binding.registerlayout, binding.loginlayout)
        }

        binding.edRegisterEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Nothing to do before text changes.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val generatedId = generateIdFromEmail(s.toString())
                edRegisterName.setText(generatedId)
            }

            override fun afterTextChanged(s: Editable?) {
                // Nothing to do after text changes.
            }
        })
    }

    private fun checkValidation(email: String, password: String): Boolean {
        if (email.isNotEmpty() && password.isNotEmpty() && isValidEmail(email)) {
            return if (password.length >= 8) {
                true
            } else {
                showToast(getString(R.string.invalid_password))
                false
            }
        } else {
            return if (isValidEmail(email)) {
                showToast(getString(R.string.invalid_email))
                false
            } else {
                showToast(getString(R.string.required))
                false
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun loginUser(email: String, password: String) {
        loginViewModel.loginUser(email, password)

        loginViewModel.loginResult.observe(this) { result ->
            if (result is ResultViewModel.Success) {
                val apiKey = result.data.loginResult?.token.toString()
                loginViewModel.saveApiKey(apiKey)
                navigateToMainActivity()
            } else if (result is ResultViewModel.Error) {
                showToast(result.message)
                binding.edLoginPassword.text = null
                loginViewModel.resetResult()
                resetLoginButton()
            }
        }
    }

    private fun registerUser(name: String, email: String, password: String) {
        registerViewModel.registerUser(name, email, password)

        registerViewModel.registerResult.observe(this) { result ->
            if (result is ResultViewModel.Success) {
                showToast(getString(R.string.regis_success))
                resetRegisterView()
            } else if (result is ResultViewModel.Error) {
                showToast(result.message)
                binding.edRegisterName.text = null
                binding.edRegisterEmail.text = null
                binding.edRegisterPassword.text = null
                registerViewModel.resetResult()
                resetRegisterButton()
            }
        }
    }

    private fun hideSoftKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun resetLoginButton() {
        binding.buttonLogin.visibility = View.VISIBLE
        binding.loadingProgressBar1.visibility = View.INVISIBLE
    }

    private fun resetRegisterButton() {
        binding.register.visibility = View.VISIBLE
        binding.loadingProgressBar.visibility = View.GONE
    }

    private fun resetRegisterView() {
        resetRegisterButton()
        val flipAnimatorOut =
            AnimatorInflater.loadAnimator(this, R.animator.flip_in) as ObjectAnimator
        flipAnimatorOut.target = binding.registerlayout
        flipAnimatorOut.interpolator = AccelerateDecelerateInterpolator()
        flipAnimatorOut.duration = 500

        flipAnimatorOut.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                binding.registerlayout.visibility = View.GONE
                binding.loginlayout.visibility = View.VISIBLE
                binding.loginlayout.rotationY = 0f
            }
        })

        flipAnimatorOut.start()
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun flipLayout(layoutIn: View, layoutOut: View) {
        val flipAnimatorOut =
            AnimatorInflater.loadAnimator(this, R.animator.flip_in) as ObjectAnimator
        flipAnimatorOut.target = layoutIn
        flipAnimatorOut.interpolator = AccelerateDecelerateInterpolator()
        flipAnimatorOut.duration = 500

        flipAnimatorOut.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                layoutIn.visibility = View.GONE
                layoutOut.visibility = View.VISIBLE
            }
        })

        flipAnimatorOut.start()
    }

    private fun generateIdFromEmail(email: String): String {
        val atIndex = email.indexOf("@")
        return if (atIndex >= 0) {
            email.substring(0, atIndex)
        } else {
            email
        }
    }

    private fun vieModelLogin() {
        val repositoryLogin = LoginRepository(this)
        val viewModelFactoryLogin = LoginViewModelFactory(repositoryLogin)
        loginViewModel = ViewModelProvider(this, viewModelFactoryLogin)[LoginViewModel::class.java]
    }

    private fun viewModelRegister() {
        val repositoryRegister = RegisterRepository(this)
        val viewModelFactoryRegister = RegisterViewModelFactory(repositoryRegister)
        registerViewModel =
            ViewModelProvider(this, viewModelFactoryRegister)[RegisterViewModel::class.java]
    }
}