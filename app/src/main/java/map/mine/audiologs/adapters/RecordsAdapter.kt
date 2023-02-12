package map.mine.audiologs.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import map.mine.audiologs.databinding.AudioItemBinding
import map.mine.audiologs.models.AudioNote

class RecordsAdapter(
    private var items: MutableList<AudioNote>,
    private val onItemClickCallback: (AudioNote) -> Unit
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
            binding.mainLayout.setOnClickListener {
                onItemClickCallback(item)
            }
        }

        fun bind(audioNote: AudioNote){
            binding.title.text = audioNote.name
            binding.user.text = "User"
        }

    }
}