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


import android.app.Dialog
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.textfield.TextInputEditText
import com.jakewharton.rxbinding2.view.RxView
import com.nsnik.nrs.mydictionary.R
import com.nsnik.nrs.mydictionary.model.DictionaryEntity
import com.nsnik.nrs.mydictionary.viewModel.DictionaryViewModel
import com.twitter.serial.stream.bytebuffer.ByteBufferSerial
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_add_new_word_dialog.*
import java.util.*


class AddNewWordDialogFragment : DialogFragment() {

    private lateinit var dictionaryViewModel: DictionaryViewModel
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var dictionaryEntity: DictionaryEntity? = null
    private lateinit var thisDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        setStyle(DialogFragment.STYLE_NORMAL, R.style.dialogWithTitle)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        thisDialog = super.onCreateDialog(savedInstanceState)
        val title = activity?.resources?.getString(R.string.newEntryDialogTitle)
        thisDialog.setTitle(Html.fromHtml("<font color='#F7AA33'>$title</font>", Html.FROM_HTML_MODE_LEGACY))
        return thisDialog
    }

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
        dictionaryEntity = ByteBufferSerial().fromByteArray(arguments?.getByteArray(activity?.resources?.getString(R.string.bundleDictionaryEntity)), DictionaryEntity.SERIALIZER)
        if (dictionaryEntity != null) setValues()
    }

    private fun setValues() {
        newEntryWord.setText(dictionaryEntity?.word)
        newEntryMeaning.setText(dictionaryEntity?.meaning)
        newEntryAdd.text = activity?.resources?.getString(R.string.update)
        val title = activity?.resources?.getString(R.string.newEntryDialogUpdateTitle)
        thisDialog.setTitle(Html.fromHtml("<font color='#F7AA33'>$title</font>", Html.FROM_HTML_MODE_LEGACY))
    }

    private fun listeners() {
        compositeDisposable.addAll(
                RxView.clicks(newEntryAdd).subscribe {
                    if (isValid()) {
                        if (dictionaryEntity == null) insertLocalAndRemoteStub(getWord(DictionaryEntity()))
                        else updateLocalAndRemoteStub(getWord(dictionaryEntity!!))
                    }
                },
                RxView.clicks(newEntryCancel).subscribe { dismiss() }
        )
    }

    private fun isValid(): Boolean = checkEmpty(newEntryWord, activity?.resources?.getString(R.string.errorNoWord)) &&
            checkEmpty(newEntryMeaning, activity?.resources?.getString(R.string.errorNoMeaning))


    private fun checkEmpty(textInputEditText: TextInputEditText, errorMessage: String?): Boolean {
        if (textInputEditText.text.toString().isEmpty()) {
            textInputEditText.error = errorMessage
            return false
        }
        return true
    }

    //TODO REPLACE WITH WORK MANAGER
    private fun updateLocalAndRemoteStub(dictionaryEntity: DictionaryEntity) {
        dictionaryViewModel.updateLocal(listOf(dictionaryEntity))
        dictionaryViewModel.updateRemote(dictionaryEntity)
        dismiss()
    }

    //TODO REPLACE WITH WORK MANAGER
    private fun insertLocalAndRemoteStub(dictionaryEntity: DictionaryEntity) {
        dictionaryViewModel.insertLocal(listOf(dictionaryEntity))
        dictionaryViewModel.insertRemote(dictionaryEntity)
        dismiss()
    }

    private fun getWord(dictionaryEntity: DictionaryEntity): DictionaryEntity {
        dictionaryEntity.word = newEntryWord.text.toString()
        dictionaryEntity.meaning = newEntryMeaning.text.toString()
        dictionaryEntity.dateModified = Calendar.getInstance().timeInMillis
        return dictionaryEntity
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

}
