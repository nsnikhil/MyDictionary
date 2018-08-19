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


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding2.view.RxView
import com.nsnik.nrs.mydictionary.R
import com.nsnik.nrs.mydictionary.viewModel.DictionaryViewModel
import com.nsnik.nrs.mydictionary.views.MainActivity
import com.nsnik.nrs.mydictionary.views.adapters.DictionaryListAdapter
import com.nsnik.nrs.mydictionary.views.fragments.dialog.AboutDialogFragment
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_dictionary_list.*
import timber.log.Timber


class DictionaryListFragment : Fragment() {

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
        dictionaryListAdapter = DictionaryListAdapter()

        dictionaryList.apply {
            layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            adapter = dictionaryListAdapter
        }

        dictionaryViewModel = ViewModelProviders.of(this).get(DictionaryViewModel::class.java)
    }

    private fun listeners() {
        compositeDisposable.addAll(
                RxView.clicks(addWord).subscribe {
                    Timber.d("Click")
                }
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

    private fun cleanUp() {
        compositeDisposable.clear()
        compositeDisposable.dispose()
    }

    override fun onDestroy() {
        cleanUp()
        super.onDestroy()
    }

}
