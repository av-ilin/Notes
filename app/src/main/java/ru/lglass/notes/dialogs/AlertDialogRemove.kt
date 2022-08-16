package ru.lglass.notes.dialogs

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import ru.lglass.notes.MainActivity

class AlertDialogRemove(private val noteName: String, private val notePosition: Int) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle("Удаление заметки!")
                .setMessage("Вы уверены, что хотите удалить заметку $noteName?")
                .setPositiveButton("Да"){ _, _ ->
                    (activity as MainActivity?)!!.removeNote(notePosition)
                }
                .setNegativeButton("Нет"){ _, _ ->
                    (activity as MainActivity?)!!.cancelRemoveNote(notePosition)
                }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
    override fun onCancel(dialog: DialogInterface) {
        (activity as MainActivity?)!!.cancelRemoveNote(notePosition)
    }
}