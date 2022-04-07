package ch.epfl.sdp.mobile.ui.speech_recognition

import android.content.Context

/** Interface ued to extract speech recognition routine */
interface SpeechRecognizable {
  /**
   * Suspending function that returns list of speech
   * @param context [Context] of app execution
   * @return speech candidates
   */
  suspend fun recognition(context: Context): List<String>
}
