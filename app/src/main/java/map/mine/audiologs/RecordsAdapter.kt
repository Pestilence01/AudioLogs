package map.mine.audiologs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import map.mine.audiologs.databinding.AudioItemBinding

class RecordsAdapter(
    private var items: MutableList<Record>,
    private val onItemClickCallback: (Record) -> Unit
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

        fun setOnClick(item: Record){
            binding.mainLayout.setOnClickListener {
                onItemClickCallback(item)
            }
        }

        fun bind(record: Record){
            binding.title.text = record.name
            binding.user.text = "User"
        }

    }
}