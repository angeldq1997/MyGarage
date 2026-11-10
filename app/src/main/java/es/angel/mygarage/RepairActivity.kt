package es.angel.mygarage

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import es.angel.mygarage.databinding.ActivityRepairBinding

class RepairActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRepairBinding
    private lateinit var revisionDb: RevisionDbHelper
    private lateinit var plate : String

    companion object {
        const val PARAMETER_PLATE = "parameter_plate"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRepairBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initComponents()
        initListeners()
    }

    private fun initComponents() {
        revisionDb = RevisionDbHelper(this)
        if (intent.getStringExtra(PARAMETER_PLATE) != null){
            plate = intent.getStringExtra(PARAMETER_PLATE)!!
            binding.tvRplate.text = plate
        }
    }

    private fun initListeners() {
        binding.radioGroup.setOnCheckedChangeListener { group, i ->
            if (binding.radioGroup.checkedRadioButtonId == R.id.rb_mechanic){
                binding.etRworkshop.visibility = View.VISIBLE
            } else if (binding.radioGroup.checkedRadioButtonId == R.id.rb_myself){
                binding.etRworkshop.setText("")
                binding.etRworkshop.visibility = View.GONE
            }
        }

        binding.bAdd.setOnClickListener {
            if ( plate.isNotEmpty() ){
                val revision = Revision(
                    plate = binding.tvRplate.text.toString(),
                    description = binding.etRdescription.text.toString(),
                    title = binding.etRtitleAppointment.text.toString(),
                    workshop = binding.etRworkshop.text.toString(),
                    date = (binding.dpRdate.dayOfMonth.toString()+"/"+binding.dpRdate.month.toString()+"/"+binding.dpRdate.year.toString())
                )
                if ( revisionDb.insert(revision) == -1L ){
                    Toast.makeText(this, R.string.e_rev_inserting, Toast.LENGTH_LONG).show()
                } else {
                    finish()
                }
            }
            /*
            else {
                //TODO: Add a way to update a revision
                revisionDb.update( 1, Revision(
                    plate = binding.tvRplate.text.toString(),
                    description = binding.etRdescription.text.toString(),
                    title = binding.etRtitleAppointment.text.toString(),
                    workshop = binding.etRworkshop.text.toString(),
                    date = (binding.dpRdate.dayOfMonth.toString()+"/"+binding.dpRdate.month.toString()+"/"+binding.dpRdate.year.toString())
                ))
            }
             */
        }

        binding.bCancel.setOnClickListener {
            finish()
        }
    }
}