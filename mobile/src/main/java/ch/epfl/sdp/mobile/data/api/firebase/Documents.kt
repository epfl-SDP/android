package ch.epfl.sdp.mobile.data.api.firebase

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName

@IgnoreExtraProperties
data class FirebaseProfileDocument(
    @DocumentId @PropertyName("userId") val userId: DocumentReference? = null,
    @PropertyName("name") val name: String? = null,
    @PropertyName("emoji") val emoji: String? = null,
    @PropertyName("backgroundColor") val backgroundColor: String? = null
)
