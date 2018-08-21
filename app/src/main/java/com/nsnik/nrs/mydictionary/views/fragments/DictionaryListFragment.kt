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

package com.nsnik.nrs.mydictionary.views.fragments


import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding2.support.v7.widget.RxPopupMenu
import com.jakewharton.rxbinding2.view.RxView
import com.nsnik.nrs.mydictionary.R
import com.nsnik.nrs.mydictionary.model.DictionaryEntity
import com.nsnik.nrs.mydictionary.util.eventBus.WordListDownloaded
import com.nsnik.nrs.mydictionary.viewModel.DictionaryViewModel
import com.nsnik.nrs.mydictionary.views.MainActivity
import com.nsnik.nrs.mydictionary.views.adapters.DictionaryListAdapter
import com.nsnik.nrs.mydictionary.views.fragments.dialog.AboutDialogFragment
import com.nsnik.nrs.mydictionary.views.fragments.dialog.ActionAlertDialog
import com.nsnik.nrs.mydictionary.views.fragments.dialog.AddNewWordDialogFragment
import com.nsnik.nrs.mydictionary.views.listeners.ItemClickListener
import com.nsnik.nrs.mydictionary.views.listeners.ItemLongClickListener
import com.twitter.serial.stream.bytebuffer.ByteBufferSerial
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_dictionary_list.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class DictionaryListFragment : Fragment(), ItemClickListener, ItemLongClickListener {

    private lateinit var dictionaryListAdapter: DictionaryListAdapter
    private lateinit var dictionaryViewModel: DictionaryViewModel
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dictionary_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        initialize()
        listeners()
    }

    private fun initialize() {
        dictionaryListAdapter = DictionaryListAdapter(this, this)

        dictionaryList.apply {
            layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            adapter = dictionaryListAdapter
        }

        dictionaryViewModel = ViewModelProviders.of(this).get(DictionaryViewModel::class.java)
        dictionaryViewModel.getRemoteList()
        dictionaryViewModel.getLocalList().observe(this, Observer { dictionaryListAdapter.submitList(it) })
    }

    private fun listeners() {
        compositeDisposable.addAll(
                RxView.clicks(addWord).subscribe { AddNewWordDialogFragment().show(fragmentManager, "newWordDialog") }
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.list_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menuListSearch -> {

            }
            R.id.menuListSettings -> Navigation.findNavController(activity as MainActivity, R.id.mainNavHost).navigate(R.id.listToPreferences)
            R.id.menuListAbout -> AboutDialogFragment().show(fragmentManager, "aboutDialog")
        }
        return super.onOptionsItemSelected(item)
    }

    override fun itemClicked(dictionaryEntity: DictionaryEntity) {
        AlertDialog.Builder(activity as Context)
                .setTitle(dictionaryEntity.word)
                .setMessage(dictionaryEntity.meaning)
                .create()
                .show()
    }

    override fun itemLongClicked(dictionaryEntity: DictionaryEntity, view: View) {
        inflatePopupMenu(dictionaryEntity, view)
    }

    @SuppressLint("CheckResult")
    private fun inflatePopupMenu(dictionaryEntity: DictionaryEntity, view: View) {
        val popupMenu = PopupMenu(activity as Context, view, Gravity.END)
        popupMenu.inflate(R.menu.item_popup_menu)
        RxPopupMenu.itemClicks(popupMenu).subscribe {
            when (it.itemId) {
                R.id.popUpMenuEdit -> showAddNewWordDialogFragment(dictionaryEntity)
                R.id.popUpMenuDelete -> showWarningDeleteDialog(dictionaryEntity)
            }
        }
        popupMenu.show()
    }

    private fun showAddNewWordDialogFragment(dictionaryEntity: DictionaryEntity) {
        val bundle = Bundle()
        val byteArray = ByteBufferSerial().toByteArray(dictionaryEntity, DictionaryEntity.SERIALIZER)
        bundle.putByteArray(activity?.resources?.getString(R.string.bundleDictionaryEntity), byteArray)
        val dialog = AddNewWordDialogFragment()
        dialog.arguments = bundle
        dialog.show(fragmentManager, "editDialog")
    }

    //TODO REPLACE WITH WORK MANAGER
    private fun showWarningDeleteDialog(dictionaryEntity: DictionaryEntity) {
        ActionAlertDialog.showDialog(activity!!, activity?.resources?.getString(R.string.warning)!!,
                activity?.resources?.getString(R.string.deleteMessage)!!,
                activity?.resources?.getString(R.string.delete)!!,
                activity?.resources?.getString(R.string.cancel)!!,
                DialogInterface.OnClickListener { dialogInterface, i ->
                    dictionaryViewModel.deleteLocal(listOf(dictionaryEntity))
                    dictionaryViewModel.deleteRemote(dictionaryEntity.id)
                },
                DialogInterface.OnClickListener { dialogInterface, i -> })
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe
    fun WordListDownloadedEvent(wordListDownloaded: WordListDownloaded) {
        dictionaryViewModel.insertLocal(wordListDownloaded.wordList)
    }

    private fun cleanUp() {
        compositeDisposable.clear()
        compositeDisposable.dispose()
    }

    override fun onDestroy() {
        cleanUp()
        super.onDestroy()
    }

}
