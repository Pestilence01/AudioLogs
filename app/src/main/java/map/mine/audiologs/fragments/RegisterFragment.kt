package map.mine.audiologs.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import map.mine.audiologs.R
import map.mine.audiologs.databinding.FragmentRegisterBinding
import map.mine.audiologs.models.User
import map.mine.audiologs.retrofit.RetrofitModule
import map.mine.audiologs.retrofit.requests.RegisterUserRequest
import map.mine.audiologs.retrofit.responses.AuthenticateResponse
import map.mine.audiologs.retrofit.responses.RegisterUserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RegisterFragment : Fragment(R.layout.fragment_register) {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonRegister.setOnClickListener {
            if(validateInput()){
                //Toast.makeText(requireContext(), "You have successfully registered", Toast.LENGTH_SHORT).show()

                val userRequest = User(username = binding.usernameRegisterText.text.toString(),
                    password = binding.passwordRegisterText.text.toString(), firstName = binding.nameRegisterText.text.toString(),
                    lastName = binding.surnameRegisterText.text.toString(), email = binding.emailRegisterText.text.toString())

                val module = RetrofitModule
                module.initRetrofit(requireContext())

                module.retrofit.registerUser(userRequest)
                    .enqueue(object: Callback<User> {
                        override fun onResponse(
                            call: Call<User>,
                            response: Response<User>
                        ) {
                            val loginResponse = response.body()

                            if (response.code() == 200) {
                                Log.i("success: ", "YES")
                            } else {
                                Log.e("Login failed", response.errorBody().toString())
                            }
                        }

                        override fun onFailure(call: Call<User>, t: Throwable) {
                            Log.e("Communication error", t.message.toString())
                            Log.i("success: ", "NO")
                        }

                    })
                findNavController().popBackStack()
            }
        }
    }

    private fun validateInput(): Boolean {
        if(binding.nameRegisterText.text!!.isBlank()){
            Toast.makeText(requireContext(), "Please enter your name", Toast.LENGTH_SHORT).show()
            return false
        }
        if(binding.surnameRegisterText.text!!.isBlank()){
            Toast.makeText(requireContext(), "Please enter your surname", Toast.LENGTH_SHORT).show()
            return false
        }
        if(binding.usernameRegisterText.text!!.isBlank()){
            Toast.makeText(requireContext(), "Please enter your username", Toast.LENGTH_SHORT).show()
            return false
        }
        if(binding.passwordRegisterText.text!!.isBlank()){
            Toast.makeText(requireContext(), "Please enter your password", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }


}