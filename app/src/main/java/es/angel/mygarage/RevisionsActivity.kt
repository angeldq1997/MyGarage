package es.angel.mygarage

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import es.angel.mygarage.databinding.ActivityRevisionsBinding


class RevisionsActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRevisionsBinding
    private lateinit var revisionDb: RevisionDbHelper
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
        initComponents()
        showRevisions()
    }

    override fun onResume() {
        super.onResume()
        showRevisions()
    }

    override fun onDestroy() {
        revisionDb.close()
        super.onDestroy()
    }

    private fun initComponents() {
        revisionDb = RevisionDbHelper(this)
        setSupportActionBar(binding.appointmentBar)
        supportActionBar?.title = intent.getStringExtra("parameter_plate")
        binding.appointmentBar.inflateMenu(R.menu.menu_bar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(selection: MenuItem): Boolean {
        val id = selection.itemId

        if (id == R.id.option_add) {
            val i = Intent(this, RepairActivity::class.java)
            i.putExtra(RepairActivity.PARAMETER_PLATE, binding.appointmentBar.title.toString())
            i.putExtra(RepairActivity.PARAMETER_ADD, "Add")
            startActivity(i)
        }
        if (id == R.id.option_remove) {
            revisionDb.remove(binding.lvRepairs.selectedItemId)
        }
        return super.onOptionsItemSelected(selection)
    }

    private fun showRevisions() {
        if (intent.getStringExtra(PARAMETER_PLATE) != null && intent.getStringExtra(PARAMETER_PLATE) != ""){
            val revisions = revisionDb.listRevision(intent.getStringExtra(PARAMETER_PLATE)!!)
            val text = revisions.map{ "${it.title} - ${it.date}"}
            binding.lvRepairs.adapter = ArrayAdapter(this, android.R.layout.simple_selectable_list_item, text)
        }
    }
}