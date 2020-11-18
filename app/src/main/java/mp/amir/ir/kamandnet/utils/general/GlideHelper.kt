package mp.amir.ir.kamandnet.utils.general

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import mp.amir.ir.kamandnet.R
import mp.amir.ir.kamandnet.respository.apiservice.ApiService


@BindingAdapter("profileUrl")
fun loadProfilePic(mProfileImage: ImageView, picUrl: String?) {
    println("debug: glideUrl:$picUrl")
    val requestOptions = RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        .placeholder(R.drawable.ic_profile_placeholder2)
    Glide.with(mProfileImage.context)
        .setDefaultRequestOptions(requestOptions)
        .load(if (picUrl != null) ApiService.domain + picUrl else "") //.transition(new DrawableTransitionOptions().crossFade()) //transition baraye circleImageView nemishe
        .into(mProfileImage)
}

@BindingAdapter("imageUrl")
fun loadPic(iv: ImageView, picUrl: String?) {
    //val _picUrl = picUrl?.toInt() //TODO in khat hatman bayad hazf shavad //test purpose
    val requestOptions = RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
      //  .placeholder(R.drawable.ic_worker)
    Glide.with(iv.context)
        .setDefaultRequestOptions(requestOptions)
        .load(
            ApiService.domain + (picUrl ?: "")
        ) //.transition(new DrawableTransitionOptions().crossFade()) //transition baraye circleImageView nemishe
        .into(iv)
}