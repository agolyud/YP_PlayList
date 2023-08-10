package com.example.yp_playlist.presentation.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModelProvider
import com.example.yp_playlist.R
import com.example.yp_playlist.domain.Track
import com.example.yp_playlist.presentation.media.MediaActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

const val HISTORY_TRACKS_SHARED_PREF = "history_tracks_shared_pref"
const val TRACK_ID = "track_position"

class SearchActivity : AppCompatActivity() {

    private var trackAdapter = TrackAdapter()
    private var tracksHistoryAdapter = TrackAdapter()
    private lateinit var placeholderNothingWasFound: LinearLayout
    private lateinit var placeholderCommunicationsProblem: LinearLayout
    private lateinit var buttonReturn: Button
    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var buttonClear: Button

    private lateinit var historyList: LinearLayout
    var historyTracks = ArrayList<Track>()
    private lateinit var searchResultsList: RecyclerView
    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { performSearch() }
    private var isPlayerOpening = false



    private val viewModel by viewModel<SearchViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)


        viewModel.searchState.observe(this) {
            trackAdapter.updateTracks(it)
        }

       viewModel.fragmentState.observe(this) {
           when (it) {

               SearchViewModel.SearchState.SUCCESS -> {
                   searchResultsList.isVisible = true
                   placeholderCommunicationsProblem.isVisible = false
               }
               SearchViewModel.SearchState.EMPTY -> {
                   searchResultsList.isVisible = false
                   placeholderNothingWasFound.isVisible = true
               }
               SearchViewModel.SearchState.LOADING -> {
                   searchResultsList.isVisible = false
                   placeholderNothingWasFound.isVisible = false
               }

               else -> {
                   searchResultsList.isVisible = false
                   placeholderCommunicationsProblem.isVisible = true
               }
           }
       }



        val trackRecyclerView = findViewById<RecyclerView>(R.id.trackRecyclerView)
        trackRecyclerView.layoutManager = LinearLayoutManager(this)
        searchResultsList = findViewById(R.id.trackRecyclerView)


        val historyRecyclerView = findViewById<RecyclerView>(R.id.recyclerViewHistory)
        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyRecyclerView.adapter = tracksHistoryAdapter


        val homeButton = findViewById<Button>(R.id.settings_toolbar)
        homeButton.setOnClickListener {
            finish()
        }


        searchEditText = findViewById(R.id.SearchForm)
        clearButton = findViewById(R.id.clear)
        trackRecyclerView.adapter = trackAdapter
        placeholderNothingWasFound = findViewById(R.id.placeholderNothingWasFound)
        placeholderCommunicationsProblem = findViewById(R.id.placeholderCommunicationsProblem)
        buttonReturn = findViewById(R.id.button_return)
        historyList = findViewById(R.id.history_list)
        buttonClear = findViewById(R.id.clearHistoryButton)



        //Объект класса для работы с историей поиске
        historyTracks = viewModel.tracksHistoryFromJson() as ArrayList<Track>

        if (historyTracks.isNotEmpty()) {
            historyList.visibility = View.VISIBLE
        }


        // Если поисковый запрос пустой, то кнопка не отображается и показывается история
        val shouldShowHistory = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                val searchText = s?.toString() ?: ""
                if (searchText.isEmpty()) {
                    trackAdapter.updateTracks(emptyList())
                    placeholderNothingWasFound.visibility = View.INVISIBLE
                    searchResultsList.visibility = View.GONE
                    historyList.visibility =
                        if (searchEditText.hasFocus() && s?.isEmpty() == true && historyTracks.isNotEmpty()) View.VISIBLE else View.GONE
                    tracksHistoryAdapter.updateTracks(historyTracks)
                } else {
                    placeholderNothingWasFound.visibility = View.GONE
                    searchResultsList.visibility = View.VISIBLE
                    historyList.visibility = View.GONE

                    // Отменить предыдущий запланированный поиск и запланировать новый
                    handler.removeCallbacks(searchRunnable)
                    handler.postDelayed(searchRunnable, 2000) // Запустить поиск через 2 секунды
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }


        //нажатие кнопки "ввод" для поиска
        searchEditText.addTextChangedListener(shouldShowHistory)
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // Отменить предыдущий запланированный поиск, если есть
                handler.removeCallbacks(searchRunnable)

                val searchText = searchEditText.text.toString()
                searchTracks(searchText)
                true
            } else {
                false
            }
        }


        //Очистка истории поиска
        buttonClear.setOnClickListener {
            historyTracks.clear()
       //     searchPresenter.clearHistory()
            viewModel.clearHistory()

            historyList.visibility = View.GONE
        }


        // Наблюдатель за нажатием треков в поиске
        trackAdapter.itemClickListener = { position, track ->
            if (!isPlayerOpening) {
                isPlayerOpening = true // Установить флаг блокировки перед открытием плеера


                viewModel.addTrack(track, position)

                val searchIntent = Intent(this, MediaActivity::class.java).apply {
                    putExtra(TRACK_ID, track.trackId)
                }
                startActivity(searchIntent)
            }
        }

        // Наблюдатель за нажатием треков в истории
        tracksHistoryAdapter.itemClickListener = { position, track ->
            if (!isPlayerOpening) {
                isPlayerOpening = true
                val searchIntent = Intent(this, MediaActivity::class.java).apply {
                    putExtra(TRACK_ID, track.trackId)
                }
                startActivity(searchIntent)
            }
        }

        //Повторить предыдущий запрос после нажатия на кнопку "Обновить"
        buttonReturn.setOnClickListener() {
            placeholderCommunicationsProblem.visibility = View.INVISIBLE
            val searchText = searchEditText.text.toString()
            searchTracks(searchText)
        }

        // Нажатие на кнопку «Очистить поисковый запрос»
        clearButton.setOnClickListener {
            searchEditText.text.clear()
            trackAdapter.updateTracks(emptyList())
            clearButton.visibility = View.INVISIBLE
            placeholderNothingWasFound.visibility = View.INVISIBLE
            placeholderCommunicationsProblem.visibility = View.INVISIBLE

            //Скрыть клавиатуру
            hideKeyboard()

            //Показать историю поисков
            historyTracks = viewModel.tracksHistoryFromJson() as ArrayList<Track>

            tracksHistoryAdapter.updateTracks(historyTracks)
            historyList.isVisible = historyTracks.isNotEmpty()
        }
    }



    private fun performSearch() {
        val searchText = searchEditText.text.toString()
        searchTracks(searchText)
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun searchTracks(searchText: String) {
        viewModel.searchTrack(searchText)

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val searchText = searchEditText.text.toString()
        outState.putString("searchText", searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val searchText = savedInstanceState.getString("searchText")
        searchEditText.setText(searchText)
    }

    override fun onResume() {
        super.onResume()
        historyTracks = viewModel.tracksHistoryFromJson() as ArrayList<Track>

        tracksHistoryAdapter.updateTracks(historyTracks)

        isPlayerOpening = false // Сбросить флаг блокировки при возобновлении активности
    }


}