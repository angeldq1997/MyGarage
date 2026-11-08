package es.angel.mygarage

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import es.angel.mygarage.databinding.ActivityVehicleBinding

class VehicleActivity : AppCompatActivity() {
    private lateinit var binding : ActivityVehicleBinding
    private lateinit var vehicleDb: VehicleDbHelper
    private var actualVehicle : Vehicle? = null
    private lateinit var prefs: SharedPreferences
    private var userId : Int = -1
    companion object{
        const val PARAMETER_TYPE = "parameter_type"
        const val PARAMETER_ID = "parameter_id"
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

        if (intent.getStringExtra(PARAMETER_TYPE) == "add"){
            binding.bChange.setText(R.string.v_button_change_add)

        } else {

            binding.bChange.setText(R.string.v_button_change_edit)
            val idVehicle = intent.getIntExtra(PARAMETER_ID, -1)
            actualVehicle = vehicleDb.findVehicle(idVehicle)

            if ( actualVehicle != null ){
                binding.etVplate.setText(actualVehicle!!.plate)
                binding.etVbrand.setText(actualVehicle!!.brand)
                binding.etVmodel.setText(actualVehicle!!.model)
                binding.etVyear.setText(actualVehicle!!.year)
                binding.etVmileage.setText(actualVehicle!!.mileage)
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
        if (userId != -1){
            val vehicle = Vehicle(
                0,
                binding.etVplate.text.toString(),
                binding.etVbrand.text.toString(),
                binding.etVmodel.text.toString(),
                binding.etVyear.text.toString().toInt(),
                binding.etVmileage.text.toString().toInt(),
                userId
            )

            if (binding.bChange.text.toString() == getString(R.string.v_button_change_add)) {
                if ( vehicleDb.insert(vehicle) == -1L ){
                    Toast.makeText(this, R.string.v_e_cannot_insert_vehicle, Toast.LENGTH_LONG).show()
                } else{
                    finish()
                }

            } else {
                if (actualVehicle == null) {
                    Toast.makeText(this, R.string.v_e_cannot_obtain_vehicle, Toast.LENGTH_LONG).show()
                } else {
                    vehicleDb.update(actualVehicle!!, vehicle)
                }
            }
        }

    }

    fun allTextFilled(): Boolean {
        return binding.etVplate.text.isNotEmpty() && binding.etVbrand.text.isNotEmpty() && binding.etVmodel.text.isNotEmpty()
                && binding.etVyear.text.isNotEmpty() && binding.etVmileage.text.isNotEmpty()
    }
}