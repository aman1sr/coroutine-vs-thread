package com.example.mycoroutinedemo

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.mycoroutinedemo.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.CountDownLatch

const val TAG = "CoroutineDemo_d"
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    var isCouroutineFlag = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //1. running 100 threads vs. 100 coroutines in a loop
        checkThreadCoroutineDiff()
        //2. blocking approach using traditional threading: ex : (ab link)
        threadBlockingNature()
        // 3.  non-blocking approach using Kotlin coroutines  (ab link)
        coroutineNonBlockingNature()
        // 4.  2 couroutine scope created in Main
        coroutineScope2Create()


    }

    private fun coroutineNonBlockingNature() {

    }

    private fun threadBlockingNature() {

    }

    private fun checkThreadCoroutineDiff() {
        binding.btnThreadCoroutineDiff.setOnClickListener {
            binding.btnThreadCoroutineDiff.text = if (isCouroutineFlag) "Coroutine Check " else "Thread Check"

            if(!isCouroutineFlag){
                Toast.makeText(this, "100 Thread STARTED...", Toast.LENGTH_SHORT).show()
                var startTime = System.currentTimeMillis()
                val latch = CountDownLatch(100)
                for (i in 1..100) {
                    Thread{
                        println("Thread: $i")
                        latch.countDown()
                    }.start()
                }
                latch.await()
                var endTime = System.currentTimeMillis()
                Toast.makeText(this@MainActivity, "Time taken Thread: ${endTime - startTime}", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Time taken Thread: ${endTime - startTime}")
                binding.btnThreadCoroutineDiff.text = "Next Coroutine Check "
            }else{
                Toast.makeText(this, "100 Coroutine STARTED...", Toast.LENGTH_SHORT).show()
                var startTime = System.currentTimeMillis()
               lifecycleScope.launch {
                   val jobs = (1..100).map {
                       async {
                        println("Coroutine: $it")
                       }
                   }
                   jobs.awaitAll()
                   var endTime = System.currentTimeMillis()
                   Log.d(TAG, "Time taken Coroutine: ${endTime - startTime}")
                   Toast.makeText(this@MainActivity, "Time taken Coroutine: ${endTime - startTime}", Toast.LENGTH_SHORT).show()
                   binding.btnThreadCoroutineDiff.text = "Next Thread Check "
               }

            }
            isCouroutineFlag = !isCouroutineFlag
        }


    }

    private fun coroutineScope2Create() {
        binding.btnCheck2Scope.setOnClickListener {
            Log.d(TAG, "Function Start")

            lifecycleScope.launch(Dispatchers.Main) {
                Log.d(TAG, "Before Task 1")
                doLongRunningTask()
                Log.d(TAG, "After Task 1")
            }

            lifecycleScope.launch(Dispatchers.IO) {
                Log.d(TAG, "Before Task 2")
                doLongRunningTask()
                Log.d(TAG, "After Task 2")
            }

            Log.d(TAG, "Function End")
        }
    }

    suspend fun doLongRunningTask() {
        delay(1000)
        Log.d(TAG, "doLongRunningTask: DONE")
    }
}