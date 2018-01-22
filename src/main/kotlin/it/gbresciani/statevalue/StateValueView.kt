package it.gbresciani.statevalue

import android.view.View
import it.gbresciani.statevalue.StateValue.*

interface StateValueRenderer<T> {
      var currentStateValue: StateValue<T>
      fun render(stateValue: StateValue<T>)
}

interface MissingStateValueView<out MV : View> {
      val setMissingState: (() -> Unit)?
      val missingView: MV
}

interface LoadingStateValueView<out LV : View> {
      val setLoadingState: ((LoadingState.Progress?) -> Unit)?
      val loadingView: LV
}

interface ErrorStateValueView<out EV : View, in E> {
      val setErrorState: ((E) -> Unit)?
      val errorView: EV
}

interface WithValueLoadingStateValueView<out WVLV : View, in T> {
      val setWithValueLoadingState: ((T, LoadingState.Progress?) -> Unit)?
      val withValueLoadingView: WVLV
}

interface WithValueErrorStateValueView<out WVEV : View, in T, in E> {
      val setWithValueErrorState: ((T, E) -> Unit)?
      val withValueErrorView: WVEV
}

interface ValueStateValueView<out VV : View, in T> {
      val setValueState: ((T) -> Unit)?
      val valueView: VV
}


open class SimpleStateValueRenderer<T, in E, out MV : View, out LV : View, out EV : View, out VV : View>(
    override val missingView: MV,
    override val loadingView: LV,
    override val errorView: EV,
    override val valueView: VV,
    override val setMissingState: (() -> Unit)? = null,
    override val setLoadingState: ((LoadingState.Progress?) -> Unit)? = null,
    override val setErrorState: ((E) -> Unit)? = null,
    override val setValueState: ((T) -> Unit)? = null,
    protected val hidingStrategy: HidingStrategy = HidingStrategy.GONE,
    protected val animate: Boolean = true
) : StateValueRenderer<T>,
    MissingStateValueView<MV>,
    LoadingStateValueView<LV>,
    ErrorStateValueView<EV, E>,
    ValueStateValueView<VV, T> {

      override var currentStateValue: StateValue<T> = NoValue.Missing()

      override fun render(stateValue: StateValue<T>) {
            if (stateValue == currentStateValue) return

            when (stateValue) {
                  is NoValue.Missing<T> -> setMissingState?.invoke()
                  is LoadingState -> setLoadingState?.invoke(stateValue.progress)
                  is ErrorState<*> -> setErrorState?.invoke(stateValue.error as E)
                  is WithValue.Value -> setValueState?.invoke(stateValue.value)
            }

            when (stateValue) {
                  is NoValue.Missing -> renderState(
                      show = listOf(missingView),
                      hide = listOf(loadingView, errorView, valueView),
                      hidingStrategy = hidingStrategy,
                      animate = animate

                  )
                  is NoValue.Loading,
                  is WithValue.Loading -> renderState(
                      show = listOf(loadingView),
                      hide = listOf(missingView, errorView, valueView),
                      hidingStrategy = hidingStrategy,
                      animate = animate

                  )
                  is NoValue.Error<*, *>,
                  is WithValue.Error<*, *> -> renderState(
                      show = listOf(errorView),
                      hide = listOf(missingView, loadingView, valueView),
                      hidingStrategy = hidingStrategy,
                      animate = animate

                  )
                  is WithValue.Value -> renderState(
                      show = listOf(valueView),
                      hide = listOf(missingView, loadingView, errorView),
                      hidingStrategy = hidingStrategy,
                      animate = animate

                  )
            }

            currentStateValue = stateValue
      }
}

class FullStateValueRenderer<T, in E, out MV : View, out LV : View, out EV : View, out WVLV : View, out WVEV : View, out VV : View>(
    override val missingView: MV,
    override val loadingView: LV,
    override val errorView: EV,
    override val withValueLoadingView: WVLV,
    override val withValueErrorView: WVEV,
    override val valueView: VV,
    setMissingState: (() -> Unit)? = null,
    setLoadingState: ((LoadingState.Progress?) -> Unit)? = null,
    setErrorState: ((E) -> Unit)? = null,
    override val setWithValueLoadingState: ((T, LoadingState.Progress?) -> Unit)? = null,
    override val setWithValueErrorState: ((T, E) -> Unit)? = null,
    setValueState: ((T) -> Unit)? = null,
    hidingStrategy: HidingStrategy = HidingStrategy.GONE,
    animate: Boolean = true
) : SimpleStateValueRenderer<T, E, MV, LV, EV, VV>(missingView, loadingView, errorView, valueView, setMissingState, setLoadingState, setErrorState, setValueState, hidingStrategy, animate),
    WithValueLoadingStateValueView<WVLV, T>,
    WithValueErrorStateValueView<WVEV, T, E> {

      override var currentStateValue: StateValue<T> = NoValue.Missing()

      override fun render(stateValue: StateValue<T>) {
            if (stateValue == currentStateValue) return

            when (stateValue) {
                  is NoValue.Missing -> setMissingState?.invoke()
                  is NoValue.Loading -> setLoadingState?.invoke(stateValue.progress)
                  is NoValue.Error<*, *>-> setErrorState?.invoke(stateValue.error as E)
                  is WithValue.Loading -> setWithValueLoadingState?.invoke(stateValue.value, stateValue.progress)
                  is WithValue.Error<*, *>-> setWithValueErrorState?.invoke(stateValue.value as T, stateValue.error as E)
                  is WithValue.Value -> setValueState?.invoke(stateValue.value)
            }

            when (stateValue) {
                  is NoValue.Missing -> renderState(
                      show = listOf(missingView),
                      hide = listOf(loadingView, errorView, withValueLoadingView, withValueErrorView, valueView),
                      hidingStrategy = hidingStrategy,
                      animate = animate

                  )
                  is NoValue.Loading -> renderState(
                      show = listOf(loadingView),
                      hide = listOf(missingView, errorView, withValueLoadingView, withValueErrorView, valueView),
                      hidingStrategy = hidingStrategy,
                      animate = animate

                  )
                  is WithValue.Loading -> renderState(
                      show = listOf(withValueLoadingView, valueView),
                      hide = listOf(missingView, loadingView, errorView, withValueErrorView),
                      hidingStrategy = hidingStrategy,
                      animate = animate

                  )
                  is NoValue.Error<*, *> -> renderState(
                      show = listOf(errorView),
                      hide = listOf(missingView, loadingView, withValueLoadingView, withValueErrorView, valueView),
                      hidingStrategy = hidingStrategy,
                      animate = animate

                  )
                  is WithValue.Error<*, *> -> renderState(
                      show = listOf(withValueErrorView, valueView),
                      hide = listOf(missingView, loadingView, errorView, withValueLoadingView),
                      hidingStrategy = hidingStrategy,
                      animate = animate

                  )
                  is WithValue.Value -> renderState(
                      show = listOf(valueView),
                      hide = listOf(missingView, loadingView, errorView, withValueLoadingView, withValueErrorView),
                      hidingStrategy = hidingStrategy,
                      animate = animate

                  )

            }
            currentStateValue = stateValue
      }
}

fun renderState(show: List<View>, hide: List<View>, hidingStrategy: HidingStrategy, animate: Boolean) {
      hide.forEach {
            it.visibleOrHide(
                show = false,
                hidingStrategy = hidingStrategy.androidInt,
                animate = animate
            )
      }
      show.forEach {
            it.visibleOrHide(
                show = true,
                hidingStrategy = hidingStrategy.androidInt,
                animate = animate
            )
      }
}
