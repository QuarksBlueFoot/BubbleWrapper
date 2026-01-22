package xyz.bluefoot.bubblewrapper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import com.solana.mobilewalletadapter.clientlib.ActivityResultSender
import xyz.bluefoot.bubblewrapper.ui.theme.BubbleWrapperTheme

// CompositionLocal to provide ActivityResultSender to composables
val LocalActivityResultSender = staticCompositionLocalOf<ActivityResultSender?> { null }

class MainActivity : ComponentActivity() {
    // Create ActivityResultSender in onCreate - MUST be done before resume
    private lateinit var activityResultSender: ActivityResultSender
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize ActivityResultSender before setContent
        activityResultSender = ActivityResultSender(this)
        
        enableEdgeToEdge()
        setContent {
            BubbleWrapperTheme {
                // Provide ActivityResultSender to all composables
                CompositionLocalProvider(LocalActivityResultSender provides activityResultSender) {
                    Surface(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        BubbleWrapperApp()
                    }
                }
            }
        }
    }
}
