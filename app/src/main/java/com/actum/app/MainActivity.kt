package com.actum.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.actum.app.navigation.AppNavigation
import com.actum.app.ui.theme.ActumTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ActumTheme {
                AppNavigation()
            }
        }
    }
}