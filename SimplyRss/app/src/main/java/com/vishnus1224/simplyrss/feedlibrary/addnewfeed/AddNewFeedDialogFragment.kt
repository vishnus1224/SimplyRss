package com.vishnus1224.simplyrss.feedlibrary.addnewfeed

import android.app.ProgressDialog
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.vishnus1224.simplyrss.R
import com.vishnus1224.simplyrss.di.provideAppModule
import com.vishnus1224.simplyrss.feedlibrary.Feed
import com.vishnus1224.simplyrss.feedlibrary.di.AddNewFeedWithSingleK
import com.vishnus1224.simplyrss.feedlibrary.addnewfeed.AddNewFeedViewModel.AddNewFeedViewState.*
import com.vishnus1224.simplyrss.feedlibrary.ui.AddNewFeedViewModelFactory
import kotlinx.android.synthetic.main.dialog_fragment_add_new_feed.*

internal class AddNewFeedDialogFragment : DialogFragment() {

    private lateinit var viewModel: AddNewFeedViewModel

    private lateinit var progressDialog: ProgressDialog

    private val onFeedAddedLiveData = MutableLiveData<Feed>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_fragment_add_new_feed, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (activity == null) return

        add_new_feed_confirm_button.setOnClickListener { onConfirmButtonClick() }
        add_new_feed_dismiss_button.setOnClickListener { dismiss() }

        val appModule = provideAppModule(activity!!.application)

        initProgressDialog()
        //initPresenter()
        initViewModel(appModule.addNewFeedModule(activity!!.application))

        viewModel
            .bindToViewState()
            .observe(activity!!, Observer { viewState ->
                when (viewState) {
                    is AddNewFeedSuccess -> onFeedAddedLiveData.value = viewState.feed
                    is AddNewFeedFailed -> showError(viewState.message)
                    is ShowErrorOnTitleField -> showTitleError(viewState.message)
                    is ShowErrorOnUrlField -> showUrlError(viewState.message)
                    is ShowErrorOnDescriptionField -> showDescriptionError(viewState.message)
                    is ShowProgress -> showProgress()
                    is HideProgress -> hideProgress()
                }
            })
    }

    fun bindToOnFeedAdded(): LiveData<Feed> = onFeedAddedLiveData

    private fun initProgressDialog() {
        progressDialog = ProgressDialog(activity!!)
    }

    private fun initViewModel(addNewFeedModule: AddNewFeedWithSingleK) {
        viewModel = ViewModelProvider(
            this,
            AddNewFeedViewModelFactory(
                activity!!,
                addNewFeedModule.saveFeedUseCase
            )
        ).get(AddNewFeedViewModel::class.java)
    }

    private fun onConfirmButtonClick() {
        val title = add_new_feed_title.text.toString()
        val url = add_new_feed_url.text.toString()
        val description = add_new_feed_description.text.toString()

        viewModel.onAddNewFeedClick(title, url, description)
    }

    private fun showProgress() {
        progressDialog.setMessage(getString(R.string.progress_message_save_feed))
        progressDialog.show()
    }

    private fun hideProgress() {
        progressDialog.hide()
    }

    private fun showError(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    private fun showTitleError(message: String) {
        add_new_feed_title.error = message
    }

    private fun showUrlError(message: String) {
        add_new_feed_url.error = message
    }

    private fun showDescriptionError(message: String) {
        add_new_feed_description.error = message
    }

    override fun onDestroy() {
        super.onDestroy()
        progressDialog.dismiss()
    }
}