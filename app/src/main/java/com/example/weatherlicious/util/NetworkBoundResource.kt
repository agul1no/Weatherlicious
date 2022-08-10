package com.example.weatherlicious.util

import kotlinx.coroutines.flow.*

inline fun <ResultType, RequestType> networkBoundResource(
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend () -> RequestType,
    crossinline saveFetchResult: suspend (RequestType) -> Unit,
    crossinline shouldFetch: (ResultType) -> Boolean = { true }
) = flow {
    val data = query().first()

    val flow = if (shouldFetch(data)) {  // if statement checks if it's time to fetch the data
        emit(Resource.Loading(data))   // if it's time it would emit a Resource Loading so we can show the progress bar

        try {
            saveFetchResult(fetch())  // saves Network Request into the SQLite DB
            query().map { Resource.Success(it) } // transforms Flow<List<Forecast>> into Flow<Resource<List<Forecast>>>
        } catch (throwable: Throwable) {
            query().map { Resource.Error(throwable.toString(), it) } // We will know we are looking at the old data cause an error occurred
        }
    } else {
        query().map { Resource.Success(it) }
    }

    emitAll(flow)
}