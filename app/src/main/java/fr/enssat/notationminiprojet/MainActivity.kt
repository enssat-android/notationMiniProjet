package fr.enssat.notationminiprojet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.Observer
import fr.enssat.notationminiprojet.databinding.ActivityNotationBinding
import android.widget.ArrayAdapter
import android.widget.TextView
import android.content.DialogInterface
import android.text.InputType
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityNotationBinding
    lateinit var model: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_notation)
        model = ViewModelProviders.of(this, NoteViewModelFactory(this)).get(NoteViewModel::class.java)

        fun Spinner.onChange(name:String,mod:NoteViewModel) {
            this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    mod.notation(name, parent!!.getItemAtPosition(position).toString().toIntOrNull())
                }
            }
        }

        init()

        binding.demoNote.onChange("demo",model)
        binding.docNote.onChange("doc",model)
        binding.gitNote.onChange("git",model)
        binding.kotlinNote.onChange("kotlin",model)
        binding.jsonNote.onChange("json",model)
        binding.livedataNote.onChange("livedata",model)
        binding.multicastNote.onChange("multicast",model)
        binding.serversocketNote.onChange("serversocket",model)
        binding.testNote.onChange("test",model)

        model.readytoSend.observe(this, Observer { bool->
            binding.sendButton.setEnabled(bool)})

        binding.resetbutton.setOnClickListener{ view ->
            init()
            model.resetAll()
        }

        binding.sendButton.setOnClickListener({ view ->
            model.resetMembers()
            if (!binding.team1.text.isNullOrEmpty()) { model.registerMember(binding.team1.text!!.toString())}
            if (!binding.team2.text.isNullOrEmpty()) { model.registerMember(binding.team2.text!!.toString())}
            if (!binding.team3.text.isNullOrEmpty()) { model.registerMember(binding.team3.text!!.toString())}
            model.sendEmail()
        })
    }

    private fun init(){
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayOf("", 0, 1, 2, 3, 4, 5))
        binding.demoNote.adapter = adapter
        binding.docNote.adapter = adapter
        binding.gitNote.adapter = adapter
        binding.kotlinNote.adapter = adapter
        binding.jsonNote.adapter = adapter
        binding.livedataNote.adapter = adapter
        binding.multicastNote.adapter = adapter
        binding.serversocketNote.adapter = adapter
        binding.testNote.adapter = adapter
        binding.team3.text= null
        binding.team2.text= null
        binding.team1.text= null
    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.addEmail -> {
               newEmail()
                true
            }
            R.id.listEmails -> {
                listOfEmail()
                true
            }
            R.id.resetEmails -> {
                resetEmails()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun newEmail()
    {   val builder = AlertDialog.Builder(this)
        builder.setTitle("New email")
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)
        builder.setPositiveButton("OK",
            DialogInterface.OnClickListener { dialog, which -> model.addEmail(input.text.toString()) })
        builder.setNegativeButton("Cancel",
            DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })
        builder.show()
    }

    private fun listOfEmail()
    {   val builder = AlertDialog.Builder(this)
        builder.setTitle("list of email")
        val txt=TextView(this)
        txt.text = model.getEmails()
        builder.setView(txt)
        builder.setPositiveButton("OK",null)
        builder.show()
    }

    private fun resetEmails()
    {   model.resetEmails()}
}

