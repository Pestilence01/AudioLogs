package map.mine.audiologs.fragments

import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import map.mine.audiologs.R
import map.mine.audiologs.databinding.FragmentSplashScreenBinding


class SplashScreenFragment : Fragment(R.layout.fragment_splash_screen) {

    private var _binding: FragmentSplashScreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSplashScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val typeface: Typeface =
            Typeface.createFromAsset(requireContext().assets, "Happy & Balloons.ttf")

        binding.name.typeface = typeface
        binding.contributions.typeface = typeface

        Handler().postDelayed({
            Navigation.findNavController(view)
                .navigate(R.id.action_splashScreenFragment_to_loginFragment)
        }, 3000)
    }


}