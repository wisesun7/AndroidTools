package com.wise.sun.androidtools.DataStructAndAlgorithm;

import android.util.Log;

/**
 * Created by wise on 2019/8/17.
 */

public class QuickSort {
    private static final String TAG = "QuickSort";
    private int Count = 0;

    public int[] getSortedArray(int[] aimArray){

        return sorting(aimArray,0,aimArray.length-1);
    }

    private int[] sorting(int[] aimArray,int low, int high){
        if (low < high){
            int aimIndex = getPartition(aimArray,low,high);
            sorting(aimArray,low,aimIndex-1);
            sorting(aimArray,aimIndex+1,high);
        }
        return aimArray;
    }

    private int getPartition(int[] aimArray,int low, int high){
        int aim = aimArray[low];
        int aimIndex = low;
        while (low < high){
            while (low < high && aimArray[high] >= aim){
                high --;
            }
            while (low < high && aimArray[low] <= aim){
                low ++;
            }
            swap(aimArray,low,high);
        }
        swap(aimArray,aimIndex,low);
        return low;
    }

    private void swap(int[] aimArray, int i, int j){
        Count++;
        int temp = aimArray[i];
        aimArray[i] = aimArray[j];
        aimArray[j] = temp;
        for (int k=0;k<aimArray.length;k++){
            Log.d("wisesun",Count+": " + aimArray[k] + ",");
        }
    }


}
