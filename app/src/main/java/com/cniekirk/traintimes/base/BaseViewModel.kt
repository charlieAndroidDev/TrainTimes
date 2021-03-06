package com.cniekirk.traintimes.base

import androidx.lifecycle.ViewModel
import com.cniekirk.traintimes.domain.Failure
import timber.log.Timber

/**
 * Base class fro all ViewModel instances to inherit from
 * It handles the failure case for all operations
 */
abstract class BaseViewModel: ViewModel() {

    val failure: SingleLiveEvent<Failure> = SingleLiveEvent()

    /**
     * @param failure: The failure associated with the failed operation
     * Logs the error to Logcat
     */
    protected fun handleFailure(failure: Failure) {
        Timber.e("Failure: $failure")
        this.failure.value = failure
    }

}