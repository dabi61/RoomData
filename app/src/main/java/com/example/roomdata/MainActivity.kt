package com.example.roomdata

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.roomdata.database.NoteDao
import com.example.roomdata.database.NoteDatabase
import com.example.roomdata.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class MainActivity : AppCompatActivity(), OnNoteClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var noteList: MutableList<Note>
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private lateinit var noteDao: NoteDao
    private val date = Date()
    private val detailForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Toast.makeText(this, "Sửa note thành công!", Toast.LENGTH_SHORT).show()
            coroutineScope.launch {
                val notes = noteDao.getListNote(binding.etSearch.text.toString(), binding.etSearch.text.toString())
                withContext(Dispatchers.Main){
                    noteAdapter.updateData(notes)
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top , systemBars.right,0)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.float_button)

        noteList = mutableListOf()
        val db = Room.databaseBuilder(
            applicationContext,
            NoteDatabase::class.java, "note_db"
        ).build()
        noteDao = db.getNodeDao()


        noteAdapter = NoteAdapter(this, noteList, this)
        showData()
        binding.rcvNotes.apply {
            adapter = noteAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
        }
        val createForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "Thêm note thành công!", Toast.LENGTH_SHORT).show()
                coroutineScope.launch {
                    val id = noteDao.getLastId()
                    val note = noteDao.getNoteById(id)
                    withContext(Dispatchers.Main)
                    {
                        noteAdapter.updateNote(note)
                        binding.rcvNotes.scrollToPosition(0)
                    }
                }
            }
        }
        binding.fbtAdd.setOnClickListener(){
            val intent = Intent(this, AddNoteActivity::class.java)
            intent.putExtra("type", "add")
            createForResult.launch(intent)
        }
        binding.ivSearch.setOnClickListener(){
           showSearch()
        }
        binding.ivBack.setOnClickListener(){
            defaultApp()
        }
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Không cần làm gì ở đây
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                coroutineScope.launch {
                    var notes = noteDao.getListNote(s.toString(), s.toString())
                    withContext(Dispatchers.Main) {
                        noteAdapter.updateData(notes)
                    }
                }
            }
            override fun afterTextChanged(s: Editable?) {
                // Không cần làm gì ở đây
            }
        })
        binding.ivFilter.setOnClickListener(){
            showFilterDialog()
        }
    }
    private fun showData() {
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val id = sharedPreferences.getString("filter", R.id.r1.toString())
        if (id != null) {
            showFilter(id.toInt())
        }

    }
    private fun showFilterDialog() {
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val dialogFilter = LayoutInflater.from(this).inflate(R.layout.dialog_filter, null)
        val rgFilter = dialogFilter.findViewById<RadioGroup>(R.id.rg_sort)
        val id = sharedPreferences.getString("filter", R.id.r1.toString())
        Toast.makeText(this, "$id", Toast.LENGTH_SHORT).show()
        if (id != null) {
            rgFilter.check(id.toInt())
        }
        val dialog = AlertDialog.Builder(this)
            .setView(dialogFilter)
            .setCancelable(false)
            .setPositiveButton("OK") { _, _ ->
                val selectedId = rgFilter.checkedRadioButtonId
                if (selectedId != -1) {
                    val selectedRadioButton = dialogFilter.findViewById<RadioButton>(selectedId)
                    val selectedText = selectedRadioButton.text.toString()
                    Toast.makeText(this, "$selectedText", Toast.LENGTH_SHORT).show()
                    editor.putString("filter", selectedId.toString())
                    editor.apply()
                }
                showFilter(selectedId)

            }
            .setNegativeButton("Cancel") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .create()
        dialog.show()


    }

    private fun showFilter(selectedId: Int) {
        coroutineScope.launch {
            val notes = noteDao.getAllNote()
            withContext(Dispatchers.Main)
            {
                when(selectedId){
                    R.id.r1 -> {
                        notes.sortByDescending {it.editTime }
                        noteAdapter.filterNote(notes)
                    }
                    R.id.r2 -> {
                        notes.sortBy {it.editTime }
                        noteAdapter.filterNote(notes)
                    }
                    R.id.r3 -> {
                        notes.sortByDescending {it.createTime }
                        noteAdapter.filterNote(notes)
                    }
                    R.id.r4 -> {
                        notes.sortBy {it.createTime }
                        noteAdapter.filterNote(notes)
                    }
                    R.id.r5 -> {
                        notes.sortByDescending {it.title }
                        noteAdapter.filterNote(notes)
                    }
                    R.id.r6 -> {
                        notes.sortBy {it.title }
                        noteAdapter.filterNote(notes)

                    }
                    else -> {
                        notes.sortByDescending {it.editTime }
                        noteAdapter.filterNote(notes)
                    }
                }
            }
        }
    }

    private fun defaultApp() {
        with(binding) {
            tvApp.visibility = View.VISIBLE
            ivBack.visibility = View.GONE
            etSearch.visibility = View.GONE
            ivFilter.visibility = View.VISIBLE
            ivSearch.visibility = View.VISIBLE
            etSearch.text?.clear()
        }
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("filter", R.id.r1.toString())
        editor.apply()
        showFilter(R.id.r1)
    }

    private fun showSearch() {
        with(binding) {
            tvApp.visibility = View.GONE
            ivBack.visibility = View.VISIBLE
            etSearch.visibility = View.VISIBLE
            ivFilter.visibility = View.GONE
            ivSearch.visibility = View.GONE
            etSearch.requestFocus()
        }
    }
    private fun getAllNote(noteDao: NoteDao) {
        coroutineScope.launch {
            val notes: MutableList<Note> = noteDao.getAllNote()
            Log.d("Database", "Notes: $notes")
            withContext(Dispatchers.Main) {
                noteAdapter.updateData(notes)
            }
        }
    }

    override fun onNoteClick(note: Note) {
        val intent = Intent(this, AddNoteActivity::class.java)
        intent.putExtra("id", note.id)
        intent.putExtra("type", "edit")
        detailForResult.launch(intent)
    }

    override fun onDeleteClick(note: Note) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Xác nhận xóa")
        builder.setMessage("Bạn có chắc chắn muốn xóa không ?")
        builder.setPositiveButton("Có") { dialog, _ ->
            coroutineScope.launch {
                noteDao.deleteNote(note.id)
                val notes = noteDao.getAllNote()
                withContext(Dispatchers.Main) {
                    noteAdapter.updateData(notes)
                }
            }
            Toast.makeText(this, "Xóa ghi chú thành công!", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        builder.setNegativeButton("Không") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

}