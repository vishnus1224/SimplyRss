package com.vishnus1224.simplyrss.feedlibrary.viewsavedfeeds

import android.app.ProgressDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.vishnus1224.simplyrss.R
import com.vishnus1224.simplyrss.di.appComponent
import com.vishnus1224.simplyrss.feedlibrary.Feed
import com.vishnus1224.simplyrss.feedlibrary.ViewSavedFeedsViewModelFactory
import com.vishnus1224.simplyrss.feedlibrary.addnewfeed.AddNewFeedDialogFragment
import com.vishnus1224.simplyrss.feedlibrary.di.*
import com.vishnus1224.simplyrss.feedlibrary.viewsavedfeeds.ViewSavedFeedsViewModel.ViewState.*
import com.vishnus1224.simplyrss.showarticles.ui.ShowArticlesActivity
import kotlinx.android.synthetic.main.activity_saved_feeds.*

private const val MENU_ADD_FEED_ID = 1
private const val TAG_ADD_NEW_FEED = "AddNewFeed"

class ViewSavedFeedsActivity : AppCompatActivity() {

    private lateinit var adapter: ViewSavedFeedsAdapter

    private lateinit var addNewFeedDialog: AddNewFeedDialogFragment

    private lateinit var viewModel: ViewSavedFeedsViewModel

    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_feeds)
        title = getString(R.string.feed_library_title)

        initModule()
        initProgressDialog()
        initRecycler()
        initViewModel()

        bindToAddFeedResultIfDialogAlreadyShowing()
        bindToSavedFeedsViewState()
        viewModel.getSavedFeeds()
    }

    private fun initProgressDialog() {
        progressDialog = ProgressDialog(this)
    }

    private fun initModule() {
        FeedLibraryModule.init(appComponent.provideFeedLibraryComponent())
        ViewSavedFeedsModuleWithSingleK.init(ViewSavedFeedsWithSingleK)
        DeleteFeedModuleWithSingleK.init(DeleteFeedWithSingleK)
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewSavedFeedsViewModelFactory(
                this,
                viewSavedFeedsComponent.viewSavedFeedsUseCase(),
                deleteFeedComponent.deleteFeedUseCase()
            )
        ).get(ViewSavedFeedsViewModel::class.java)
    }

    private fun bindToSavedFeedsViewState() {
        viewModel
            .bindToViewState()
            .observe(this, Observer { viewState -> when (viewState) {
                    is ShowFeeds -> adapter.setFeeds(viewState.feeds)
                    is ShowError -> showError(viewState.message)
                    is ShowDeleteFeedConfirmation -> showDeleteFeedConfirmationAlert(viewState.feedToDelete)
                    is ShowProgress -> showProgress(viewState.message)
                    is HideProgress -> hideProgress()
                }
            })
    }

    private fun showDeleteFeedConfirmationAlert(feedToDelete: Feed) {
        val alert = AlertDialog.Builder(this)
            .setMessage(getString(R.string.confirmation_message_delete_feed))
            .setTitle(getString(R.string.title_delete_feed_confirmation))
            .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                viewModel.onDeleteFeedConfirmed(feedToDelete)
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.no)) { dialog, _ -> dialog.dismiss() }
            .create()
        alert.show()
    }

    private fun showProgress(message: String) {
        progressDialog.setMessage(message)
        progressDialog.show()
    }

    private fun hideProgress() {
        progressDialog.hide()
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun bindToAddFeedResultIfDialogAlreadyShowing() {
        val fragment = supportFragmentManager.findFragmentByTag(TAG_ADD_NEW_FEED)
        if (fragment != null) {
            addNewFeedDialog = fragment as AddNewFeedDialogFragment
            bindToNewFeedAdded()
        }
    }

    private fun bindToNewFeedAdded() {
        addNewFeedDialog
            .bindToOnFeedAdded()
            .observe(this, Observer {
                addNewFeedDialog.dismiss()
                viewModel.getSavedFeeds()
            })
    }

    private fun initRecycler() {
        adapter = ViewSavedFeedsAdapter(arrayListOf(), ::onFeedClick, ::onDeleteFeedClick)
        recycler_saved_feeds.layoutManager = GridLayoutManager(this, 2)
        recycler_saved_feeds.adapter = adapter
    }

    private fun onFeedClick(feed: Feed) {
        ShowArticlesActivity.launch(this, feed.feedUrl)
    }

    private fun onDeleteFeedClick(feed: Feed) {
        viewModel.onDeleteFeedClicked(feed)
    }

    private fun onAddNewFeedClick() {
        addNewFeedDialog = AddNewFeedDialogFragment()
        addNewFeedDialog.show(supportFragmentManager, TAG_ADD_NEW_FEED)
        bindToNewFeedAdded()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu
            ?.add(0, MENU_ADD_FEED_ID, 0, getString(R.string.add_new_feed))
            ?.setIcon(android.R.drawable.ic_menu_add)
            ?.setOnMenuItemClickListener {
                onAddNewFeedClick()
                true
            }
            ?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onDestroy() {
        super.onDestroy()
        progressDialog.dismiss()
    }

}