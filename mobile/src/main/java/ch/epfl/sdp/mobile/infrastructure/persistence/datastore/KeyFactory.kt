package ch.epfl.sdp.mobile.infrastructure.persistence.datastore

/** An interface representing ways to build some [Key]s for a [DataStore]. */
interface KeyFactory {

  /**
   * Returns a [Key] associated with an [Int] value.
   *
   * @param name the name of the property.
   */
  fun int(name: String): Key<Int>

  /**
   * Returns a [Key] associated with a [Double] value.
   *
   * @param name the name of the property.
   */
  fun double(name: String): Key<Double>

  /**
   * Returns a [Key] associated with a [String] value.
   *
   * @param name the name of the property.
   */
  fun string(name: String): Key<String>

  /**
   * Returns a [Key] associated with a [Boolean] value.
   *
   * @param name the name of the property.
   */
  fun boolean(name: String): Key<Boolean>

  /**
   * Returns a [Key] associated with a [Float] value.
   *
   * @param name the name of the property.
   */
  fun float(name: String): Key<Float>

  /**
   * Returns a [Key] associated with a [Long] value.
   *
   * @param name the name of the property.
   */
  fun long(name: String): Key<Long>

  /**
   * Returns a [Key] associated with a [Set] of [String] values.
   *
   * @param name the name of the property.
   */
  fun stringSet(name: String): Key<Set<String>>
}
