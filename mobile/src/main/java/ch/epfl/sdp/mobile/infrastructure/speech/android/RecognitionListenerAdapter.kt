package ch.epfl.sdp.mobile.infrastructure.speech.android

import android.os.Bundle
import android.speech.RecognitionListener

/**
 * An abstract adapter around the Android [RecognitionListener] interface which provides some
 * default implementations for all the methods.
 */
abstract class RecognitionListenerAdapter : RecognitionListener {
  override fun onReadyForSpeech(params: Bundle?) = Unit
  override fun onBeginningOfSpeech() = Unit
  override fun onRmsChanged(rmsdB: Float) = Unit
  override fun onBufferReceived(buffer: ByteArray?) = Unit
  override fun onEndOfSpeech() = Unit
  override fun onError(error: Int) = Unit
  override fun onResults(results: Bundle?) = Unit
  override fun onPartialResults(partialResults: Bundle?) = Unit
  override fun onEvent(eventType: Int, params: Bundle?) = Unit
}
