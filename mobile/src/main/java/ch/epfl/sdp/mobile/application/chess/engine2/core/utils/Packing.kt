@file:Suppress("NOTHING_TO_INLINE")

package ch.epfl.sdp.mobile.application.chess.engine2.core.utils

/**
 * Unpacks the 4 most significant bytes from [value] and transforms them into a [Short].
 * @param value the original [Int] value.
 * @return the unpacked [Short].
 */
inline fun unpackShort1(value: Int): Short = (value shr Short.SIZE_BITS).toShort()

/**
 * Unpacks the 4 least significant bytes from [value] and transforms them into a [Short].
 * @param value the original [Int] value.
 * @return the unpacked [Short].
 */
inline fun unpackShort2(value: Int): Short = (value and 0xFFFF).toShort()

/**
 * Packs two [Short] values in an [Int] to avoid boxing them in an object.
 * @param val1 the first [Short] to pack. Can be unpacked with [unpackShort1].
 * @param val2 the second [Short] to pack. Can be unpacked with [unpackShort2].
 * @return the packed [Int] with both values.
 */
inline fun packShorts(val1: Short, val2: Short): Int =
    (val1.toInt() shl Short.SIZE_BITS) or (val2.toInt() and 0xFFFF)
