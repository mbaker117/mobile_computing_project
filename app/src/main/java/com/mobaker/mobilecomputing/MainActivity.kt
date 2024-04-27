package com.mobaker.mobilecomputing

import LocationUtil
import NotificationUtil
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import com.google.firebase.database.DatabaseError

import com.mobaker.mobilecomputing.databinding.ActivityMainBinding
import com.mobaker.mobilecomputing.enums.UserRole
import com.mobaker.mobilecomputing.models.User
import com.mobaker.mobilecomputing.callback.UserFirebaseCallback
import com.mobaker.mobilecomputing.services.IUserService
import com.mobaker.mobilecomputing.utils.AesEncryptionUtil
import com.psut.farmjo.util.SharedPreferencesUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var userService: IUserService
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(!LocationUtil.isLocationPermissionGranted(this)){
            LocationUtil.requestLocationPermission(this)
        }

        if(!NotificationUtil.isNotificationPermissionGranted(this)){
            NotificationUtil.requestNotificationPermission(this)
        }
        binding.loginBtn.setOnClickListener {
            login()
        }

        binding.signUpBtn.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }
    }

    private fun login() {
        userService.getUserByUsernameAndPassword(binding.userNameET.text.toString(),
            AesEncryptionUtil.encrypt(binding.passwordET.text.toString())!!,
            object : UserFirebaseCallback {

                override fun onDataReceived(user: User) {
                    Toast.makeText(this@MainActivity, " Login Success", Toast.LENGTH_LONG)
                        .show()
                    goToNextActivity(user)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@MainActivity, error.message, Toast.LENGTH_LONG).show()

                }

                override fun onError(error: String) {
                    Toast.makeText(this@MainActivity, error, Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun goToNextActivity(user: User?) {
        val targetActivity = if(user?.userRole == UserRole.MANAGER) ManagerActivity::class.java else WorkerActivity::class.java
        val intent = Intent(this, targetActivity)
        SharedPreferencesUtil.addObjectToSharedPreferences(this, "loginUser", user!!)
        startActivity(intent)
        finish()

    }
}
