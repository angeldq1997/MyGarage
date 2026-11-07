package es.angel.mygarage

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import es.angel.mygarage.databinding.ActivityMyvehiclesBinding

class MyVehiclesActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMyvehiclesBinding
    private lateinit var userDb: UserDbHelper
    private lateinit var vehicleDb: VehicleDbHelper

    companion object{
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

    override fun onDestroy() {
        userDb.close()
        vehicleDb.close()
        super.onDestroy()
    }

    private fun initComponents() {
        userDb = UserDbHelper(this)
        vehicleDb = VehicleDbHelper(this)
    }

    private fun initListeners() {
        binding.bMvadd.setOnClickListener {
            val i = Intent(this, VehicleActivity::class.java)
            startActivity(i)
        }

        binding.bMvedit.setOnClickListener {
            val i = Intent(this, VehicleActivity::class.java)
            if ( binding.lvMvehicles.selectedItem == null ){
                Toast.makeText(this, R.string.mv_e_not_selected, Toast.LENGTH_LONG).show()
            } else {
                i.putExtra(VehicleActivity.PARAMETER_ID, (binding.lvMvehicles.selectedItem as Vehicle).id )
                startActivity(i)
            }
        }

        binding.bMvremove.setOnClickListener {
            if ( binding.lvMvehicles.selectedItem == null ){
                Toast.makeText(this, R.string.mv_e_not_selected, Toast.LENGTH_LONG).show()
            } else {
                vehicleDb.remove(binding.lvMvehicles.selectedItem as Vehicle)
            }
        }

        binding.bMvdetails.setOnClickListener {
            val i = Intent(this, VehicleActivity::class.java)
            if ( binding.lvMvehicles.selectedItem == null ){
                Toast.makeText(this, R.string.mv_e_not_selected, Toast.LENGTH_LONG).show()
            } else {
                i.putExtra(VehicleActivity.PARAMETER_ID, (binding.lvMvehicles.selectedItem as Vehicle).id )
                i.putExtra(VehicleActivity.PARAMETER_TYPE, "details")
                startActivity(i)
            }
        }
    }

    private fun showVehicles(){
        val username : String = intent.getStringExtra(PARAMETER_USER).toString()
        val user = userDb.findUser(username)
        if ( user != null ){
            val vehicles = vehicleDb.listAll(user.id )
            val text = vehicles.map{"${it.plate} - ${it.brand} ${it.model} ${it.year} ${it.mileage} kms" }
            binding.lvMvehicles.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, text)
        }
    }
}