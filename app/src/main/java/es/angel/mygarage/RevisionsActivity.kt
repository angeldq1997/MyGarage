package es.angel.mygarage

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar
import es.angel.mygarage.databinding.ActivityRevisionsBinding


class RevisionsActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRevisionsBinding
    companion object{
        const val PARAMETER_PLATE = "parameter_plate"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //Necessary to move the toolbar and see everything on screen
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding = ActivityRevisionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val myBar : MaterialToolbar = findViewById(R.id.appointment_bar)
        setSupportActionBar(myBar)
        supportActionBar?.title = intent.getStringExtra("parameter_plate")
        myBar.inflateMenu(R.menu.menu_bar)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(selection: MenuItem): Boolean {
        val id = selection.itemId

        if (id == R.id.option_add) {
            //TODO: Write the code to move to another activity (repair)
        }
        if (id == R.id.option_remove) {
            //TODO: Write the code to remove the appointment to the database
        }
        return super.onOptionsItemSelected(selection)
    }
}