package com.yourname.infinityxsecured

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    // Define the aesthetic colors
    private val AmoledBlack = Color(0xFF000000)
    private val NeonCyan = Color(0xFF00FFFF)
    private val GlassSurface = Color(0x1AFFFFFF) // Semi-transparent white
    private val GlassBorder = Color(0x33FFFFFF)

    private fun restartLauncher() {
        try {
            // Using tsu to force-stop the launcher so it rebuilds the recents screen
            Runtime.getRuntime().exec(arrayOf("tsu", "-c", "am force-stop com.android.launcher3"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("WorldReadableFiles")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Prevents the app from appearing in screenshots and blanks it in the Recents menu
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        val prefs = getSharedPreferences("secured_apps_prefs", Context.MODE_PRIVATE)

        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    background = AmoledBlack,
                    surface = GlassSurface,
                    primary = NeonCyan
                )
            ) {
                AppListScreen(prefs, packageManager, ::restartLauncher)
            }
        }
    }

    @Composable
    fun AppListScreen(prefs: SharedPreferences, pm: PackageManager, onRestartClick: () -> Unit) {
        var installedApps by remember { mutableStateOf<List<ApplicationInfo>>(emptyList()) }
        var securedPackages by remember { 
            mutableStateOf(prefs.getStringSet("packages", emptySet()) ?: emptySet()) 
        }

        LaunchedEffect(Unit) {
            withContext(Dispatchers.IO) {
                val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
                    .filter { it.flags and ApplicationInfo.FLAG_SYSTEM == 0 } 
                    .sortedBy { pm.getApplicationLabel(it).toString() }
                installedApps = apps
            }
        }

        Scaffold(
            containerColor = AmoledBlack,
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onRestartClick,
                    containerColor = NeonCyan,
                    contentColor = AmoledBlack
                ) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Restart Launcher")
                }
            }
        ) { paddingValues ->
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp)) {
                Text(
                    text = "Infinity X Security",
                    color = NeonCyan,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 24.dp, top = 32.dp)
                )

                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(installedApps) { app ->
                        val packageName = app.packageName
                        val appName = pm.getApplicationLabel(app).toString()
                        val isSecured = securedPackages.contains(packageName)

                        AppListItem(
                            appName = appName,
                            packageName = packageName,
                            isSecured = isSecured,
                            onCheckedChange = { checked ->
                                val newSet = securedPackages.toMutableSet()
                                if (checked) newSet.add(packageName) else newSet.remove(packageName)
                                securedPackages = newSet
                                prefs.edit().putStringSet("packages", newSet).apply()
                            }
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun AppListItem(appName: String, packageName: String, isSecured: Boolean, onCheckedChange: (Boolean) -> Unit) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(GlassSurface)
                .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = appName, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Text(text = packageName, color = Color.Gray, fontSize = 12.sp)
            }
            
            Switch(
                checked = isSecured,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = AmoledBlack,
                    checkedTrackColor = NeonCyan,
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = GlassSurface
                )
            )
        }
    }
}
