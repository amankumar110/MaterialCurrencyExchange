package `in`.amankumar110.currencyconverterapp.utils

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import `in`.amankumar110.currencyconverterapp.R

class AnimationUtils {

    companion object {
        /**
         * Scales the view in and restores its original size.
         * @param view the view to animate
         * @param duration duration of the animation in milliseconds
         * @param onComplete a Runnable to be executed after the animation completes
         */
        @JvmStatic
        fun scaleInAndRestore(view: View, duration: Long = 150, onComplete: Runnable? = null) {

            val animator =
                AnimatorInflater.loadAnimator(view.context, R.animator.scale_in_and_restore)
            animator.setTarget(view)
            animator.duration = duration
            animator.interpolator = AccelerateDecelerateInterpolator()
            // Create an AnimatorSet to synchronize the two animations
            val animatorSet = AnimatorSet()
            animatorSet.playTogether(animator)

            // Set an end listener to run a Runnable after the animation ends
            animatorSet.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(p0: Animator) {}

                override fun onAnimationEnd(p0: Animator) {
                    onComplete?.run()
                }

                override fun onAnimationCancel(p0: Animator) {}

                override fun onAnimationRepeat(p0: Animator) {}
            })

            // Start the animation
            animatorSet.start()
        }
    }

}
