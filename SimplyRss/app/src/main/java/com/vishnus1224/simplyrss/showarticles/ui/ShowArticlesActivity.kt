package com.vishnus1224.simplyrss.showarticles.ui

import android.app.ProgressDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import arrow.effects.ForSingleK
import com.vishnus1224.simplyrss.R
import com.vishnus1224.simplyrss.di.AppModule
import com.vishnus1224.simplyrss.di.provideAppModule
import com.vishnus1224.simplyrss.showarticles.Article
import com.vishnus1224.simplyrss.showarticles.ShowArticlesViewModel
import com.vishnus1224.simplyrss.showarticles.ShowArticlesViewModel.ViewState.*
import com.vishnus1224.simplyrss.showarticles.ShowArticlesViewModelFactory
import com.vishnus1224.simplyrss.showarticles.di.ShowArticlesModule
import com.vishnus1224.simplyrss.util.getStringExtraOrThrow
import com.vishnus1224.simplyrss.util.provideAndroidStrings
import kotlinx.android.synthetic.main.activity_show_articles.*

private const val EXTRA_FEED_URL = "feedUrlToLoad"

class ShowArticlesActivity : AppCompatActivity() {

    private lateinit var showArticlesViewModel: ShowArticlesViewModel

    private lateinit var progressDialog: ProgressDialog

    private lateinit var adapter: ArticleListAdapter

    companion object {
        fun launch(context: Context, feedUrl: String) {
            val intent = Intent(context, ShowArticlesActivity::class.java)
            intent.putExtra(EXTRA_FEED_URL, feedUrl)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_articles)

        val appModule: AppModule = provideAppModule(application)
        initProgressBar()
        initRecycler()
        initViewModel(appModule.showArticlesModule())
        bindViewModel()

        showArticlesViewModel.fetchArticles(getStringExtraOrThrow(EXTRA_FEED_URL))
    }

    private fun bindViewModel() {
        showArticlesViewModel
            .bindViewState()
            .observe(this, Observer<ShowArticlesViewModel.ViewState> { viewState ->
                when (viewState) {
                    is ShowArticles -> showArticles(viewState.articles)
                    is ShowError -> showToast(viewState.message)
                    ShowProgress -> showProgressDialog()
                    HideProgress -> hideProgressDialog()
                }
            })
    }

    private fun initViewModel(module: ShowArticlesModule<ForSingleK>) {
        showArticlesViewModel = ViewModelProvider(
            this,
            ShowArticlesViewModelFactory(
                provideAndroidStrings(this),
                module.getArticlesUseCase
            )
        ).get(ShowArticlesViewModel::class.java)
    }

    private fun initProgressBar() {
        progressDialog = ProgressDialog(this)
    }

    private fun initRecycler() {
        adapter = ArticleListAdapter(arrayListOf())
        recycler_article_list.layoutManager = LinearLayoutManager(this)
        recycler_article_list.adapter = adapter
    }

    private fun showProgressDialog() {
        progressDialog.setMessage(getString(R.string.loading_indicator_message))
        progressDialog.show()
    }

    private fun hideProgressDialog() = progressDialog.hide()

    private fun showArticles(articles: List<Article>) {
        adapter.setItems(articles)
    }

    private fun showToast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

    override fun onDestroy() {
        super.onDestroy()
        progressDialog.dismiss()
    }
}
