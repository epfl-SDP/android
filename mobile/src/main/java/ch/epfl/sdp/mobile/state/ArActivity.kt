package ch.epfl.sdp.mobile.state

import android.os.Bundle
import androidx.fragment.app.*
import ch.epfl.sdp.mobile.R

class ArActivity : FragmentActivity(R.layout.ar_activity) {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    supportFragmentManager.commit { add(R.id.containerFragment, ArFragment::class.java, Bundle()) }
  }
}
