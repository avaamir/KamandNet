package mp.amir.ir.kamandnet.ui.activities.customs.osm

import android.view.View
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mp.amir.ir.kamandnet.R
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.OverlayWithIW
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow


class DriverInfoWindow(
    mapView: MapView
) : MarkerInfoWindow(R.layout.my_map_info, mapView) {

    override fun onOpen(item: Any) {
        super.onOpen(item)
        val overlay = item as OverlayWithIW
        val title = overlay.title
        val tvTitle = mView.findViewById<TextView>(R.id.bubble_title)
        if (title.isNullOrBlank()) {
            tvTitle.visibility = View.GONE
        } else {
            tvTitle.visibility = View.VISIBLE
            //Title dar super.onOpen set mishe va niazi nist dg setesh konam
        }

        if (mapView.overlays.size < 100) {
            mapView.overlays.forEach {
                if (it is Marker) {
                    if (it.isInfoWindowShown) {
                        it.closeInfoWindow()
                    }
                }
            }
        } else { //vase in ke age tedad marker ha ziad bud UI freeze nashe
            CoroutineScope(Main).launch {
                mapView.overlays.forEach {
                    if (it is Marker) {
                        if (it.isInfoWindowShown) {
                            withContext(Main) {
                                it.closeInfoWindow()
                            }
                        }
                    }
                }
            }
        }


    }
}