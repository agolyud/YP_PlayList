package com.example.yp_playlist

import android.content.Context
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val HISTORY_TRACKS_SHARED_PREF = "history_tracks_shared_pref"

class SearchActivity : AppCompatActivity() {

    companion object {
        const val itunesBaseUrl = "https://itunes.apple.com"
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val tracksApi = retrofit.create(iTunesApi::class.java)
    private var trackAdapter = TrackAdapter ()
    private var tracksHistoryAdapter = TrackAdapter()
    private lateinit var placeholderNothingWasFound: LinearLayout
    private lateinit var placeholderCommunicationsProblem: LinearLayout
    private lateinit var buttonReturn: Button
    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var buttonClear: Button
    private lateinit var searchHistory: SearchHistory
    private lateinit var historyList: LinearLayout
    var historyTracks  = ArrayList<Track>()
    private lateinit var searchResultsList: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

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
        searchHistory =
            SearchHistory(getSharedPreferences(HISTORY_TRACKS_SHARED_PREF, Context.MODE_PRIVATE))
        historyTracks = searchHistory.tracksHistoryFromJson() as ArrayList<Track>
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
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        }


        //нажатие кнопки "ввод" для поиска
        searchEditText.addTextChangedListener(shouldShowHistory)
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val searchText = searchEditText.text.toString()
                searchTracks(searchText)
                true
            }
            false
        }


        //Очистка истории поиска
        buttonClear.setOnClickListener {
            historyTracks.clear()
            searchHistory.clearHistory()
            historyList.visibility = View.GONE
        }


        // Наблюдатель за нажатием треков
        trackAdapter.itemClickListener = { position, track ->
            searchHistory.addTrack(track, position)
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

            //Показать историю поисков
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)

            //Показать историю поисков
            historyTracks = searchHistory.tracksHistoryFromJson() as ArrayList<Track>
            tracksHistoryAdapter.tracksHistory = historyTracks
            tracksHistoryAdapter.notifyDataSetChanged()
            if (historyTracks.isEmpty()) {
                historyList.visibility = View.INVISIBLE
            } else {
                historyList.visibility = View.VISIBLE
            }

        }
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun searchTracks(searchText: String) {

        tracksApi.searchTrack(searchText).enqueue(object : Callback<TrackResponse> {
            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                trackAdapter.updateTracks(emptyList())
                if (searchText.isNotEmpty() && response.isSuccessful) {
                    val tracks = response.body()?.results ?: listOf()
                    if (tracks.isNotEmpty()) {
                        trackAdapter.updateTracks(tracks)
                        placeholderNothingWasFound.visibility = View.INVISIBLE
                        placeholderCommunicationsProblem.visibility = View.INVISIBLE
                    } else {
                        placeholderNothingWasFound.visibility = View.VISIBLE
                        placeholderCommunicationsProblem.visibility = View.INVISIBLE
                    }
                } else {
                    placeholderNothingWasFound.visibility = View.VISIBLE
                    placeholderCommunicationsProblem.visibility = View.INVISIBLE
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                placeholderCommunicationsProblem.visibility = View.VISIBLE
                placeholderNothingWasFound.visibility = View.INVISIBLE
            }
        })
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
        historyTracks = searchHistory.tracksHistoryFromJson() as ArrayList<Track>
        tracksHistoryAdapter.tracksHistory = historyTracks
        tracksHistoryAdapter.notifyDataSetChanged()
    }
}
