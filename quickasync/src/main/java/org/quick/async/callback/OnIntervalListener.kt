package org.quick.async.callback

interface OnIntervalListener<T> : Consumer<T> {
    fun onNext(value: T)
}