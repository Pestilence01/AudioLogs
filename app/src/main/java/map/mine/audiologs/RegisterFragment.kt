package map.mine.audiologs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import map.mine.audiologs.databinding.FragmentRegisterBinding


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
                Toast.makeText(requireContext(), "You have successfully registered", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }
    }

    private fun validateInput(): Boolean {
        if(binding.name.text.isBlank()){
            Toast.makeText(requireContext(), "Please enter your name", Toast.LENGTH_SHORT).show()
            return false
        }
        if(binding.surname.text.isBlank()){
            Toast.makeText(requireContext(), "Please enter your surname", Toast.LENGTH_SHORT).show()
            return false
        }
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