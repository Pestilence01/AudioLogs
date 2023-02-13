package map.mine.audiologs.fragments

import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import map.mine.audiologs.R
import map.mine.audiologs.activities.MainActivity
import map.mine.audiologs.databinding.FragmentRegisterBinding
import map.mine.audiologs.models.User
import map.mine.audiologs.retrofit.RetrofitModule
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RegisterFragment : Fragment(R.layout.fragment_register) {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var parentActivity: MainActivity


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentActivity = activity as MainActivity

        val typeface: Typeface =
            Typeface.createFromAsset(requireContext().assets, "Happy & Balloons.ttf")
        binding.register.typeface = typeface

        binding.login.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_registerFragment_to_loginFragment)
        }

        binding.buttonRegister.setOnClickListener {
            if (validateInput()) {

                parentActivity.showProgressDialog()

                val userRequest = User(
                    username = binding.usernameRegisterText.text.toString(),
                    password = binding.passwordRegisterText.text.toString(),
                    firstName = binding.nameRegisterText.text.toString(),
                    lastName = binding.surnameRegisterText.text.toString(),
                    email = binding.emailRegisterText.text.toString()
                )

                val module = RetrofitModule
                module.initRetrofit(requireContext())

                module.retrofit.registerUser(userRequest)
                    .enqueue(object : Callback<User> {
                        override fun onResponse(
                            call: Call<User>,
                            response: Response<User>
                        ) {
                            parentActivity.hideProgressDialog()

                            if (response.code() == 200) {
                                parentActivity.showSnackBar(
                                    "You have successfully registered",
                                    false
                                )
                            } else {
                                parentActivity.showSnackBar("Error during registration", true)
                            }
                        }

                        override fun onFailure(call: Call<User>, t: Throwable) {
                            parentActivity.showSnackBar(t.message.toString(), true)
                            parentActivity.hideProgressDialog()
                        }

                    })
                Navigation.findNavController(view)
                    .navigate(R.id.action_registerFragment_to_loginFragment)
            }
        }
    }

    private fun validateInput(): Boolean {

        val emailPattern = Regex(".+@.+[.].*")

        if (binding.nameRegisterText.text!!.isBlank()) {
            Toast.makeText(requireContext(), "Please enter your name", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.surnameRegisterText.text!!.isBlank()) {
            Toast.makeText(requireContext(), "Please enter your surname", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.usernameRegisterText.text!!.isBlank()) {
            Toast.makeText(requireContext(), "Please enter your username", Toast.LENGTH_SHORT)
                .show()
            return false
        }
        if (binding.emailRegisterText.text!!.isBlank() || !emailPattern.containsMatchIn(binding.emailRegisterText.text!!)) {
            Toast.makeText(
                requireContext(),
                "Please enter a valid email address",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        if (binding.passwordRegisterText.text!!.isBlank()) {
            Toast.makeText(requireContext(), "Please enter your password", Toast.LENGTH_SHORT)
                .show()
            return false
        }
        return true
    }


}