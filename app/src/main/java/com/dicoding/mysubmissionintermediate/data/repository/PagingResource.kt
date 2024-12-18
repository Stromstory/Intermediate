package com.dicoding.mysubmissionintermediate.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dicoding.mysubmissionintermediate.data.response.ListStoryItem
import com.dicoding.mysubmissionintermediate.data.response.StoryResponse
import com.dicoding.mysubmissionintermediate.data.retrofit.ApiService

class PagingResource(
    private val apiService: ApiService,
    private val token: String
) : PagingSource<Int, ListStoryItem>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val page = params.key ?: INITIAL_PAGE_INDEX
            val response: StoryResponse = apiService.getStories("Bearer $token", page, params.loadSize)

            val stories = response.listStory

            LoadResult.Page(
                data = stories,
                prevKey = if (page == INITIAL_PAGE_INDEX) null else page - 1,
                nextKey = if (stories.isEmpty()) null else page + 1
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }
}