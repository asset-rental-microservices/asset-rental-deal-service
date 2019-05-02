package org.rentalhouse.deal.rental.assets.utils

sealed class Either<out L, out R> {

    fun isRight() = when (this) {
        is Right -> true
        else -> false
    }

    companion object {
        fun <T : Throwable> left(th: T) = Left(th)
        fun <T : Any> right(value: T)   = Right(value)
    }
}

class Left<T : Throwable>(val error: T) : Either<T, Nothing>()
class Right<T : Any>(val value: T) : Either<Nothing, T>()