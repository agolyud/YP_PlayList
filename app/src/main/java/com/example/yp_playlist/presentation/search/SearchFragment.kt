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
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.example.yp_playlist.R
import com.example.yp_playlist.databinding.FragmentSearchBinding
import com.example.yp_playlist.domain.Track
import com.example.yp_playlist.presentation.media.MediaActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

const val PLAYLIST_SHARED_PREFERENCES = "playlist_shared_preferences"
const val TRACK_ID = "track_position"

class SearchFragment : Fragment() {

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
    private lateinit var progressBar: ProgressBar



    private val viewModel by viewModel<SearchViewModel>()
    private lateinit var searchBinding: FragmentSearchBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        searchBinding = FragmentSearchBinding.inflate(inflater, container, false)
        return searchBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar = searchBinding.progressBar



        viewModel.searchState.observe(viewLifecycleOwner) {
            trackAdapter.updateTracks(it)
        }


        viewModel.fragmentState.observe(viewLifecycleOwner) {
            when (it) {

                SearchViewModel.SearchState.SUCCESS -> {
                    searchResultsList.isVisible = true
                    placeholderCommunicationsProblem.isVisible = false
                    progressBar.isVisible = false
                }

                SearchViewModel.SearchState.EMPTY -> {
                    searchResultsList.isVisible = false
                    placeholderNothingWasFound.isVisible = true
                    progressBar.isVisible = false
                }

                SearchViewModel.SearchState.LOADING -> {
                    progressBar.isVisible = true
                    searchResultsList.isVisible = false
                    placeholderNothingWasFound.isVisible = false
                }

                else -> {
                    searchResultsList.isVisible = false
                    progressBar.isVisible = false
                    placeholderCommunicationsProblem.isVisible = true
                }
            }
        }


        val trackRecyclerView = searchBinding.trackRecyclerView
        trackRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        searchResultsList = searchBinding.trackRecyclerView


        val historyRecyclerView = searchBinding.recyclerViewHistory
        historyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        historyRecyclerView.adapter = tracksHistoryAdapter



        searchEditText = searchBinding.SearchForm
        clearButton = searchBinding.clear
        trackRecyclerView.adapter = trackAdapter
        placeholderNothingWasFound = searchBinding.placeholderNothingWasFound
        placeholderCommunicationsProblem = searchBinding.placeholderCommunicationsProblem
        buttonReturn = searchBinding.buttonReturn
        historyList = searchBinding.historyList
        buttonClear = searchBinding.clearHistoryButton


        //Объект класса для работы с историей поиске
        historyTracks = viewModel.tracksHistoryFromJson() as ArrayList<Track>

        if (historyTracks.isNotEmpty()) {
            historyList.visibility = View.VISIBLE
        }


        // Объект класса для работы с историей поиска
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

                    // Отменить предыдущий запланированный поиск
                    handler.removeCallbacks(searchRunnable)

                    // Запланировать новый поиск только если поисковый запрос не пустой
                    if (searchText.isNotEmpty()) {
                        handler.postDelayed(searchRunnable, 2000) // Запустить поиск через 2 секунды
                    }
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
                hideKeyboard()

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

                val searchIntent = Intent(requireContext(), MediaActivity::class.java)
                searchIntent.putExtra(TRACK_ID, track.trackId)
                startActivity(searchIntent)
            }
        }

        // Наблюдатель за нажатием треков в истории
        tracksHistoryAdapter.itemClickListener = { position, track ->
            if (!isPlayerOpening) {
                isPlayerOpening = true
                val searchIntent = Intent(requireContext(), MediaActivity::class.java)
                searchIntent.putExtra(TRACK_ID, track.trackId)
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

            // Скрыть клавиатуру
            hideKeyboard()

            // Показать историю поисков
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
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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


    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        val searchText = searchEditText.text.toString()
        savedInstanceState.putString("searchText", searchText)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            val searchText = savedInstanceState.getString("searchText")
            searchEditText.setText(searchText)
        }
    }

    override fun onResume() {
        super.onResume()
        historyTracks = viewModel.tracksHistoryFromJson() as ArrayList<Track>

        tracksHistoryAdapter.updateTracks(historyTracks)

        isPlayerOpening = false // Сбросить флаг блокировки при возобновлении активности
    }


}