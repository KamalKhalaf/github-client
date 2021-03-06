package com.jraska.github.client.settings

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jraska.console.Console

internal class ConsoleActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(Console(this))
  }

  companion object {
    fun start(inActivity: Activity) {
      val intent = Intent(inActivity, ConsoleActivity::class.java)
      inActivity.startActivity(intent)
    }
  }
}
