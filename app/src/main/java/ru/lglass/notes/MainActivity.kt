package ru.lglass.notes

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.android.synthetic.main.activity_main.*
import ru.lglass.notes.adapters.AdapterRecyclerNotes
import ru.lglass.notes.dialogs.AlertDialogRemove
import ru.lglass.notes.structures.Note
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private var notes: MutableList<Note> = mutableListOf()

    private fun fillNotes(){
        val notesFiles = applicationContext.fileList()
        for (noteFile in notesFiles!!){
            val fileNoteContent = applicationContext.openFileInput(noteFile).readBytes().toString(Charsets.UTF_8)
            val note = JSONObject(fileNoteContent)
            notes.add(
                Note(
                note.getString("id"),
                note.getString("name"),
                note.getString("description"),
                note.getString("images").split("|")
            )
            )
        }
    }

    public fun cancelRemoveNote(position: Int){ rvNotes.adapter!!.notifyItemChanged(position) }

    public fun removeNote(position: Int){
        applicationContext.deleteFile("Note_${notes[position].id}")
        notes.removeAt(position)
        rvNotes.adapter!!.notifyItemRemoved(position)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btAdd.setOnClickListener {
            val intent = Intent(this, AddActivity::class.java)
            startActivity(intent)
        }
        btExit.setOnClickListener { finishAffinity() }
        val myCallback = object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when(direction){
                    ItemTouchHelper.RIGHT -> {
                        val dialogFragment = AlertDialogRemove(notes[viewHolder.adapterPosition].name, viewHolder.adapterPosition)
                        val manager = supportFragmentManager
                        dialogFragment.show(manager, "removeDialog")
                    }
                    ItemTouchHelper.LEFT -> {
                        val intent = Intent(this@MainActivity, ChangeActivity::class.java)
                        val note = notes[viewHolder.adapterPosition]
                        intent.putExtra("Name", note.name)
                        intent.putExtra("Description", note.description)
                        intent.putExtra("ID", note.id)
                        intent.putExtra("Images", note.image.joinToString("|"))
                        startActivity(intent)
                    }
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addCornerRadius(applicationContext.resources.displayMetrics.density.toInt(), 100)
                    .addSwipeRightLabel("Delete") //left
                    .setSwipeRightLabelColor(ContextCompat.getColor(applicationContext, R.color.black))
                    .setSwipeRightLabelTextSize(applicationContext.resources.displayMetrics.density.toInt(), 18f)
                    .addSwipeRightActionIcon(R.drawable.ic_baseline_delete_forever_24)
                    .setSwipeRightActionIconTint(ContextCompat.getColor(applicationContext, R.color.black))
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(applicationContext, R.color.red))
                    .addSwipeLeftLabel("Change")
                    .setSwipeLeftLabelColor(ContextCompat.getColor(applicationContext, R.color.black))
                    .setSwipeLeftLabelTextSize(applicationContext.resources.displayMetrics.density.toInt(), 18f)
                    .addSwipeLeftActionIcon(R.drawable.ic_baseline_create_24)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(applicationContext, R.color.green))
                    .setSwipeLeftActionIconTint(ContextCompat.getColor(applicationContext, R.color.black))
                    .create()
                    .decorate()

                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }
        val myHelper = ItemTouchHelper(myCallback)
        myHelper.attachToRecyclerView(rvNotes)
        rvNotes.layoutManager = LinearLayoutManager(this)
    }

    override fun onStart() {
        super.onStart()
        notes = mutableListOf()
        fillNotes()
        rvNotes.adapter = AdapterRecyclerNotes(notes, object: AdapterRecyclerNotes.OnItemClickListener{
            override fun onItemClick(note: Note, context: Context) {
                val intent = Intent(context, ViewActivity::class.java)
                intent.putExtra("Name", note.name)
                intent.putExtra("Description", note.description)
                intent.putExtra("Images", note.image.joinToString("|"))
                context.startActivity(intent)
            }
        })
    }
}