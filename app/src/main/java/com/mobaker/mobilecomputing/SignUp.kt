package com.mobaker.mobilecomputing

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.database.DatabaseError
import com.mobaker.mobilecomputing.databinding.ActivitySignUpBinding
import com.mobaker.mobilecomputing.enums.UserRole
import com.mobaker.mobilecomputing.models.User
import com.mobaker.mobilecomputing.services.IUserService
import com.mobaker.mobilecomputing.callback.UserFirebaseCallback
import com.mobaker.mobilecomputing.utils.AesEncryptionUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SignUp : ComponentActivity() {
    private lateinit var binding: ActivitySignUpBinding

    @Inject
    lateinit var userService: IUserService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.signUpBtn.setOnClickListener {
            signup()
        }
    }

    private fun signup() {
        if(!isValid(binding)){
            Toast.makeText(this, "Invalid Data",Toast.LENGTH_LONG).show()
            return
        }
        val user = User(
            binding.signUpUserNameET.text.toString(),
            AesEncryptionUtil.encrypt(binding.signUpPasswordET.text.toString()),
            if (binding.managerRadio.isChecked) UserRole.MANAGER else UserRole.WORKER,
            binding.nameEditText.text.toString()
        )
        userService.addUser(user, object : UserFirebaseCallback {
            override fun onDataReceived(user: User) {
                Toast.makeText(this@SignUp, "Signed Up successfully", Toast.LENGTH_LONG).show()
                finish()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SignUp, error.message, Toast.LENGTH_LONG).show()
            }

            override fun onError(error: String) {
                Toast.makeText(this@SignUp, error, Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun isValid(binding: ActivitySignUpBinding):Boolean {
        var valid = true
        if(binding.nameEditText.text.toString().isBlank()){
            valid = false
            binding.nameEditText.error = "Invalid Name"
        }

        if(binding.signUpUserNameET.text.toString().isBlank()){
            valid = false
            binding.signUpUserNameET.error = "Invalid username"
        }
        if(binding.signUpPasswordET.text.toString().isBlank()){
            valid = false
            binding.signUpPasswordET.error = "Invalid password"
        }
        if(binding.signUpPasswordET.text.toString() != binding.signUpConfirmPasswordET.text.toString()){
            valid = false
            binding.signUpPasswordET.error = "password don't match"
            binding.signUpConfirmPasswordET.error = "password don't match"
        }


        return valid
    }
}