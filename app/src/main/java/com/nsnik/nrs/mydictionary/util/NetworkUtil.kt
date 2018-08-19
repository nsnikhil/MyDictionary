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

package com.nsnik.nrs.mydictionary.util

import com.nsnik.nrs.mydictionary.dagger.scopes.ApplicationScope
import com.nsnik.nrs.mydictionary.model.DictionaryEntity
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import timber.log.Timber
import javax.inject.Inject

@ApplicationScope
class NetworkUtil @Inject constructor(private val retrofit: Retrofit) {

    fun getWordList() {
        retrofit.create(DictionaryNetwrokApi::class.java)
                .getUserList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<List<DictionaryEntity>> {
                    override fun onSuccess(t: List<DictionaryEntity>) {

                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {
                        Timber.d(e)
                    }
                })
    }

    fun insertWord(dictionaryEntity: DictionaryEntity) {
        retrofit.create(DictionaryNetwrokApi::class.java)
                .addWord(dictionaryEntity.word, dictionaryEntity.meaning, dictionaryEntity.dateModified)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<String> {
                    override fun onSuccess(t: String) {
                        Timber.d(t)
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {
                        Timber.d(e)
                    }
                })
    }

    fun updateWord(dictionaryEntity: DictionaryEntity) {
        retrofit.create(DictionaryNetwrokApi::class.java)
                .updateWord(dictionaryEntity.id, dictionaryEntity.word, dictionaryEntity.meaning, dictionaryEntity.dateModified)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<String> {
                    override fun onSuccess(t: String) {
                        Timber.d(t)
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {
                        Timber.d(e)
                    }
                })
    }

    fun deleteWord(id: Int) {
        retrofit.create(DictionaryNetwrokApi::class.java)
                .deleteWord(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<String> {
                    override fun onSuccess(t: String) {
                        Timber.d(t)
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {
                        Timber.d(e)
                    }
                })
    }

}