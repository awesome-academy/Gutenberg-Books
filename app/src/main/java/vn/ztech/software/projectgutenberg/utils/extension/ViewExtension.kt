package vn.ztech.software.projectgutenberg.utils.extension

import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import vn.ztech.software.projectgutenberg.R

fun ImageView.loadImage(
    url: String,
    @DrawableRes drawablePlaceHolder: Int = R.drawable.image_book_body
) {
    Glide
        .with(this)
        .load(url)
        .centerCrop()
        .placeholder(drawablePlaceHolder)
        .into(this)
}

fun ImageView.loadImage(
    res: Int,
    @DrawableRes drawablePlaceHolder: Int = R.drawable.image_book_body
) {
    Glide
        .with(this)
        .load(res)
        .centerCrop()
        .placeholder(drawablePlaceHolder)
        .into(this)
}
