package es.angel.mygarage

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import es.angel.mygarage.databinding.ActivityRevisionsBinding


class RevisionsActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRevisionsBinding
    private lateinit var revisionDb: RevisionDbHelper
    private lateinit var plate : String
    private lateinit var idRevision : String
    companion object{
        const val PARAMETER_PLATE = "parameter_plate"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRevisionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initComponents()
        showRevisions()
        initListeners()
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
        if ( intent.getStringExtra(PARAMETER_PLATE) != null || intent.getStringExtra(PARAMETER_PLATE) != "" ){
            plate = intent.getStringExtra("parameter_plate")!!
            supportActionBar!!.title = intent.getStringExtra("parameter_plate")
        }
        binding.appointmentBar.inflateMenu(R.menu.menu_bar)
    }

    private fun initListeners() {
        binding.lvRepairs.setOnItemClickListener{ parent, _, position, _ ->
            val element : String = parent.getItemAtPosition(position) as String
            idRevision = element.substringBefore(')', "-1")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(selection: MenuItem): Boolean {
        val id = selection.itemId

        if (id == R.id.option_add) {
            val i = Intent(this, RepairActivity::class.java)
            i.putExtra(RepairActivity.PARAMETER_PLATE, intent.getStringExtra("parameter_plate"))
            startActivity(i)
        }
        if (id == R.id.option_remove) {
            if (revisionDb.remove(idRevision.toLong()) == 0){
                Toast.makeText(this, "Error, deleting revision", Toast.LENGTH_LONG).show()
            }
            showRevisions()
        }
        return super.onOptionsItemSelected(selection)
    }

    private fun showRevisions() {
        if (intent.getStringExtra(PARAMETER_PLATE) != null && plate != ""){
            val revisions = revisionDb.listRevision(plate)
            val text = revisions.map{ "${it.id}) ${it.title} - ${it.date}"}
            binding.lvRepairs.adapter = ArrayAdapter(this, android.R.layout.simple_selectable_list_item, text)
        }
    }
}