package com.example.network




object AppLogger {

    private const val MSG_LENGTH_LIMIT = 3000

    val isDebugEnabled: Boolean
        get() = true

    fun networkLog(message: String?) {
        if ((message?.length ?: 0) > MSG_LENGTH_LIMIT) {
            handleLongLogMessage(message ?: EMPTY_STRING, null)
            return
        }
        d(message = message)
    }

    fun d(
        throwable: Throwable? = null,
        exception: Exception? = null,
        message: String? = null,
    ) {

        logOnFirebase(throwable, exception, message)
    }

    fun e(
        throwable: Throwable? = null,
        exception: Exception? = null,
        message: String? = null,
    ) {

        logOnFirebase(throwable, exception, message)
    }

    fun w(
        throwable: Throwable? = null,
        exception: Exception? = null,
        message: String? = null,
    ) {
//        Timber.w(throwable)
//        Timber.w(exception)
//        Timber.w(message)
        logOnFirebase(throwable, exception, message)
    }

    fun v(
        throwable: Throwable? = null,
        exception: Exception? = null,
        message: String? = null,
    ) {
//        Timber.v(throwable)
//        Timber.v(exception)
//        Timber.v(message)
        logOnFirebase(throwable, exception, message)
    }

    private fun logOnFirebase(
        throwable: Throwable? = null,
        exception: Exception? = null,
        message: String? = null
    ) {
        //
    }

    private fun handleLongLogMessage(msg: String, throwable: Throwable?) {
        // Split up and log each substring separately...
        var message = msg
        while (message.length > MSG_LENGTH_LIMIT) {
            try {
                networkLog(message.substring(0, MSG_LENGTH_LIMIT))
                message = message.substring(MSG_LENGTH_LIMIT)
            } catch (e: OutOfMemoryError) {
                return
            }
        }
        // Log the last remaining substring < MSG_LENGTH_LIMIT
        if (message.isNotEmpty()) {
            networkLog(message)
        }
        // If there was a throwable, we log this last (otherwise it will split up the long msg)
        if (throwable != null) {
            d(throwable)
        }
    }
}
