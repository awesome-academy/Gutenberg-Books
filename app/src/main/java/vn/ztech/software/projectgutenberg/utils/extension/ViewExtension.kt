package vn.ztech.software.projectgutenberg.utils.extension

import android.widget.ImageView
import com.bumptech.glide.Glide
import vn.ztech.software.projectgutenberg.R

fun ImageView.loadImage(url: String) {
    Glide
        .with(this)
        .load(url)
        .centerCrop()
        .placeholder(R.drawable.image_book_body)
        .into(this)
}
