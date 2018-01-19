package it.gbresciani.statevalue

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import io.kotlintest.properties.Gen.Companion.oneOf
import io.kotlintest.properties.Gen.Companion.string
import io.kotlintest.properties.forAll
import io.kotlintest.specs.ShouldSpec
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class StateValueTest : ShouldSpec() {
      init {
            "StateValue.copyToMissing" {
                  should("return always a StateValue.NoValue.Missing") {
                        forAll(StateValueGen.stateValueGen(Gen.int())) { stateValue ->
                              stateValue.copyToMissing() == StateValue.NoValue.Missing<Int>()
                        }
                  }
            }

            "StateValue.copyToLoading" {

                  should("return always a StateValue.NoValue.Loading when called on a StateValue.NoValue") {
                        forAll(StateValueGen.noValueGen<Int>(), StateValueGen.progressGen()) { stateValue, progress ->
                              stateValue.copyToLoading(progress) == StateValue.NoValue.Loading<Int>(progress)
                        }
                  }
                  should("return always a StateValue.WithValue.Loading when called on a StateValue.WithValue") {
                        forAll(StateValueGen.withValueGen(Gen.int()), StateValueGen.progressGen()) { stateValue, progress ->
                              stateValue.copyToLoading(progress) == StateValue.WithValue.Loading(stateValue.value, progress)
                        }
                  }
            }

            "StateValue.copyToError" {

                  should("return always a StateValue.NoValue.Error when called on a StateValue.NoValue") {
                        forAll(StateValueGen.noValueGen<Int>(), Gen.string()) { stateValue, error ->
                              stateValue.copyToError(error) == StateValue.NoValue.Error<Int, String>(error)
                        }
                  }

                  should("return always a StateValue.WithValue.Error when called on a StateValue.WithValue") {
                        forAll(StateValueGen.withValueGen(Gen.int()), Gen.string()) { stateValue, error ->
                              stateValue.copyToError(error) == StateValue.WithValue.Error(stateValue.value, error)
                        }
                  }
            }




            "StateValue.copyToValue" {
                  should("return always a StateValue.WithValue.Value") {
                        forAll(StateValueGen.stateValueGen(Gen.int()), Gen.int()) { stateValue, value ->
                              stateValue.copyToValue(value) == StateValue.WithValue.Value(value)
                        }
                  }
            }
      }

      object StateValueGen {

            fun progressGen() = object : Gen<StateValue.LoadingState.Progress> {
                  override fun generate(): StateValue.LoadingState.Progress =
                      StateValue.LoadingState.Progress(oneOf((0..100).toList()).generate())
            }

            fun <T> noValueGen() = object : Gen<StateValue.NoValue<T>> {
                  override fun generate(): StateValue.NoValue<T> = when (oneOf(listOf(0, 1)).generate()) {
                        0 -> StateValue.NoValue.Missing()
                        1 -> StateValue.NoValue.Loading(oneOf(listOf(progressGen().generate(), null)).generate())
                        else -> StateValue.NoValue.Error(oneOf(listOf(string().generate(), null)).generate())
                  }
            }

            fun <T> withValueGen(tGen: Gen<T>) = object : Gen<StateValue.WithValue<T>> {
                  override fun generate(): StateValue.WithValue<T> = when (oneOf(listOf(0, 1)).generate()) {
                        0 -> StateValue.WithValue.Loading(tGen.generate(), oneOf(listOf(progressGen().generate(), null)).generate())
                        1 -> StateValue.WithValue.Error(tGen.generate(), oneOf(listOf(string().generate(), null)).generate())
                        else -> StateValue.WithValue.Value(tGen.generate())
                  }
            }

            fun <T> stateValueGen(tGen: Gen<T>) = object : Gen<StateValue<T>> {
                  override fun generate(): StateValue<T> = oneOf(listOf(
                      noValueGen<T>(),
                      withValueGen(tGen)
                  )).generate().generate()
            }

      }

}