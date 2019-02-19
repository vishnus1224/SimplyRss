package com.vishnus1224.simplyrss.feedlibrary.viewsavedfeeds

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import arrow.core.left
import arrow.core.right
import com.vishnus1224.simplyrss.R
import com.vishnus1224.simplyrss.feedlibrary.di.deleteWithUseCaseWithSingleK
import com.vishnus1224.simplyrss.feedlibrary.di.getAllFeedsWithSingleKUseCase
import com.vishnus1224.simplyrss.feedlibrary.feedFrom
import com.vishnus1224.simplyrss.feedlibrary.repository.FeedRepository
import com.vishnus1224.simplyrss.makeStringProviderWith
import com.vishnus1224.simplyrss.mock
import com.vishnus1224.simplyrss.testSchedulersRule
import com.vishnus1224.simplyrss.util.StringProvider
import com.vishnus1224.simplyrss.feedlibrary.viewsavedfeeds.ViewSavedFeedsViewModel.ViewState.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.*

internal class ViewSavedFeedsViewModelTest {

    @Rule
    @JvmField
    val liveDataRule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    val schedulersRule = testSchedulersRule

    private val repository = FeedRepository::class.java.mock()

    private val provideString = makeStringProviderWith(strings)

    val observer = Observer<ViewSavedFeedsViewModel.ViewState> { }.mock()

    val viewModel = createViewModel(repository)


    @Before
    fun setup() {
        viewModel.bindToViewState().observeForever(observer)
    }

    @After
    fun cleanUp() {
        viewModel.bindToViewState().removeObserver(observer)
    }

    @Test
    fun `when there are no saved feeds, show empty feeds error message`() {
        `when`(repository.getAllFeeds()).thenReturn(emptyList())

        viewModel.getSavedFeeds()

        verify(observer).onChanged(ShowError(provideString(R.string.error_message_empty_feeds)))
        verify(observer, never()).onChanged(ShowFeeds(ArgumentMatchers.anyList()))
    }

    @Test
    fun `when there is atleast one saved feed, should show it on screen`() {
        `when`(repository.getAllFeeds()).thenReturn(listOf(testFeed, testFeed))

        viewModel.getSavedFeeds()

        verify(observer).onChanged(ShowFeeds(listOf(testFeed, testFeed)))
        verify(observer, never()).onChanged(ShowError(provideString(R.string.error_message_empty_feeds)))
        verify(observer, never()).onChanged(ShowError(provideString(R.string.error_message_get_saved_feeds)))
    }

    @Test
    fun `when getting saved feeds fails, show and hide progress before and after respectively`() {
        `when`(repository.getAllFeeds()).thenReturn(listOf(testFeed, testFeed))

        viewModel.getSavedFeeds()

        val inOrder = inOrder(observer)
        inOrder.verify(observer).onChanged(ShowProgress(provideString(R.string.loading_indicator_message)))
        inOrder.verify(observer).onChanged(HideProgress)
        inOrder.verify(observer).onChanged(ShowFeeds(listOf(testFeed, testFeed)))
    }

    @Test
    fun `on delete feed clicked, should show confirmation alert`() {
        viewModel.onDeleteFeedClicked(testFeed)

        verify(observer).onChanged(ShowDeleteFeedConfirmation(testFeed))
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun `on delete feed confirmed, should show and hide progress before and after deleting feed respectively`() {
        viewModel.onDeleteFeedConfirmed(testFeed)

        val inOrder = inOrder(observer)
        inOrder.verify(observer).onChanged(ShowProgress(provideString(R.string.progress_message_delete_feed)))
        inOrder.verify(observer).onChanged(HideProgress)
    }

    @Test
    fun `on failed to delete feed, should show error message`() {
        `when`(repository.deleteFeed(testFeed)).thenReturn(Throwable("failed").left())

        viewModel.onDeleteFeedConfirmed(testFeed)

        verify(observer).onChanged(ShowError(provideString(R.string.error_message_delete_feed_failed)))
        verify(observer, never()).onChanged(ShowFeeds(ArgumentMatchers.anyList()))
    }

    @Test
    fun `after deleting a feed, should reload the feeds`() {
        `when`(repository.deleteFeed(testFeed)).thenReturn(Unit.right())
        `when`(repository.getAllFeeds()).thenReturn(listOf(testFeed))

        viewModel.onDeleteFeedConfirmed(testFeed)

        verify(observer).onChanged(ShowFeeds(listOf(testFeed)))
        verify(observer, never()).onChanged(ShowError(provideString(R.string.error_message_delete_feed_failed)))
        verify(observer, never()).onChanged(ShowError(provideString(R.string.error_message_get_saved_feeds)))
    }
}

private val testTitle = "test title"
private val testUrl = "www.testUrl.co"
private val testDescription = "test description"
private val testFeed = feedFrom(testTitle, testUrl, testDescription)

private fun createViewModel(
    feedRepository: FeedRepository,
    provideString: StringProvider = makeStringProviderWith(strings)
) = ViewSavedFeedsViewModel(
    getSavedFeedsUseCase = getAllFeedsWithSingleKUseCase(feedRepository),
    deleteFeedUseCase = deleteWithUseCaseWithSingleK(feedRepository),
    provideString = provideString
)

private val strings: Map<Int, String> = mapOf(
    R.string.loading_indicator_message to "show progress",
    R.string.progress_message_delete_feed to "delete feed",
    R.string.error_message_delete_feed_failed to "delete failed",
    R.string.error_message_empty_feeds to "empty feeds",
    R.string.error_message_get_saved_feeds to "get failed"
)