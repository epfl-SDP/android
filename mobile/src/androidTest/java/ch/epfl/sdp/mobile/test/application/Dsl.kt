package ch.epfl.sdp.mobile.test.application

import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.authentication.AuthenticationFacade
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first

/** Awaits and returns the first [AuthenticatedUser] user from this [AuthenticationFacade]. */
suspend fun AuthenticationFacade.awaitAuthenticatedUser(): AuthenticatedUser =
    currentUser.filterIsInstance<AuthenticatedUser>().first()
