package com.example.parsecmdcoroutines

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.parsecmdcoroutines.databinding.FragmentMainBinding
import java.util.*


class MainFragment: Fragment() {

    val viewModel: MainFragmentViewModel by viewModels()

    lateinit var viewBinding: FragmentMainBinding
    lateinit var spinnerMinute: Spinner
    lateinit var spinnerHour: Spinner
    var selectedMinute: Int? = null
    var selectedHour: Int? = null
    var dayOfOccurence: TodayOrTomorrow? = null
    var remainingSeconds: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentMainBinding.inflate(inflater)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        spinnerMinute = viewBinding.spinnerMinutes
        spinnerHour = viewBinding.spinnerHour

        setupSpinnerList(spinnerMinute, R.array.stringArrayMinutes)
        setupSpinnerList(spinnerHour, R.array.stringArrayHours)
        setupSpinnerOnItemSelected(spinnerMinute, R.array.stringArrayMinutes, MinOrHour.MINUTE)
        setupSpinnerOnItemSelected(spinnerHour, R.array.stringArrayHours,  MinOrHour.HOUR)
    }

    enum class MinOrHour { MINUTE, HOUR }
    enum class TodayOrTomorrow { TODAY, TOMORROW }

    private fun setupSpinnerList(spinner: Spinner, stringArraySpinner: Int) {
        ArrayAdapter.createFromResource(
            viewBinding.root.context,
            stringArraySpinner,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
    }

    private fun setupSpinnerOnItemSelected(spinner: Spinner, stringArraySpinner: Int, minOrHour: MinOrHour) {
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val stringArray = view?.resources?.getStringArray(stringArraySpinner)
                if (stringArray?.get(position) != "…" && stringArray?.get(position) != "*"){
                    when(minOrHour){
                        MinOrHour.MINUTE -> selectedMinute = stringArray?.get(position)?.toInt()
                        MinOrHour.HOUR -> selectedHour = stringArray?.get(position)?.toInt()
                    }
                }
                // -1 is used to indicate every minute or every hour
                if (stringArray?.get(position) == "*") {
                    when(minOrHour){
                        MinOrHour.MINUTE -> selectedMinute = -1
                        MinOrHour.HOUR -> selectedHour = -1
                    }
                }
                getWhichDay()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
    }

    private fun getWhichDay() {

        // There should be Hour and Minute set
        if (selectedMinute != null && selectedHour != null){

            val calendarTodayNow = Calendar.getInstance()

            // Task executed once per day at specified minute and hour
            if (selectedMinute != -1 && selectedHour != -1) {
                viewBinding.tvInfoAboutPlannedTasks.text = "Task executed once per day at specified minute and hour"

                val calendarTodayAtSelectedHourAtSelectedMinute = Calendar.getInstance()
                calendarTodayAtSelectedHourAtSelectedMinute.set(Calendar.MINUTE, selectedMinute!!)
                calendarTodayAtSelectedHourAtSelectedMinute.set(Calendar.HOUR, selectedHour!!)
                if (calendarTodayNow.after(calendarTodayAtSelectedHourAtSelectedMinute)) {
                    dayOfOccurence = TodayOrTomorrow.TOMORROW
                } else dayOfOccurence = TodayOrTomorrow.TODAY

                viewBinding.tvNextExecutionTime.text = "dayOfOccurence: ${dayOfOccurence!!.name} Minute: $selectedMinute Hour: $selectedHour"
            }

            // Task executed 60 times per day during specified hour
            if (selectedMinute == -1 && selectedHour != -1) {
                viewBinding.tvInfoAboutPlannedTasks.text = "Task executed 60 times per day during specified hour"

                val calendarTodayDuringSelectedHour = Calendar.getInstance()

                calendarTodayDuringSelectedHour.set(Calendar.HOUR, selectedHour!! +1)
                if (calendarTodayNow.after(calendarTodayDuringSelectedHour)) {
                    dayOfOccurence = TodayOrTomorrow.TOMORROW
                    viewBinding.tvNextExecutionTime.text = "dayOfOccurence: ${dayOfOccurence!!.name} Minute: 00min selectedHour: $selectedHour"

                } else {
                    dayOfOccurence = TodayOrTomorrow.TODAY
                    viewBinding.tvNextExecutionTime.text = "dayOfOccurence: ${dayOfOccurence!!.name} Minute: NEXT MINUTE selectedHour: $selectedHour"

                }
            }

            // Task executed 24 times per day each hour at the specified minute
            if (selectedMinute != -1 && selectedHour == -1) {
                viewBinding.tvInfoAboutPlannedTasks.text = "Task executed 24 times per day each hour at the specified minute"

                val calendarTodayAnyHourAtSelectedMinute = Calendar.getInstance()
                val currentHour = calendarTodayNow.get(Calendar.HOUR)

                calendarTodayAnyHourAtSelectedMinute.set(
                    Calendar.HOUR,
                    currentHour
                )
                calendarTodayAnyHourAtSelectedMinute.set(Calendar.MINUTE, selectedMinute!! +60)
                if (calendarTodayNow.after(calendarTodayAnyHourAtSelectedMinute)) {
                    dayOfOccurence = TodayOrTomorrow.TOMORROW
                    viewBinding.tvNextExecutionTime.text = "dayOfOccurence: ${dayOfOccurence!!.name} Minute: $selectedMinute Hour: 00h"

                } else {
                    dayOfOccurence = TodayOrTomorrow.TODAY
                    viewBinding.tvNextExecutionTime.text = "dayOfOccurence: ${dayOfOccurence!!.name} Minute: $selectedMinute CURRENT HOUR: $currentHour"
                }

            }

            // Task executed 24 hours X 60 minutes  per day, every minute of every hour per day
            if (selectedMinute == -1 && selectedHour == -1) {
                viewBinding.tvInfoAboutPlannedTasks.text = "Task executed 24 hours X 60 minutes  per day, every minute of every hour per day"

                val currentMinute = calendarTodayNow.get(Calendar.MINUTE)
                val currentHour = calendarTodayNow.get(Calendar.HOUR)
                val calendarTodayEveryMinuteOfEveryHour = Calendar.getInstance()

                calendarTodayEveryMinuteOfEveryHour.set(
                    Calendar.HOUR,
                    currentHour
                )
                calendarTodayEveryMinuteOfEveryHour.set(
                    Calendar.MINUTE,
                    currentMinute
                )
                calendarTodayEveryMinuteOfEveryHour.set(
                    Calendar.SECOND,
                    0
                )

                remainingSeconds = 60 - calendarTodayNow.get(Calendar.SECOND)

                if (currentHour != 23 && currentMinute != 59){
                    dayOfOccurence = TodayOrTomorrow.TODAY
                    viewBinding.tvNextExecutionTime.text = "dayOfOccurence: ${dayOfOccurence!!.name} Minute: $currentMinute CURENT HOUR: $currentHour \nremainingSeconds >>> $remainingSeconds <<<"
                } else {
                    dayOfOccurence = TodayOrTomorrow.TOMORROW
                    viewBinding.tvNextExecutionTime.text = "dayOfOccurence: ${dayOfOccurence!!.name} Minute: 00min Hour: 00h"
                }

            }

        }

    }

}