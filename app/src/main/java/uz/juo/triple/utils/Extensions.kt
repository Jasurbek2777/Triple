package uz.juo.triple.utils

import android.view.View
import com.google.android.material.snackbar.Snackbar

fun View.NoInternet(){
    Snackbar.make(this,"No Internet connection",Snackbar.LENGTH_SHORT).show()
}