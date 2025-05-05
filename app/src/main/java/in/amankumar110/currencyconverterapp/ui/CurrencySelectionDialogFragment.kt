package `in`.amankumar110.currencyconverterapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import `in`.amankumar110.currencyconverterapp.databinding.FragmentCurrencySelectionDialogBinding
import `in`.amankumar110.currencyconverterapp.models.currency.Currency
import `in`.amankumar110.currencyconverterapp.ui.adapters.CurrencySelectionAdapter
import `in`.amankumar110.currencyconverterapp.utils.SpacingItemDecoration

class CurrencySelectionDialogFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentCurrencySelectionDialogBinding
    private lateinit var currencySelectionAdapter: CurrencySelectionAdapter

    // Declare the listener as a variable to be set by the parent
    private var listener: ((Currency) -> Unit)? = null

    // Provide a method to set the listener
    fun setOnCurrencySelectedListener(listener: (Currency) -> Unit) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCurrencySelectionDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Initialize the adapter with the onItemClick callback
        currencySelectionAdapter = CurrencySelectionAdapter(requireContext(), onCurrencyItemClicked)

        binding.rvCurrencySelection.apply {
            adapter = currencySelectionAdapter
            addItemDecoration(SpacingItemDecoration(requireContext(),50,100))
            setHasFixedSize(true)
        }
    }

    // Define the item click listener
    private val onCurrencyItemClicked: (Currency) -> Unit = { currency ->
        // Trigger the listener when an item is clicked
        listener?.invoke(currency)
        dismiss() // Close the dialog after selection
    }

    companion object {
        fun newInstance(): CurrencySelectionDialogFragment {
            return CurrencySelectionDialogFragment()
        }
    }
}
