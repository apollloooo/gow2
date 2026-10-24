package com.example.gow2launcher

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.Toast
import android.app.Activity

class MainActivity : Activity() {
    private lateinit var web: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        web = WebView(this)
        web.settings.javaScriptEnabled = true
        web.settings.domStorageEnabled = true
        web.addJavascriptInterface(Bridge(), "Android")
        web.loadUrl("file:///android_asset/index.html")
        setContentView(web)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (web.canGoBack()) web.goBack() else super.onBackPressed()
    }

    inner class Bridge {
        // Runs when one of your custom buttons is tapped.
        @JavascriptInterface
        fun launch(pkg: String, uri: String) {
            runOnUiThread {
                try {
                    val intent = if (uri.isNotBlank()) {
                        Intent(Intent.ACTION_VIEW, Uri.parse(uri)).apply {
                            if (pkg.isNotBlank()) setPackage(pkg)
                        }
                    } else {
                        packageManager.getLaunchIntentForPackage(pkg)
                    }
                    if (intent == null) {
                        Toast.makeText(this@MainActivity, "App not found: $pkg", Toast.LENGTH_LONG).show()
                    } else {
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, "Could not open: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
