package com.example.yp_playlist.presentation.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yp_playlist.R
import com.example.yp_playlist.databinding.FragmentSearchBinding
import com.example.yp_playlist.domain.Track
import com.example.yp_playlist.presentation.media.MediaFragment
import com.example.yp_playlist.util.Constants.DELAY_TIME_MILLIS
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


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
    private lateinit var searchResultsList: RecyclerView
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

        viewModel.setHistory()
        progressBar = searchBinding.progressBar
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


        bindClicks()
        observeData()
        bindListeners()
    }

    private fun observeData() {
        observeTracksHistory()
        observeClearButton()
        observeSearchState()
        observeFragmentState()
    }

    private fun observeTracksHistory() {
        viewModel.tracksHistory.observe(viewLifecycleOwner) { list ->
            tracksHistoryAdapter.updateTracks(list ?: listOf())
        }
    }

    private fun observeClearButton() {
        viewModel.clearButtonVisibility.observe(viewLifecycleOwner) {
            clearButton.visibility = it
        }
    }

    private fun observeSearchState() {
        viewModel.searchState.observe(viewLifecycleOwner) { list ->
            list?.let {
                trackAdapter.updateTracks(it)
            }
        }
    }

    private fun observeFragmentState() {
        viewModel.fragmentState.observe(viewLifecycleOwner) {
            when (it) {
                SearchViewModel.FragmentState.SUCCESS -> {
                    searchResultsList.isVisible = true
                    placeholderCommunicationsProblem.isVisible = false
                    progressBar.isVisible = false
                    clearButton.isEnabled = true
                }

                SearchViewModel.FragmentState.HISTORY -> {
                    searchResultsList.isVisible = false
                    placeholderCommunicationsProblem.isVisible = false
                    historyList.isVisible = true
                    clearButton.isEnabled = false
                }

                SearchViewModel.FragmentState.HISTORY_EMPTY -> {
                    searchResultsList.isVisible = false
                    placeholderCommunicationsProblem.isVisible = false
                    historyList.isVisible = false
                    clearButton.isEnabled = true
                }

                SearchViewModel.FragmentState.SEARCH -> {
                    searchResultsList.isVisible = true
                    placeholderCommunicationsProblem.isVisible = false
                    historyList.isVisible = false
                    clearButton.isEnabled = true
                }

                SearchViewModel.FragmentState.EMPTY -> {
                    searchResultsList.isVisible = false
                    placeholderNothingWasFound.isVisible = true
                    progressBar.isVisible = false
                    clearButton.isEnabled = true
                }

                SearchViewModel.FragmentState.LOADING -> {
                    progressBar.isVisible = true
                    searchResultsList.isVisible = false
                    placeholderNothingWasFound.isVisible = false
                    clearButton.isEnabled = false
                }

                else -> {
                    searchResultsList.isVisible = false
                    progressBar.isVisible = false
                    placeholderCommunicationsProblem.isVisible = true
                    clearButton.isEnabled = true
                }
            }
        }
    }

    private fun bindClicks() {
        //Очистка истории поиска
        buttonClear.setOnClickListener {
            viewModel.clearHistory()
            historyList.visibility = View.GONE
        }

        // Наблюдатель за нажатием треков в поиске
        trackAdapter.itemClickListener = { position, track ->
            lifecycleScope.launch {
                delay(DELAY_TIME_MILLIS)
                viewModel.addTrack(track, position)
                /* val searchIntent = Intent(requireContext(), MediaFragment::class.java)
                 searchIntent.putExtra(TRACK_ID, track.trackId)
                 startActivity(searchIntent)*/

                sendToPlayer(track)

            }

        }

        // Наблюдатель за нажатием треков в истории
        tracksHistoryAdapter.itemClickListener = { position, track ->
            lifecycleScope.launch {
                delay(DELAY_TIME_MILLIS)
                /*    val searchIntent = Intent(requireContext(), MediaFragment::class.java)
                    searchIntent.putExtra(TRACK_ID, track.trackId)
                    startActivity(searchIntent)*/
                sendToPlayer(track)
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
            hideKeyboard()

        }
    }

    private fun sendToPlayer(track: Track) {
        findNavController().navigate(
            R.id.searchFragment_to_playerFragment,
            MediaFragment.createArgs(track.trackId)
        )
    }


    private fun bindListeners() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onSearchTextChanged(s)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val searchText = searchEditText.text.toString()
                searchTracks(searchText)
                hideKeyboard()
                true
            } else {
                false
            }
        }
    }

    private fun hideKeyboard() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
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

}