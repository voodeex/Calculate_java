package com.example.roomlab10

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import androidx.room.withTransaction
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import java.time.DateTimeException
import java.time.LocalDate
import java.util.Locale
import kotlin.system.measureTimeMillis

class Console(private val db: AppDatabase) {
    private val masters = db.masters()
    private val orders = db.orders()
    private val aliases = mutableMapOf<String, Long>()
    private val orderComparator = compareByDescending<Order> { it.cost }.thenBy { it.id }

    suspend fun run(script: List<String>) {
        for (line in script) {
            out("> $line")
            try {
                execute(line)
            } catch (error: CancellationException) {
                throw error
            } catch (_: SQLiteConstraintException) {
                out("Ошибка: нарушен внешний ключ. Изменения команды отменены.")
            } catch (error: Exception) {
                out("Ошибка: ${error.message ?: error.javaClass.simpleName}")
            }
        }
    }

    private suspend fun execute(line: String) {
        val command = line.substringBefore(' ')
        val input = line.substringAfter(' ', "")
        val arguments = parts(input)
        when (command) {
            "add" -> {
                if (input.startsWith("master ")) {
                    val (name, experience) = parts(input.removePrefix("master ")).requireSize(2)
                    require(name.isNotBlank()) { "Имя мастера пустое" }
                    val years = experience.toIntOrNull()?.takeIf { it >= 0 }
                        ?: error("Стаж должен быть неотрицательным целым числом")
                    val id = masters.insert(Master(name = name, experience = years))
                    out("Мастер #$id: $name")
                } else {
                    val (alias, master, car, cost, date) = arguments.requireSize(5)
                    val id = orders.insert(order(master, car, cost, date))
                    aliases[alias] = id
                    out("Заказ #$id [@$alias]")
                }
            }
            "addmany" -> {
                val entries = input.split(';').map { entry ->
                    val (master, car, cost, date) = parts(entry).requireSize(4)
                    order(master, car, cost, date)
                }
                val ids = db.withTransaction { orders.insertAll(entries) }
                out("Добавлено в транзакции: ${ids.size}, id: ${ids.joinToString()}")
            }
            "update" -> {
                val (key, master, car, cost, date) = arguments.requireSize(5)
                val id = resolve(key)
                require(orders.update(order(master, car, cost, date).copy(id = id)) == 1) {
                    "Заказ #$id не найден"
                }
                out("Обновлён заказ #$id")
            }
            "delete" -> {
                val id = resolve(arguments.requireSize(1).single())
                val existing = orders.byId(id) ?: error("Заказ #$id не найден")
                orders.delete(existing)
                out("Удалён заказ #$id")
            }
            "list" -> {
                val masterId = input.takeIf { it.isNotBlank() }?.let(::resolve)
                val rows = orders.getAll().filter { masterId == null || it.masterId == masterId }
                    .sortedBy { it.car }.map { "#${it.id} | ${it.car} | ${amount(it.cost)} | ${it.date}" }
                out("Заказов: ${rows.size}")
                rows.forEach(::out)
            }
            "masters" -> masters.getAll().forEach { out("#${it.id} | ${it.name} | стаж ${it.experience}") }
            "find" -> {
                val (master, minimum) = arguments.requireSize(2)
                val id = resolve(master)
                val threshold = cost(minimum)
                require(masters.byId(id) != null) { "Мастер #$id не найден" }
                val sql = orders.above(id, threshold)
                val kotlin = findInKotlin(id, threshold)
                check(sql == kotlin) { "SQL и Kotlin вернули разные записи" }
                out("Выборка SQL/Kotlin: ${sql.size} совпадающих записей")
                sql.forEach { out("#${it.id} | ${it.car} | ${amount(it.cost)}") }
            }
            "stat" -> {
                val id = resolve(arguments.requireSize(1).single())
                require(masters.byId(id) != null) { "Мастер #$id не найден" }
                out("Выручка мастера #$id: ${amount(orders.revenue(id))} руб.")
            }
            "join" -> {
                val id = resolve(arguments.requireSize(1).single())
                val rows = orders.details(id)
                out("Строк JOIN: ${rows.size}")
                rows.forEach { out("#${it.id} | ${it.master} | ${it.car} | ${amount(it.cost)}") }
            }
            "flow" -> {
                val id = resolve(arguments.requireSize(1).single())
                out("Flow, заказы мастера #$id: ${orders.observeCount(id).first()}")
            }
            else -> error("Неизвестная команда: $command")
        }
    }

    suspend fun benchmark(): String {
        val number = 2_000
        val date = LocalDate.of(2026, 10, 1)
        val master = db.withTransaction {
            val id = masters.insert(Master(name = "Замер производительности", experience = 0))
            orders.insertAll(List(number) { index ->
                Order(masterId = id, car = "Контрольный заказ ${index + 1}", cost = index * 10.0, date = date)
            })
            id
        }
        val minimum = 17_000.0
        val expected = orders.above(master, minimum)
        check(findInKotlin(master, minimum) == expected)
        val repeats = 20
        var kotlinMs = 0L
        var sqlMs = 0L
        repeat(repeats) { index ->
            // Чередуем порядок замеров, чтобы снизить влияние прогрева.
            suspend fun measureKotlin() {
                kotlinMs += measureTimeMillis { check(findInKotlin(master, minimum) == expected) }
            }
            suspend fun measureSql() {
                sqlMs += measureTimeMillis { check(orders.above(master, minimum) == expected) }
            }
            if (index % 2 == 0) {
                measureKotlin()
                measureSql()
            } else {
                measureSql()
                measureKotlin()
            }
        }
        val result = "Замер: $number записей, $repeats повторов, совпадений: ${expected.size}. " +
            "getAll + filter: $kotlinMs мс, SQL: $sqlMs мс."
        out(result)
        return result
    }

    private suspend fun findInKotlin(master: Long, minimum: Double) =
        orders.getAll().filter { it.masterId == master && it.cost > minimum }.sortedWith(orderComparator)

    private fun order(master: String, car: String, price: String, date: String): Order {
        require(car.isNotBlank()) { "Название автомобиля пустое" }
        val parsedDate = try {
            LocalDate.parse(date)
        } catch (_: DateTimeException) {
            error("Дата должна быть существующей в формате ГГГГ-ММ-ДД")
        }
        return Order(masterId = resolve(master), car = car, cost = cost(price), date = parsedDate)
    }

    private fun resolve(value: String): Long =
        if (value.startsWith('@')) aliases[value.drop(1)] ?: error("Нет такого псевдонима: $value")
        else value.toLongOrNull()?.takeIf { it > 0 } ?: error("Некорректный id: $value")

    private fun cost(value: String): Double =
        value.replace(',', '.').toDoubleOrNull()?.takeIf { it.isFinite() && it >= 0 }
            ?: error("Стоимость должна быть неотрицательным конечным числом")

    private fun parts(value: String) = value.split('|').map(String::trim)
    private fun List<String>.requireSize(size: Int): List<String> {
        require(this.size == size) { "Ожидалось аргументов: $size" }
        return this
    }

    private fun amount(value: Double) = String.format(Locale.ROOT, "%.2f", value)

    companion object {
        fun out(line: String) { Log.d("ROOM_LAB", line) }
    }
}
