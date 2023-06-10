package org.tmurakam.presentationtimer

import android.app.Activity
import android.os.Bundle
import android.content.pm.PackageManager
import android.content.Intent
import android.content.pm.PackageInfo
import android.net.Uri
import android.os.Build
import android.view.View
import org.tmurakam.presentationtimer.databinding.InfoBinding

class InfoActivity : Activity() {
    private lateinit var binding: InfoBinding

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = InfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.TextAppName.setText(R.string.app_name)
        binding.TextVersion.text = String.format("Version %s", getVersion())
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