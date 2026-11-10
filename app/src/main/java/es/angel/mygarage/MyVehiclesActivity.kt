package es.angel.mygarage

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import es.angel.mygarage.databinding.ActivityMyvehiclesBinding

class MyVehiclesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyvehiclesBinding
    private lateinit var userDb: UserDbHelper
    private lateinit var vehicleDb: VehicleDbHelper
    private lateinit var prefs: SharedPreferences
    private var plate : String = ""
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
        binding.lvMvehicles.setOnItemClickListener{ parent, _, position, _ ->
            val element : String = parent.getItemAtPosition(position) as String
            plate = element.substring(0, 8)
        }

        binding.bMvadd.setOnClickListener {
            val i = Intent(this, VehicleActivity::class.java)
            i.putExtra(VehicleActivity.PARAMETER_PLATE, "NEW_VEHICLE")
            startActivity(i)
        }

        binding.bMvedit.setOnClickListener {
            val i = Intent(this, VehicleActivity::class.java)
            if (plate.isEmpty() || vehicleDb.findVehicle(plate) == null) {
                Toast.makeText(this, R.string.mv_e_not_selected, Toast.LENGTH_LONG).show()
            } else {
                i.putExtra(VehicleActivity.PARAMETER_PLATE, plate)
                startActivity(i)
            }
        }

        binding.bMvremove.setOnClickListener {
            if ( plate.isEmpty() || vehicleDb.remove(plate) == 0){
                Toast.makeText(this, "ERROR ERASING", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Erased correctly, vehicle with plate: $plate D:", Toast.LENGTH_LONG).show()
                showVehicles()
            }
        }

        binding.bMvdetails.setOnClickListener {
            val i = Intent(this, RevisionsActivity::class.java)
            if (binding.lvMvehicles.selectedItemId == AdapterView.INVALID_ROW_ID) {
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
            val text = vehicles.map { "${it.plate} - ${it.brand} ${it.model} ${it.year} ${it.mileage} kms" }
            binding.lvMvehicles.adapter = ArrayAdapter(this, android.R.layout.simple_selectable_list_item, text)
        }
    }
}