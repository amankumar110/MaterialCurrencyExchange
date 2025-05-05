package `in`.amankumar110.currencyconverterapp.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.marginBottom
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import `in`.amankumar110.currencyconverterapp.R
import `in`.amankumar110.currencyconverterapp.databinding.CurrencySelectionItemLayoutBinding
import `in`.amankumar110.currencyconverterapp.databinding.CurrenySelectionInputItemLayoutBinding
import `in`.amankumar110.currencyconverterapp.models.currency.Currency
import `in`.amankumar110.currencyconverterapp.utils.JsonUtils

class CurrencySelectionAdapter(
    val context: Context,
    val listener: (Currency) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_HEADER_INPUT = 0
    private val VIEW_TYPE_CURRENCY = 1

    private val allCurrencies: List<Currency>
    private var currencyList: List<Currency>

    init {
        val json = JsonUtils.readRawJson(context, R.raw.currencies)
        allCurrencies = JsonUtils.parseCurrencies(json.toString())
        currencyList = allCurrencies
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_HEADER_INPUT else VIEW_TYPE_CURRENCY
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER_INPUT) {
            val binding = CurrenySelectionInputItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            CurrencySelectionInputViewHolder(binding, this::filterCurrencies)
        } else {

            val binding = CurrencySelectionItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

            CurrencySelectionViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {

            is CurrencySelectionViewHolder -> {
                val currency = currencyList[position - 1]
                val isLast = position == itemCount - 1
                holder.bind(currency, listener)
                holder.setIsLastItem(isLast)
            }

            is CurrencySelectionInputViewHolder -> {
                holder.bind()
            }
        }
    }


    override fun getItemCount(): Int {
        // Add 1 for input view at the top
        return currencyList.size + 1
    }

    private fun filterCurrencies(input: String) {
        currencyList = if (input.trim().isEmpty()) {
            allCurrencies
        } else {
            allCurrencies.filter { currency ->
                currency.name.contains(input, ignoreCase = true) ||
                        currency.code.contains(input, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }

    class CurrencySelectionViewHolder(val binding: CurrencySelectionItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(currency: Currency, listener: (Currency) -> Unit) {
            binding.currency = currency
            binding.root.setOnClickListener { listener(currency) }
        }

        fun setIsLastItem(isLast: Boolean) {
            val layoutParams = binding.root.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.bottomMargin = if (isLast) {
                (16 * binding.root.resources.displayMetrics.density).toInt()
            } else {
                0
            }
            binding.root.layoutParams = layoutParams
        }

    }

    class CurrencySelectionInputViewHolder(
        val binding: CurrenySelectionInputItemLayoutBinding,
        val filterCurrencies: (String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.etCurrencyInput.addTextChangedListener { editable ->
                filterCurrencies(editable.toString())
            }
        }
    }
}
