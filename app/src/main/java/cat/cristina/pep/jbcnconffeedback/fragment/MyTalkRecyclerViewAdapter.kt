package cat.cristina.pep.jbcnconffeedback.fragment

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import cat.cristina.pep.jbcnconffeedback.R


import cat.cristina.pep.jbcnconffeedback.fragment.ChooseTalkFragment.OnChooseTalkListener
import cat.cristina.pep.jbcnconffeedback.fragment.dummy.TalkContent.TalkItem
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

import kotlinx.android.synthetic.main.fragment_choose_talk.view.*
import java.lang.Exception

/**
 * [RecyclerView.Adapter] that can display a [TalkItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 */

private val TAG = MyTalkRecyclerViewAdapter::class.java.name

const val URL_SPEAKERS_IMAGES = "https://github.com/barcelonajug/jbcnconf_web/blob/master/2018/"

class MyTalkRecyclerViewAdapter(
        private val mValues: List<TalkItem>,
        private val mListener: OnChooseTalkListener?,
        private val context: Context)
    : RecyclerView.Adapter<MyTalkRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as TalkItem
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onChooseTalk(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_choose_talk, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.mIdView.text = item.id
        Glide
                .with(context)
                .load("https://github.com/barcelonajug/jbcnconf_web/blob/master/2018/assets/img/speakers/abrauner.jpg")
                //.load(URL_SPEAKERS_IMAGES + item.speaker.image)
                //.load("https://images.freeimages.com/images/large-previews/981/cow-1380252.jpg")
                .override(65, 43)
                .error(R.drawable.cry)
                .into(holder.mSpeakerImageView)
        holder.mTitleView.text = item.talk.title
        holder.mSpeakerView.text = item.speaker.name
        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mIdView: TextView = mView.item_number
        val mSpeakerImageView: ImageView = mView.speakerImage
        val mTitleView: TextView = mView.title
        val mSpeakerView: TextView = mView.speakerName

        override fun toString(): String {
            return super.toString() + " '" + mTitleView.text + "'"
        }
    }
}
