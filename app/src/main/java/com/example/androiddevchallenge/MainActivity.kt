/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.ArrowDropUp
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androiddevchallenge.ui.theme.MyTheme

class MainActivity : AppCompatActivity() {

    private val timerViewModel by viewModels<CountTimeViewModel>()

    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                TimerApp(timerViewModel)
            }
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun TimerApp(countTimeViewModel: CountTimeViewModel, modifier: Modifier = Modifier) {

    val secs = countTimeViewModel.seconds.observeAsState()
    val minutes = countTimeViewModel.minutes.observeAsState()
    val hours = countTimeViewModel.hours.observeAsState()
    val resumed = countTimeViewModel.isRunning.observeAsState()

    val progress = countTimeViewModel.progress.observeAsState(1f)
    val timeShow = countTimeViewModel.time.observeAsState(initial = "00:00:00")

    Surface(color = MaterialTheme.colors.background) {
        val typography = MaterialTheme.typography

        Image(
            bitmap = ImageBitmap.imageResource(R.drawable.background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(modifier = Modifier.padding()) {
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(start = 40.dp, end = 40.dp, top=10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Let's start the countdown!",
                    fontSize = 24.sp,
                    style = typography.h4,
                    color = Color.White,
                    fontStyle = FontStyle.Italic
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalArrangement = Arrangement.Center
            ) {
                Box(Modifier.padding(40.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        color = Color.Yellow,
                        modifier = Modifier.size(250.dp),
                        progress = progress.value,
                        strokeWidth = 12.dp
                    )
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        ReusableHeaderText(
                            text = timeShow.value,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 2.dp, end = 2.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = CountTimeViewModel.Companion.TimeUnit.HOUR.name,
                    fontSize = 20.sp,
                    color = Color.White,
                    style = typography.caption
                )
                Text(
                    text = CountTimeViewModel.Companion.TimeUnit.MIN.name,
                    fontSize = 20.sp,
                    color = Color.White,
                    style = typography.caption
                )
                Text(
                    text = CountTimeViewModel.Companion.TimeUnit.SEC.name,
                    fontSize = 20.sp,
                    color = Color.White,
                    style = typography.caption
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(4.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TimerComponent(
                    value = hours.value,
                    timeUnit = CountTimeViewModel.Companion.TimeUnit.HOUR,
                    enabled = resumed.value != true
                ) {
                    countTimeViewModel.modifyTime(CountTimeViewModel.Companion.TimeUnit.HOUR, it)
                }

                Text(text = " : ", fontSize = 36.sp)

                TimerComponent(
                    value = minutes.value,
                    timeUnit = CountTimeViewModel.Companion.TimeUnit.MIN,
                    enabled = resumed.value != true
                ) {
                    countTimeViewModel.modifyTime(CountTimeViewModel.Companion.TimeUnit.MIN, it)
                }

                Text(text = " : ", fontSize = 36.sp)

                TimerComponent(
                    value = secs.value,
                    timeUnit = CountTimeViewModel.Companion.TimeUnit.SEC,
                    enabled = resumed.value != true
                ) {
                    countTimeViewModel.modifyTime(CountTimeViewModel.Companion.TimeUnit.SEC, it)
                }
            }
            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(start = 40.dp, end = 40.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FloatingActionButton(
                    onClick = {
                        if (!((secs.value ?: 0) == 0 && (minutes.value ?: 0) == 0 && (hours.value ?: 0) == 0)) {
                            if (resumed.value != true) {
                                countTimeViewModel.startCountDown()
                            } else {
                                countTimeViewModel.cancelTimer()
                            }
                        } else null
                    },
                    modifier = modifier
                        .padding(16.dp)
                        .height(48.dp)
                        .widthIn(min = 48.dp),
                    backgroundColor = Color.Yellow,
                    contentColor = MaterialTheme.colors.onPrimary
                ) {
                    AnimatingFabContent(
                        icon = {
                            if (resumed.value != true)
                                Icon(
                                    imageVector = Icons.Outlined.PlayArrow,
                                    contentDescription = null
                                ) else
                                Icon(
                                    imageVector = Icons.Outlined.Pause,
                                    contentDescription = null
                                )
                        },
                        text = {
                            if (resumed.value != true)
                                Text(
                                    color = Color.DarkGray,
                                    text = "Count Down!"
                                ) else
                                Text(
                                    color = Color.DarkGray,
                                    text = "Pause"
                                )
                        },
                        extended = true

                    )
                }
            }
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun TimerComponent(
    value: Int?,
    timeUnit: CountTimeViewModel.Companion.TimeUnit,
    enabled: Boolean,
    onClick: (CountTimeViewModel.Companion.TimeOperator) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val typography = MaterialTheme.typography

        Spacer(modifier = Modifier.height(8.dp))

        OperatorButton(
            timeOperator = CountTimeViewModel.Companion.TimeOperator.INCREASE,
            isEnabled = enabled,
            onClick = onClick
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = String.format("%02d", value ?: 0),
            fontSize = 32.sp,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        OperatorButton(
            timeOperator = CountTimeViewModel.Companion.TimeOperator.DECREASE,
            isEnabled = enabled,
            onClick = onClick
        )
    }
}

@ExperimentalAnimationApi
@Composable
fun OperatorButton(
    isEnabled: Boolean,
    timeOperator: CountTimeViewModel.Companion.TimeOperator,
    onClick: (CountTimeViewModel.Companion.TimeOperator) -> Unit
) {
    AnimatedVisibility(
        visible = isEnabled
    ) {
        Button(
            onClick = { onClick.invoke(timeOperator) },
            enabled = isEnabled,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Gray,
                //    backgroundColor = MaterialTheme.colors.background,
                disabledBackgroundColor = MaterialTheme.colors.background
            ),
            elevation = ButtonDefaults.elevation(defaultElevation = 0.dp)
        ) {

            when (timeOperator) {
                CountTimeViewModel.Companion.TimeOperator.INCREASE -> Icon(
                    Icons.Outlined.ArrowDropUp,
                    null,
                    Modifier.size(24.dp)
                )
                CountTimeViewModel.Companion.TimeOperator.DECREASE -> Icon(
                    Icons.Outlined.ArrowDropDown,
                    null,
                    Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun ReusableHeaderText(text: String, color: Color) {
    Text(text = text, fontSize= 42.sp, textAlign = TextAlign.Center,style = MaterialTheme.typography.h1, color = color)
}


