package es.angel.mygarage

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import es.angel.mygarage.databinding.ActivityVehicleBinding
import java.time.Year

class VehicleActivity : AppCompatActivity() {
    private lateinit var actualVehicle: Vehicle
    private lateinit var binding : ActivityVehicleBinding
    private lateinit var vehicleDb: VehicleDbHelper
    private lateinit var prefs: SharedPreferences
    private var plate : String = ""
    private var userId : Int = -1
    companion object{
        const val PARAMETER_PLATE = "parameter_plate"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVehicleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initComponents()
        initListeners()
    }

    private fun initComponents() {
        vehicleDb = VehicleDbHelper(this)
        prefs = getSharedPreferences("userid", MODE_PRIVATE)
        userId = prefs.getInt("userId", -1)

        if ( intent.getStringExtra(PARAMETER_PLATE) == "NEW_VEHICLE" ){

            binding.bChange.setText(R.string.v_button_change_add)

        } else if (intent.getStringExtra(PARAMETER_PLATE) != null && intent.getStringExtra(PARAMETER_PLATE)!!.isNotEmpty() ) {

            binding.bChange.setText(R.string.v_button_change)
            plate = intent.getStringExtra(PARAMETER_PLATE).toString()
            if( vehicleDb.findVehicle(plate) != null ){
                actualVehicle = vehicleDb.findVehicle(plate)!!
                binding.etVplate.setText(actualVehicle.plate)
                binding.etVbrand.setText(actualVehicle.brand)
                binding.etVmodel.setText(actualVehicle.model)
                binding.etVyear.setText(actualVehicle.year.toString())
                binding.etVmileage.setText(actualVehicle.mileage.toString())
            }
        }
    }

    private fun initListeners() {
        binding.bChange.setOnClickListener {
            if ( allTextFilled() ){
                updateOrInsertVehicle()
            } else {
                Toast.makeText(this, R.string.v_e_missing_text, Toast.LENGTH_LONG).show()
            }
        }

        binding.bCancel.setOnClickListener {
            finish()
        }
    }

    private fun updateOrInsertVehicle() {
        if (userId != -1) {
            if (!validVehicle( binding.etVplate.text.toString(), binding.etVyear.text.toString(), binding.etVmileage.text.toString() )) {
                Toast.makeText(this, R.string.v_e_cannot_insert_vehicle, Toast.LENGTH_LONG).show()
            } else {
                val vehicle = Vehicle(
                    0,
                    binding.etVplate.text.toString(),
                    binding.etVbrand.text.toString(),
                    binding.etVmodel.text.toString(),
                    binding.etVyear.text.toString().toInt(),
                    binding.etVmileage.text.toString().toInt(),
                    userId
                )

                if (intent.getStringExtra(PARAMETER_PLATE) == "NEW_VEHICLE") {
                    insertVehicle(vehicle)
                } else {
                    updateVehicle(vehicle)
                }
            }

        } else {
            Toast.makeText(this, "ERROR WITH USER ID", Toast.LENGTH_LONG).show()
        }
    }

    private fun updateVehicle(vehicle: Vehicle) {
        if (vehicleDb.update(actualVehicle, vehicle) == 0) {
            Toast.makeText(this, R.string.v_e_cannot_obtain_vehicle, Toast.LENGTH_LONG).show()
        } else {
            finish()
        }
    }

    private fun insertVehicle(vehicle: Vehicle) {
        if (vehicleDb.insert(vehicle) == -1L) {
            Toast.makeText(this, R.string.v_e_cannot_insert_vehicle, Toast.LENGTH_LONG).show()
        } else {
            finish()
        }
    }

    private fun validVehicle(plate : String, year : String, mileage : String) : Boolean {
        if (!validPlate(plate)){
            Toast.makeText(this, R.string.v_e_invalid_plate, Toast.LENGTH_LONG).show()
            return false
        }
        if (!validYear(year)){
            Toast.makeText(this, R.string.v_e_invalid_year, Toast.LENGTH_LONG).show()
            return false
        }
        if (!validMileage(mileage)){
            Toast.makeText(this, R.string.v_e_invalid_mileage, Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    private fun validPlate (plate: String): Boolean {
        return plate.matches("\\d{4}-\\w{3}".toRegex())
    }

    private fun validYear (year: String): Boolean {
        return year.toInt() > 1950 && year.toInt() <= Year.now().toString().toInt()
    }

    private fun validMileage (mileage: String): Boolean {
        return mileage.toInt() in 1..999999
    }

    private fun allTextFilled(): Boolean {
        return binding.etVplate.text.isNotEmpty() && binding.etVbrand.text.isNotEmpty() && binding.etVmodel.text.isNotEmpty()
                && binding.etVyear.text.isNotEmpty() && binding.etVmileage.text.isNotEmpty()
    }
}