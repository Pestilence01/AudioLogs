package map.mine.audiologs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import map.mine.audiologs.databinding.FragmentLoginBinding


class LoginFragment : Fragment(R.layout.fragment_login) {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonRegister.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.buttonLogin.setOnClickListener {
            if(validateInput()){
                Toast.makeText(requireContext(), "You have successfully logged in", Toast.LENGTH_SHORT).show()
                Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_dashboardFragment)
            }
        }
    }

    private fun validateInput(): Boolean {
        if(binding.username.text.isBlank()){
            Toast.makeText(requireContext(), "Please enter your username", Toast.LENGTH_SHORT).show()
            return false
        }
        if(binding.password.text.isBlank()){
            Toast.makeText(requireContext(), "Please enter your password", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }


}