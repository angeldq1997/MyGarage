package es.angel.mygarage

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import es.angel.mygarage.databinding.ActivityRepairBinding

class RepairActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRepairBinding
    private lateinit var revisionDb: RevisionDbHelper

    companion object {
        const val PARAMETER_PLATE = "parameter_plate"
        const val PARAMETER_ADD = "parameter_add"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRepairBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initComponents()
        initListeners()
    }

    override fun onDestroy() {
        revisionDb.close()
        super.onDestroy()
    }

    private fun initComponents() {
        if (intent.getStringExtra(PARAMETER_ADD) != null && intent.getStringExtra(PARAMETER_ADD) == "Add"){
            binding.bChange.setText(R.string.rep_add)
            binding.bChange.setBackgroundResource(R.color.green)
        } else {
            binding.bChange.setText(R.string.v_button_change)
            binding.bChange.setBackgroundResource(R.color.dark_blue)
        }

            binding.tvRplate
    }

    private fun initListeners() {
        binding.rbMechanic.setOnClickListener {
            binding.etRworkshop.visibility to View.VISIBLE
        }

        binding.rbMyself.setOnClickListener {
            binding.etRworkshop.setText("")
            binding.etRworkshop.visibility to View.GONE
        }

        binding.bChange.setOnClickListener {
            if (intent.getStringExtra(PARAMETER_ADD) != null && intent.getStringExtra(PARAMETER_ADD) == "Add"){
                revisionDb.insert(Revision(
                    plate = binding.tvRplate.text.toString(),
                    description = binding.etRdescription.text.toString(),
                    title = binding.etRtitleAppointment.text.toString(),
                    workshop = binding.etRworkshop.text.toString(),
                    date = (binding.dpRdate.dayOfMonth.toString()+"/"+binding.dpRdate.month.toString()+"/"+binding.dpRdate.year.toString())
                ))
            } else {
                //TODO: Add a way to update a revision
                revisionDb.update( 1, Revision(
                    plate = binding.tvRplate.text.toString(),
                    description = binding.etRdescription.text.toString(),
                    title = binding.etRtitleAppointment.text.toString(),
                    workshop = binding.etRworkshop.text.toString(),
                    date = (binding.dpRdate.dayOfMonth.toString()+"/"+binding.dpRdate.month.toString()+"/"+binding.dpRdate.year.toString())
                ))
            }
        }

        binding.bCancel.setOnClickListener {
            finish()
        }
    }
}