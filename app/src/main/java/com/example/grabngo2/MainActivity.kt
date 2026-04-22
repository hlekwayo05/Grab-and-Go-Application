package com.example.grabngo2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.grabngo2.ui.navigation.NavGraph
import com.example.grabngo2.ui.theme.Grabngo2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Grabngo2Theme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}
