package com.ajou.helpt.auth.view.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.children
import androidx.fragment.app.DialogFragment
import com.ajou.helpt.R
import com.ajou.helpt.databinding.CalendarDayBinding
import com.ajou.helpt.databinding.DialogCalendarBinding
import com.ajou.helpt.displayText
import com.kizitonwose.calendar.core.*
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.ViewContainer
import java.time.LocalDate
import java.time.YearMonth

class CalendarDialog() : DialogFragment() {
    private val selectedDates = mutableSetOf<LocalDate>()
    private val today = LocalDate.now()
    // TODO 값 하나만 넣는 쪽으로 하기(하나만 선택될 수 있도록
    // 생년월일 선택은 NumberPicker로 하는 게 나을 듯
    // 이건 마이페이지, 운동기록 조회에서 하는 걸로 하고 today를 selectedDate로 설정하고 그걸 바꾸는 걸로 하기기    private val today = LocalDate.now()

    private lateinit var binding: DialogCalendarBinding
    private var mContext: Context? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL,R.style.CustomDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogCalendarBinding.inflate(inflater, container, false)
        isCancelable = true
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val daysOfWeek = daysOfWeek()
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(100)  // Adjust as needed
        val endMonth = currentMonth.plusMonths(100)  // Adjust as needed

        class DayViewContainer(view: View) : ViewContainer(view) {
            // Will be set when this container is bound. See the dayBinder.
            lateinit var day: CalendarDay
            val textView = CalendarDayBinding.bind(view).calendarDayText

            init {
                view.setOnClickListener {
                    if (day.position == DayPosition.MonthDate) {
                        dateClicked(date = day.date)
                    }
                }
            }
        }
        binding.calendar.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.day = data
                bindDate(data.date, container.textView, data.position == DayPosition.MonthDate)

            }

            override fun create(view: View): DayViewContainer = DayViewContainer(view)
        }

        binding.calendar.monthScrollListener = { updateTitle() }
        binding.calendar.setup(startMonth, endMonth, daysOfWeek.first())
        binding.calendar.scrollToMonth(currentMonth)
        binding.calendar.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun bind(container: MonthViewContainer, data: CalendarMonth) {
                if (container.titlesContainer.tag == null) {
                    container.titlesContainer.tag = data.yearMonth
                    container.titlesContainer.children.map { it as TextView }
                        .forEachIndexed { index, textView ->
                            textView.text = daysOfWeek[index].displayText()
                            textView.setTextColor(resources.getColor(R.color.black))
                        }
                }
            }

            override fun create(view: View): MonthViewContainer = MonthViewContainer(view)

        }
    }

    class MonthViewContainer(view: View) : ViewContainer(view) {
        val titlesContainer = view as ViewGroup
    }

    private fun updateTitle() {
        val month = binding.calendar.findFirstVisibleMonth()?.yearMonth ?: return
        binding.year.text = month.year.toString()
        binding.month.text = month.month.displayText(short = false)
    }

    private fun dateClicked(date: LocalDate) {
        if (selectedDates.contains(date)) {
            selectedDates.remove(date)
        } else {
            selectedDates.add(date)
        }
        // Refresh both calendar views..
        binding.calendar.notifyDateChanged(date)
    }

    private fun bindDate(date: LocalDate, textView: TextView, isSelectable: Boolean) {
        textView.text = date.dayOfMonth.toString()
        if (isSelectable) {
            when {
                selectedDates.contains(date) -> {
                    textView.setTextColor(resources.getColor(R.color.white))
                    textView.setBackgroundResource(R.drawable.calendar_selected_bg)
                }
                today == date -> {
                    textView.setTextColor(resources.getColor(R.color.black))
//                    textView.setBackgroundResource(R.drawable.example_1_today_bg)
                }
                else -> {
                    textView.setTextColor(resources.getColor(R.color.black))
                    textView.background = null
                }
            }
        } else {
            textView.setTextColor(resources.getColor(R.color.gray_off))
            textView.background = null
        }
    }
}