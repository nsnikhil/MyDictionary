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

package com.nsnik.nrs.mydictionary.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.twitter.serial.serializer.ObjectSerializer
import com.twitter.serial.serializer.SerializationContext
import com.twitter.serial.stream.SerializerInput
import com.twitter.serial.stream.SerializerOutput

@Entity
class DictionaryEntity {

    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    var id: Int = 0
    @SerializedName("word")
    var word: String? = null
    @SerializedName("meaning")
    var meaning: String? = null
    @SerializedName("modifieddate")
    var dateModified: Long = 0L

    companion object {

        @Ignore
        val SERIALIZER: ObjectSerializer<DictionaryEntity> = DictionaryEntitySerializer()

        class DictionaryEntitySerializer : ObjectSerializer<DictionaryEntity>() {

            override fun serializeObject(context: SerializationContext, output: SerializerOutput<out SerializerOutput<*>>, dictionaryEntity: DictionaryEntity) {
                output.writeInt(dictionaryEntity.id)
                output.writeString(dictionaryEntity.word)
                output.writeString(dictionaryEntity.meaning)
                output.writeLong(dictionaryEntity.dateModified)
            }

            override fun deserializeObject(context: SerializationContext, input: SerializerInput, versionNumber: Int): DictionaryEntity? {
                val dictionaryEntity = DictionaryEntity()
                dictionaryEntity.id = input.readInt()
                dictionaryEntity.word = input.readString()
                dictionaryEntity.meaning = input.readString()
                dictionaryEntity.dateModified = input.readLong()
                return dictionaryEntity
            }

        }

    }
}