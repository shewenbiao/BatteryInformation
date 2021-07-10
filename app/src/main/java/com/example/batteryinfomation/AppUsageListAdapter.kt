package com.example.batteryinfomation

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.battery.library.BatteryConstants
import com.battery.library.data.LastUsedApp

/**
 * @author : She Wenbiao
 * @date   : 2021/7/10 12:03 下午
 */
class AppUsageListAdapter : ListAdapter<LastUsedApp, AppUsageListAdapter.ItemViewHolder>(object :
    DiffUtil.ItemCallback<LastUsedApp>() {

    override fun areItemsTheSame(oldItem: LastUsedApp, newItem: LastUsedApp): Boolean {
        return oldItem.packageName == newItem.packageName
    }

    override fun areContentsTheSame(oldItem: LastUsedApp, newItem: LastUsedApp): Boolean {
        return oldItem.name == newItem.name && oldItem.launchCount == newItem.launchCount
                && oldItem.totalTimeInForeground == newItem.totalTimeInForeground
    }

}) {

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var iconIv: ImageView = itemView.findViewById(R.id.iv_icon)
        var nameTv: TextView = itemView.findViewById(R.id.tv_name)
        var timeTv: TextView = itemView.findViewById(R.id.tv_time)
        var launchCountTv: TextView = itemView.findViewById(R.id.tv_launch_count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_app_usage_list, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val lastUsedApp = getItem(position)
        holder.iconIv.setImageDrawable(lastUsedApp.icon)
        holder.nameTv.text = lastUsedApp.name
        holder.launchCountTv.text = String.format(holder.itemView.context.getString(R.string.launch_count), lastUsedApp.launchCount)
        holder.timeTv.text = getDuration(holder.itemView.context, lastUsedApp.totalTimeInForeground)
    }

    private fun getDuration(context: Context, duration: Long): String {
        if (duration <= 0) {
            return "--"
        }
        val hour = (duration / BatteryConstants.ONE_HOUR).toInt()
        val minute = (duration % BatteryConstants.ONE_HOUR / BatteryConstants.ONE_MINUTE).toInt()
        var second =
            (duration % (BatteryConstants.ONE_HOUR * BatteryConstants.ONE_MINUTE) / BatteryConstants.ONE_SECOND).toInt()
        if (second < 1) {
            second = 1
        }
        return when {
            hour > 0 -> {
                String.format(
                    context.getString(R.string.duration_time_h_min),
                    hour, minute
                )
            }
            minute > 0 -> {
                String.format(
                    context.getString(R.string.duration_time_min),
                    minute
                )
            }
            else -> {
                String.format(
                    context.getString(R.string.duration_time_second),
                    second
                )
            }
        }
    }
}