package br.com.setupbuilder.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import br.com.setupbuilder.view.LoginActivity
import br.com.setupbuilder.R
import br.com.setupbuilder.controller.SetupController
import br.com.setupbuilder.controller.UserController
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.account_fragment.*
import kotlinx.android.synthetic.main.dialog_exclusion.view.*
import kotlinx.android.synthetic.main.dialog_with_edittext_setup_creation.view.*
import kotlinx.android.synthetic.main.fragment_home.*

class AccountFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.account_fragment, container, false)
    }

    override fun onStart() {
        super.onStart()
        val user = FirebaseAuth.getInstance().currentUser
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        sign_out.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(context, LoginActivity::class.java))
        }

        home_text2.setText("${user?.email}")

        delete_button.setOnClickListener {
            if (password_home_input.text.toString().isEmpty()) {
                password_home_input.setError("Field kosong")
                return@setOnClickListener
            }
            val mDialogView =
                LayoutInflater.from(context)
                    .inflate(R.layout.dialog_exclusion, null)
            val mBuilder = AlertDialog.Builder(context)
                .setView(mDialogView)

            var cUser = UserController().getUser();
            var setups = SetupController()

            val credential = EmailAuthProvider
                .getCredential(user?.email.toString(), password_home_input.text.toString())
            user?.reauthenticate(credential)?.addOnSuccessListener {
                val mAlertDialog = mBuilder.show()
                mDialogView.confirm_dialog.setOnClickListener {
                    mAlertDialog.dismiss()
                    user?.delete()
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                FirebaseFirestore.getInstance()
                                    .collection("users")
                                    .document(user?.uid).get()
                                    .addOnSuccessListener { document ->
                                        document.reference.delete()
                                            .addOnSuccessListener {
                                                Toast.makeText(
                                                    context,
                                                    "Akun telah keluar",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                    }
                                FirebaseFirestore.getInstance()
                                    .collection("setup")
                                    .whereEqualTo("userUid", user?.uid)
                                    .get()
                                    .addOnSuccessListener { documents ->
                                        for (document in documents) {
                                            document.reference.delete()
                                        }
                                        startActivity(
                                            Intent(
                                                context,
                                                LoginActivity::class.java
                                            )
                                        )
                                    }
                            } else {
                            }
                        }

                }
                mDialogView.cancel_dialog_exclusion.setOnClickListener {
                    mAlertDialog.dismiss()
                }
            }?.addOnFailureListener {
                if (it.message.toString().contains("password is invalid")) {
                    password_home_input.setError("Kata sandi salah.")
                } else {
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                }
            }

        }

    }
}


