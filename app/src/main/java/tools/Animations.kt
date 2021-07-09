package tools

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.View
import android.view.animation.OvershootInterpolator

class Animations {

    companion object {
        fun showView (view : View) {
            val objectAnimatorScaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0.8f, 1f)
            val objectAnimatorScaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.8f, 1f)
            val objectAnimatorAlpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0.5f, 1f)
            ObjectAnimator.ofPropertyValuesHolder(view, objectAnimatorScaleX, objectAnimatorScaleY, objectAnimatorAlpha).apply {
                interpolator = OvershootInterpolator()
                duration = 180
            }.start()
        }
        fun hideView (view : View) {
            val objectAnimatorScaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 0f)
            val objectAnimatorScaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 01f, 0f)
            val objectAnimatorAlpha = PropertyValuesHolder.ofFloat(View.ALPHA, 1f, 0f)
            ObjectAnimator.ofPropertyValuesHolder(view, objectAnimatorScaleX, objectAnimatorScaleY, objectAnimatorAlpha).apply {
                interpolator = OvershootInterpolator()
                duration = 180
            }.start()
        }
    }
}