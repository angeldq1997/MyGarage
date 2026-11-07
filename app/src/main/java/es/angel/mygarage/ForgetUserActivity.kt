package es.angel.mygarage

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import es.angel.mygarage.databinding.ActivityForgetUserBinding
import java.util.regex.Pattern

class ForgetUserActivity : AppCompatActivity() {

    private lateinit var binding : ActivityForgetUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgetUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initListeners()
    }

    private fun initListeners() {
        binding.bEnterF.setOnClickListener {
            val emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$"
            val p : Pattern = Pattern.compile(emailRegex)
            val email : String = binding.etEmailF.text.toString()
            if ( email.isNotEmpty() && p.matcher(email).matches()){
                val emailBody = (getText(R.string.s_email_body).toString()+binding.etUserF.text.toString()+"\n"+getText(R.string.s_email_cont)+binding.etEmailF.text.toString())

                val intent = Intent(Intent.ACTION_SEND)

                intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getText(R.string.s_email_send)) )
                intent.putExtra(Intent.EXTRA_SUBJECT, getText(R.string.s_email_subject))
                intent.putExtra(Intent.EXTRA_TEXT, emailBody)

                intent.type = "message/rfc6854"

                startActivity(Intent.createChooser(intent, "Choose an Email client: "))
            } else {
                Toast.makeText(this, getText(R.string.e_email), Toast.LENGTH_LONG).show()
            }
        }

        binding.bExitF.setOnClickListener {
            Toast.makeText(this, getText(R.string.h_exit), Toast.LENGTH_LONG).show()
            finish()
        }
    }
}