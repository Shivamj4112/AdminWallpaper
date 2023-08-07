package com.example.adminwallpaper

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.adminwallpaper.databinding.ActivityMainBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    lateinit var arrayAdapter : ArrayAdapter<String>
    var category = arrayOf("Neon","Anime","Vehicle","Flower","Animal","Music","Cartoon","Material","Baby","Festival")
    lateinit var dbRef : DatabaseReference
    lateinit var dbStorage : StorageReference
    lateinit var txtCat : String
    lateinit var key : String
    lateinit var uri : Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbRef = FirebaseDatabase.getInstance().reference
        dbStorage = FirebaseStorage.getInstance().reference


        arrayAdapter  = ArrayAdapter(this@MainActivity,android.R.layout.simple_spinner_dropdown_item,category)
        binding.catSpinner.adapter = arrayAdapter


        binding.catSpinner.onItemSelectedListener = object : OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int,  id: Long) {

                txtCat = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }


        }

        binding.imgAdd.setOnClickListener {
            var intent = Intent(Intent.ACTION_PICK)
            intent.setType("image/*")
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(intent,10)
        }

        binding.btnSave.setOnClickListener{

            if (uri != null){

                uploadToFirebase(uri)

            }
            else{
                Toast.makeText(this@MainActivity,"Please select image",Toast.LENGTH_SHORT).show()
            }

        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK  && requestCode == 10){

             uri = data?.data!!

            binding.imgAdd.setImageURI(uri)
        }
    }

    private fun uploadToFirebase(uri: Uri) {
        var filRef = dbStorage.child("Img_"+txtCat+"_"+System.currentTimeMillis())
        filRef.putFile(uri).addOnSuccessListener {

            filRef.downloadUrl.addOnCompleteListener {

                var downloaduri = it.result

                key = dbRef.push().key!!
//                var model = ModelClass(downloaduri.toString())
                dbRef.child("Image").child(txtCat).child(key).setValue(downloaduri.toString())

            }
        }
    }



}