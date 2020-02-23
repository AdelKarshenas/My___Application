package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        // Write a message to the database
        // Write a message to the database
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("message")

        myRef.setValue("Hello, World!")
        RegisterID.setOnClickListener() {
            performRegister()

        }
        selectphoto.setOnClickListener() {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
        AlreadyhaveID.setOnClickListener() {
            val intent = Intent(this, Login_activity::class.java)
            startActivity(intent)
        }
    }

    var selectedphoto:Uri? =null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Log.d("image","yesyes")
            selectedphoto = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,selectedphoto)

            //selectphoto.setBackgroundDrawable(BitmapDrawable(bitmap))
            select_image_view.setImageBitmap(bitmap)
            selectphoto.alpha = 0f
        }

    }
    fun performRegister()
    {

        var email = emailtxt.text.toString()
        var password = passwordtxt.text.toString()
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) {
                    Toast.makeText(this, "FUCKno", Toast.LENGTH_SHORT).show()
                    return@addOnCompleteListener
                }//else successful
                else

                    uploadimagetofirebase()

            }


    }

    private fun uploadimagetofirebase(){
        val filename = UUID.randomUUID().toString()
        var DownloadUri:Uri?
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedphoto!!).addOnSuccessListener {
            Log.d("register","image uploaded successfully ${it.metadata?.path}")
            ref.downloadUrl.addOnSuccessListener {
                DownloadUri=it
                SavetoDataBase(it.toString())
            }

        }

    }
    fun SavetoDataBase(imageUri:String)
    {

     val uid=   FirebaseAuth.getInstance().uid?:""
        val ref=FirebaseDatabase.getInstance().getReference("/users/${uid}")


        val user=Users(uid,UsernameID.text.toString(),imageUri)
        ref.setValue(user).addOnSuccessListener {
            Log.d("regis","yesfinal")
            Toast.makeText(this, "FUCK", Toast.LENGTH_SHORT).show()
            var intent = Intent(this,LatestMessages::class.java)
            intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }




    }


}
class Users(val uid :String, val username:String,  val image : String)



