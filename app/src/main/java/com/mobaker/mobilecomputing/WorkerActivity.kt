package com.mobaker.mobilecomputing

import DistanceCalculator
import LocationUtil
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DatabaseError
import com.mobaker.mobilecomputing.callback.TaskFirebaseCallback
import com.mobaker.mobilecomputing.databinding.ActivityWorkerBinding
import com.mobaker.mobilecomputing.models.Task
import com.mobaker.mobilecomputing.models.User
import com.mobaker.mobilecomputing.services.ITaskService
import com.psut.farmjo.util.SharedPreferencesUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

import android.Manifest
import android.content.Intent
import androidx.core.app.NotificationCompat.PRIORITY_MAX
import kotlin.math.roundToInt


@AndroidEntryPoint
class WorkerActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityWorkerBinding
    private val CHANNEL_ID = "my_channel_id"
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationCallback: LocationCallback? = null
    private lateinit var notificationManager: NotificationManager



    @Inject
    lateinit var taskService: ITaskService

    private var currentLocation: Location? = null

    private val taskMap: MutableMap<String, Task> = LinkedHashMap()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWorkerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        notificationManager= getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        createNotificationChannel()

        if(!LocationUtil.isLocationPermissionGranted(this)) {
            LocationUtil.requestLocationPermission(this)
        }


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Set up location callback
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {


                val location = locationResult.lastLocation
                if(location == null || currentLocation == null ){
                    return
                }


              val distance =   DistanceCalculator.calculateDistance(
                  currentLocation!!.latitude, currentLocation!!.longitude, location.latitude,
                    location.longitude
                )

                if (distance <= 20) {
                    return
                }
                this@WorkerActivity.currentLocation = location
                showNearestTasks()
            }
        }
        binding.workerLogoutBtn.setOnClickListener {
            SharedPreferencesUtil.remove(this)
            stopLocationUpdates()
            locationCallback = null
            taskService.reset()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()

        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "My Channel", NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.isMyLocationEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = true
        if (LocationUtil.isLocationPermissionGranted(this)) {
            // Permission is granted, get current location
            LocationUtil.getCurrentLocation(this) { location ->
                this.currentLocation = location
                mMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            location.latitude, location.longitude
                        ), 15f
                    )
                )
                showNearestTasks()

            }
        }

        addTasks()
        mMap.setOnMarkerClickListener { marker ->
            val task = taskMap[marker.id]
            task?.let { showMarkerDialog(it) }
            true
        }

    }

    private fun showNearestTasks() {
        if (currentLocation == null) {
            return
        }

        for (task in taskMap.values) {
            val distance = DistanceCalculator.calculateDistance(
                currentLocation!!.latitude,
                currentLocation!!.longitude,
                task.latitude!!,
                task.longitude!!
            )
            if (distance <= 3000.0) {
                sendNotification(task, distance)
            }

        }

    }

    private fun sendNotification(task: Task, distance: Double) {
        val notificationBuilder =
            NotificationCompat.Builder(this, CHANNEL_ID).setContentTitle("You are near ${task.name}")
                .setContentText("You are ${distance.roundToInt()} m away from this task").setSmallIcon(R.drawable.ic_notification)
                .setPriority(PRIORITY_MAX)

        notificationManager.notify(task.hashCode(), notificationBuilder.build())
    }


    private fun addTasks() {
         taskService.getByIsCompleted(false,  object:TaskFirebaseCallback{
            override fun onDataReceived(tasks: List<Task>) {

                mMap.clear()
                taskMap.clear()
                for (task in tasks) {
                    val location = LatLng(task.latitude!!, task.longitude!!)
                    val marker = mMap.addMarker(
                        MarkerOptions().position(location).title(task.name)
                            .snippet(task.description)
                    )
                    marker?.id?.let {
                        taskMap.put(it, task)
                    }
                }
                showNearestTasks()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@WorkerActivity, "request canceled", Toast.LENGTH_LONG).show()
            }

            override fun onError(error: String) {
                Toast.makeText(this@WorkerActivity, error, Toast.LENGTH_LONG).show()

            }

        })
    }

    private fun showMarkerDialog(task: Task) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(task.name).setMessage(task.description)
            .setPositiveButton("Complete task") { dialog, _ ->
                completeTask(task)
                dialog.dismiss()
            }.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
        val dialog = builder.create()
        dialog.show()
    }

    private fun completeTask(task: Task) {
        task.isCompleted = true
        task.completedBy =
            SharedPreferencesUtil.getObject(this, "loginUser", User::class.java).username
        taskService.update(task)
    }


    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = 30000 // Update interval in milliseconds
            fastestInterval = 30000 // Fastest update interval in milliseconds
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        locationCallback?.let {
            fusedLocationClient.requestLocationUpdates(
                locationRequest, it, null /* Looper */
            )
        }
    }

    private fun stopLocationUpdates() {
        locationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
    }

    override fun onStart() {
        super.onStart()
        startLocationUpdates()
    }

    override fun onStop() {
        super.onStop()
        taskService.reset()
    }

}