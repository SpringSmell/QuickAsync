package org.quick.async.callback

interface OnASyncListener<T> : Consumer<T> {
    fun onASync(): T
    fun onError(O_O: Exception) {}
}