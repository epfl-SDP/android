package ch.epfl.sdp.mobile.test.application

import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.authentication.AuthenticationFacade
import ch.epfl.sdp.mobile.infrastructure.persistence.store.CollectionReference as CR
import ch.epfl.sdp.mobile.infrastructure.persistence.store.DocumentReference as DR
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first

/**
 * Awaits the first [AuthenticatedUser] from this [AuthenticationFacade].
 *
 * @param D the type of [DR] from the [AuthenticationFacade].
 * @param C the type of [CR] from the [AuthenticationFacade].
 *
 * @receiver the [AuthenticationFacade] that we query.
 * @return the first [AuthenticatedUser] instance.
 */
suspend fun <D : DR<C>, C : CR<D>> AuthenticationFacade<D, C>.awaitAuthenticatedUser():
    AuthenticatedUser<D, C> = currentUser.filterIsInstance<AuthenticatedUser<D, C>>().first()
