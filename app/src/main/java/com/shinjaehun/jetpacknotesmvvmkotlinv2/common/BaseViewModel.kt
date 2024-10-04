package com.shinjaehun.jetpacknotesmvvmkotlinv2.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel<T>(
    protected val uiContext:CoroutineContext
) : ViewModel(), CoroutineScope {
    // 여기서 궁금한 점 ViewModel은 ViewModel(), CoroutineScope은 CoroutineScope인 까닭은?
    abstract fun handleEvent(event: T)

    protected lateinit var jobTracker: Job

    init {
        jobTracker = Job()
    }

    //suggestion from Al Warren:
    //to promote encapsulation and immutability, hide the MutableLiveData objects behind
    //LiveData references:
    protected val errorState = MutableLiveData<String>()
    val error: LiveData<String> get() = errorState

    protected val loadingState = MutableLiveData<Unit>()
    val loading: LiveData<Unit> get() = loadingState

    // 근데 이게 뭔지 모르겠어요... 왜 context를 더하는지........!!
    override val coroutineContext: CoroutineContext
        get() = uiContext + jobTracker

}