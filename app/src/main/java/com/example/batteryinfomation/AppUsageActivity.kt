package com.example.batteryinfomation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.battery.library.BatteryApp
import com.battery.library.BatteryConstants
import com.battery.library.viewmodel.BatteryViewModel

class AppUsageActivity : AppCompatActivity() {

    private lateinit var viewModel: BatteryViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AppUsageListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_usage)

        adapter = AppUsageListAdapter()
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter

        viewModel = BatteryApp.getBatteryViewModel()
        viewModel.getLastUsedAppList().observe(this, { list ->
//            list.forEach {
//                Log.d("xxxxx", "lastUsedAppList$it")
//            }
            adapter.submitList(list)
        })
    }

    override fun onStart() {
        super.onStart()
        val currentTimeMillis = System.currentTimeMillis()
        //获取最近十天内的数据
        viewModel.getLastUsedApps(
            currentTimeMillis - 10 * BatteryConstants.ONE_DAY,
            currentTimeMillis
        )
    }
}