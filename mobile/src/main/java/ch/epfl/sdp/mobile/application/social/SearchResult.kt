package ch.epfl.sdp.mobile.application.social

import ch.epfl.sdp.mobile.application.Profile
import ch.epfl.sdp.mobile.infrastructure.persistence.auth.Auth
import ch.epfl.sdp.mobile.infrastructure.persistence.auth.User
import ch.epfl.sdp.mobile.ui.social.Person
import java.util.concurrent.Flow

sealed interface SearchResult {
    data class Success(val searchedPlayers: List<Profile>) : SearchResult

    /** Indicates that there was a failure during authentication. */
    object FailureInternal : SearchResult

}