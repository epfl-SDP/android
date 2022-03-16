package ch.epfl.sdp.mobile.test.application

import ch.epfl.sdp.mobile.application.ProfileDocument
import ch.epfl.sdp.mobile.application.authentication.AuthenticationFacade
import ch.epfl.sdp.mobile.application.authentication.AuthenticationResult
import ch.epfl.sdp.mobile.application.social.SocialFacade
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.buildAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.emptyAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.buildStore
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.document
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.emptyStore
import com.google.common.truth.Truth
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SocialFacadeTest {

    @Test
    fun search_returnWithEmptySearchString() = runTest {
        val auth = emptyAuth()
        val store = buildStore { collection("users") { document("uid", ProfileDocument(name = "test"))} }
        val facade = SocialFacade(auth, store)

        Truth.assertThat(facade.search("").first())
            .isEmpty()
    }

    @Test
    fun search_successfullySearch() = runTest {
        val auth = emptyAuth()
        val store = buildStore { collection("users") { document("uid", ProfileDocument(name = "test"))} }
        val facade = SocialFacade(auth, store)

        val user = facade.search("test").first()[0]
        Truth.assertThat(user.name).isEqualTo("test")
    }
}