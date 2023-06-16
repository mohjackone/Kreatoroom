package br.com.setupbuilder.view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.setupbuilder.R
import br.com.setupbuilder.controller.UserController
import kotlinx.android.synthetic.main.recovery_activity.*

class PasswordRecoveryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recovery_activity)
        val repository = UserController()

        send_email.setOnClickListener {
            var email = email_recovery.text.toString()
            if(email.isEmpty()){
                email_recovery.setError("Kolom ini tidak boleh kosong!")
                return@setOnClickListener
            }
            repository.changePassword(email
                ).addOnSuccessListener {
                    Toast.makeText(this, "E-mail untuk pemulihan kata sandi telah terkirim", Toast.LENGTH_LONG).show()
                }.addOnFailureListener{
                    if(it.message.toString().contains("badly formatted")){
                        email_recovery.setError("Masukkan e-mail yang valid")
                    }else
                    if(it.message.toString().contains("There is no user")){
                        email_recovery.setError("Masukkan e-mail yang terdaftar")
                    }else {
                        Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    }
                }

        }
    }
}
