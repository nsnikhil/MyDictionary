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

package com.nsnik.nrs.mydictionary.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.nsnik.nrs.mydictionary.MyApplication
import com.nsnik.nrs.mydictionary.model.DictionaryEntity
import com.nsnik.nrs.mydictionary.util.DbUtil
import com.nsnik.nrs.mydictionary.util.NetworkUtil

class DictionaryViewModel(application: Application) : AndroidViewModel(application) {

    private val dbUtil: DbUtil = (application as MyApplication).dbUtil
    private val netwrokUtil: NetworkUtil = (application as MyApplication).networkUtil

    fun getLocalList(): LiveData<List<DictionaryEntity>> {
        return dbUtil.getWordList()
    }

    fun insertLocal(dictionaryEntity: List<DictionaryEntity>) {
        dbUtil.insertWords(dictionaryEntity)
    }

    fun updateLocal(dictionaryEntity: List<DictionaryEntity>) {
        dbUtil.updateWords(dictionaryEntity)
    }

    fun deleteLocal(dictionaryEntity: List<DictionaryEntity>) {
        dbUtil.deleteWord(dictionaryEntity)
    }

    fun getRemoteList() {
        netwrokUtil.getWordList()
    }

    fun insertRemote(vararg dictionaryEntity: DictionaryEntity) {
        dictionaryEntity.forEach { netwrokUtil.insertWord(it) }
    }

    fun updateRemote(vararg dictionaryEntity: DictionaryEntity) {
        dictionaryEntity.forEach { netwrokUtil.updateWord(it) }
    }

    fun deleteRemote(vararg id: Int) {
        id.forEach { netwrokUtil.deleteWord(it) }
    }

}