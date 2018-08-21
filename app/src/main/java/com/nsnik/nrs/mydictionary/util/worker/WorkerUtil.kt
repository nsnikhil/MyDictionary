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

package com.nsnik.nrs.mydictionary.util.worker

import androidx.work.*
import com.nsnik.nrs.mydictionary.model.DictionaryEntity
import java.util.*

class WorkerUtil {

    companion object {

        fun buildLocalRequest(data: Data, workerClass: Class<out Worker>): OneTimeWorkRequest = OneTimeWorkRequest.Builder(workerClass)
                .setInputData(data)
                .build()

        fun buildRemoteRequest(data: Data, constraints: Constraints, workerClass: Class<out Worker>): OneTimeWorkRequest =
                OneTimeWorkRequest.Builder(workerClass)
                        .setConstraints(constraints)
                        .setInputData(data)
                        .build()

        fun getConstraints(): Constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

        fun getWord(inputData: Data): DictionaryEntity = getWord(
                inputData.getString("word"),
                inputData.getString("meaning"),
                inputData.getLong("time", Calendar.getInstance().timeInMillis))

        fun getWordForUpdate(inputData: Data): DictionaryEntity = getWord(
                inputData.getInt("id", -1),
                inputData.getString("word"),
                inputData.getString("meaning"),
                inputData.getLong("time", Calendar.getInstance().timeInMillis))

        private fun getWord(word: String?, meaning: String?, time: Long): DictionaryEntity {
            val dictionaryEntity = DictionaryEntity()
            dictionaryEntity.word = word
            dictionaryEntity.meaning = meaning
            dictionaryEntity.dateModified = time
            return dictionaryEntity
        }

        private fun getWord(id: Int, word: String?, meaning: String?, time: Long): DictionaryEntity {
            val dictionaryEntity = DictionaryEntity()
            dictionaryEntity.id = id
            dictionaryEntity.word = word
            dictionaryEntity.meaning = meaning
            dictionaryEntity.dateModified = time
            return dictionaryEntity
        }

    }

}