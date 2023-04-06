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
    lateinit var placeholderNothingWasFound: LinearLayout
    lateinit var placeholderCommunicationsProblem: LinearLayout
    lateinit var buttonReturn: Button
    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView


    lateinit var buttonClear: Button
    lateinit var searchHistory: SearchHistory
    lateinit var historyList: LinearLayout
    var historyTracks  = ArrayList<Track>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val trackRecyclerView = findViewById<RecyclerView>(R.id.trackRecyclerView)
        trackRecyclerView.layoutManager = LinearLayoutManager(this)


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


        // Если поисковый запрос пустой, то кнопка не отображается
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrBlank()) {
                    clearButton.visibility = View.INVISIBLE
                } else {
                    clearButton.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })


        //нажатие кнопки "ввод" для поиска
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
            searchHistory.clearHistory()
            historyTracks = searchHistory.tracksHistoryFromJson() as ArrayList<Track>
            tracksHistoryAdapter.updateTracks(historyTracks)
            historyList.visibility = View.INVISIBLE
        }

        // Наблюдатель за нажатием треков
        trackAdapter.itemClickListener = { position, track ->
            searchHistory.addTrack(track, position)
        }


        // Нажатие на поле ввода
        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT)
                historyList.visibility = View.GONE
            }
        }

        searchEditText.setOnClickListener { // скрыть блок с историей при нажатии на ввод
            historyList.visibility = View.GONE
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
