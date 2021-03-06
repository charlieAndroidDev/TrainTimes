package com.cniekirk.traintimes.ui.planner

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cniekirk.traintimes.R
import com.cniekirk.traintimes.base.withFactory
import com.cniekirk.traintimes.data.local.model.CRS
import com.cniekirk.traintimes.databinding.FragmentPlannerStationSearchBinding
import com.cniekirk.traintimes.ui.adapter.StationListAdapter
import com.cniekirk.traintimes.ui.viewmodel.JourneyPlannerViewModel
import com.cniekirk.traintimes.ui.viewmodel.JourneyPlannerViewModelFactory
import com.cniekirk.traintimes.utils.extensions.hideKeyboard
import com.cniekirk.traintimes.utils.extensions.onFocusChange
import com.cniekirk.traintimes.utils.viewBinding
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class PlannerStationSearchFragment: Fragment(R.layout.fragment_planner_station_search), StationListAdapter.OnStationItemSelected {

    @Inject
    lateinit var viewModelFactory: JourneyPlannerViewModelFactory

    private val binding by viewBinding(FragmentPlannerStationSearchBinding::bind)
    private val viewModel: JourneyPlannerViewModel by activityViewModels { withFactory(viewModelFactory, arguments) }

    private var isDeparture: Boolean = false

    override fun onResume() {
        super.onResume()
        viewModel.getCrsCodes()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.listenForNewSearch()
        viewModel.crsStationCodes.observe(viewLifecycleOwner, {
            it?.let {
                val adapter =
                    StationListAdapter(
                        it,
                        this
                    )
                binding.stationList.adapter = adapter
                adapter.notifyDataSetChanged()
            }
        })
        arguments?.let { isDeparture = it.getBoolean("isDeparture") }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val backward =  MaterialSharedAxis(MaterialSharedAxis.Z,  false)
        exitTransition = backward

        val forward =  MaterialSharedAxis(MaterialSharedAxis.Z,  true)
        enterTransition = forward
    }

    override fun onPause() {
        // To reset the search screen, horrible I know
        GlobalScope.launch { viewModel.queryChannel.send("") }
        super.onPause()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchDepStations.onFocusChange { hasFocus ->
            if (hasFocus)
                binding.searchDepStations.setHint(R.string.search_hint_focused)
            else
                binding.searchDepStations.setHint(R.string.station_search_hint)
        }

        binding.stationList.layoutManager = LinearLayoutManager(requireContext())
        binding.stationList.adapter =
            StationListAdapter(
                emptyList(),
                this
            )
        binding.btnBack.setOnClickListener { it.findNavController().popBackStack() }
        binding.searchDepStations.doAfterTextChanged {
            GlobalScope.launch {
                viewModel.queryChannel.send(it.toString())
            }
        }
    }

    override fun onStationItemClicked(crs: CRS) {
        hideKeyboard()
        if (isDeparture) {
            //viewModel.depStation.value = crs
            Timber.d("ACTUALLY SAVED")
            viewModel.saveDepStation(crs)
        } else {
            //viewModel.destStation.value = crs
            Timber.d("ACTUALLY SAVED dest")
            viewModel.saveDestStation(crs)
        }
        binding.root.findNavController().popBackStack()
    }

}