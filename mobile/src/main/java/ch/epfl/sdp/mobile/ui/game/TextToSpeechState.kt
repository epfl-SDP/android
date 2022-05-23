package ch.epfl.sdp.mobile.ui.game

interface TextToSpeechState {

    val muted: Boolean

    fun onTTsVolumeClick()
}