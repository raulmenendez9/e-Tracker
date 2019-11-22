package com.unicomer.e_tracker_test.Adapters

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.unicomer.e_tracker_test.R

@BindingAdapter("setImage")
fun bindSetImage(view: ImageView, itemType: String){
    when(itemType){
        "0" ->{
            view.setImageResource(R.drawable.ic_cat_food)
        }
        "1" ->{
            view.setImageResource(R.drawable.ic_cat_car)
        }
        "2" ->{
            view.setImageResource(R.drawable.ic_cat_hotel)
        }
        "3" ->{
            view.setImageResource(R.drawable.ic_cat_other)
        }
    }
}