package com.mhamza.installedappsgames

import android.content.Context
import android.content.pm.PackageManager
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class AppAdapter(context : Context, var apps : List<AppInfo>) : ArrayAdapter<AppInfo>(context, R.layout.app_item_layout, apps){

    var layoutInflater : LayoutInflater = LayoutInflater.from(context)
    var packageManager : PackageManager = context.packageManager

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val current : AppInfo = apps[position]
        var view : View? = convertView

        if (view == null){
            view = layoutInflater.inflate(R.layout.app_item_layout, parent, false)
        }

        val appTitle = view!!.findViewById<TextView>(R.id.app_title)
        appTitle.text = current.label

        try {
            if (!TextUtils.isEmpty(current.info.packageName)){
                var subTitle = view.findViewById<TextView>(R.id.sub_title)
                subTitle.text = current.info.packageName
            } else {
                Log.e("App", "Package Name Error")
            }
        } catch (e : PackageManager.NameNotFoundException){
            Log.e("App", "Package Name Error : $e")
        }

        val appIcon = view.findViewById<ImageView>(R.id.app_icon)
        appIcon.background = current.info.loadIcon(packageManager)

        return view
    }
}