package com.example.medtrack.reminder.platform

import android.content.Context
import android.util.AtomicFile
import com.example.medtrack.reminder.ReminderCandidate
import com.example.medtrack.reminder.ReminderKey
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileNotFoundException

class FileReminderStateStore(
    context: Context
) : ReminderStateStore {

    private val file = AtomicFile(
        File(
            context.noBackupFilesDir,
            "reminder-platform-state.json"
        )
    )

    override fun read(): Map<ReminderKey, StoredReminder> {
        val text = try {
            file.openRead().use { stream ->
                stream.readBytes().toString(Charsets.UTF_8)
            }
        } catch (_: FileNotFoundException) {
            return emptyMap()
        }

        val root = JSONObject(text)
        check(root.getInt("version") == 1) {
            "Unsupported reminder state version."
        }

        val items = root.getJSONArray("items")

        return buildMap {
            for (index in 0 until items.length()) {
                val item = items.getJSONObject(index)

                val key = ReminderKey(
                    ownerId = item.getString("ownerId"),
                    scheduleId = item.getString("scheduleId")
                )

                val candidate = ReminderCandidate(
                    key = key,
                    medicineName = item.getString("medicineName"),
                    dose = item.getString("dose"),
                    triggerAtMillis = item.getLong("triggerAtMillis")
                )

                put(
                    key,
                    StoredReminder(
                        candidate = candidate,
                        handled = item.getBoolean("handled")
                    )
                )
            }
        }
    }

    override fun write(
        records: Map<ReminderKey, StoredReminder>
    ) {
        val items = JSONArray()

        records.values.forEach { record ->
            val candidate = record.candidate

            items.put(
                JSONObject()
                    .put("ownerId", candidate.key.ownerId)
                    .put("scheduleId", candidate.key.scheduleId)
                    .put("medicineName", candidate.medicineName)
                    .put("dose", candidate.dose)
                    .put("triggerAtMillis", candidate.triggerAtMillis)
                    .put("handled", record.handled)
            )
        }

        val root = JSONObject()
            .put("version", 1)
            .put("items", items)

        val stream = file.startWrite()

        try {
            stream.write(
                root.toString().toByteArray(Charsets.UTF_8)
            )
            file.finishWrite(stream)
        } catch (exception: Exception) {
            file.failWrite(stream)
            throw exception
        }
    }
}