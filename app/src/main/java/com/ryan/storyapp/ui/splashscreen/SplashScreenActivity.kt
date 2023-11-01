package com.ryan.storyapp.ui.splashscreen

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.ryan.storyapp.R
import com.ryan.storyapp.databinding.ActivitySplashScreenBinding
import com.ryan.storyapp.ui.main.MainActivity


@Suppress("DEPRECATION")
@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    private val SPLASH_DELAY = 5000L
    private val animationDelay = 3000L
    private val animation2Delay = 400L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        Handler().postDelayed({
            startLottieAnimation()
        }, animationDelay)


        Handler().postDelayed({
            val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, SPLASH_DELAY)
    }

    private fun startLottieAnimation() {
        binding.lottieAnimationView.animate()
            .translationX(-200f)
            .setDuration(1000)
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(p0: Animator) {
                    Handler().postDelayed({
                        startTextTypingAnimation()
                        startFadeText()
                    }, animation2Delay)
                }

                override fun onAnimationEnd(p0: Animator) {
                }

                override fun onAnimationCancel(p0: Animator) {}

                override fun onAnimationRepeat(p0: Animator) {}
            })
    }

    private fun startTextTypingAnimation() {
        binding.textView.visibility = View.VISIBLE
        val text = getString(R.string.splash)
        val animationDuration = 120
        val textView = binding.textView
        val handler = Handler(Looper.getMainLooper())
        val translationXStep = 30

        val reversedText = text.reversed()
        val endChar = reversedText.length - 1
        var currentIndex = -1
        var currentTranslationX = 0

        fun animateNextChar() {
            if (currentIndex < endChar) {
                currentIndex++
                textView.text = reversedText.substring(0, currentIndex + 1).reversed()

                if (currentIndex < endChar) {
                    currentTranslationX += translationXStep
                    textView.translationX = currentTranslationX.toFloat()
                } else {
                    textView.translationX = currentTranslationX.toFloat()
                }

                handler.postDelayed({
                    animateNextChar()
                }, animationDuration.toLong())
            }
        }

        animateNextChar()
    }

    @SuppressLint("ResourceType")
    private fun startFadeText() {
        val fadeAnimation = AnimationUtils.loadAnimation(this, R.animator.fade_in)
        val poweredByTextView = binding.poweredBy

        poweredByTextView.startAnimation(fadeAnimation)
        poweredByTextView.visibility = View.VISIBLE
    }
}