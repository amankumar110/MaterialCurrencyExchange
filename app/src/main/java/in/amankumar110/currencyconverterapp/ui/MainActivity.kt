package `in`.amankumar110.currencyconverterapp.ui

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.View.OVER_SCROLL_NEVER
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.MaterialColors
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import `in`.amankumar110.currencyconverterapp.R
import `in`.amankumar110.currencyconverterapp.databinding.ActivityMainBinding
import `in`.amankumar110.currencyconverterapp.databinding.CurrencyItemLayoutBinding
import `in`.amankumar110.currencyconverterapp.models.currency.Currency
import `in`.amankumar110.currencyconverterapp.ui.adapters.NumberAdapter
import `in`.amankumar110.currencyconverterapp.utils.CalculationHistory
import `in`.amankumar110.currencyconverterapp.utils.DateUtils
import `in`.amankumar110.currencyconverterapp.utils.DisplayUtils
import `in`.amankumar110.currencyconverterapp.utils.NetworkMonitor
import `in`.amankumar110.currencyconverterapp.utils.SpacingItemDecoration
import `in`.amankumar110.currencyconverterapp.viewmodel.CurrencyViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var numberAdapter: NumberAdapter
    private lateinit var selectedCurrencyField: EditText
    private lateinit var currencyViewModel: CurrencyViewModel
    private lateinit var selectedCurrencyItemBinding: CurrencyItemLayoutBinding
    private lateinit var networkState: NetworkMonitor
    private lateinit var watcher1: TextWatcher
    private lateinit var watcher2: TextWatcher

    private var useDefaultValues = true

    private val ARG_USE_DEFAULT = "use_default"
    private val ARG_FIRST_CURRENCY = "first_currency"
    private val ARG_SECOND_CURRENCY = "second_currency"
    private val ARG_LAST_UPDATED_TIME = "last_updated"
    private val ARG_FIRST_CURRENCY_AMOUNT = "first_currency_amount"
    private val ARG_SECOND_CURRENCY_AMOUNT = "second_currency_amount"

    override fun onCreate(savedInstanceState: Bundle?) {
        DynamicColors.applyToActivityIfAvailable(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // status bar color
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorBackground)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.colorBackground)

        // initializing viewmodel
        currencyViewModel = ViewModelProvider(this)[CurrencyViewModel::class.java]

        // setup networkState
        networkState = NetworkMonitor(this)
        observeNetwork(networkState)

        // Initializing views and critical properties like watcher1, watcher, etc
        // required in restoring states
        initUi()

        // makes sure to setup default values even if user is not connected to Wi-Fi
        setupDefaultValuesIfRequired()
        // values are restored after config changes, either this or setupDefault values run
        restoreStatesIfRequired(savedInstanceState)

        // makes sure to update the ui based on new exchange rates
        observeExchangeRatesReady()
    }

    private fun observeNetwork(monitor: NetworkMonitor) {

        monitor.observe(this) { isConnected ->
            if (!isConnected)
                currencyViewModel.getLocalExchangeRates()
            else
                currencyViewModel.refreshExchangeRates()
        }
    }

    private fun cacheLatestExchangeRates() {
        lifecycleScope.launch {
            currencyViewModel.refreshExchangeRates()
        }
    }

    private fun observeExchangeRatesReady() {

        currencyViewModel.exchangeRatesReady.observe(this) { isReady ->

            if(currencyViewModel.isLoading.value==true || !isReady)
                return@observe

            // remove loading if the new rates data has been fetched and synced to db
            binding.swipeRefreshLayout.isRefreshing = false

            // Update LAt Updated Status
            val lastUpdated = currencyViewModel.lastUpdated.value
            if(lastUpdated!=null)
                binding.tvLastUpdated.text = DateUtils.DateUtils.getDateStringFrom(lastUpdated)

            // Convert amount when new exchange rates are accepted
            convertAmount(binding.firstCurrencyItemLayout,binding.secondCurrencyItemLayout)

        }
    }

    private fun setupDefaultValuesIfRequired() {

        Log.v("useDefault", useDefaultValues.toString())

        if (useDefaultValues==true) {

            Log.v("mainActivity: default", "USing default titles")

            val defaultCurrencyFrom = CalculationHistory.getDefaultCurrencyFrom()
            val defaultCurrencyTo = CalculationHistory.getDefaultCurrencyTo(this)
            val defaultAmount = CalculationHistory.getDefaultAmount()

            setCurrency(defaultCurrencyFrom, binding.firstCurrencyItemLayout)
            setCurrency(defaultCurrencyTo, binding.secondCurrencyItemLayout)
            updateCurrencyField(defaultAmount.toString(),binding.firstCurrencyItemLayout)
            convertAmount(binding.firstCurrencyItemLayout,binding.secondCurrencyItemLayout)
        }
    }

    private fun initUi() {

        // Numbers List and Adapter Initialization
        numberAdapter = NumberAdapter(this, onNumberItemClicked)

        binding.rvNumbers.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 4)
            adapter = numberAdapter
            setHasFixedSize(true)
            overScrollMode = OVER_SCROLL_NEVER
            addItemDecoration(SpacingItemDecoration(this@MainActivity, 10, 10))
        }

        // Setting Up Currency Fields
        setupCurrencyItem(binding.firstCurrencyItemLayout)
        setupCurrencyItem(binding.secondCurrencyItemLayout)
        watcher1 = addTextWatcher(binding.firstCurrencyItemLayout)
        watcher2 = addTextWatcher(binding.secondCurrencyItemLayout)

        // Default Selected Currency Field
        setSelectedCurrencyItem(binding.firstCurrencyItemLayout)

        binding.swipeRefreshLayout.apply {
            setOnRefreshListener {
                cacheLatestExchangeRates()
            }
        }

    }

    private fun setupCurrencyItem(itemBinding: CurrencyItemLayoutBinding) {

        itemBinding.etCurrencyInput.showSoftInputOnFocus = false

        val openSelectionCallback = {
            val dialog = CurrencySelectionDialogFragment.newInstance()
            dialog.setOnCurrencySelectedListener { currency ->
                setCurrency(currency, itemBinding)
                useDefaultValues = false
                convertAmount(binding.firstCurrencyItemLayout,binding.secondCurrencyItemLayout)
            }
            dialog.show(supportFragmentManager, "currency_selection")
        }

        itemBinding.tvCurrencyTitle.setOnClickListener { openSelectionCallback() }
        itemBinding.ivCurrencySelection.setOnClickListener { openSelectionCallback() }

        itemBinding.etCurrencyInput.apply {
            showSoftInputOnFocus = false
            isCursorVisible = true


            setOnClickListener {
                setSelectedCurrencyItem(itemBinding)
            }

            setOnFocusChangeListener { it, hasFocus ->
                if (hasFocus) {
                    setSelectedCurrencyItem(itemBinding)
                }
            }
        }

        itemBinding.root.setOnClickListener { setSelectedCurrencyItem(itemBinding) }
    }

    private fun setSelectedCurrencyItem(itemBinding: CurrencyItemLayoutBinding) {

        selectedCurrencyItemBinding = itemBinding
        setSelectedCurrencyField(itemBinding.etCurrencyInput)

        // Set Selected Currency Item Color
        val colorPrimary = MaterialColors.getColor(
            itemBinding.tvCurrencyTitle,
            com.google.android.material.R.attr.colorPrimary,
            getColor(R.color.colorPrimary)
        )
        selectedCurrencyItemBinding.tvCurrencyTitle.setTextColor(colorPrimary)
        selectedCurrencyItemBinding.ivCurrencySelection.setColorFilter(colorPrimary)

        val otherCurrencyItem = getOtherCurrencyBinding(itemBinding)

        // Set Selected Currency Item Color
        val colorOnSurfaceVariant = MaterialColors.getColor(
            itemBinding.tvCurrencyTitle,
            com.google.android.material.R.attr.colorOnSurfaceVariant,
            getColor(R.color.colorOnSurfaceVariant)
        )

        otherCurrencyItem.tvCurrencyTitle.setTextColor(colorOnSurfaceVariant)
        otherCurrencyItem.ivCurrencySelection.setColorFilter(colorOnSurfaceVariant)
    }

    private fun setSelectedCurrencyField(editText: EditText) {
        editText.apply {
            selectedCurrencyField = editText
            setSelection(text.length)
        }
    }

    private fun setCurrency(
        currency: Currency,
        currencyBinding: CurrencyItemLayoutBinding
    ) {
        currencyBinding.currency = currency
    }

    private val onNumberItemClicked: (String) -> Unit = { item ->
        Log.d("TAG", "onNumberItemClicked: $item")

        val currentText = selectedCurrencyField.text.toString()

        when {
            item == "AC" -> {
                updateCurrencyField("",selectedCurrencyItemBinding)
            }

            item == "." -> {
                if (!currentText.contains(".") && currentText.length < 14) {
                    updateCurrencyField(currentText + item,selectedCurrencyItemBinding)
                }
            }

            item.toIntOrNull() != null -> {
                if (currentText.length < 14) {
                    updateCurrencyField(currentText + item,selectedCurrencyItemBinding)
                }
            }

            else -> {
                if (currentText.isNotEmpty()) {
                    updateCurrencyField(currentText.dropLast(1),selectedCurrencyItemBinding)
                }
            }
        }
    }

    fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun updateCurrencyField(text: String,currencyItemLayoutBinding: CurrencyItemLayoutBinding) {

        if(text.isEmpty()) {
            currencyItemLayoutBinding.etCurrencyInput.setText("")
            return
        }

        useDefaultValues = false

        val formattedAmount = DisplayUtils.getFormatedAmount(text)
        currencyItemLayoutBinding.etCurrencyInput.setText(formattedAmount)
        val targetEditText = currencyItemLayoutBinding.etCurrencyInput

        val cursorPos = formattedAmount.length.coerceAtMost(targetEditText.text.length)
        try {
            targetEditText.setSelection(cursorPos)
        } catch (_: IndexOutOfBoundsException) {
            // Fallback just in case
            targetEditText.setSelection(targetEditText.text.length)
        }
        hideKeyboard(binding.firstCurrencyItemLayout.etCurrencyInput)
    }

    private fun convertAmount(fromBinding: CurrencyItemLayoutBinding, toBinding: CurrencyItemLayoutBinding) {
        val fromCode = fromBinding.currency?.code
        val toCode = toBinding.currency?.code
        val amount = fromBinding.etCurrencyInput.text.toString().toDoubleOrNull()
        val targetEditText = toBinding.etCurrencyInput
        val watcher = if (toBinding == binding.firstCurrencyItemLayout) watcher1 else watcher2

        // Temporarily remove watcher to avoid feedback loop
        targetEditText.removeTextChangedListener(watcher)

        if (fromCode != null && toCode != null && amount != null) {
            val convertedAmount = currencyViewModel.convertAmount(fromCode, toCode, amount)
            val convertedText = convertedAmount?.toString() ?: ""
            updateCurrencyField(convertedText,toBinding)
        } else {
            targetEditText.setText("")
        }

        // Re-attach watcher
        targetEditText.addTextChangedListener(watcher)
    }

    private fun addTextWatcher(itemBinding: CurrencyItemLayoutBinding): TextWatcher {

        val watcher = object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
                val otherCurrencyItem = getOtherCurrencyBinding(itemBinding)
                convertAmount(itemBinding, otherCurrencyItem)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        itemBinding.etCurrencyInput.addTextChangedListener(watcher)
        return watcher
    }

    private fun getOtherCurrencyBinding(itemBinding: CurrencyItemLayoutBinding)
        : CurrencyItemLayoutBinding {

        val currencyItems = listOf(binding.firstCurrencyItemLayout, binding.secondCurrencyItemLayout)
        return currencyItems.find { it.root.id != itemBinding.root.id }?: return binding.firstCurrencyItemLayout
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.v("useDefault", "Saving state")

        val lastUpdated = binding.tvLastUpdated.text.toString()
        val firstCurrencyAmount = binding.firstCurrencyItemLayout.etCurrencyInput.text.toString()
        val secondCurrencyAmount = binding.secondCurrencyItemLayout.etCurrencyInput.text.toString()

        val firstCurrency = binding.firstCurrencyItemLayout.currency
        val firstCurrencyJson =  Gson().toJson(firstCurrency)

        val secondCurrency = binding.secondCurrencyItemLayout.currency
        val secondCurrencyJson =  Gson().toJson(secondCurrency)

        outState.putString(ARG_LAST_UPDATED_TIME, lastUpdated)
        outState.putString(ARG_FIRST_CURRENCY, firstCurrencyJson)
        outState.putString(ARG_SECOND_CURRENCY, secondCurrencyJson)
        outState.putBoolean(ARG_USE_DEFAULT, useDefaultValues)
        outState.putString(ARG_FIRST_CURRENCY_AMOUNT, firstCurrencyAmount)
        outState.putString(ARG_SECOND_CURRENCY_AMOUNT, secondCurrencyAmount)
    }

    private fun restoreStatesIfRequired(savedInstanceState: Bundle?) {

        if(savedInstanceState==null)
            return

        useDefaultValues = savedInstanceState.getBoolean(ARG_USE_DEFAULT, true)

        if(!useDefaultValues) {
            val lastUpdated = savedInstanceState.getString(ARG_LAST_UPDATED_TIME, "")
            val firstCurrencyJson = savedInstanceState.getString(ARG_FIRST_CURRENCY)
            val secondCurrencyJson = savedInstanceState.getString(ARG_SECOND_CURRENCY)
            val firstCurrencyAmount = savedInstanceState.getString(ARG_FIRST_CURRENCY_AMOUNT, "")

            val firstCurrency = Gson().fromJson(firstCurrencyJson, Currency::class.java)
            val secondCurrency = Gson().fromJson(secondCurrencyJson, Currency::class.java)

            binding.firstCurrencyItemLayout.currency = firstCurrency
            binding.firstCurrencyItemLayout.etCurrencyInput.setText(firstCurrencyAmount)
            binding.secondCurrencyItemLayout.currency = secondCurrency
            binding.tvLastUpdated.text = lastUpdated
        }
    }

}



