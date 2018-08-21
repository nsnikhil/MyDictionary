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

package com.nsnik.nrs.mydictionary.util

import androidx.lifecycle.LiveData
import com.nsnik.nrs.mydictionary.dagger.scopes.ApplicationScope
import com.nsnik.nrs.mydictionary.model.DictionaryDatabase
import com.nsnik.nrs.mydictionary.model.DictionaryEntity
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

@ApplicationScope
class DbUtil @Inject constructor(private val dictionaryDatabase: DictionaryDatabase) {

    fun getWordList(): LiveData<List<DictionaryEntity>> {
        return dictionaryDatabase.dictionaryDao.getWordList()
    }

    fun insertWords(dictionaryEntity: List<DictionaryEntity>) {
        val single = Single.fromCallable { dictionaryDatabase.dictionaryDao.insertWords(dictionaryEntity) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        single.subscribe(object : SingleObserver<LongArray> {
            override fun onSuccess(t: LongArray) {
                t.forEach {
                    Timber.d(it.toString())
                }
            }

            override fun onSubscribe(d: Disposable) {

            }

            override fun onError(e: Throwable) {
                Timber.d(e)
            }

        })
    }

    fun updateWords(dictionaryEntity: List<DictionaryEntity>) {
        val single = Single.fromCallable { dictionaryDatabase.dictionaryDao.updateWords(dictionaryEntity) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        single.subscribe(object : SingleObserver<Int> {
            override fun onSuccess(t: Int) {
                Timber.d(t.toString())
            }

            override fun onSubscribe(d: Disposable) {

            }

            override fun onError(e: Throwable) {
                Timber.d(e)
            }
        })
    }

    fun deleteWord(dictionaryEntity: List<DictionaryEntity>) {
        val completable = Completable.fromCallable { dictionaryDatabase.dictionaryDao.deleteWord(dictionaryEntity) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        completable.subscribe(object : CompletableObserver {
            override fun onComplete() {
                Timber.d("Deletion successful")
            }

            override fun onSubscribe(d: Disposable) {

            }

            override fun onError(e: Throwable) {
                Timber.d(e)
            }

        })
    }

    fun deleteObsoleteData(ids: List<Int>) {
        val completable = Completable.fromCallable { dictionaryDatabase.dictionaryDao.deleteObsoleteData(ids) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        completable.subscribe(object : CompletableObserver {
            override fun onComplete() {
                Timber.d("Deletion successful")
            }

            override fun onSubscribe(d: Disposable) {

            }

            override fun onError(e: Throwable) {
                Timber.d(e)
            }

        })
    }

}