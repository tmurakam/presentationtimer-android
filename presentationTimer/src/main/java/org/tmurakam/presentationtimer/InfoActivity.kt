package org.tmurakam.presentationtimer

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import android.content.pm.PackageManager
import android.content.Intent
import android.content.pm.PackageInfo
import android.net.Uri
import android.os.Build
import android.view.View

class InfoActivity : Activity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.info)

        val textAppName: TextView = findViewById(R.id.TextAppName)
        textAppName.setText(R.string.app_name)

        val textVersion: TextView = findViewById(R.id.TextVersion)
        textVersion.text = String.format("Version %s", getVersion())
    }

    private fun getVersion(): String {
        var version = "?"
        try {
            val pkgname = this.packageName
            var pi: PackageInfo
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pi = packageManager.getPackageInfo(pkgname, PackageManager.PackageInfoFlags.of(0))
            } else {
                pi = packageManager.getPackageInfo(pkgname, 0)
            }
            version = pi.versionName
        } catch (e: PackageManager.NameNotFoundException) {
        }
        return version
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