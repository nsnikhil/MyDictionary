/*
 *     Notes  Copyright (C) 2018  Nikhil Soni
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

package com.nsnik.nrs.mydictionary.dagger.modules


import android.content.Context
import androidx.room.Room
import com.nsnik.nrs.mydictionary.dagger.qualifiers.ApplicationQualifier
import com.nsnik.nrs.mydictionary.dagger.qualifiers.DatabaseName
import com.nsnik.nrs.mydictionary.dagger.scopes.ApplicationScope
import com.nsnik.nrs.mydictionary.model.DictionaryDatabase
import dagger.Module
import dagger.Provides

@Module(includes = [(ContextModule::class)])
class DatabaseModule {

    internal val databaseName: String
        @Provides
        @DatabaseName
        @ApplicationScope
        get() = DATABASE_NAME

    @Provides
    @ApplicationScope
    internal fun getNoteDatabase(@ApplicationQualifier context: Context, @DatabaseName @ApplicationScope databaseName: String): DictionaryDatabase {
        return Room.databaseBuilder(context, DictionaryDatabase::class.java, databaseName).build()
    }

    companion object {
        private const val DATABASE_NAME = "notesDb"
        private const val DEFAULT_FOLDER_NAME = "noFolder"
    }

}