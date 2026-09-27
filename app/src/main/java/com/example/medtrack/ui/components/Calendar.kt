package com.example.medtrack.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.medtrack.ui.theme.MedTrackTheme
import kotlinx.coroutines.delay
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

private val CalendarHeaderTextSize = 22.sp
private val CalendarArrowSize = 24.dp
private val CalendarHeaderHeight = 56.dp
private val CalendarDateShape = RoundedCornerShape(10.dp)
private val CalendarFullShape = RoundedCornerShape(20.dp)
private val CalendarDateHeight = 56.dp
private const val CalendarAnimationDuration = 280


private fun startOfWeek(date: LocalDate): LocalDate {
    // atur awal minggu (dimulai dari senin)
    return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
}

@Composable
fun MedTrackCalendar(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {

    // Hari ini, mengikuti tanggal perangkat.
    val today by rememberCurrentDate()

    // anchordate: menentukan minggu yang ditampilkan
    var anchorDate by remember {
        mutableStateOf(selectedDate)
    }

    // cek apakah full calendar view dibuka
    var showMonthCalendar by remember {
        mutableStateOf(false)
    }

    // Cari hari Minggu pada minggu yang sedang aktif.
    val weekStart = remember(anchorDate) {
        startOfWeek(anchorDate)
    }


    val weekDates = remember(weekStart) {
        List(7) { index ->
            weekStart.plusDays(
                index.toLong()
            )
        }
    }
    // nama bulan mengikuti selected date
    val displayedMonth = remember(selectedDate) {
        YearMonth.from(selectedDate)
    }

    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        Surface (
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp), // padding luar layar
            shape = CalendarFullShape, // bentuk sudut melengkung
            tonalElevation = 2.dp,            // warna tonal khas Material 3
            shadowElevation = 10.dp,           // KAPASITAS DROP SHADOW DI SINI
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                CalendarHeader(
                    yearMonth = displayedMonth,

                    onPrevious = {
                        // mundur 1 minggu, selected date tidak berubah
                        anchorDate =
                            anchorDate.minusWeeks(1)
                    },

                    onNext = {
                        // maju 1 minggu
                        anchorDate =
                            anchorDate.plusWeeks(1)
                    },

                    onMonthClick = {
                        showMonthCalendar = true
                    }
                )

                // weekly calendar row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp, bottom = 12.dp, top = 8.dp),

                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {

                    weekDates.forEach { date ->

                        WeekDateItem(
                            date = date,

                            // hari ini masih muncul, meski pilih tanggal lain
                            isToday = date == today,

                            // tanggal di pilih untuk melihat informasi (jadwal)
                            isSelected = date == selectedDate,

                            modifier = Modifier.weight(1f),

                            // saat user pilih tanggal, header month ikut berubah menyesuaikan
                            onClick = {
                                onDateSelected(date)
                            }
                        )
                    }
                }
            }
        }


        // full calendar pop up view
        if (showMonthCalendar) {

            MonthCalendarDialog(
                initialMonth = displayedMonth,
                today = today,
                selectedDate = selectedDate,

                onDateSelected = { date ->
                    // user pilih tanggal di full calendar
                    onDateSelected(date)
                    // weekly ikut berubah
                    anchorDate = date
                },

                onDismiss = {
                    showMonthCalendar = false
                }
            )
        }
    }
}

@Composable
private fun CalendarHeader(
    yearMonth: YearMonth,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onMonthClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(CalendarHeaderHeight)
            .padding(horizontal = 22.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        IconButton(
            onClick = onPrevious
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Previous",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(CalendarArrowSize)
            )
        }

        Text(
            text = yearMonth.formatMonthYear(),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = CalendarHeaderTextSize,
            fontWeight = FontWeight.Medium,

            modifier = if (onMonthClick != null) {
                Modifier.clickable {
                    onMonthClick()
                }
            } else {
                Modifier
            }
        )

        IconButton(
            onClick = onNext
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Next",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(CalendarArrowSize)
            )
        }
    }
}

@Composable
private fun WeekDateItem(
    date: LocalDate,
    isToday: Boolean,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    val dayFormatter = remember {
        DateTimeFormatter.ofPattern(
            "EEE",
            Locale.ENGLISH
        )
    }

    // TODAY = Primary
    // NORMAL / SELECTED = Secondary
    val backgroundColor =
        if (isToday) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.secondary
        }

    val textColor =
        if (isToday) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onSecondary
        }

    // Hanya today yang melayang.
    val verticalOffset =
        if (isToday) {
            (-10).dp
        } else {
            0.dp
        }

    Box(
        modifier = modifier.height(CalendarDateHeight),
        contentAlignment = Alignment.BottomCenter
    ) {

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(CalendarDateHeight)
                .offset(y = verticalOffset)
                .clickable {
                    onClick()
                },

            shape = CalendarDateShape,
            color = backgroundColor,

            // selected date (not today)
            border =
                if (isSelected && !isToday) {
                    BorderStroke(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                } else {
                    null
                }
        ) {

            Box(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Text(
                        text = dayFormatter.format(date),
                        fontSize = 14.sp,
                        color = textColor,
                        fontWeight =
                            if (isToday) {
                                FontWeight.Medium
                            } else {
                                FontWeight.Normal
                            }
                    )

                    Text(
                        text = date.dayOfMonth.toString(),
                        fontSize = 16.sp,
                        color = textColor,
                        fontWeight =
                            if (isToday) {
                                FontWeight.SemiBold
                            } else {
                                FontWeight.Normal
                            }
                    )
                }

                // dot untuk menandakan today
                if (isToday) {

                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 5.dp)

                            // circle dot
                            .size(4.dp)
                            .background(
                                color = MaterialTheme.colorScheme.background,
                                shape = CircleShape
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun MonthCalendarDialog(
    initialMonth: YearMonth,
    today: LocalDate,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {

    var currentMonth by remember(initialMonth) {
        mutableStateOf(initialMonth)
    }

    var visible by remember {
        mutableStateOf(false)
    }


    // jalankan animasi
    LaunchedEffect(Unit) {
        visible = true
    }


    // tutup animasi calendar
    fun closeCalendar() {
        visible = false
    }

    // animasi selesai, tutup dialog
    LaunchedEffect(visible) {
        if (!visible) {
            delay(
                CalendarAnimationDuration.toLong().milliseconds
            )
            onDismiss()
        }
    }


    Dialog(
        onDismissRequest = {
            closeCalendar()
        },

        properties = DialogProperties(

            // Membuat Dialog bebas memakai
            // seluruh ukuran layar.
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {

        // untuk tampilan dialog (calendar full view)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.Black.copy(
                        alpha = 0.28f
                    )
                )
                .clickable(
                    indication = null,
                    interactionSource =
                        remember {
                            MutableInteractionSource()
                        }
                ) {
                    closeCalendar()
                },

            contentAlignment =
                Alignment.TopCenter
        ) {


            AnimatedVisibility(
                visible = visible,
                modifier = Modifier.padding(top = 56.dp),
                enter =
                    fadeIn(
                        tween(
                            CalendarAnimationDuration
                        )
                    ) +
                            scaleIn(
                                initialScale = 0.92f,
                                animationSpec =
                                    tween(
                                        CalendarAnimationDuration
                                    )
                            ),

                exit =
                    fadeOut(
                        tween(180)
                    ) +
                            scaleOut(
                                targetScale = 0.92f,
                                animationSpec =
                                    tween(220)
                            )
            ) {


                /*
                 * Box ini menghentikan klik supaya
                 * klik di calendar tidak dianggap
                 * klik outside.
                 */
                Box(
                    modifier = Modifier
                        .padding(
                            horizontal = 24.dp
                        )
                        .clickable(
                            indication = null,
                            interactionSource =
                                remember {
                                    MutableInteractionSource()
                                }
                        ) {
                            // Sengaja kosong.
                        }
                ) {


                    MonthCalendarCard(
                        yearMonth = currentMonth,
                        today = today,
                        selectedDate = selectedDate,

                        onPreviousMonth = {
                            currentMonth =
                                currentMonth.minusMonths(1)
                        },

                        onNextMonth = {
                            currentMonth =
                                currentMonth.plusMonths(1)
                        },

                        onDateSelected = { date ->

                            onDateSelected(date)

                            closeCalendar()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MonthCalendarCard(
    yearMonth: YearMonth,
    today: LocalDate,
    selectedDate: LocalDate,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(
                max = 520.dp
            )
            .shadow(
                elevation = 20.dp,
                shape =
                    RoundedCornerShape(26.dp),
                clip = false
            ),

        color =
            MaterialTheme.colorScheme.background,

        shape =
            RoundedCornerShape(26.dp),

        border = BorderStroke(
            width = 1.dp,
            color =
                MaterialTheme.colorScheme.primary
                    .copy(alpha = 0.45f)
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    bottom = 24.dp
                )
        ) {


            // weekly calendar header
            CalendarHeader(
                yearMonth = yearMonth,

                onPrevious =
                    onPreviousMonth,

                onNext =
                    onNextMonth,

                onMonthClick = null
            )


            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )


            MonthDayHeader()

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )


            MonthGrid(
                yearMonth =
                    yearMonth,

                today =
                    today,

                selectedDate =
                    selectedDate,

                onDateSelected =
                    onDateSelected
            )
        }
    }
}

@Composable
private fun MonthDayHeader() {

    val days = listOf(
        "Mon",
        "Tue",
        "Wed",
        "Thu",
        "Fri",
        "Sat",
        "Sun"
    )


    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        days.forEach { day ->
            Box(
                modifier =
                    Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = day,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun MonthGrid(
    yearMonth: YearMonth,
    today: LocalDate,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {

    val firstDate =
        yearMonth.atDay(1)


    /*
     * Hitung offset tanggal 1.
     *
     * Sunday = index 0
     * Monday = index 1
     * ...
     */
    val firstDayOffset = firstDate.dayOfWeek.value - 1


    /*
     * Otomatis:
     *
     * 28
     * 29
     * 30
     * 31
     */
    val numberOfDays =
        yearMonth.lengthOfMonth()


    val requiredCells =
        firstDayOffset +
                numberOfDays


    val rowCount =
        (requiredCells + 6) / 7


    Column(
        verticalArrangement =
            Arrangement.spacedBy(5.dp)
    ) {

        repeat(rowCount) { row ->

            Row(
                modifier =
                    Modifier.fillMaxWidth()
            ) {

                repeat(7) { column ->

                    val index =
                        row * 7 + column


                    val dayNumber =
                        index -
                                firstDayOffset +
                                1


                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1.12f),

                        contentAlignment =
                            Alignment.Center
                    ) {

                        if (
                            dayNumber in
                            1..numberOfDays
                        ) {

                            val date =
                                yearMonth.atDay(
                                    dayNumber
                                )


                            MonthDateItem(
                                date = date,

                                isToday =
                                    date == today,

                                isSelected =
                                    date ==
                                            selectedDate,

                                onClick = {
                                    onDateSelected(
                                        date
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthDateItem(
    date: LocalDate,
    isToday: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier.size(42.dp),
        contentAlignment = Alignment.Center
    ) {

        Box(
            modifier = Modifier
                .size(38.dp)

                // Today = Primary circle.
                .background(
                    color =
                        if (isToday) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            Color.Transparent
                        },
                    shape = CircleShape
                )

                // Selected non-today = tertiary outline.
                .then(
                    if (isSelected && !isToday) {
                        Modifier.border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.tertiary,
                            shape = CircleShape
                        )
                    } else {
                        Modifier
                    }
                )

                .clickable {
                    onClick()
                },

            contentAlignment = Alignment.Center
        ) {

            Text(
                text = date.dayOfMonth.toString(),

                color =
                    if (isToday) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSecondary
                    },

                fontSize = 14.sp,

                fontWeight =
                    if (isToday) {
                        FontWeight.SemiBold
                    } else {
                        FontWeight.Normal
                    }
            )
        }
    }
}

private fun YearMonth.formatMonthYear(): String {

    val formatter =
        DateTimeFormatter.ofPattern(
            "MMMM yyyy",
            Locale.ENGLISH
        )


    return format(formatter)
}

@Composable
private fun rememberCurrentDate():
        androidx.compose.runtime.State<LocalDate> {

    return produceState(
        initialValue =
            LocalDate.now()
    ) {

        while (true) {

            val currentDate =
                LocalDate.now()

            /*
             * Hanya update jika tanggal
             * benar-benar berubah.
             */
            if (
                value != currentDate
            ) {
                value = currentDate
            }


            /*
             * Cek tiap 30 detik.
             *
             * Ini ringan karena yang diperiksa
             * hanya LocalDate.
             */
            delay(30_000.milliseconds)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MedTrackCalendarPreview() {
    // Simulasi state tanggal yang dipilih
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    MedTrackTheme {
        Surface(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            MedTrackCalendar(
                selectedDate = selectedDate,
                onDateSelected = { newDate ->
                    selectedDate = newDate
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CalendarHeaderPreview() {
    MedTrackTheme {
        Surface(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            CalendarHeader(
                yearMonth = YearMonth.of(2026, 9), // Contoh bulan September 2026
                onPrevious = { /* Aksi ketika tombol kiri diklik */ },
                onNext = { /* Aksi ketika tombol kanan diklik */ },
                onMonthClick = { /* Aksi ketika teks bulan diklik untuk buka popup */ }
            )
        }
    }
}