package ch.epfl.sdp.mobile.infrastructure.speech.android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent.*
import android.speech.SpeechRecognizer as NativeSpeechRecognizer
import android.speech.SpeechRecognizer.RESULTS_RECOGNITION
import ch.epfl.sdp.mobile.infrastructure.speech.SpeechRecognizer
import ch.epfl.sdp.mobile.infrastructure.speech.SpeechRecognizerFactory

/** The default locale we'll be using for speech recognition. */
private const val DefaultLanguage = "en-US"

/** The default maximum number of results we'll receive from the system speech recognizer. */
private const val DefaultResultsCount = 10

/**
 * An implementation of a [SpeechRecognizer] which is backed by a [NativeSpeechRecognizer].
 *
 * @param context the [Context] which will be used to create the speech recognizers.
 * @param language the default language to use.
 * @param resultsCount the maximum results count for each result set.
 */
class AndroidSpeechRecognizerFactory(
    private val context: Context,
    private val language: String = DefaultLanguage,
    private val resultsCount: Int = DefaultResultsCount,
) : SpeechRecognizerFactory {

  override fun createSpeechRecognizer(): SpeechRecognizer =
      AndroidSpeechRecognizer(
          recognizer = NativeSpeechRecognizer.createSpeechRecognizer(context),
          language = language,
          resultsCount = resultsCount,
      )
}

/**
 * An implementation of a [SpeechRecognizer] which is backed by a [NativeSpeechRecognizer].
 *
 * @param recognizer the underlying [NativeSpeechRecognizer].
 * @param language the language code that we use for recognition.
 * @param resultsCount the maximum count of results that we are interested in.
 */
class AndroidSpeechRecognizer(
    private val recognizer: NativeSpeechRecognizer,
    private val language: String = DefaultLanguage,
    private val resultsCount: Int = DefaultResultsCount,
) : SpeechRecognizer {

  override fun setListener(listener: SpeechRecognizer.Listener) =
      recognizer.setRecognitionListener(
          object : RecognitionListenerAdapter() {
            override fun onError(error: Int) = listener.onError()
            override fun onResults(
                results: Bundle?,
            ) = listener.onResults(results?.getStringArrayList(RESULTS_RECOGNITION) ?: emptyList())
          },
      )

  override fun startListening() =
      recognizer.startListening(
          Intent(ACTION_RECOGNIZE_SPEECH)
              .putExtra(EXTRA_LANGUAGE, language)
              .putExtra(EXTRA_MAX_RESULTS, resultsCount),
      )

  override fun stopListening() = recognizer.stopListening()

  override fun destroy() = recognizer.destroy()
}
