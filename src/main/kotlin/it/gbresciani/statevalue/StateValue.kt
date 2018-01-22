package it.gbresciani.statevalue

/**
 * Sealed class that represents a value that could be in multiple state depending on whether it is
 * loading or in error and if a previous value was already loaded. To represent this classification
 * it is in turn divided into two sub sealed classes, [NoValue] and [WithValue], and it uses two
 * interfaces, [ErrorState] and [LoadingState].
 * The full class structure is given below:
 *
 * - StateValue
 *      - NoValue
 *          - Missing: The value is missing and it is not loading nor in error
 *          - Loading [LoadingState]: The value is loading and it does not encapsulate a previous loaded value
 *          - Error [ErrorState]: The value was not loaded because of an error and it does not encapsulate a previous loaded value
 *      - WithValue: No previous state
 *          - Loading [LoadingState]: The value is loading and it does encapsulate a previous loaded value
 *          - Error [ErrorState]: The value was not loaded because of an error but it does encapsulate a previous loaded value
 *          - Value: The value is loaded
 *
 * Cases implementing [ErrorState] could provide an object [E] to provide more info about the error
 *
 * Cases implementing [LoadingState] could provide a [LoadingState.Progress] representing the progress
 * of the loading process from 0 to 100.
 */
sealed class StateValue<T> {

    interface ErrorState<out E> {
        val error: E?
    }

    interface LoadingState {
        val progress: Progress?

        data class Progress(val value: Int) {
            init {
                if (value !in (0..100)) throw IllegalStateException("Progress value should be a value between 0 and 100.")
            }
        }
    }

    sealed class NoValue<T> : StateValue<T>() {
        class Missing<T> : NoValue<T>() {
            override fun equals(other: Any?): Boolean = other is Missing<*>
            override fun hashCode(): Int = super.hashCode()
        }

        data class Loading<T>(override val progress: LoadingState.Progress? = null) : NoValue<T>(), LoadingState
        data class Error<T, out E>(override val error: E? = null) : NoValue<T>(), ErrorState<E>
    }

    sealed class WithValue<T> : StateValue<T>() {
        abstract val value: T

        data class Loading<T>(override val value: T, override val progress: LoadingState.Progress? = null) : WithValue<T>(), LoadingState
        data class Error<T, out E>(override val value: T, override val error: E? = null) : WithValue<T>(), ErrorState<E>
        data class Value<T>(override val value: T) : WithValue<T>()
    }

    fun copyToMissing(): StateValue<T> = NoValue.Missing()

    fun copyToLoading(progress: LoadingState.Progress? = null): StateValue<T> = when (this) {
        is StateValue.NoValue -> NoValue.Loading(progress)
        is StateValue.WithValue -> WithValue.Loading(value, progress)
    }

    fun <E> copyToError(error: E? = null): StateValue<T> = when (this) {
        is StateValue.NoValue -> NoValue.Error(error)
        is StateValue.WithValue -> WithValue.Error(value, error)
    }

    fun copyToValue(value: T): StateValue<T> = WithValue.Value(value)
}