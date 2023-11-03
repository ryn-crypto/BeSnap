package com.ryan.storyapp.ui.main.adapter

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ryan.storyapp.R
import com.ryan.storyapp.data.model.ListStoryItem
import com.ryan.storyapp.databinding.StoryCardBinding
import com.ryan.storyapp.ui.detail.DetailStoryActivity
import com.ryan.storyapp.utils.DateUtils

class StoryPagingAdapter :
    PagingDataAdapter<ListStoryItem, StoryPagingAdapter.MyViewHolder>(DIFF_CALLBACK) {
    var showShimmer = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = StoryCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    class MyViewHolder(private val binding: StoryCardBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        private lateinit var itemId: String

        init {
            binding.ivItemPhoto.setOnClickListener(this)
        }

        fun bind(data: ListStoryItem) {
            itemId = data.id
            binding.tvItemName.text = data.name
            binding.uploadTime.text =
                DateUtils.calculateTimeAgo(binding.root.context, data.createdAt)

            val builder = SpannableStringBuilder()

            val name = SpannableString(data.name)
            name.setSpan(
                StyleSpan(Typeface.BOLD),
                0,
                name.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            name.setSpan(
                RelativeSizeSpan(1.2f),
                0,
                name.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            builder.append(name)
            builder.append(" ")

            val description = SpannableString(data.description.replace("\n", " "))
            builder.append(description)

            binding.description.text = builder

            if (data.photoUrl.endsWith(".jpg")) {
                Glide.with(binding.ivItemPhoto.context)
                    .load(data.photoUrl)
                    .into(binding.ivItemPhoto)
            } else {
                binding.ivItemPhoto.visibility = View.INVISIBLE

                val layoutParams = binding.ivItemPhoto.layoutParams
                layoutParams.height = 0
                binding.ivItemPhoto.layoutParams = layoutParams
            }

            binding.description.setOnClickListener {
                if (binding.description.maxLines == Int.MAX_VALUE) {
                    binding.description.maxLines = 3
                    binding.description.ellipsize = TextUtils.TruncateAt.END
                } else {
                    binding.description.maxLines = Int.MAX_VALUE
                    binding.description.ellipsize = null
                }
            }
        }

        override fun onClick(view: View?) {
            when (view?.id) {
                R.id.iv_item_photo -> {
                    val intent = Intent(view.context, DetailStoryActivity::class.java)
                    intent.putExtra("story_id", itemId)

                    val imagePair = Pair.create(binding.ivItemPhoto as View, "image")
                    val profilePair = Pair.create(binding.profileCardView as View, "profileImg")
                    val namePair = Pair.create(binding.tvItemName as View, "name")
                    val descriptionPair = Pair.create(binding.description as View, "description")
                    val uploadPair = Pair.create(binding.profileCardView as View, "timeUpload")
                    val likePair = Pair.create(binding.likeButton as View, "like")
                    val commentPair = Pair.create(binding.comment as View, "comment")
                    val sharePair = Pair.create(binding.shareButton as View, "share")

                    val optionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            itemView.context as Activity,
                            imagePair,
                            namePair,
                            descriptionPair,
                            likePair,
                            commentPair,
                            sharePair,
                            profilePair,
                            uploadPair
                        )

                    itemView.context.startActivity(intent, optionsCompat.toBundle())
                }
            }
        }
    }


    companion object {

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}