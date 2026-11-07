package es.angel.mygarage

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import es.angel.mygarage.databinding.ActivityRepairBinding

class RepairActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRepairBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRepairBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}