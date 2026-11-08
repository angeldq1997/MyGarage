package es.angel.mygarage

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import es.angel.mygarage.databinding.ActivityMyvehiclesBinding

class MyVehiclesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyvehiclesBinding
    private lateinit var userDb: UserDbHelper
    private lateinit var vehicleDb: VehicleDbHelper
    private lateinit var prefs: SharedPreferences
    private var userId: Int = -1

    companion object {
        const val PARAMETER_USER = "parameter_user"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyvehiclesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initComponents()
        showVehicles()
        initListeners()
    }

    override fun onResume() {
        super.onResume()
        showVehicles()
    }

    override fun onDestroy() {
        userDb.close()
        vehicleDb.close()
        super.onDestroy()
    }

    private fun initComponents() {
        userDb = UserDbHelper(this)
        vehicleDb = VehicleDbHelper(this)
        prefs = getSharedPreferences("userid", MODE_PRIVATE)
        userId = prefs.getInt("userId", -1)
    }

    private fun initListeners() {


        binding.bMvadd.setOnClickListener {
            val i = Intent(this, VehicleActivity::class.java)
            i.putExtra(VehicleActivity.PARAMETER_TYPE, "add")
            startActivity(i)
        }

        binding.bMvedit.setOnClickListener {
            val i = Intent(this, VehicleActivity::class.java)
            if (binding.lvMvehicles.selectedItemId == -1L) {
                Toast.makeText(this, R.string.mv_e_not_selected, Toast.LENGTH_LONG).show()
            } else {
                i.putExtra(VehicleActivity.PARAMETER_ID, binding.lvMvehicles.selectedItemId)
                startActivity(i)
            }
        }

        binding.bMvremove.setOnClickListener {
            if ( binding.lvMvehicles.selectedItemId != -1L || vehicleDb.remove(binding.lvMvehicles.selectedItemId) == 0 ){
                Toast.makeText(this, "ERROR ERASING", Toast.LENGTH_LONG).show()
            } else {
                showVehicles()
            }
        }

        binding.bMvdetails.setOnClickListener {
            val i = Intent(this, RevisionsActivity::class.java)
            if (binding.lvMvehicles.selectedItemId == -1L) {
                Toast.makeText(this, R.string.mv_e_not_selected, Toast.LENGTH_LONG).show()
            } else {
                //TODO: Modify putExtra for successfully getting plate with binding.lvMvehicles.selectedItem.plate
                i.putExtra(RevisionsActivity.PARAMETER_PLATE, "3577-CVN")
                startActivity(i)
            }
        }
    }

    private fun showVehicles() {
        if (userId != -1) {
            val vehicles = vehicleDb.listAll(userId)
            val text =
                vehicles.map { "${it.plate} - ${it.brand} ${it.model} ${it.year} ${it.mileage} kms" }
            binding.lvMvehicles.adapter =
                ArrayAdapter(this, android.R.layout.simple_selectable_list_item, text)
        }
    }
}