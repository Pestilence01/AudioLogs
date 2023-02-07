package map.mine.audiologs

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import map.mine.audiologs.databinding.FragmentDashboardBinding


class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecordsAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root

    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupActionBar()

        recyclerView = binding.recyclerView
        adapter = RecordsAdapter(mutableListOf()) { record ->
            playRecording(record)
        }
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                activity,
                LinearLayoutManager.VERTICAL
            )
        )

        val bottomSheetFragment = BottomSheetFragment(this)

        binding.recordLog.setOnClickListener {
            bottomSheetFragment.show(requireActivity().supportFragmentManager, "dsa")
        }

    }

    fun addRecord(record: Record){
        adapter.items.add(record)
        recyclerView.adapter!!.notifyDataSetChanged()
    }

    private fun playRecording(record: Record) {
        var mediaPlayer = MediaPlayer()
        mediaPlayer.setDataSource(record.path)
        mediaPlayer.prepare()
        mediaPlayer.start()
    }


    private fun setupActionBar() {
        val toolbar = binding.toolbarDashboard
        toolbar.title = "Username"
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_baseline_logout_24)
        toolbar.setNavigationOnClickListener {
            Navigation.findNavController(requireView()).navigate(R.id.action_dashboardFragment_to_loginFragment2)
        }
    }


}