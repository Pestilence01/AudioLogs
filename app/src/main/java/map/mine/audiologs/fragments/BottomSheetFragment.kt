package map.mine.audiologs.fragments


import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import map.mine.audiologs.R
import map.mine.audiologs.models.AudioNote
import map.mine.audiologs.databinding.BottomsheetFragmentBinding
import map.mine.audiologs.retrofit.Message
import map.mine.audiologs.retrofit.RetrofitModule
import map.mine.audiologs.retrofit.SessionManager
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class BottomSheetFragment(fragment: DashboardFragment) :
    BottomSheetDialogFragment(R.layout.bottomsheet_fragment) {

    private var _binding: BottomsheetFragmentBinding? = null
    private val binding get() = _binding!!

    private var parentFragment = fragment


    private val MICROPHONE_PERMISSION_CODE = 200

    private lateinit var recorder: MediaRecorder

    private lateinit var sessionManager: SessionManager

    private lateinit var module: RetrofitModule

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BottomsheetFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getMicrophonePermission()

        sessionManager = SessionManager(requireContext())

        recorder = MediaRecorder()

        buttonState(binding.record, true)
        buttonState(binding.stop, false)
        buttonState(binding.play, false)



        binding.record.setOnClickListener {
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            recorder.setOutputFile(getRecordingFilePath(binding.nameLoginBottomSheetText.text.toString()))
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            recorder.prepare()
            recorder.start()

            buttonState(binding.record, false)
            buttonState(binding.stop, true)
            buttonState(binding.play, false)
        }

        binding.stop.setOnClickListener {
            recorder.stop()
            recorder.release()

            buttonState(binding.record, false)
            buttonState(binding.stop, false)
            buttonState(binding.play, true)
        }

        binding.play.setOnClickListener {
            var mediaPlayer = MediaPlayer()
            mediaPlayer.setDataSource(getRecordingFilePath(binding.nameLoginBottomSheetText.text.toString()))
            mediaPlayer.prepare()
            mediaPlayer.start()
        }

        binding.upload.setOnClickListener {
            val location = getRecordingFilePath(binding.nameLoginBottomSheetText.text.toString())

            val file: File = File(location)
            val audioNote = AudioNote(
                path = file.absolutePath,
                name = binding.nameLoginBottomSheetText.text.toString(),
                description = binding.descriptionLoginBottomSheetText.text.toString(),
                size = file.length(),
                url = "default"
            )
            parentFragment.addRecord(audioNote)
            uploadRecording(audioNote)
            dismiss()
        }


    }

    private fun uploadRecording(audioNote: AudioNote) {

        module = RetrofitModule
        module.initRetrofit(requireContext())

        val file: File = File(audioNote.path)

        val requestFile: RequestBody =
            file
                .asRequestBody(
                    "audio/mpeg".toMediaTypeOrNull()
                )

        val body: MultipartBody.Part =
            MultipartBody.Part.createFormData("file", file.name, requestFile)
        val description: RequestBody = audioNote.description.toRequestBody(MultipartBody.FORM)

        module.retrofit.uploadAudioNote(
            token = "Bearer ${sessionManager.fetchAuthToken()}",
            description,
            body
        )
            .enqueue(object : Callback<Message> {
                override fun onResponse(
                    call: Call<Message>,
                    response: Response<Message>
                ) {
                    val uploadResponse = response.body()

                    if (response.code() == 200) {
                        if (uploadResponse != null) {
                            Log.i("success: ", uploadResponse.message)
                        }

                    } else {
                        Log.e("Upload fail", response.errorBody().toString())
                    }
                }

                override fun onFailure(call: Call<Message>, t: Throwable) {
                    Log.e("Communication error", t.message.toString())
                    Log.i("success: ", "NO")
                }

            })

    }

    private fun buttonState(button: AppCompatButton, enabled: Boolean) {
        when(button){
            binding.record -> {
                if(enabled){
                    binding.record.background = binding.record.context.getDrawable(R.drawable.ic_baseline_mic_24)
                    binding.record.isEnabled = true
                } else {
                    binding.record.background = binding.record.context.getDrawable(R.drawable.ic_baseline_mic_disabled_24)
                    binding.record.isEnabled = false
                }
            }
            binding.stop -> {
                if(enabled){
                    binding.stop.background = binding.record.context.getDrawable(R.drawable.ic_baseline_stop_24)
                    binding.stop.isEnabled = true
                } else {
                    binding.stop.background = binding.record.context.getDrawable(R.drawable.ic_baseline_stop_disabled_24)
                    binding.stop.isEnabled = false
                }
            }
            binding.play -> {
                if(enabled){
                    binding.play.background = binding.record.context.getDrawable(R.drawable.ic_baseline_play_arrow_24)
                    binding.play.isEnabled = true
                } else {
                    binding.play.background = binding.record.context.getDrawable(R.drawable.ic_baseline_play_arrow_disabled_24)
                    binding.play.isEnabled = false
                }
            }
        }
    }

    private fun getMicrophonePermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    android.Manifest.permission.RECORD_AUDIO,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                MICROPHONE_PERMISSION_CODE
            )
        }
    }

    private fun getRecordingFilePath(name: String): String {
        val contextWrapper = ContextWrapper(requireContext())
        val musicDir = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        val file = File(musicDir, "$name.mp3")
        return file.path
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MICROPHONE_PERMISSION_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        }
    }

}