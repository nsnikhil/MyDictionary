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

package com.nsnik.nrs.mydictionary

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.StrictMode
import androidx.appcompat.app.AppCompatDelegate
import com.facebook.stetho.Stetho
import com.github.moduth.blockcanary.BlockCanary
import com.nsnik.nrs.mydictionary.dagger.components.DaggerDatabaseComponent
import com.nsnik.nrs.mydictionary.dagger.components.DaggerNetworkComponent
import com.nsnik.nrs.mydictionary.dagger.components.DaggerSharedPrefComponent
import com.nsnik.nrs.mydictionary.dagger.modules.ContextModule
import com.nsnik.nrs.mydictionary.util.AppBlockCanaryContext
import com.nsnik.nrs.mydictionary.util.DbUtil
import com.nsnik.nrs.mydictionary.util.NetworkUtil
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
import timber.log.Timber

class MyApplication : Application() {

    companion object {

        init {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        fun getRefWatcher(context: Context): RefWatcher? {
            val application = context.applicationContext as MyApplication
            return application.refWatcher
        }
    }

    private var refWatcher: RefWatcher? = null

    private var contextModule: ContextModule = ContextModule(this)

    lateinit var dbUtil: DbUtil
    lateinit var sharedPreferences: SharedPreferences
    lateinit var networkUtil: NetworkUtil

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
            Timber.plant(object : Timber.DebugTree() {
                override fun createStackElementTag(element: StackTraceElement): String {
                    return super.createStackElementTag(element) + ":" + element.lineNumber
                }
            })
            refWatcher = LeakCanary.install(this)
            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build())
            StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build())
            BlockCanary.install(this, AppBlockCanaryContext()).start()
        }
        if (LeakCanary.isInAnalyzerProcess(this)) return
        moduleSetter()
    }

    private fun moduleSetter() {
        setDatabaseComponent()
        setSharedPrefComponent()
        setNetworkModule()
    }

    private fun setDatabaseComponent() {
        val databaseComponent = DaggerDatabaseComponent.builder().contextModule(contextModule).build()
        dbUtil = databaseComponent.dbUtil
    }

    private fun setSharedPrefComponent() {
        val sharedPrefComponent = DaggerSharedPrefComponent.builder().contextModule(contextModule).build()
        sharedPreferences = sharedPrefComponent.sharedPreferences
    }

    private fun setNetworkModule() {
        val networkComponent = DaggerNetworkComponent.create()
        networkUtil = networkComponent.getNetworkUtil()
    }

}