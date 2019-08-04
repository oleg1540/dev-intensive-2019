package ru.skillbranch.devintensive.extensions

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.graphics.Rect
import android.util.Log
import kotlinx.android.synthetic.main.activity_profile.*
import android.util.DisplayMetrics
import android.os.Build




fun Activity.hideKeyboard() {
    val inputManager: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputManager.hideSoftInputFromWindow(currentFocus.windowToken, InputMethodManager.SHOW_FORCED)
}

fun Activity.isKeyboardOpen(): Boolean {
    return keyboardHeight(this) > 0
}

fun Activity.isKeyboardClosed(): Boolean {
    return keyboardHeight(this) == 0
}

private fun keyboardHeight(activity: Activity): Int {
    val r = Rect()
    activity.window.decorView.getWindowVisibleDisplayFrame(r)

    var offsetY: Int = when(activity.resources.configuration.orientation) {
        1 -> getStatusBarHeight(activity) + getSoftButtonsBarHeight(activity)
        else -> getStatusBarHeight(activity)
    }
    val screenHeight = activity.window.decorView.height
    val heightDifference = screenHeight - (r.bottom - r.top) - offsetY
    Log.d("M_Keyboard Size", "Size: $heightDifference")
    return heightDifference
}

private fun getStatusBarHeight(activity: Activity): Int {
    var result = 0
    val resourceId = activity.resources
        .getIdentifier(
            "status_bar_height",
            "dimen", "android"
        )
    if (resourceId > 0) {
        result = activity.resources.getDimensionPixelSize(resourceId)
    }
    return result
}
private fun getSoftButtonsBarHeight(activity: Activity): Int {
    // getRealMetrics is only available with API 17 and +
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        val metrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(metrics)
        val usableHeight = metrics.heightPixels
        activity.windowManager.defaultDisplay.getRealMetrics(metrics)
        val realHeight = metrics.heightPixels
        return if (realHeight > usableHeight)
            realHeight - usableHeight
        else
            0
    }
    return 0
}