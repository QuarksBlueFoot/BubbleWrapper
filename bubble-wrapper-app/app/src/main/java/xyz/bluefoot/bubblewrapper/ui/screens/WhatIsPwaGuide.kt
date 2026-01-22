package xyz.bluefoot.bubblewrapper.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.bluefoot.bubblewrapper.ui.theme.*

@Composable
fun WhatIsPwaGuide(onBack: () -> Unit) {
    val steps = listOf(
        GuideStep(
            number = 1,
            title = "Definition",
            description = "A Progressive Web App (PWA) is a type of application software delivered through the web, built using common web technologies including HTML, CSS, JavaScript, and WebAssembly.",
            tips = listOf(
                "Works on any platform that uses a standards-compliant browser",
                "Functionality includes working offline, push notifications, and device hardware access",
                "Enables creating user experiences similar to native applications on mobile and desktop devices"
            )
        ),
        GuideStep(
            number = 2,
            title = "Why PWAs on Solana Mobile?",
            description = "Solana Mobile dApp Store supports PWAs (via Trusted Web Activity) as first-class citizens.",
            tips = listOf(
                "Single codebase for Web, Android, and iOS",
                "Instant updates without app store review for web content",
                "Full access to Solana Mobile Stack (SMS) features",
                "Lower barrier to entry for developers"
            )
        ),
        GuideStep(
            number = 3,
            title = "Key Characteristics",
            description = "PWAs are designed to be:",
            tips = listOf(
                "Progressive: Work for every user, regardless of browser choice",
                "Responsive: Fit any form factor: desktop, mobile, tablet",
                "Connectivity independent: Service workers allow work offline or on low-quality networks",
                "App-like: Use the app-shell model to provide app-style navigation and interactions",
                "Fresh: Always up-to-date thanks to the service worker update process",
                "Safe: Served via HTTPS to prevent snooping and ensure content hasn't been tampered with",
                "Discoverable: Are essentially websites so are identifiable as 'applications' thanks to W3C manifests",
                "Linkable: Easily shareable via a URL and do not require complex installation"
            )
        )
    )

    GuideContent(
        title = "What is a PWA?",
        subtitle = "Understanding Progressive Web Apps",
        steps = steps,
        onBack = onBack
    )
}
