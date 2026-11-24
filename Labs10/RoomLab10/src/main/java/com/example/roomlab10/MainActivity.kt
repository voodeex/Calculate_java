package com.example.roomlab10

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val root = ScrollView(this)
        val padding = (24 * resources.displayMetrics.density).toInt()
        val page = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(padding, padding, padding, padding)
        }
        root.addView(page)
        setContentView(root)
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val safe = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(safe.left, safe.top, safe.right, safe.bottom)
            insets
        }
        val title = TextView(this).apply { textSize = 28f; text = "Автосервис" }
        val status = TextView(this).apply { textSize = 17f }
        val run = Button(this).apply { text = "Выполнить сценарий"; isEnabled = false }
        val hint = TextView(this).apply {
            text = "Команды и результаты выводятся в Logcat с тегом ROOM_LAB."
        }
        listOf(title, status, run, hint).forEach { page.addView(it) }
        val db = AppDatabase.get(this)

        lifecycleScope.launch {
            try {
                val (masters, orders) = withContext(Dispatchers.IO) {
                    db.masters().getAll().size to db.orders().count()
                }
                status.text = "Мастеров: $masters\nЗаказов: $orders"
                Console.out("Состояние базы: мастеров $masters, заказов $orders.")
                run.isEnabled = masters == 0 && orders == 0
                if (!run.isEnabled) run.text = "Сценарий уже запускался"
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                status.text = "Не удалось открыть базу: ${error.message}"
            }
        }

        run.setOnClickListener {
            run.isEnabled = false
            status.text = "Сценарий выполняется…"
            lifecycleScope.launch {
                try {
                    val result = withContext(Dispatchers.IO) { demonstration(db) }
                    status.text = "Сценарий завершён.\n\n$result"
                    run.text = "Сценарий завершён"
                } catch (error: CancellationException) {
                    throw error
                } catch (error: Exception) {
                    Console.out("Сценарий прерван: ${error.message}")
                    status.text = "Сценарий прерван: ${error.message}"
                }
            }
        }
    }

    private suspend fun demonstration(db: AppDatabase): String = coroutineScope {
        val console = Console(db)
        val ready = CompletableDeferred<Unit>()
        val changed = CompletableDeferred<Unit>()
        val watcher = launch {
            db.orders().observeCount(1).collect { count ->
                Console.out("Подписка Flow, заказы мастера #1: $count")
                ready.complete(Unit)
                if (count > 0) changed.complete(Unit)
            }
        }
        val benchmark: String
        try {
            ready.await()
            console.run(script)
            check(withTimeoutOrNull(3000) { changed.await() } != null) { "Flow не увидел изменения" }
            benchmark = console.benchmark()
        } finally {
            watcher.cancelAndJoin()
        }
        val masters = db.masters().getAll().size
        val orders = db.orders().count()
        Console.out("После сценария: мастеров $masters, заказов $orders.")
        "Мастеров: $masters\nЗаказов: $orders\n\n$benchmark"
    }

    private val script = listOf(
        "list",
        "add master Алексей|7",
        "add master Нина|4",
        "add master |5",
        "masters",
        "flow 1",
        "add car1|1|Лада Веста|4500|2026-10-01",
        "add car2|1|Хёндэ Солярис|7200|2026-10-02",
        "add car3|2|Фольксваген Поло|6300|2026-10-03",
        "addmany 1|Шкода Октавия|8000|2026-10-04;2|Киа Рио|5100|2026-10-05",
        "addmany 1|Отмена транзакции|9000|2026-10-05;9999|Нет мастера|1200|2026-10-05",
        "list",
        "find 1|5000",
        "stat 1",
        "join 1",
        "update @car1|1|Лада Веста|5600|2026-10-06",
        "update 9999|1|Не найден|50|2026-10-06",
        "add bad|1|Неверная дата|60|2026-02-30",
        "delete 9999",
        "delete @car2",
        "find 1|5000",
        "list 1",
        "stat 1",
        "join 2",
        "flow 1",
    )
}
