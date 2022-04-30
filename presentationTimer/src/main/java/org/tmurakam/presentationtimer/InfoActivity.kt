package org.tmurakam.presentationtimer

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import android.content.pm.PackageManager
import android.content.Intent
import android.net.Uri
import android.view.View

class InfoActivity : Activity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.info)

        val textAppName = findViewById<View>(R.id.TextAppName) as TextView
        textAppName.setText(R.string.app_name)

        val textVersion = findViewById<View>(R.id.TextVersion) as TextView
        var version = "?"
        try {
            val pkgname = this.packageName
            val pi = packageManager.getPackageInfo(pkgname, 0)
            version = pi.versionName
        } catch (e: PackageManager.NameNotFoundException) {
        }
        textVersion.text = String.format("Version %s", version)
    }

    fun onClickHelp(v: View?) {
        val url = resources.getString(R.string.help_url)
        openBrowser(url)
    }

    private fun openBrowser(url: String) {
        val i = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(i)
    }

    fun onClickPrivacyPolicy(v: View?) {
        openBrowser("http://apps.tmurakam.org/privacy_policy.html")
    }
}