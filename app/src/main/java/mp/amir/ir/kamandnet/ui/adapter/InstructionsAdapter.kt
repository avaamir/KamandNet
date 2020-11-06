package mp.amir.ir.kamandnet.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import mp.amir.ir.kamandnet.R
import mp.amir.ir.kamandnet.databinding.ItemInstructionBinding
import mp.amir.ir.kamandnet.models.Instruction

class InstructionsAdapter(private val interaction: Interaction) :
    ListAdapter<Instruction, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Instruction>() {

            override fun areItemsTheSame(oldItem: Instruction, newItem: Instruction): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Instruction, newItem: Instruction): Boolean {
                return oldItem == newItem
            }

        }
    }

    private lateinit var layoutInflater: LayoutInflater


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (!::layoutInflater.isInitialized) {
            layoutInflater = LayoutInflater.from(parent.context)
        }
        val binding = DataBindingUtil.inflate<ItemInstructionBinding>(
            layoutInflater,
            R.layout.item_instruction,
            parent,
            false
        )
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is FavoriteViewHolder -> {
                holder.bindFavorite(currentList[position])
            }
        }
    }

    inner class FavoriteViewHolder(private val mBinding: ItemInstructionBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        fun bindFavorite(item: Instruction) {
            itemView.setOnClickListener {
                interaction?.onInstructionItemClicked(item)
            }
            mBinding.data = item
            mBinding.executePendingBindings()
        }
    }

    interface Interaction {
        fun onInstructionItemClicked(item: Instruction)
    }
}