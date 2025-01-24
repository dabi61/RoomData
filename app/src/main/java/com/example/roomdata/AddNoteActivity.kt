package com.example.roomdata

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.roomdata.database.NoteDao
import com.example.roomdata.database.NoteDatabase
import com.example.roomdata.databinding.ActivityAddNoteBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date

class AddNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddNoteBinding
    private lateinit var noteDao: NoteDao
    private val date = Date()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.float_button)

        // Tạo lớp Constants
        // Nên truyền dữ liệu note quay lại
        val type = intent.getStringExtra("type")
        binding.ivBack.setOnClickListener() {
            onBackPressedDispatcher.onBackPressed()
        }
        val db = NoteDatabase.getInstance(this)
        noteDao = db.getNodeDao()
        binding.ivDone.setOnClickListener() {
            if (type != null) {
                putData(type)
            }
        }
        binding.tvDate.text = formatDate(date)
        if (type == "edit") {
            lifecycleScope.launch(Dispatchers.IO) {
                val id = intent.getIntExtra("id", 0)
                val note = noteDao.getNoteById(id)
                withContext(Dispatchers.Main) {
                    binding.etTitle.setText(note.title)
                    binding.tvApp.text = note.title
                    binding.etContent.setText(note.content)
                }
            }
        }
    }

    private fun putData(type: String) {
        if (type == "add") {
            val title: String = binding.etTitle.text.toString()
            val content: String = binding.etContent.text.toString()
            lifecycleScope.launch(Dispatchers.IO) {
                val note = Note(
                    title = title,
                    content = content,
                    createTime = date.time,
                    editTime = date.time
                )
                noteDao.insertNote(note)
            }
            val createIntent = Intent()
            setResult(RESULT_OK, createIntent)
            finish()
        } else if (type == "edit") {
            val id = intent.getIntExtra("id", 0)
            lifecycleScope.launch(Dispatchers.IO) {
                noteDao.updateNote(
                    id,
                    binding.etTitle.text.toString(),
                    binding.etContent.text.toString(),
                    date.time
                )
            }
            val editIntent = Intent()
            editIntent.putExtra("id", id)
            setResult(RESULT_OK, editIntent)
            finish()
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun formatDate(date: Date): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        return dateFormat.format(date)
    }
}