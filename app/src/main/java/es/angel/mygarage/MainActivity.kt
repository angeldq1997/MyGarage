package es.angel.mygarage

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import es.angel.mygarage.databinding.ActivityMainBinding
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var db: UserDbHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initComponents()
        initListeners()
    }

    override fun onDestroy() {
        db.close()
        super.onDestroy()
    }

    private fun initComponents() {
        db = UserDbHelper(this)
    }

    private fun initListeners() {
        binding.bEnter.setOnClickListener {
            resetErrorTexts()
            checkLogIn()
        }

        binding.bRegister.setOnClickListener {
            if (binding.etRepeatPassword.isGone){
                binding.etRepeatPassword.visibility = View.VISIBLE
                Toast.makeText(this, getText(R.string.t_fill_repeat_password), Toast.LENGTH_LONG).show()
            } else{
                resetErrorTexts()
                checkSignIn()
            }
        }

        binding.tvForgetPassword.setOnClickListener {
            val i = Intent(this, ForgetUserActivity::class.java)
            startActivity(i)
        }
    }

    private fun resetErrorTexts() {
        binding.tvEUser.setText(R.string.e_user)
        binding.tvEPassword.setText(R.string.e_password)
        binding.tvEEnd.setText(R.string.e_passwords_matching)

        binding.tvEUser.visibility = View.GONE
        binding.tvEPassword.visibility = View.GONE
        binding.tvEEnd.visibility = View.GONE
    }

    private fun checkLogIn(){
        //USER_EMPTY
        if (binding.etUser.text.isBlank()){
            binding.tvEUser.visibility = View.VISIBLE
            binding.tvEUser.setText(R.string.e_user_empty)

        //PASSWORD_EMPTY
        } else if (binding.etPassword.text.isBlank()){
            binding.tvEUser.visibility = View.VISIBLE
            binding.tvEUser.setText(R.string.e_password_empty)

        //CORRECT_LOGIN
        } else if (userOnDB()){
            val user = db.findUser(binding.etUser.text.toString())
            val i = Intent(this, MyVehiclesActivity::class.java)
            i.putExtra(MyVehiclesActivity.PARAMETER_USER, user?.username)

            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder
                .setMessage(R.string.t_correct_login)
                .setTitle(R.string.m_d_success_login)
                .setNegativeButton(R.string.m_d_cancel_login) { dialog, which ->
                    Toast.makeText(this, getText(R.string.t_canceled_login), Toast.LENGTH_LONG).show()
                }
                .setPositiveButton(R.string.m_d_confirm_login) { dialog, which ->
                    Toast.makeText(this, getText(R.string.t_correct_login), Toast.LENGTH_LONG).show()
                    startActivity(i)
                }
            val dialog: AlertDialog = builder.create()
            dialog.show()

        //NOT_MATCHING_USER_PASSWORD
        } else {
            binding.tvEPassword.visibility = View.VISIBLE
            binding.tvEPassword.setText(R.string.e_incorrect_user_password)
        }
    }

    private fun checkSignIn() {
        //USER_EMPTY
        if (binding.etUser.text.isBlank()){
            binding.tvEUser.visibility = View.VISIBLE
            binding.tvEUser.setText(R.string.e_user_empty)

        //PASSWORD_EMPTY
        } else if (binding.etPassword.text.isBlank()){
            binding.tvEUser.visibility = View.VISIBLE
            binding.tvEUser.setText(R.string.e_password_empty)

        //REPEAT_PASSWORD_EMPTY
        }else if (binding.etRepeatPassword.text.isBlank()) {
            binding.tvEEnd.visibility = View.VISIBLE
            binding.tvEEnd.setText(R.string.e_rep_password_empty)

        //REPEAT_PASSWORDS_NOT_MATCHING
        }else if (!matchingPasswords()) {
            binding.tvEEnd.visibility = View.VISIBLE
            binding.tvEEnd.setText(R.string.e_passwords_matching)

        //USER_ALREADY_ON_DB
        } else if (userOnDB()){
            binding.tvEEnd.visibility = View.VISIBLE
            binding.tvEEnd.setText(R.string.e_user_already_exists)

        //CORRECT_SIGN_IN
        } else if (matchingPasswords()) {
            val user = User(
                username = binding.etUser.text.toString(),
                password = sha256(binding.etPassword.text.toString())
            )
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder
                .setMessage(R.string.t_correct_sign_in)
                .setTitle(R.string.m_d_success_sign_in)
                .setNegativeButton(R.string.m_d_cancel_sign_in) { dialog, which ->
                    Toast.makeText(this, R.string.t_canceled_sign_in, Toast.LENGTH_LONG).show()
                }
                .setPositiveButton(R.string.m_d_confirm_sign_in) { dialog, which ->
                    db.insert(user)
                    Toast.makeText(this, R.string.t_correct_sign_in, Toast.LENGTH_LONG).show()
                }
            val dialog: AlertDialog = builder.create()
            dialog.show()

        //ERROR SIGNING IN
        } else {
            binding.tvEEnd.visibility = View.VISIBLE
            binding.tvEEnd.setText(R.string.e_sign_in)
        }
    }

    private fun matchingPasswords(): Boolean = ( binding.etPassword.text.toString() == binding.etRepeatPassword.text.toString() )

    private fun userOnDB() : Boolean{
        var foundUser = false
        val user = db.findUser(binding.etUser.text.toString())
        if ( user != null && user.password == sha256(binding.etPassword.text.toString()) ){
            foundUser = true
        }
        return foundUser
    }

    @Throws(NoSuchAlgorithmException::class)
    fun sha256(password: String): String {
        var sha: MessageDigest? = null
        sha = MessageDigest.getInstance("SHA-256")
        val digest = sha.digest(password.toByteArray())

        val hexString = StringBuilder()

        for (b in digest) {
            hexString.append(String.format("%02x", b))
        }
        return hexString.toString()
    }
}