package map.mine.audiologs.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import map.mine.audiologs.databinding.AudioItemBinding
import map.mine.audiologs.fragments.DashboardFragment
import map.mine.audiologs.models.AudioNote

class RecordsAdapter(
    private var items: MutableList<AudioNote>,
    private var parentFragment: DashboardFragment
) : RecyclerView.Adapter<RecordsAdapter.RecordsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordsViewHolder {
        val binding = AudioItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecordsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecordsViewHolder, position: Int) {
        holder.setOnClick(items[position]!!)
        holder.bind(items[position]!!)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class RecordsViewHolder(private val binding: AudioItemBinding) : RecyclerView.ViewHolder(binding.root){

        fun setOnClick(item: AudioNote){
            binding.playButton.setOnClickListener {
                parentFragment.playRecording(item)
            }
            binding.deleteButton.setOnClickListener {
                parentFragment.deleteRecord(item)
            }
        }

        fun bind(audioNote: AudioNote){
            binding.title.text = audioNote.name
            binding.description.text = "Description"
        }

    }
}