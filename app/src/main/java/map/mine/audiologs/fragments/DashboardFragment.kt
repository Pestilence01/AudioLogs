package map.mine.audiologs.fragments

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import map.mine.audiologs.R
import map.mine.audiologs.activities.MainActivity
import map.mine.audiologs.models.AudioNote
import map.mine.audiologs.adapters.RecordsAdapter
import map.mine.audiologs.databinding.FragmentDashboardBinding
import map.mine.audiologs.retrofit.Message
import map.mine.audiologs.retrofit.RetrofitModule
import map.mine.audiologs.retrofit.SessionManager
import map.mine.audiologs.retrofit.responses.AudioNotesResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files.delete
import java.nio.file.Paths


class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecordsAdapter

    private lateinit var sessionManager: SessionManager

    private var audioNoteList: MutableList<AudioNote> = mutableListOf()

    private lateinit var module: RetrofitModule

    private lateinit var saveDir: File

    private lateinit var parentActivity: MainActivity

    private var mediaPlayer = MediaPlayer()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentActivity = activity as MainActivity

        sessionManager = SessionManager(requireContext())

        setupActionBar()
        createDownloadsDirectory()
        fetchStoredAudioNotes()

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = RecordsAdapter(audioNoteList, this)
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

    private fun createDownloadsDirectory() {
        val dir = requireContext().filesDir
        saveDir = File(dir, "Downloads")
        saveDir.mkdirs()
    }

    private fun fetchStoredAudioNotes() {

        module = RetrofitModule
        module.initRetrofit(requireContext())

        parentActivity.showProgressDialog()

        module.retrofit.getUserAudioNotes(token = "Bearer ${sessionManager.fetchAuthToken()}")
            .enqueue(object : Callback<List<AudioNotesResponse>> {
                override fun onResponse(
                    call: Call<List<AudioNotesResponse>>,
                    response: Response<List<AudioNotesResponse>>
                ) {
                    val loginResponse = response.body()

                    parentActivity.hideProgressDialog()

                    if (response.code() == 200) {
                        var test: String = String()
                        for (element in loginResponse!!) {
                            fetchAndStoreFile(element!!)
                        }

                    } else {
                        parentActivity.showSnackBar("Something went wrong", true)
                    }
                }

                override fun onFailure(call: Call<List<AudioNotesResponse>>, t: Throwable) {
                    parentActivity.hideProgressDialog()
                    parentActivity.showSnackBar("Something went wrong", true)
                }

            })
    }

    private fun fetchAndStoreFile(element: AudioNotesResponse) {

        var urlSplitted = element.url!!.split("/")
        var lastElement = urlSplitted[urlSplitted.size - 1]

        module.retrofit.getAudioNote(
            token = "Bearer ${sessionManager.fetchAuthToken()}",
            lastElement
        ).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val data = response.body() ?: return

                var file = File(saveDir, "${element.name}")
                Log.i("PATH", file.absolutePath)
                val fileOutputStream = FileOutputStream(file)
                fileOutputStream.use {
                    it.write(data!!.bytes())
                }
                fileOutputStream.close()

                val audioNote: AudioNote = AudioNote(
                    path = file!!.absolutePath,
                    name = element.name!!,
                    description = element.description!!,
                    size = element.size!!,
                    url = element.url
                )

                audioNoteList.add(audioNote)

                binding.recyclerView.visibility = View.VISIBLE

                adapter.notifyDataSetChanged()


            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("ERROR", "Saving file error")
            }

        })
    }

    fun addRecord(audioNote: AudioNote) {
        audioNoteList.add(audioNote)
        recyclerView.adapter!!.notifyDataSetChanged()

    }

    fun playRecording(audioNote: AudioNote) {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        mediaPlayer = MediaPlayer()
        mediaPlayer.setDataSource(audioNote.path)
        mediaPlayer.prepare()
        mediaPlayer.start()
    }


    private fun setupActionBar() {
        val toolbar = binding.toolbarDashboard
        toolbar.title = sessionManager.fetchUserName()
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_baseline_logout_24)
        toolbar.setNavigationOnClickListener {

            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Do you wish to log out?").setCancelable(true)
                .setPositiveButton("Yes") { _, _ ->
                    deleteTokenAndData()
                    Navigation.findNavController(requireView())
                        .navigate(R.id.action_dashboardFragment_to_loginFragment2)
                }.setNegativeButton("No") { _, _ ->

                }.show()
        }
    }

    private fun deleteTokenAndData() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }

        audioNoteList.forEach {
            File(it.path).delete()
        }
        audioNoteList.clear()
        adapter.notifyDataSetChanged()
        sessionManager.shutdown()

    }

    override fun onStop() {
        super.onStop()
        deleteTokenAndData()
        Log.i("CLOSED", "Success")
    }

    fun deleteRecording(item: AudioNote) {

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Are you sure you with to delete this recording?").setCancelable(true)
            .setPositiveButton("Yes") { _, _ ->
                parentActivity.showProgressDialog()

                module = RetrofitModule
                module.initRetrofit(requireContext())

                val splitted = item.url.split("/")
                val fileId = splitted[splitted.size - 1]

                module.retrofit.deleteAudioNote(
                    token = "Bearer ${sessionManager.fetchAuthToken()}",
                    fileId
                )
                    .enqueue(object : Callback<Message> {
                        override fun onResponse(
                            call: Call<Message>,
                            response: Response<Message>
                        ) {
                            val responseMessage = response.body()

                            parentActivity.hideProgressDialog()

                            if (response.code() == 200) {
                                if (responseMessage != null) {
                                    parentActivity.showSnackBar("Delete successful", false)
                                }
                                deleteLocally(item)

                            } else {
                                parentActivity.showSnackBar("Delete failed", true)
                            }
                        }

                        override fun onFailure(call: Call<Message>, t: Throwable) {
                            parentActivity.hideProgressDialog()
                            parentActivity.showSnackBar("Something went wrong", true)
                        }

                    })
            }.setNegativeButton("No") { _, _ ->

            }.show()

    }

    private fun deleteLocally(item: AudioNote) {
        audioNoteList.remove(item)
        adapter.notifyDataSetChanged()

    }
}