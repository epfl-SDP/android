package ch.epfl.sdp.mobile.androidTest.infrastructure.persistence.store.firestore

import ch.epfl.sdp.mobile.infrastructure.persistence.store.arrayRemove
import ch.epfl.sdp.mobile.infrastructure.persistence.store.arrayUnion
import ch.epfl.sdp.mobile.infrastructure.persistence.store.firestore.FirestoreDocumentReference
import com.google.android.gms.tasks.Tasks
import com.google.common.truth.Truth.assertThat
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Test

class FirestoreFieldValueTest {

  @Test
  fun updateWithArrayUnion_callsApiWithRightArguments() = runTest {
    val doc = mockk<DocumentReference>()
    val reference = FirestoreDocumentReference(doc)

    every { doc.set(any(), SetOptions.merge()) } answers
        {
          // Because FieldValue.equals() is not properly defined, we can't really check for
          // equality, but we can check that it extends Firestore's FieldValue (and that the
          // translation was successful.
          val arg = firstArg<Map<String, FieldValue>>()
          assertThat(arg).isNotEmpty()
          assertThat(arg["key"]).isInstanceOf(FieldValue::class.java)
          Tasks.forResult(null)
        }

    reference.update { arrayUnion("key", "value") }

    verify { doc.set(any(), SetOptions.merge()) }
  }

  @Test
  fun updateWithArrayRemove_callsApiWithRightArguments() = runTest {
    val doc = mockk<DocumentReference>()
    val reference = FirestoreDocumentReference(doc)

    every { doc.set(any(), SetOptions.merge()) } answers
        {
          // Because FieldValue.equals() is not properly defined, we can't really check for
          // equality, but we can check that it extends Firestore's FieldValue (and that the
          // translation was successful.
          val arg = firstArg<Map<String, FieldValue>>()
          assertThat(arg).isNotEmpty()
          assertThat(arg["key"]).isInstanceOf(FieldValue::class.java)
          Tasks.forResult(null)
        }

    reference.update { arrayRemove("key", "value") }

    verify { doc.set(any(), SetOptions.merge()) }
  }
}
