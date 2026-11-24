package com.example.jsonlab10

import android.os.Bundle
import android.text.InputType
import android.util.AtomicFile
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonParseException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.math.BigDecimal

class MainActivity : ComponentActivity() {
    private val adapter = OrderAdapter()
    private var orders = emptyList<Order>()
    private var minimum: BigDecimal? = null
    private var sorted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val root = ScrollView(this)
        val page = column().apply {
            val padding = (24 * resources.displayMetrics.density).toInt()
            setPadding(padding, padding, padding, padding)
            isFocusableInTouchMode = true
        }
        root.addView(page)
        setContentView(root)
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val safe = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())
            view.setPadding(safe.left, safe.top, safe.right, safe.bottom)
            insets
        }

        val title = TextView(this).apply { textSize = 28f; text = "Заказы" }
        val status = TextView(this).apply { accessibilityLiveRegion = View.ACCESSIBILITY_LIVE_REGION_POLITE }
        val summary = TextView(this).apply { textSize = 16f }
        val filter = field("Сумма строго больше", decimalType)
        val apply = button("Применить")
        val clear = button("Сбросить")
        val sort = button("Сначала дорогие")
        val list = RecyclerView(this).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
            isNestedScrollingEnabled = false
        }
        val edit = button("Новый заказ")
        val form = column().apply { visibility = View.GONE }
        val number = field("Номер заказа")
        val amount = field("Сумма заказа", decimalType)
        val name = field("Имя клиента (необязательно)")
        val city = field("Город (необязательно)")
        val items = field("Позиции через запятую (необязательно)")
        val paid = CheckBox(this).apply { text = "Оплачен" }
        val add = button("Добавить")
        listOf(number, amount, name, city, items, paid, add).forEach { form.addView(it) }
        val save = button("Сохранить и прочитать JSON")
        val jsonText = TextView(this).apply {
            textSize = 13f
            setTextIsSelectable(true)
            typeface = android.graphics.Typeface.MONOSPACE
        }
        listOf(title, status, summary, filter, row(apply, clear), sort, list, edit, form, save, jsonText)
            .forEach { page.addView(it) }
        val file = AtomicFile(File(filesDir, "orders.json"))
        val actions = listOf(apply, clear, sort, edit, save)

        fun render() {
            val visible = orders.filter { order -> minimum?.let { order.total!! > it } ?: true }
            val rows = if (sorted) visible.sortedByDescending { it.total } else visible
            adapter.show(rows)
            val total = orders.fold(BigDecimal.ZERO) { sum, order -> sum + requireNotNull(order.total) }
            summary.text = "Показано: ${rows.size} из ${orders.size}\nСумма всех заказов: ${money(total)}"
        }

        fun showJson(raw: String) {
            jsonText.text = raw
            raw.chunked(1200).forEach { Log.d("JSON_LAB10", it) }
        }

        actions.forEach { it.isEnabled = false }
        lifecycleScope.launch {
            try {
                val loaded = withContext(Dispatchers.IO) {
                    val source = if (file.baseFile.exists()) {
                        file.openRead().bufferedReader(Charsets.UTF_8).use { it.readText() }
                    } else resources.openRawResource(R.raw.data).bufferedReader(Charsets.UTF_8)
                        .use { it.readText() }
                    OrdersJson.parse(source)
                }
                orders = loaded
            } catch (error: JsonParseException) {
                status.text = "Не удалось разобрать JSON: ${error.message.orEmpty()}"
            } catch (error: IOException) {
                status.text = "Не удалось прочитать файл: ${error.message.orEmpty()}"
            } finally {
                actions.forEach { it.isEnabled = true }
                render()
            }
        }

        apply.setOnClickListener {
            val value = amountOf(filter)
            if (value == null) {
                filter.error = "Введите сумму, например 780,00"
            } else {
                minimum = value
                hideKeyboard(filter)
                status.text = ""
                render()
            }
        }
        clear.setOnClickListener {
            minimum = null
            filter.text.clear()
            filter.error = null
            status.text = ""
            render()
        }
        sort.setOnClickListener {
            sorted = true
            status.text = "Отсортировано по убыванию суммы."
            render()
        }
        edit.setOnClickListener {
            form.visibility = if (form.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }
        add.setOnClickListener {
            val orderNumber = number.text.toString().trim()
            val orderAmount = amountOf(amount)
            if (orderNumber.isEmpty()) {
                number.error = "Введите номер заказа"
                return@setOnClickListener
            }
            if (orderAmount == null) {
                amount.error = "Введите неотрицательную сумму"
                return@setOnClickListener
            }
            val nextId = (orders.maxOfOrNull { it.id } ?: 0) + 1
            val customerName = name.text.toString().trim().ifEmpty { null }
            val customerCity = city.text.toString().trim().ifEmpty { null }
            val customer = if (customerName == null && customerCity == null) null
            else Customer(customerName, customerCity)
            val positions = items.text.toString().split(",").map(String::trim).filter(String::isNotEmpty)
            orders = orders + Order(nextId, orderNumber, orderAmount, paid.isChecked, customer, positions)
            hideKeyboard(number)
            listOf(number, amount, name, city, items).forEach { it.text.clear() }
            paid.isChecked = false
            form.visibility = View.GONE
            status.text = "Заказ $orderNumber добавлен."
            render()
            showJson(OrdersJson.encode(orders))
        }
        save.setOnClickListener {
            val json = OrdersJson.encode(orders)
            actions.forEach { it.isEnabled = false }
            add.isEnabled = false
            lifecycleScope.launch {
                try {
                    val restored = withContext(Dispatchers.IO) {
                        val stream = file.startWrite()
                        try {
                            stream.write(json.toByteArray(Charsets.UTF_8))
                            file.finishWrite(stream)
                        } catch (error: IOException) {
                            file.failWrite(stream)
                            throw error
                        }
                        file.openRead().bufferedReader(Charsets.UTF_8).use { it.readText() }
                    }
                    val reloaded = withContext(Dispatchers.IO) { OrdersJson.parse(restored) }
                    orders = reloaded
                    status.text = "orders.json сохранён и прочитан."
                    render()
                    showJson(restored)
                } catch (error: IOException) {
                    status.text = "Ошибка файла: ${error.message.orEmpty()}"
                } catch (error: JsonParseException) {
                    status.text = "Ошибка JSON: ${error.message.orEmpty()}"
                } finally {
                    actions.forEach { it.isEnabled = true }
                    add.isEnabled = true
                }
            }
        }
    }

    private val decimalType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
    private fun amountOf(field: EditText): BigDecimal? {
        val raw = field.text.toString().trim().replace(',', '.')
        return raw.takeIf { it.matches(Regex("[0-9]{1,12}(?:\\.[0-9]{1,2})?")) }?.toBigDecimalOrNull()
    }

    private fun hideKeyboard(field: View) {
        getSystemService(InputMethodManager::class.java).hideSoftInputFromWindow(field.windowToken, 0)
    }

    private fun column() = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
    private fun button(caption: String) = Button(this).apply { text = caption }

    private fun field(placeholder: String, kind: Int = InputType.TYPE_CLASS_TEXT) = EditText(this).apply {
        hint = placeholder
        inputType = kind
        if (kind == decimalType) {
            keyListener = android.text.method.DigitsKeyListener.getInstance("0123456789,.")
            setRawInputType(kind)
        }
        setSingleLine()
    }

    private fun row(vararg buttons: Button) = LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        buttons.forEach { addView(it, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)) }
    }
}
