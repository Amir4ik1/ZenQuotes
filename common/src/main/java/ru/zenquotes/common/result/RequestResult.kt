package ru.zenquotes.common.result

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.zenquotes.common.result.RequestResult.Failure
import ru.zenquotes.common.result.RequestResult.Loading
import ru.zenquotes.common.result.RequestResult.Success

/**
 * Интерфейс с возможными значениями: успех [Success], ошибка [Failure] или загрузка [Loading]
 * Применяется как результат ответов от слоя модели
 *
 * @param T возвращаемый тип
 */
sealed interface RequestResult<out T> {
    data class Success<T>(val data: T) : RequestResult<T>
    data class Failure<T>(val message: String, val throwable: Throwable? = null) : RequestResult<T>
    data object Loading : RequestResult<Nothing>

    fun <T> success(data: T) = Success(data)

    suspend fun suspendWhenResult(
        onSuccess: suspend (T) -> Unit = {},
        onFailure: suspend (message: String, throwable: Throwable?) -> Unit = { _, _ -> },
        onLoading: suspend () -> Unit = {}
    ) = when (this) {
        is Success -> onSuccess(data)
        is Failure -> onFailure(message, throwable)
        Loading -> onLoading()
    }

    val dataOrNull: T?
        get() = (this as? Success)?.data

}

inline fun <T> RequestResult<T>.whenResult(
    onSuccess: (T) -> Unit = {},
    onFailure: (message: String, throwable: Throwable?) -> Unit = { _, _ -> },
    onLoading: () -> Unit = {}
) {
    when (this) {
        is RequestResult.Success -> onSuccess(data)
        is RequestResult.Failure -> onFailure(message, throwable)
        RequestResult.Loading -> onLoading()
    }
}

fun <T> RequestResult<T>.toFlow(): Flow<T> = flow {
    when (this@toFlow) {
        is RequestResult.Success -> emit(data)
        is RequestResult.Failure -> throw throwable ?: Exception(message)
        is RequestResult.Loading -> {
            throw IllegalStateException("Can't emit value from Loading state")
        }
    }
}