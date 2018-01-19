package it.gbresciani.statevalue

import android.view.View

sealed class HidingStrategy(val androidInt: Int) {
      object INVISIBLE : HidingStrategy(View.INVISIBLE)
      object GONE : HidingStrategy(View.GONE)
}