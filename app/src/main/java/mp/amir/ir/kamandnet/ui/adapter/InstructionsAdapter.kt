package mp.amir.ir.kamandnet.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import mp.amir.ir.kamandnet.R
import mp.amir.ir.kamandnet.databinding.ItemInstructionBinding
import mp.amir.ir.kamandnet.models.Instruction
import mp.amir.ir.kamandnet.models.enums.RepairType
import mp.amir.ir.kamandnet.models.enums.SendingState
import mp.amir.ir.kamandnet.utils.general.exhaustive
import mp.amir.ir.kamandnet.utils.general.exhaustiveAsExpression

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
                holder.bindItem(currentList[position])
            }
        }
    }

    inner class FavoriteViewHolder(private val mBinding: ItemInstructionBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        fun bindItem(item: Instruction) {
            itemView.setOnClickListener {
                interaction.onInstructionItemClicked(item)
            }
            mBinding.data = item
            mBinding.executePendingBindings()

            mBinding.ivRepairType.setImageResource(
                when (item.repairType) {
                    RepairType.PM -> R.drawable.ic_pm
                    RepairType.EM -> R.drawable.ic_run
                    RepairType.CM -> R.drawable.ic_cm
                    RepairType.PDM -> R.drawable.ic_pdm
                }.exhaustiveAsExpression()
            )

            when(item.sendingState) {
                SendingState.NotReady -> {
                    mBinding.progressBar.visibility = View.GONE
                    mBinding.ivSave.visibility = View.GONE
                }
                SendingState.Ready -> {
                    mBinding.progressBar.visibility = View.GONE
                    mBinding.ivSave.visibility = View.VISIBLE
                    mBinding.ivSave.setImageResource(R.drawable.ic_save)
                }
                SendingState.Sending -> {
                    mBinding.progressBar.visibility = View.VISIBLE
                    mBinding.ivSave.visibility = View.GONE
                }
                SendingState.Sent -> {
                    mBinding.progressBar.visibility = View.GONE
                    mBinding.ivSave.visibility = View.VISIBLE
                    mBinding.ivSave.setImageResource(R.drawable.ic_check)
                }
            }.exhaustive()

        }
    }

    interface Interaction {
        fun onInstructionItemClicked(item: Instruction)
    }
}