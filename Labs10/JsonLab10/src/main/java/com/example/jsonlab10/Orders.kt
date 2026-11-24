package com.example.jsonlab10

import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import com.google.gson.Strictness
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

data class Order(
    val id: Long,
    val number: String?,
    val total: BigDecimal?,
    val paid: Boolean?,
    val customer: Customer?,
    val items: List<String?>?,
)

data class Customer(val name: String?, val city: String?)
data class OrderResponse(val orders: List<Order?>?, val total: Int)

object OrdersJson {
    private val gson = GsonBuilder().setStrictness(Strictness.STRICT).setPrettyPrinting().create()

    fun parse(json: String): List<Order> {
        val parsed = gson.fromJson(json, OrderResponse::class.java)?.orders
            ?: throw JsonParseException("В JSON нет массива orders")
        return parsed.mapIndexed { i, order ->
            if (order == null || order.id <= 0 || order.number.isNullOrBlank() ||
                order.total == null || order.total.signum() < 0 ||
                order.total.precision() > 30 || order.total.scale() !in -12..12
            ) {
                throw JsonParseException("Некорректные поля заказа №${i + 1}")
            }
            order
        }
    }

    fun encode(orders: List<Order>): String = gson.toJson(OrderResponse(orders, orders.size))
}

fun money(amount: BigDecimal): String =
    NumberFormat.getCurrencyInstance(Locale.forLanguageTag("ru-RU")).format(amount)
