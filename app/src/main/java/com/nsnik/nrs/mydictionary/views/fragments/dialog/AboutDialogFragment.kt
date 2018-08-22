/*
 *     MyDictionary  Copyright (C) 2018  Nikhil Soni
 *     This program comes with ABSOLUTELY NO WARRANTY; for details type `show w'.
 *     This is free software, and you are welcome to redistribute it
 *     under certain conditions; type `show c' for details.
 *
 * The hypothetical commands `show w' and `show c' should show the appropriate
 * parts of the General Public License.  Of course, your program's commands
 * might be different; for a GUI interface, you would use an "about box".
 *
 *   You should also get your employer (if you work as a programmer) or school,
 * if any, to sign a "copyright disclaimer" for the program, if necessary.
 * For more information on this, and how to apply and follow the GNU GPL, see
 * <http://www.gnu.org/licenses/>.
 *
 *   The GNU General Public License does not permit incorporating your program
 * into proprietary programs.  If your program is a subroutine library, you
 * may consider it more useful to permit linking proprietary applications with
 * the library.  If this is what you want to do, use the GNU Lesser General
 * Public License instead of this License.  But first, please read
 * <http://www.gnu.org/philosophy/why-not-lgpl.html>.
 */

package com.nsnik.nrs.mydictionary.views.fragments.dialog


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.jakewharton.rxbinding2.view.RxView
import com.nsnik.nrs.mydictionary.R
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_about_dialog.*

class AboutDialogFragment : DialogFragment() {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_about_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }

    private fun initialize() {
        aboutSourceCode.movementMethod = LinkMovementMethod.getInstance()
        aboutNikhil.movementMethod = LinkMovementMethod.getInstance()
        compositeDisposable.addAll(
                RxView.clicks(aboutLibraries).subscribe { startActivity(Intent(activity, OssLicensesMenuActivity::class.java)) },
                RxView.clicks(aboutLicense).subscribe { chromeCustomTab(activity?.resources?.getString(R.string.aboutLicenseUrl)) }
        )
    }

    private fun chromeCustomTab(url: String?) {
        val customTabsIntent = CustomTabsIntent.Builder()
                .addDefaultShareMenuItem()
                .setToolbarColor(ContextCompat.getColor(activity!!, R.color.colorPrimary))
                .setSecondaryToolbarColor(ContextCompat.getColor(activity!!, R.color.colorPrimaryDark))
                .setExitAnimations(activity!!, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .setShowTitle(true)
                .build()
        if (customTabsIntent.intent.resolveActivity(activity?.packageManager!!) != null) customTabsIntent.launchUrl(activity, Uri.parse(url))
        else Toast.makeText(activity, activity?.resources?.getString(R.string.errorNoActivityToHandleChrome), Toast.LENGTH_LONG).show()
    }

    override fun onResume() {
        super.onResume()
        val params = dialog.window!!.attributes
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog.window!!.attributes = params as android.view.WindowManager.LayoutParams
    }

    private fun cleanUp() {
        compositeDisposable.clear()
        compositeDisposable.dispose()
    }

    override fun onDestroy() {
        super.onDestroy()
        cleanUp()
    }

}
