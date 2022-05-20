package com.molloyruaidhri.realmsync

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.molloyruaidhri.realmsync.App.Companion.app
import com.molloyruaidhri.realmsync.databinding.ActivitySignedInBinding
import io.realm.Realm
import io.realm.kotlin.where
import io.realm.mongodb.User
import io.realm.mongodb.sync.SyncConfiguration

class SignedInActivity: AppCompatActivity() {

    private lateinit var binding: ActivitySignedInBinding

    private lateinit var realm: Realm
    private var user: User? = null
    private lateinit var adapter: NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignedInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRemove.setOnClickListener {
            realm.executeTransactionAsync{realm ->
                val notes = realm.where<Note>().sort("date").findAll()
                if(!notes.isNullOrEmpty()){
                    notes[0]?.deleteFromRealm()
                }
            }
        }

        binding.btnAdd.setOnClickListener {
            val input = EditText(this)
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setMessage("Enter your Note: ")
                .setCancelable(true)
                .setPositiveButton("Create") { _, _ ->
                    //1
                    val note = Note(input.text.toString())

                    //all realm writes needs to occur inside of a transaction
                    //2
                    realm.executeTransactionAsync{realm ->
                        realm.insert(note)
                    }
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                }
            val dialog = dialogBuilder.create()
            dialog.setView(input)
            dialog.setTitle("Create New Note")
            dialog.show()
        }
    }

    override fun onStart() {
        super.onStart()

        //1
        user = app.currentUser()

        //configure realm and use the current user and the public partition
        //2
        val config = SyncConfiguration.Builder(user, "Public")
            .waitForInitialRemoteData()
            .build()

        //It is recommended to get a Realm Instance asynchronously
        //3
        Realm.getInstanceAsync(config, object : Realm.Callback() {
            override fun onSuccess(realm: Realm) {
                // since this realm should live exactly as long as this activity, assign the realm to a member variable
                this@SignedInActivity.realm = realm
                setUpRecyclerView(realm)
            }
        })
    }

    private fun setUpRecyclerView(realm: Realm) {
        binding.rvTasks.layoutManager = LinearLayoutManager(this)
        val realmCollection = realm.where<Note>().sort("date").findAll()
        adapter = NoteAdapter(realmCollection)
        binding.rvTasks.adapter = adapter
    }


    override fun onDestroy() {
        super.onDestroy()
        // its recommended to close realm references even if the user does not logout
        realm.close()
    }

}