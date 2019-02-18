package com.vishnus1224.simplyrss.feedlibrary.addnewfeed

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import arrow.core.left
import arrow.core.right
import com.vishnus1224.simplyrss.R
import com.vishnus1224.simplyrss.feedlibrary.di.saveFeedUseCaseWithSingleK
import com.vishnus1224.simplyrss.feedlibrary.repository.FeedRepository
import com.vishnus1224.simplyrss.makeStringProviderWith
import com.vishnus1224.simplyrss.mock
import com.vishnus1224.simplyrss.feedlibrary.addnewfeed.AddNewFeedViewModel.AddNewFeedViewState.*
import com.vishnus1224.simplyrss.feedlibrary.feedFrom
import com.vishnus1224.simplyrss.testSchedulersRule
import com.vishnus1224.simplyrss.util.StringProvider
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*

internal class AddNewFeedViewModelTest {

    @Rule
    @JvmField
    val liveDataRule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    val schedulersRule = testSchedulersRule

    private val repository = FeedRepository::class.java.mock()

    private val provideString = makeStringProviderWith(strings)

    val observer = Observer<AddNewFeedViewModel.AddNewFeedViewState> { }.mock()

    private val viewModel = createViewModel(repository)

    @Before
    fun setup() {
        viewModel.bindToViewState().observeForever(observer)
    }

    @After
    fun cleanUp() {
        viewModel.bindToViewState().removeObserver(observer)
    }

    @Test
    fun `when all inputs are invalid only one input field should show error at a time starting from top to bottom`() {
        // Given
        val viewModelToTest = createViewModel(repository = repository)
        viewModelToTest
            .bindToViewState()
            .observeForever(observer)

        // When
        viewModelToTest.onAddNewFeedClick("", "", "")

        // Then
        verify(observer).onChanged(ShowErrorOnTitleField(provideString(R.string.error_message_invalid_title)))
        verifyNoMoreInteractions(observer)
        verifyZeroInteractions(repository)
        viewModelToTest.bindToViewState().removeObserver(observer)
    }

    @Test
    fun `when title is not empty and rest of the fields are empty or invalid, show error on url field first`() {
        // Given
        val viewModelToTest = createViewModel(repository = repository)
        viewModelToTest
            .bindToViewState()
            .observeForever(observer)

        // When
        viewModelToTest.onAddNewFeedClick(testTitle, "fjqkkdq", "")

        // Then
        verify(observer).onChanged(ShowErrorOnUrlField(provideString(R.string.error_message_invalid_url)))
        verifyNoMoreInteractions(observer)
        verifyZeroInteractions(repository)
        viewModelToTest.bindToViewState().removeObserver(observer)
    }


    @Test
    fun `when title is not empty, url is valid and description is empty, show error on description field`() {
        // Given
        val viewModelToTest = createViewModel(repository = repository)

        viewModelToTest
            .bindToViewState()
            .observeForever(observer)

        // When
        viewModelToTest.onAddNewFeedClick(testTitle, testUrl, "")

        // Then
        verify(observer).onChanged(ShowErrorOnDescriptionField(provideString(R.string.error_message_invalid_description)))
        verifyNoMoreInteractions(observer)
        verifyZeroInteractions(repository)
        viewModelToTest.bindToViewState().removeObserver(observer)
    }

    @Test
    fun `when inputs are valid, should save feed to the repository`() {
        viewModel.onAddNewFeedClick(testTitle, testUrl, testDescription)

        verify(repository).saveFeed(testFeed)
    }

    @Test
    fun `when saving feed to the repository, should show and hide progress dialog before sending the result of save operation`() {
        `when`(repository.saveFeed(testFeed)).thenReturn(testFeed.right())

        viewModel.onAddNewFeedClick(testTitle, testUrl, testDescription)

        val inOrder = inOrder(observer)
        inOrder.verify(observer).onChanged(ShowProgress)
        inOrder.verify(observer).onChanged(HideProgress)
        inOrder.verify(observer).onChanged(AddNewFeedSuccess(testFeed))
    }

    @Test
    fun `when saving feed fails, show error message`() {
        `when`(repository.saveFeed(testFeed)).thenReturn(Throwable("failed to save feed").left())

        viewModel.onAddNewFeedClick(testTitle, testUrl, testDescription)

        verify(observer).onChanged(AddNewFeedFailed(provideString(R.string.error_message_add_feed_failed)))
    }
}

private fun createViewModel(
    repository: FeedRepository,
    provideString: StringProvider = makeStringProviderWith(strings)
) = AddNewFeedViewModel(
    saveFeedUseCase = saveFeedUseCaseWithSingleK(repository),
    provideString = provideString
)

private val testTitle = "test title"
private val testUrl = "www.testUrl.co"
private val testDescription = "test description"
private val testFeed = feedFrom(testTitle, testUrl, testDescription)

private val strings: Map<Int, String> = mapOf(
    R.string.error_message_invalid_title to "invalid title",
    R.string.error_message_invalid_url to "invalid url",
    R.string.error_message_invalid_description to "invalid description",
    R.string.error_message_add_feed_failed to "failed to add feed"
)