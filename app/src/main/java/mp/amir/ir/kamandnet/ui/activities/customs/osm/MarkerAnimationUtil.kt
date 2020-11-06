package mp.amir.ir.kamandnet.ui.activities.customs.osm

import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import kotlin.math.abs


class MarkerAnimationUtil { //TODO in faghat vase ye marker dorost kar mikone , dorost ine ke rotateMarker va aniamteMarker dar class marker bashe na inja

    private var rotateAnimator: ValueAnimator? = null
    private var moveMarkerAnimator: ValueAnimator? = null


    private var lastDestRotation = 0f
    private var lastDestLocation: GeoPoint? = null

    fun rotateMarker(
        map: MapView,
        marker: ImageMarker,
        destRotation: Float,
        interpolator: TimeInterpolator = LinearInterpolator(),
        duration: Long = 500L
    ) {
        /*rotation: jam shodan => anti clock wise , tafrigh shdan => clock wise*/


        if (destRotation == lastDestRotation) {
            return
        }

        lastDestRotation = destRotation
        rotateAnimator?.cancel()

        val startRotation = marker.rotation
        if (startRotation == destRotation) {
            return
        }

        /*if (destRotation > 360) {
            destRotation %= 360
        }
        if (startRotation > 360) {
            startRotation %= 360
        }*/

        var diff = destRotation - startRotation
        if (abs(diff) > 180) {
            if (diff > 0) {
                diff -= 360
            } else {
                diff += 360
            }
        }

        rotateAnimator = ValueAnimator.ofFloat(0f, 1f).also { animator ->
            animator.duration = duration
            animator.interpolator = interpolator
            animator.addUpdateListener {
                marker.rotation =
                    startRotation + ((diff) * it.animatedFraction)
                map.postInvalidate()
            }
            animator.start()
        }

    }

    fun animateMarker(
        map: MapView,
        marker: ImageMarker,
        destGeoPoint: GeoPoint,
        interpolator: TimeInterpolator = LinearInterpolator(),
        duration: Long = 1000L
    ) {

        if ((lastDestLocation?.latitude == destGeoPoint.latitude) && (lastDestLocation?.longitude == destGeoPoint.longitude)) {
            return
        }

        moveMarkerAnimator?.cancel()
        lastDestLocation = destGeoPoint

        if ((marker.position.latitude == destGeoPoint.latitude) && (marker.position.longitude == destGeoPoint.longitude))
            return

        //val projection = mBinding.map.projection
        //val startPoint = projection.toPixels(marker.position, null)
        val startGeoPoint = marker.position //projection.fromPixels(startPoint.x, startPoint.y)
        moveMarkerAnimator = ValueAnimator.ofFloat(0f, 1f).also { animator ->
            animator.interpolator = interpolator
            animator.duration = duration

            animator.addUpdateListener {
                val weight = it.animatedFraction
                val lng = weight * destGeoPoint.longitude + (1 - weight) * startGeoPoint.longitude
                val lat = weight * destGeoPoint.latitude + (1 - weight) * startGeoPoint.latitude

                marker.position = GeoPoint(lat, lng)
                map.postInvalidate()
            }
            animator.start()
        }
    }

}