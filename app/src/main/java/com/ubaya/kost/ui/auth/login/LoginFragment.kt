package com.ubaya.kost.ui.auth.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.ubaya.kost.R
import com.ubaya.kost.data.Global
import com.ubaya.kost.data.models.User
import com.ubaya.kost.databinding.FragmentLoginBinding
import com.ubaya.kost.util.PrefManager
import com.ubaya.kost.util.VolleyClient
import org.json.JSONObject

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginBtnLogin.setOnClickListener {
            val username = binding.loginInputUsername.text
            val password = binding.loginInputPassword.text

            if (username.isNullOrEmpty() && password.isNullOrEmpty()) {
                MaterialAlertDialogBuilder(requireContext())
                    .setMessage("Lengkapi username dan password")
                    .setPositiveButton("OK", null)
                    .show()
            } else {
                login(username.toString(), password.toString())
            }
        }

        binding.loginBtnRegister.setOnClickListener {
            findNavController().navigate(R.id.action_fragment_login_to_fragment_register)
        }
    }

    private fun login(username: String, password: String) {
        val url = VolleyClient.API_URL + "/auth/login"
        val params: Map<String, String> = hashMapOf("username" to username, "password" to password)

        val request = JsonObjectRequest(Request.Method.POST, url, JSONObject(params),
            { res ->
                val data = JSONObject(res["data"].toString())
                val user = Gson().fromJson(data["user"].toString(), User::class.java)
                val token = data["token"].toString()

                PrefManager.getInstance(requireContext()).apply {
                    authUser = user
                    authToken = token
                }

                Global.apply {
                    authUser = user
                    authToken = token
                }

                findNavController().navigate(R.id.action_fragment_login_to_owner_navigation)
            },
            { err ->
                try {
                    val data = JSONObject(String(err.networkResponse.data))
                    MaterialAlertDialogBuilder(requireContext())
                        .setMessage(data["msg"].toString())
                        .setPositiveButton("OK", null)
                        .show()
                } catch (e: Exception) {
                    Log.d("LOGIN_ERR", e.message.toString())
                    MaterialAlertDialogBuilder(requireContext())
                        .setMessage("Terjadi kesalahan pada sistem. Silahkan coba lagi")
                        .setPositiveButton("OK", null)
                        .show()
                }
            }
        )

        VolleyClient(requireContext()).addToRequestQueue(request)
    }
}