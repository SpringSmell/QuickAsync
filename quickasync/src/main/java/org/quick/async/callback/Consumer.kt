package org.quick.async.callback

interface Consumer<T> {
    /**
     * Consume the given value.
     * @param value the value
     * @throws Exception on error
     */
    @Throws(Exception::class)
    fun onAccept(value: T)
}