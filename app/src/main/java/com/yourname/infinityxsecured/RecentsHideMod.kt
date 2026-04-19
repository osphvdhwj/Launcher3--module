package com.yourname.infinityxsecured

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.view.View
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class RecentsHideMod : IXposedHookLoadPackage {

    // Target the companion app's package to read its preferences
    private val prefs = XSharedPreferences("com.yourname.infinityxsecured", "secured_apps_prefs")

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != "com.android.launcher3") return

        try {
            val taskThumbnailViewClass = XposedHelpers.findClassIfExists(
                "com.android.quickstep.views.TaskThumbnailView",
                lpparam.classLoader
            ) ?: return

            XposedHelpers.findAndHookMethod(
                taskThumbnailViewClass,
                "onDraw",
                Canvas::class.java,
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        val view = param.thisObject as View
                        val canvas = param.args[0] as Canvas

                        val task = XposedHelpers.getObjectField(view, "mTask") ?: return
                        val taskKey = XposedHelpers.getObjectField(task, "key") ?: return
                        
                        val packageName = try {
                            XposedHelpers.getObjectField(taskKey, "getPackageName") as String?
                        } catch (e: NoSuchFieldError) {
                            XposedHelpers.callMethod(taskKey, "getPackageName") as String?
                        }

                        // Reload prefs dynamically in case the user changed them
                        prefs.reload()
                        val securedApps = prefs.getStringSet("packages", emptySet()) ?: emptySet()

                        if (securedApps.contains(packageName)) {
                            param.result = null

                            // AMOLED Black Background
                            canvas.drawColor(Color.parseColor("#000000"))

                            // Neon Cyan "SECURED" Text
                            val paint = Paint().apply {
                                color = Color.parseColor("#00FFFF")
                                textSize = 72f
                                textAlign = Paint.Align.CENTER
                                isAntiAlias = true
                                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                            }

                            val xPos = canvas.width / 2f
                            val yPos = (canvas.height / 2f) - ((paint.descent() + paint.ascent()) / 2f)

                            canvas.drawText("SECURED", xPos, yPos, paint)
                        }
                    }
                }
            )
        } catch (e: Throwable) {
            XposedBridge.log("InfinityXSecured Error: " + e.message)
        }
    }
}
