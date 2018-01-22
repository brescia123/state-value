package it.gbresciani.statevalue

import android.view.View
import it.gbresciani.statevalue.StateValue.*


interface StateValueRenderer<T> {
      var currentStateValue: StateValue<T>
      fun render(stateValue: StateValue<T>)
}

interface MissingStateValueView<out MV : View> {
      val setMissingState: (() -> Unit)?
      val missingView: MV?
}

interface LoadingStateValueView<out LV : View> {
      val setLoadingState: ((LoadingState.Progress?) -> Unit)?
      val loadingView: LV?
}

interface ErrorStateValueView<out EV : View, in E> {
      val setErrorState: ((E) -> Unit)?
      val errorView: EV?
}

interface WithValueLoadingStateValueView<out WVLV : View, in T> {
      val setWithValueLoadingState: ((T, LoadingState.Progress?) -> Unit)?
      val withValueLoadingView: WVLV?
}

interface WithValueErrorStateValueView<out WVEV : View, in T, in E> {
      val setWithValueErrorState: ((T, E) -> Unit)?
      val withValueErrorView: WVEV?
}

interface ValueStateValueView<out VV : View, in T> {
      val setValueState: ((T) -> Unit)?
      val valueView: VV?
}

class FullStateValueRenderer<T, in E, out MV : View, out LV : View, out EV : View, out WVLV : View, out WVEV : View, out VV : View>(
    override val missingView: MV? = null,
    override val loadingView: LV? = null,
    override val errorView: EV? = null,
    override val withValueLoadingView: WVLV? = null,
    override val withValueErrorView: WVEV? = null,
    override val valueView: VV? = null,
    override val setMissingState: (() -> Unit)? = null,
    override val setLoadingState: ((LoadingState.Progress?) -> Unit)? = null,
    override val setErrorState: ((E) -> Unit)? = null,
    override val setWithValueLoadingState: ((T, LoadingState.Progress?) -> Unit)? = null,
    override val setWithValueErrorState: ((T, E) -> Unit)? = null,
    override val setValueState: ((T) -> Unit)? = null,
    private val hidingStrategy: HidingStrategy = HidingStrategy.GONE,
    private val animate: Boolean = true
) : StateValueRenderer<T>,
    MissingStateValueView<MV>,
    LoadingStateValueView<LV>,
    ErrorStateValueView<EV, E>,
    WithValueLoadingStateValueView<WVLV, T>,
    WithValueErrorStateValueView<WVEV, T, E>,
    ValueStateValueView<VV, T> {

      override var currentStateValue: StateValue<T> = NoValue.Missing()

      override fun render(stateValue: StateValue<T>) {
            if (stateValue == currentStateValue) return

            when (stateValue) {
                  is NoValue.Missing -> setMissingState?.invoke()
                  is NoValue.Loading -> setLoadingState?.invoke(stateValue.progress)
                  is NoValue.Error<*, *> -> setErrorState?.invoke(stateValue.error as E)
                  is WithValue.Loading -> {
                        withValueLoadingView?.let {
                              setWithValueLoadingState?.invoke(stateValue.value, stateValue.progress)
                              setValueState?.invoke(stateValue.value)
                        }
                  }
                  is WithValue.Error<*, *> -> {
                        withValueErrorView?.let {
                              setWithValueErrorState?.invoke(stateValue.value as T, stateValue.error as E)
                              setValueState?.invoke(stateValue.value as T)
                        }
                  }
                  is WithValue.Value -> setValueState?.invoke(stateValue.value)
            }

            when (stateValue) {
                  is NoValue.Missing -> renderState(
                      show = listOfNotNull(missingView),
                      hide = listOfNotNull(loadingView, errorView, withValueLoadingView, withValueErrorView, valueView),
                      hidingStrategy = hidingStrategy,
                      animate = animate

                  )
                  is NoValue.Loading -> renderState(
                      show = listOfNotNull(loadingView),
                      hide = listOfNotNull(missingView, errorView, withValueLoadingView, withValueErrorView, valueView),
                      hidingStrategy = hidingStrategy,
                      animate = animate

                  )
                  is WithValue.Loading -> {
                        val showList: List<View>
                        val hideList: List<View>

                        if (withValueLoadingView == null) {
                              showList = listOfNotNull(loadingView)
                              hideList = listOfNotNull(missingView, errorView, withValueLoadingView, withValueErrorView, valueView)
                        } else {
                              showList = listOfNotNull(withValueLoadingView, valueView)
                              hideList = listOfNotNull(missingView, errorView, loadingView, withValueErrorView)
                        }

                        renderState(
                            show = showList,
                            hide = hideList,
                            hidingStrategy = hidingStrategy,
                            animate = animate

                        )
                  }
                  is NoValue.Error<*, *> -> renderState(
                      show = listOfNotNull(errorView),
                      hide = listOfNotNull(missingView, loadingView, withValueLoadingView, withValueErrorView, valueView),
                      hidingStrategy = hidingStrategy,
                      animate = animate

                  )
                  is WithValue.Error<*, *> -> {
                        val showList: List<View>
                        val hideList: List<View>

                        if (withValueErrorView == null) {
                              showList = listOfNotNull(errorView)
                              hideList = listOfNotNull(missingView, loadingView, withValueLoadingView, withValueErrorView, valueView)
                        } else {
                              showList = listOfNotNull(withValueErrorView, valueView)
                              hideList = listOfNotNull(missingView, errorView, loadingView, withValueLoadingView)
                        }

                        renderState(
                            show = showList,
                            hide = hideList,
                            hidingStrategy = hidingStrategy,
                            animate = animate

                        )
                  }
                  is WithValue.Value -> renderState(
                      show = listOfNotNull(valueView),
                      hide = listOfNotNull(missingView, loadingView, errorView, withValueLoadingView, withValueErrorView),
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
