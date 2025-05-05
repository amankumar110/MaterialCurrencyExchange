package `in`.amankumar110.currencyconverterapp.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.recyclerview.widget.RecyclerView
import `in`.amankumar110.currencyconverterapp.R
import `in`.amankumar110.currencyconverterapp.databinding.NumberItemLayoutBinding
import `in`.amankumar110.currencyconverterapp.databinding.UtilButtonItemLayoutBinding
import `in`.amankumar110.currencyconverterapp.utils.AnimationUtils

class NumberAdapter(val context : Context, val listener : (String) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_NUMBER = 1
    private val VIEW_TYPE_UTIL = 2
    private val VIEW_TYPE_EMPTY = 3

    private val items = listOf(
        "7", "8", "9", "AC",
        "4", "5", "6", "Clear",
        "1", "2", "3", "",
        "0", ".", "00", ""
    )

    override fun getItemViewType(position: Int): Int {
        val value = items[position]
        return when {
            value.isEmpty() -> VIEW_TYPE_EMPTY
            value == "AC" || value == "Clear" -> VIEW_TYPE_UTIL
            else -> VIEW_TYPE_NUMBER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if (viewType == VIEW_TYPE_NUMBER) {
            val binding =
                NumberItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return NumberViewHolder(binding)
        }

        if (viewType == VIEW_TYPE_UTIL) {
            val binding = UtilButtonItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return UtilOptionViewHolder(binding)
        }

        val view = View(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(70, 70)
        }
        return EmptyViewHolder(view)

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val value = items[position]

        when (holder) {
            is NumberViewHolder -> {
                holder.numberBinding.tvNumber.text = value
                holder.numberBinding.root.setOnClickListener {
                    AnimationUtils.scaleInAndRestore(view = it, onComplete = null)
                    listener(value)
                }
            }

            is UtilOptionViewHolder -> {
                holder.utilBinding.tvOption.text = value
                holder.utilBinding.root.setOnClickListener {
                    AnimationUtils.scaleInAndRestore(view = it, onComplete =null)
                    listener(value)
                }
            }
        }
    }

    override fun getItemCount(): Int = items.size

    private class NumberViewHolder(val numberBinding: NumberItemLayoutBinding) :
        RecyclerView.ViewHolder(numberBinding.root)

    private class UtilOptionViewHolder(val utilBinding: UtilButtonItemLayoutBinding) :
        RecyclerView.ViewHolder(utilBinding.root)

    private class EmptyViewHolder(view: View) : RecyclerView.ViewHolder(view)

}


