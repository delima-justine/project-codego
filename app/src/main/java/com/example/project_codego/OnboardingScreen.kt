package com.example.project_codego

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project_codego.ui.theme.ProjectcodegoTheme
import kotlinx.coroutines.launch

// Onboarding page data
data class OnboardingPage(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val color: Color
)

val onboardingPages = listOf(
    OnboardingPage(
        icon = Icons.Default.Person, // Replaced Group with Person
        title = "Join RescQ PH Community",
        description = "Connect with fellow Filipinos to share survival stories, disaster tips, and emergency preparedness advice.",
        color = Color(0xFF3A86FF)
    ),
    OnboardingPage(
        icon = Icons.Default.Info, // Replaced Security with Info
        title = "Share Survival Stories",
        description = "Share your experiences during bagyo, lindol, and baha to help others prepare and stay safe.",
        color = Color(0xFFFF6F00)
    ),
    OnboardingPage(
        icon = Icons.Default.Phone,
        title = "Emergency Hotlines",
        description = "Access 36+ Philippine emergency contacts from NDRRMC, PAGASA, Red Cross PH and more - even offline!",
        color = Color(0xFFD81B60)
    )
)

val DarkWavyBackground = Color(0xFF1A1A2E)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onGetStarted: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkWavyBackground)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { pageIndex ->
            OnboardingPageLayout(page = onboardingPages[pageIndex])
        }

        // Controls (Indicators, Skip, Next)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PageIndicator(
                    pageCount = onboardingPages.size,
                    currentPage = pagerState.currentPage
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Skip Button
                TextButton(onClick = onGetStarted) {
                    Text("Skip", color = Color.White.copy(alpha = 0.7f))
                }

                // Next / Get Started Button
                val isLastPage = pagerState.currentPage == onboardingPages.size - 1
                Button(
                    onClick = {
                        if (isLastPage) {
                            onGetStarted()
                        } else {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(
                                    pagerState.currentPage + 1,
                                    animationSpec = tween(500)
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = onboardingPages[pagerState.currentPage].color.copy(alpha = 0.8f) // Fixed copy call
                    )
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(if (isLastPage) "Get Started" else "Next")
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                    }
                }
            }
        }
    }
}

@Composable
fun OnboardingPageLayout(page: OnboardingPage) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.7f)
                .background(page.color, RoundedCornerShape(32.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // "PH" indicator
            Box(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(8.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.9f)),
                contentAlignment = Alignment.Center
            ) {
                Text("PH", fontWeight = FontWeight.Bold, color = page.color)
            }

            Spacer(modifier = Modifier.weight(0.5f))

            // Main Icon
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = page.icon,
                    contentDescription = page.title,
                    modifier = Modifier.size(70.dp),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Text Content
            Text(
                text = page.title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = page.description,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = Color.White.copy(alpha = 0.85f),
                lineHeight = 24.sp
            )
            
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun PageIndicator(pageCount: Int, currentPage: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until pageCount) {
            val isSelected = i == currentPage
            Box(
                modifier = Modifier
                    .size(if (isSelected) 12.dp else 8.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) Color.White else Color.White.copy(alpha = 0.4f))
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingScreenPreview() {
    ProjectcodegoTheme {
        OnboardingScreen(onGetStarted = {})
    }
}
