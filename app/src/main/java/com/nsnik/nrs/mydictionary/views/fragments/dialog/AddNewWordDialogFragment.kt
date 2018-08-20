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


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import androidx.work.*
import com.jakewharton.rxbinding2.view.RxView
import com.nsnik.nrs.mydictionary.R
import com.nsnik.nrs.mydictionary.model.DictionaryEntity
import com.nsnik.nrs.mydictionary.viewModel.DictionaryViewModel
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_add_new_word_dialog.*
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*


class AddNewWordDialogFragment : DialogFragment() {

    private lateinit var dictionaryViewModel: DictionaryViewModel
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_new_word_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
        listeners()
    }

    private fun initialize() {
        dictionaryViewModel = ViewModelProviders.of(this).get(DictionaryViewModel::class.java)
    }

    private fun listeners() {
        compositeDisposable.addAll(
                RxView.clicks(newEntryAdd).subscribe { insertLocalAndRemoteStub(getWord()) },
                RxView.clicks(newEntryCancel).subscribe { dismiss() }
        )
    }

    private fun insertLocalAndRemoteStub(dictionaryEntity: DictionaryEntity) {
        dictionaryViewModel.insertLocal(listOf(dictionaryEntity))
        dictionaryViewModel.insertRemote(dictionaryEntity)
        dismiss()
    }

    private fun insertLocalAndRemote(dictionaryEntity: DictionaryEntity) {
        val workManager: WorkManager = WorkManager.getInstance()

        val constraint = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

        val insertLocalWork = OneTimeWorkRequest.Builder(InsertWordLocalWork::class.java).build()

        val insertRemoteWork = OneTimeWorkRequest.Builder(InsertWordRemoteWork::class.java)
                .setConstraints(constraint)
                .build()

        workManager.beginWith(insertLocalWork)
                .then(insertRemoteWork)
                .enqueue()

        val status = workManager.getStatusById(insertRemoteWork.id)
                .observe(this, androidx.lifecycle.Observer {
                    if (it != null && it.state.isFinished) {
                        //INSERT INTO REMOTE DATABASE COMPLETE
                    }
                })
    }

    private fun getWord(): DictionaryEntity {
        val dictionaryEntity = DictionaryEntity()
        dictionaryEntity.word = newEntryWord.text.toString()
        dictionaryEntity.meaning = newEntryMeaning.text.toString()
        dictionaryEntity.dateModified = getDate()
        return dictionaryEntity
    }


    private fun getDate(): Date {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSSX", Locale.ENGLISH)
        val stringDate = dateFormat.format(Calendar.getInstance().time)
        Timber.d(stringDate)
        Timber.d(dateFormat.parse(stringDate).toString())
        return dateFormat.parse(stringDate)
    }

    private fun cleanUp() {
        compositeDisposable.clear()
        compositeDisposable.dispose()
    }

    override fun onResume() {
        super.onResume()
        val params = dialog.window!!.attributes
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog.window!!.attributes = params as android.view.WindowManager.LayoutParams
    }

    override fun onDestroy() {
        super.onDestroy()
        cleanUp()
    }

    inner class InsertWordLocalWork : Worker() {

        override fun doWork(): Worker.Result {
            dictionaryViewModel.insertLocal(listOf(getWord()))
            return Worker.Result.SUCCESS
        }

    }

    inner class InsertWordRemoteWork : Worker() {

        override fun doWork(): Result {
            dictionaryViewModel.insertRemote(getWord())
            return Result.SUCCESS
        }

    }

}
