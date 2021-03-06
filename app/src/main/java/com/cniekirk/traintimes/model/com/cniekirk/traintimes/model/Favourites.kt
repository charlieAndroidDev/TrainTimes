// Code generated by Wire protocol buffer compiler, do not edit.
// Source: Favourites in Favourites.proto
package com.cniekirk.traintimes.model

import android.os.Parcelable
import com.squareup.wire.AndroidMessage
import com.squareup.wire.FieldEncoding
import com.squareup.wire.ProtoAdapter
import com.squareup.wire.ProtoReader
import com.squareup.wire.ProtoWriter
import com.squareup.wire.Syntax.PROTO_3
import com.squareup.wire.WireField
import com.squareup.wire.internal.immutableCopyOf
import com.squareup.wire.internal.redactElements
import kotlin.Any
import kotlin.AssertionError
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.DeprecationLevel
import kotlin.Int
import kotlin.Long
import kotlin.Nothing
import kotlin.String
import kotlin.collections.List
import kotlin.jvm.JvmField
import okio.ByteString

/**
 * option java_multiple_files = true;
 */
class Favourites(
  favourite: List<Favourite> = emptyList(),
  unknownFields: ByteString = ByteString.EMPTY
) : AndroidMessage<Favourites, Nothing>(ADAPTER, unknownFields) {
  @field:WireField(
    tag = 1,
    adapter = "com.cniekirk.traintimes.model.Favourite#ADAPTER",
    label = WireField.Label.REPEATED
  )
  val favourite: List<Favourite> = immutableCopyOf("favourite", favourite)

  @Deprecated(
    message = "Shouldn't be used in Kotlin",
    level = DeprecationLevel.HIDDEN
  )
  override fun newBuilder(): Nothing = throw AssertionError()

  override fun equals(other: Any?): Boolean {
    if (other === this) return true
    if (other !is Favourites) return false
    if (unknownFields != other.unknownFields) return false
    if (favourite != other.favourite) return false
    return true
  }

  override fun hashCode(): Int {
    var result = super.hashCode
    if (result == 0) {
      result = unknownFields.hashCode()
      result = result * 37 + favourite.hashCode()
      super.hashCode = result
    }
    return result
  }

  override fun toString(): String {
    val result = mutableListOf<String>()
    if (favourite.isNotEmpty()) result += """favourite=$favourite"""
    return result.joinToString(prefix = "Favourites{", separator = ", ", postfix = "}")
  }

  fun copy(favourite: List<Favourite> = this.favourite, unknownFields: ByteString =
      this.unknownFields): Favourites = Favourites(favourite, unknownFields)

  companion object {
    @JvmField
    val ADAPTER: ProtoAdapter<Favourites> = object : ProtoAdapter<Favourites>(
      FieldEncoding.LENGTH_DELIMITED, 
      Favourites::class, 
      "type.googleapis.com/Favourites", 
      PROTO_3, 
      null
    ) {
      override fun encodedSize(value: Favourites): Int {
        var size = value.unknownFields.size
        size += Favourite.ADAPTER.asRepeated().encodedSizeWithTag(1, value.favourite)
        return size
      }

      override fun encode(writer: ProtoWriter, value: Favourites) {
        Favourite.ADAPTER.asRepeated().encodeWithTag(writer, 1, value.favourite)
        writer.writeBytes(value.unknownFields)
      }

      override fun decode(reader: ProtoReader): Favourites {
        val favourite = mutableListOf<Favourite>()
        val unknownFields = reader.forEachTag { tag ->
          when (tag) {
            1 -> favourite.add(Favourite.ADAPTER.decode(reader))
            else -> reader.readUnknownField(tag)
          }
        }
        return Favourites(
          favourite = favourite,
          unknownFields = unknownFields
        )
      }

      override fun redact(value: Favourites): Favourites = value.copy(
        favourite = value.favourite.redactElements(Favourite.ADAPTER),
        unknownFields = ByteString.EMPTY
      )
    }

    @JvmField
    val CREATOR: Parcelable.Creator<Favourites> = AndroidMessage.newCreator(ADAPTER)

    private const val serialVersionUID: Long = 0L
  }
}
