package ch.epfl.sdp.mobile.test

import kotlinx.coroutines.suspendCancellableCoroutine

/** Returns any value by suspending forever. */
suspend inline fun suspendForever(): Nothing = suspendCancellableCoroutine {}
