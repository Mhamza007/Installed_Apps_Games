package com.mhamza.installedappsgames

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_games.*
import java.lang.Exception

class GamesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_games)

        apps_list.isTextFilterEnabled = true

        //refresh
        apps_swipe_refresh.setOnRefreshListener {
            refreshApps()
        }

        apps_list.onItemClickListener = AdapterView.OnItemClickListener{
                parent, view, position, id ->

            val packageManager = this.packageManager

            val app = parent?.getItemAtPosition(position) as AppInfo
            val appIntent = packageManager.getLaunchIntentForPackage(app.info.packageName)

            if (appIntent == null){
                Toast.makeText(this, "This app could not be launched", Toast.LENGTH_SHORT).show()
            } else {
                startActivity(appIntent)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        val loadApps = LoadApps()
        loadApps.execute(PackageManager.GET_META_DATA)
    }

    private fun refreshApps() {
        val loadApps = LoadApps()
        loadApps.execute(PackageManager.GET_META_DATA)

    }

    inner class LoadApps : AsyncTask<Int, Int, List<AppInfo>>(){

        override fun onPreExecute() {
            super.onPreExecute()
            apps_swipe_refresh.isRefreshing = true
        }

        override fun doInBackground(vararg params: Int?): List<AppInfo> {
            val apps : MutableList<AppInfo> = ArrayList()
            val packageManager = this@GamesActivity.packageManager

            val infos = packageManager.getInstalledApplications(params[0]!!)

            for (info in infos){
                if ((info.flags and ApplicationInfo.FLAG_SYSTEM) === 1){
                    continue
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    if (info.category == ApplicationInfo.CATEGORY_GAME){
                        val app = AppInfo()
                        app.info = info
                        app.label = info.loadLabel(packageManager) as String
                        apps.add(app)
                    }
                } else {
                    if ((info.flags and ApplicationInfo.FLAG_IS_GAME) == ApplicationInfo.FLAG_IS_GAME){
                        val app = AppInfo()
                        app.info = info
                        app.label = info.loadLabel(packageManager) as String
                        apps.add(app)
                    }
                }
            }
            return apps
        }

        override fun onPostExecute(result: List<AppInfo>?) {
            super.onPostExecute(result)

            try {
                apps_list.adapter = AppAdapter(this@GamesActivity, result!!)
                apps_swipe_refresh.isRefreshing = false
                Toast.makeText(this@GamesActivity, "${result.size} application loaded", Toast.LENGTH_SHORT).show()
            } catch (e : Exception){
                Log.e("App", "Post Execute Exception : $e")
            }
        }

    }
}
