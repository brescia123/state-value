# State Value

[![Build Status](https://travis-ci.org/brescia123/state-value.svg?branch=master)](https://travis-ci.org/brescia123/state-value)

Tools to represent values that can be found in multiple states and display them easily in Android.

## StateValue

StateValue is a wrapper type that allows you to easily represent values which are loaded 
asynchronously and which can be then in various states depending on the asynchronous call,
such as a loading or error. It allows you to represent six states described here:

- `NoValue`
    - `Missing`: The value is missing and it is not loading nor in error
    - `Loading` (`LoadingState`): The value is loading and it does not encapsulate a previous loaded value
    - `Error` (`ErrorState`): The value was not loaded because of an error and it does not encapsulate a previous loaded value
- `WithValue`
    - `Loading` (`LoadingState`): The value is loading and it does encapsulate a previous loaded value
    - `Error` (`ErrorState`): The value was not loaded because of an error but it does encapsulate a previous loaded value
    - `Value`: The value is loaded
    
Cases implementing `ErrorState` could provide an object `E` to provide more info about the error
Cases implementing `LoadingState could provide a `LoadingState.Progress` representing the progress
of the loading process from 0 to 100.

It provides also methods to move a `StateValue` into another state respecting the semantic of the type:

- `copyToMissing()` 
- `copyToLoading()` 
- `copyToError()` 
- `copyToValue()` 

## StateValueView

TODO
