package org.quick.async

import android.os.Handler
import android.os.Looper
import org.quick.async.callback.OnASyncListener
import org.quick.async.callback.OnIntervalListener
import java.util.concurrent.Executors

/**
 * @describe 方便的使用简单异步，此类只做简单异步封装。
 *           更复杂的场景推荐使用RxAndroid：https://github.com/ReactiveX/RxAndroid
 * @author ChrisZou
 * @date 2018/08/03-10:16
 * @from https://github.com/SpringSmell/QuickAsync
 * @email chrisSpringSmell@gmail.com
 */
object Async {

    private val executorService = Executors.newFixedThreadPool(50)
    val mainHandler: Handler by lazy { return@lazy Handler(Looper.getMainLooper()) }

    fun runOnUiThread(onRunOnUiThreadListener: () -> Unit) {
        mainHandler.post { onRunOnUiThreadListener.invoke() }
    }

    /**
     * 异常线程处理数据，然后返回值
     */
    fun <T> action(onASyncListener: OnASyncListener<T>) = executorService.submit {
        try {
            val value = onASyncListener.onASync()
            runOnUiThread { onASyncListener.onAccept(value) }
        } catch (O_O: Exception) {
            runOnUiThread { onASyncListener.onError(O_O) }
        }
    }

    /**
     * 秒表计步，for example:60秒（1....60）
     * @param interval 间隔时间，单位：毫秒
     * @param maxSteps 最大步数
     * @param isReversal 是否反转 （59....0）
     */
    fun time(
        onIntervalListener: OnIntervalListener<Long>,
        interval: Long,
        maxSteps: Long,
        isReversal: Boolean = false
    ) = executorService.submit {
        var steps = if (isReversal) maxSteps else 0L
        if (isReversal)
            while (steps > 0) {
                steps--
                runOnUiThread { onIntervalListener.onNext(steps) }
                Thread.sleep(interval)
            }
        else
            while (steps < maxSteps) {
                steps++
                runOnUiThread { onIntervalListener.onNext(steps) }
                Thread.sleep(interval)
            }
        runOnUiThread { onIntervalListener.onAccept(steps) }
    }

    /**
     * 延迟执行
     * @param delayTime 延迟时间（毫秒）
     */
    fun delay(onEndListener: () -> Unit, delayTime: Long) {
        mainHandler.postDelayed({
            onEndListener.invoke()
        }, delayTime)
    }

    /**
     * 循环执行，如果不调用cancel方法永不停止
     * onASync with false break
     * @param interval 间隔时间（毫秒）
     */
    fun loop(interval: Long,onASyncListener: OnASyncListener<Boolean>) = executorService.submit {
//        var steps = 0
        while (true) {
//            steps++
            if (onASyncListener.onASync()) runOnUiThread { onASyncListener.onAccept(true) } else break
            Thread.sleep(interval)
        }
    }




}