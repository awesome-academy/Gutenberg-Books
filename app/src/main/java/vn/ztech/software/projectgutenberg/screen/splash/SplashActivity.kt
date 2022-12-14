package vn.ztech.software.projectgutenberg.screen.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import vn.ztech.software.projectgutenberg.screen.MainActivity
import vn.ztech.software.projectgutenberg.utils.Constant

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Handler(mainLooper).postDelayed(Runnable {
            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, Constant.SPLASH_DELAY_TIME)
    }
}
