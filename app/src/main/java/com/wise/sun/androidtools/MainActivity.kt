package com.wise.sun.androidtools

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.wise.sun.androidtools.DataStructAndAlgorithm.QuickSort
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var sortArray : IntArray = intArrayOf(33,7,3,51,9,10,16,4,2,8)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var quickSort : QuickSort = QuickSort()
        sortArray = quickSort.getSortedArray(sortArray)
        var resultString : String = ""
        for (i in sortArray.indices){
            resultString += sortArray[i].toString() + ","
        }
        tv_text.text = resultString
    }
}
