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

import com.nsnik.nrs.mydictionary.model.DictionaryEntity
import io.reactivex.Single
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import java.util.*

interface DictionaryNetwrokApi {

    @GET("/php/readAll.php")
    fun getUserList(): Single<List<DictionaryEntity>>

    @FormUrlEncoded
    @POST("/php/insert.php")
    fun addWord(@Field("word") word: String?,
                @Field("meaning") meaning: String?): Single<String>

    @FormUrlEncoded
    @POST("/php/update.php")
    fun updateWord(@Field("id") id: Int?,
                   @Field("word") word: String?,
                   @Field("meaning") meaning: String?,
                   @Field("modifieddate") modifieddate: Date?): Single<String>

    @FormUrlEncoded
    @POST("/php/delete.php")
    fun deleteWord(@Field("id") id: Int?): Single<String>

}