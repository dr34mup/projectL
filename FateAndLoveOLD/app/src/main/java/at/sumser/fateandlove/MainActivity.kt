package at.sumser.fateandlove

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.view.View
import android.widget.Toast
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback

    val REQUEST_CODE = 1000

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            REQUEST_CODE->(
                    if(grantResults.size > 0)
                        if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                            Toast.makeText(this@MainActivity, "Permission granted", Toast.LENGTH_SHORT)
                                    .show()
                    else
                            Toast.makeText(this@MainActivity, "Permission denied", Toast.LENGTH_SHORT)
                                    .show()
                    )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //check permissions
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {

            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
        } else {
            buildLocationRequest()
            buildLocationCallBack()

            //Create Provider
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

            //set event
            btn_start_updates.setOnClickListener(View.OnClickListener {

                if (ActivityCompat.checkSelfPermission(this@MainActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this@MainActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this@MainActivity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
                    return@OnClickListener
                }

                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())

                //Change state of button
                btn_start_updates.isEnabled = !btn_start_updates.isEnabled
                btn_stop_updates.isEnabled = !btn_stop_updates.isEnabled
            })

            btn_stop_updates.setOnClickListener(View.OnClickListener {
                if (ActivityCompat.checkSelfPermission(this@MainActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this@MainActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this@MainActivity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
                    return@OnClickListener
                }

                fusedLocationProviderClient.removeLocationUpdates(locationCallback)

                //Change state of button
                btn_start_updates.isEnabled = !btn_start_updates.isEnabled
                btn_stop_updates.isEnabled = !btn_stop_updates.isEnabled

            })

        }

    }

    private fun buildLocationCallBack(){
        locationCallback = object : LocationCallback(){
            //ctrl + 0
            override fun onLocationResult(p0: LocationResult?) {
                var location = p0!!.locations.get(p0.locations.size-1)
                txt_location.text = location.latitude.toString()+"/"+location.longitude.toString()
            }
        }
    }

    private fun buildLocationRequest(){
        locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 3000
        locationRequest.smallestDisplacement = 10f
    }

}


