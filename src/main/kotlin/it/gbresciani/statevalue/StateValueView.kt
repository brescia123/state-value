package it.gbresciani.statevalue

import android.view.View
import it.gbresciani.statevalue.StateValue.NoValue
import it.gbresciani.statevalue.StateValue.WithValue

interface StateValueRenderer<T> {
      fun render(stateValue: StateValue<T>)
}

interface MissingStateValueView<out MV : View> {
      val missingView: MV
}

interface LoadingStateValueView<out LV : View> {
      val loadingView: LV
}

interface ErrorStateValueView<out EV : View> {
      val errorView: EV
}

interface WithValueLoadingStateValueView<out WVLV : View> {
      val withValueLoadingView: WVLV
}

interface WithValueErrorStateValueView<out WVEV : View> {
      val withValueErrorView: WVEV
}

interface ValueStateValueView<out VV : View> {
      val valueView: VV
}


open class SimpleStateValueRenderer<T, out MV : View, out LV : View, out EV : View, out VV : View>(
    override val missingView: MV,
    override val loadingView: LV,
    override val errorView: EV,
    override val valueView: VV,
    protected val hidingStrategy: HidingStrategy = HidingStrategy.GONE,
    protected val animate: Boolean = true
) : StateValueRenderer<T>,
    MissingStateValueView<MV>,
    LoadingStateValueView<LV>,
    ErrorStateValueView<EV>,
    ValueStateValueView<VV> {

      override fun render(stateValue: StateValue<T>) {
            when (stateValue) {
                  is NoValue.Missing -> renderState(
                      show = missingView,
                      hide = listOf(loadingView, errorView, valueView),
                      hidingStrategy = hidingStrategy,
                      animate = animate

                  )
                  is NoValue.Loading,
                  is WithValue.Loading -> renderState(
                      show = loadingView,
                      hide = listOf(missingView, errorView, valueView),
                      hidingStrategy = hidingStrategy,
                      animate = animate

                  )
                  is NoValue.Error<*, *>,
                  is WithValue.Error<*, *> -> renderState(
                      show = errorView,
                      hide = listOf(missingView, loadingView, valueView),
                      hidingStrategy = hidingStrategy,
                      animate = animate

                  )
                  is WithValue.Value -> renderState(
                      show = valueView,
                      hide = listOf(missingView, loadingView, errorView),
                      hidingStrategy = hidingStrategy,
                      animate = animate

                  )
            }
      }
}

class FullStateValueRenderer<T, out MV : View, out LV : View, out EV : View, out WVLV : View, out WVEV : View, out VV : View>(
    override val missingView: MV,
    override val loadingView: LV,
    override val errorView: EV,
    override val withValueLoadingView: WVLV,
    override val withValueErrorView: WVEV,
    override val valueView: VV,
    hidingStrategy: HidingStrategy = HidingStrategy.GONE,
    animate: Boolean = true
) : SimpleStateValueRenderer<T, MV, LV, EV, VV>(missingView, loadingView, errorView, valueView, hidingStrategy, animate),
    WithValueLoadingStateValueView<WVLV>,
    WithValueErrorStateValueView<WVEV> {

      override fun render(stateValue: StateValue<T>) {
            when (stateValue) {
                  is NoValue.Missing -> renderState(
                      show = missingView,
                      hide = listOf(loadingView, errorView, withValueLoadingView, withValueErrorView, valueView),
                      hidingStrategy = hidingStrategy,
                      animate = animate

                  )
                  is NoValue.Loading -> renderState(
                      show = loadingView,
                      hide = listOf(missingView, errorView, withValueLoadingView, withValueErrorView, valueView),
                      hidingStrategy = hidingStrategy,
                      animate = animate

                  )
                  is WithValue.Loading -> renderState(
                      show = withValueLoadingView,
                      hide = listOf(missingView, loadingView, errorView, withValueErrorView, valueView),
                      hidingStrategy = hidingStrategy,
                      animate = animate

                  )
                  is NoValue.Error<*, *> -> renderState(
                      show = errorView,
                      hide = listOf(missingView, loadingView, withValueLoadingView, withValueErrorView, valueView),
                      hidingStrategy = hidingStrategy,
                      animate = animate

                  )
                  is WithValue.Error<*, *> -> renderState(
                      show = withValueErrorView,
                      hide = listOf(missingView, loadingView, errorView, withValueLoadingView, valueView),
                      hidingStrategy = hidingStrategy,
                      animate = animate

                  )
                  is WithValue.Value -> renderState(
                      show = valueView,
                      hide = listOf(missingView, loadingView, errorView, withValueLoadingView, withValueErrorView),
                      hidingStrategy = hidingStrategy,
                      animate = animate

                  )
            }
      }
}

fun renderState(show: View, hide: List<View>, hidingStrategy: HidingStrategy, animate: Boolean) {
      hide.forEach {
            it.visibleOrHide(
                show = false,
                hidingStrategy = hidingStrategy.androidInt,
                animate = animate
            )
      }
      show.visibleOrHide(
          show = true,
          hidingStrategy = hidingStrategy.androidInt,
          animate = animate
      )
}
