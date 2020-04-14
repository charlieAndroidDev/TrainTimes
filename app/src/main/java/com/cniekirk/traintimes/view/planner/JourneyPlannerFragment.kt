package com.cniekirk.traintimes.view.planner

import android.graphics.Typeface
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.fonts.FontFamily
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.datetime.dateTimePicker
import com.afollestad.materialdialogs.list.customListAdapter
import com.cniekirk.traintimes.R
import com.cniekirk.traintimes.base.withFactory
import com.cniekirk.traintimes.databinding.FragmentJourneyPlannerBinding
import com.cniekirk.traintimes.di.Injectable
import com.cniekirk.traintimes.domain.Failure
import com.cniekirk.traintimes.utils.viewBinding
import com.cniekirk.traintimes.view.adapter.PassengerAdapter
import com.cniekirk.traintimes.view.adapter.RailcardAdapter
import com.cniekirk.traintimes.view.viewmodel.HomeViewModel
import com.cniekirk.traintimes.view.viewmodel.HomeViewModelFactory
import com.cniekirk.traintimes.view.viewmodel.JourneyPlannerViewModel
import com.cniekirk.traintimes.view.viewmodel.JourneyPlannerViewModelFactory
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialFadeThrough
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.*
import javax.inject.Inject

class JourneyPlannerFragment: Fragment(R.layout.fragment_journey_planner), Injectable,
    RailcardAdapter.RailcardClickListener, PassengerAdapter.OnPassengerClickedListener {

    companion object {
        fun newInstance() = JourneyPlannerFragment()
    }

    @Inject
    lateinit var viewModelFactory: JourneyPlannerViewModelFactory

    private val binding by viewBinding(FragmentJourneyPlannerBinding::bind)
    private val viewModel: JourneyPlannerViewModel by activityViewModels { withFactory(viewModelFactory, arguments) }

    private val railcardCodes by lazy(LazyThreadSafetyMode.NONE) { resources.getStringArray(R.array.railcard_codes).toList() }
    private val passengerTypes by lazy(LazyThreadSafetyMode.NONE) { resources.getStringArray(R.array.passenger_types).toList() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough.create(requireContext()).apply { duration = 300 }
        exitTransition = MaterialFadeThrough.create(requireContext()).apply { duration = 300 }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_journey_planner, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Not really the proper way to use a chip but looks nice
        binding.datetimeChip.setOnClickListener {
            MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                dateTimePicker(requireFutureDateTime = true) { _, dateTime ->
                    viewModel.saveDatetime(dateTime)
                }
            }
        }

        binding.railcardChip.setOnClickListener {
            val dlg = MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT))
                .customView(R.layout.custom_railcard_selection)

            val railcardView = dlg.getCustomView()

            railcardView.findViewById<MaterialButton>(R.id.btn_railcard_ok).setOnClickListener {
                if (viewModel.railcards.size > 0) {
                    binding.railcardChip.text = String.format(getString(R.string.railcards_chip_text), viewModel.railcards.size)
                }
                dlg.dismiss()
            }

            railcardView.findViewById<MaterialButton>(R.id.btn_railcard_cancel).setOnClickListener {
                // Clear the LiveData field in the ViewModel
                viewModel.railcards.clear()
                binding.railcardChip.text = getString(R.string.default_railcard)
                dlg.dismiss()
            }

            val railcardList = railcardView.findViewById<RecyclerView>(R.id.railcard_list)
            val railcardDescriptions = resources.getStringArray(R.array.railcards).toList()
            val railcardAdapter = RailcardAdapter(railcardDescriptions, this)

            railcardList.layoutManager = LinearLayoutManager(requireContext())
            railcardList.adapter = railcardAdapter

            dlg.show()
        }

        binding.passengersChip.setOnClickListener {
            MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                val passengerAdapter = PassengerAdapter(passengerTypes, this@JourneyPlannerFragment)
                customListAdapter(passengerAdapter)
                title(R.string.passenger_select_title)
                positiveButton {
                    var numPassengers = 0
                    viewModel.adults.value?.let {  adults ->
                        viewModel.children.value?.let { children ->
                            numPassengers = adults + children
                        } ?: run { numPassengers = adults }
                    } ?: run { viewModel.children.value?.let { children -> { numPassengers = children } } }

                    if (numPassengers > 0) {
                        binding.passengersChip.text = String.format(getString(R.string.passenger_text), numPassengers)
                    }
                    dismiss()
                }
                negativeButton {
                    binding.passengersChip.text = getString(R.string.default_passengers)
                    dismiss()
                }
            }
        }

        binding.searchSelectDepStation.setOnClickListener {
            val extras = FragmentNavigatorExtras(binding.searchSelectDepStation
                    to getString(R.string.dep_search_transition))
            view.findNavController().navigate(R.id.plannerStationSearchFragment,
                bundleOf("isDeparture" to true), null, extras)
        }

        binding.searchSelectDestStation.setOnClickListener {
            val extras = FragmentNavigatorExtras(binding.searchSelectDestStation
                    to getString(R.string.dep_search_transition))
            view.findNavController().navigate(R.id.plannerStationSearchFragment,
                bundleOf("isDeparture" to false), null, extras)
        }

        binding.btnJourneyPlan.setOnClickListener {
            startLoadingAnim()
            viewModel.getJourneyPlan()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.failure.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Failure.NoDestinationFailure -> {
                    Snackbar.make(binding.root, R.string.error_no_destination, Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(resources.getColor(R.color.colorRed, null))
                        .show()
                }
                is Failure.MoreRailcardsThanPassengersError -> {
                    Snackbar.make(binding.root, R.string.error_more_railcards, Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(resources.getColor(R.color.colorRed, null))
                        .show()
                }
            }
        })

        viewModel.chipDateTime.observe(viewLifecycleOwner, Observer {
            binding.datetimeChip.text = it
        })

        viewModel.depStation.observe(viewLifecycleOwner, Observer {
            if (it == null) {
                binding.searchArrowDep.setImageDrawable(resources.getDrawable(R.drawable.ic_keyboard_arrow_right, null))
                binding.searchDepText.text = getString(R.string.departing_from)
                binding.searchArrowDep.setOnClickListener(null)
            } else {
                if (binding.searchDepText.text.toString().equals(getString(R.string.departing_from), false)) {
                    binding.searchArrowDep.setImageDrawable(resources.getDrawable(R.drawable.ic_clear, null))
                    binding.searchDepText.text = it.crs
                    binding.searchArrowDep.setOnClickListener {
                        viewModel.clearDepStation()
                    }
                }
            }
        })

        viewModel.destStation.observe(viewLifecycleOwner, Observer {
            if (it == null) {
                binding.searchArrowDest.setImageDrawable(resources.getDrawable(R.drawable.ic_keyboard_arrow_right, null))
                binding.searchDestText.text = getString(R.string.arriving_at)
                binding.searchArrowDest.setOnClickListener(null)
            } else {
                if (binding.searchDestText.text.toString().equals(getString(R.string.arriving_at), false)) {
                    binding.searchArrowDest.setImageDrawable(resources.getDrawable(R.drawable.ic_clear, null))
                    binding.searchDestText.text = it.crs
                    binding.searchArrowDest.setOnClickListener {
                        viewModel.clearDestStation()
                    }
                }
            }
        })

        viewModel.depStationText()?.let { savedDepStation ->
            viewModel.crsStationCodes.observe(viewLifecycleOwner, Observer { crsList ->
                val crs = crsList.find { crs -> crs.crs.equals(savedDepStation, true) }
                crs?.let {
                    viewModel.saveDepStation(crs)
                }
            })
            viewModel.getCrsCodes()
            binding.searchDepText.text = savedDepStation
            binding.searchArrowDep.setImageDrawable(resources.getDrawable(R.drawable.ic_clear, null))
            binding.searchArrowDep.setOnClickListener {
                viewModel.clearDepStation()
            }
        } ?: run {
            binding.searchArrowDep.setImageDrawable(resources.getDrawable(R.drawable.ic_keyboard_arrow_right, null))
            binding.searchDepText.text = getString(R.string.departing_from)
            binding.searchArrowDep.setOnClickListener(null)
        }

        val date: String? = viewModel.handle[getString(R.string.date_key)]
        date?.let { binding.datetimeChip.text = it }
    }

    /**
     * Start the loading animation, looping it with the listeners
     */
    private fun startLoadingAnim() {
        if (loading_indicator.drawable is AnimatedVectorDrawable) {
            val avd = binding.loadingIndicator.drawable as AnimatedVectorDrawable
            avd.start()
        }
    }

    // Update the railcard status in the ViewModel
    override fun onClick(position: Int) {
        viewModel.updateRailcards(railcardCodes[position], 1)
    }

    override fun onPassengerClick(position: Int) {
        viewModel.updatePassengers(passengerTypes[position])
    }

}