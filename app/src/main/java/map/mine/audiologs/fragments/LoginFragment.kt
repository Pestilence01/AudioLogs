package map.mine.audiologs.fragments

import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.Navigation
import map.mine.audiologs.R
import map.mine.audiologs.activities.MainActivity
import map.mine.audiologs.databinding.FragmentLoginBinding
import map.mine.audiologs.retrofit.RetrofitModule
import map.mine.audiologs.retrofit.SessionManager
import map.mine.audiologs.retrofit.requests.AuthenticateRequest
import map.mine.audiologs.retrofit.responses.AuthenticateResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginFragment : Fragment(R.layout.fragment_login) {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager
    private lateinit var parentActivity: MainActivity


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentActivity = activity as MainActivity

        val typeface: Typeface =
            Typeface.createFromAsset(requireContext().assets, "Happy & Balloons.ttf")
        binding.login.typeface = typeface

        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }

        })

        binding.register.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_loginFragment_to_registerFragment)

        }

        binding.buttonLogin.setOnClickListener {
            if (validateInput()) {
                parentActivity.showProgressDialog()

                val authenticateRequest = AuthenticateRequest(
                    username = binding.usernameLoginText.text.toString(),
                    password = binding.passwordLoginText.text.toString()
                )
                sessionManager = SessionManager(requireContext())
                sessionManager.saveUserName(username = binding.usernameLoginText.text.toString())

                val module = RetrofitModule
                module.initRetrofit(requireContext())

                module.retrofit.authenticateUser(authenticateRequest)
                    .enqueue(object : Callback<AuthenticateResponse> {
                        override fun onResponse(
                            call: Call<AuthenticateResponse>,
                            response: Response<AuthenticateResponse>
                        ) {
                            val loginResponse = response.body()

                            parentActivity.hideProgressDialog()

                            if (response.code() == 200) {
                                sessionManager.saveAuthToken(loginResponse!!.token)

                                Toast.makeText(
                                    requireContext(),
                                    "Log in successful",
                                    Toast.LENGTH_SHORT
                                ).show()

                                Navigation.findNavController(view)
                                    .navigate(R.id.action_loginFragment_to_dashboardFragment)

                            } else {
                                parentActivity.showSnackBar("User doesn't exist", true)
                            }
                        }

                        override fun onFailure(call: Call<AuthenticateResponse>, t: Throwable) {
                            parentActivity.hideProgressDialog()
                            parentActivity.showSnackBar("Something went wrong", true)
                        }

                    })
            }
        }
    }

    private fun validateInput(): Boolean {
        if (binding.usernameLoginText.text!!.isBlank()) {
            Toast.makeText(requireContext(), "Please enter your username", Toast.LENGTH_SHORT)
                .show()
            return false
        }
        if (binding.passwordLoginText.text!!.isBlank()) {
            Toast.makeText(requireContext(), "Please enter your password", Toast.LENGTH_SHORT)
                .show()
            return false
        }

        return true
    }


}