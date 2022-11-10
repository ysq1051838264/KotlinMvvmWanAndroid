package com.kotlin.mvvm.ui.home

import android.annotation.SuppressLint
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.kotlin.mvvm.R
import com.kotlin.mvvm.ext.checkLogin
import com.kotlin.mvvm.ext.onClick
import com.kotlin.mvvm.ext.startWebViewActivity
import com.kotlin.mvvm.ui.home.bean.HomeBean

/**
 * description:
 *
 * @author Db_z
 * @Date 2021/12/16 16:04
 */
class HomeAdapter : BaseQuickAdapter<HomeBean, BaseViewHolder>(R.layout.item_home), LoadMoreModule {

    private lateinit var listener: (collect: Boolean, id: Int, position: Int) -> Unit

    @SuppressLint("CheckResult")
    override fun convert(holder: BaseViewHolder, item: HomeBean) {
        holder.setGone(R.id.tv_top, !item.top)
        holder.setGone(R.id.tv_refresh, !item.fresh)
        holder.setGone(R.id.tv_article_tag, item.tags.isEmpty())
        holder.setText(
            R.id.tv_name,
            item.author.ifEmpty { item.shareUser }
        )
        holder.setText(R.id.tv_time, item.niceDate)
        val imageView = holder.getView<ImageView>(R.id.image)
        if (item.envelopePic.isNotEmpty()) {
            Glide.with(imageView)
                .load(item.envelopePic)
                .apply { RequestOptions.bitmapTransform(RoundedCorners(20)) }
                .into(imageView)
        }
        holder.setGone(R.id.image, item.envelopePic.isEmpty())
        holder.setText(R.id.tv_title, item.title)
        holder.setText(R.id.tv_type, "${item.superChapterName}/${item.chapterName}")
        holder.setImageResource(
            R.id.iv_collection,
            if (item.collect) R.drawable.ic_like else R.drawable.ic_like_not
        )
        holder.itemView.onClick { startWebViewActivity(item.id, item.link, item.title) }
        holder.getView<AppCompatImageView>(R.id.iv_collection).onClick {
            checkLogin {
                listener.invoke(item.collect, item.id, getItemPosition(item))
            }
        }
    }

    fun setCollectionListener(listener: (collect: Boolean, id: Int, position: Int) -> Unit) {
        this.listener = listener
    }
}