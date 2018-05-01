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

//const val URL_SPEAKERS_IMAGES = "https://github.com/barcelonajug/jbcnconf_web/blob/master/2018/"

//const val URL_SPEAKERS_IMAGES = "http://www.jbcnconf.com/2018/assets/img/speakers/"
const val URL_SPEAKERS_IMAGES = "http://www.jbcnconf.com/2018/"

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
                .load(URL_SPEAKERS_IMAGES + item.speaker.image)
               // .override(65, 43)
                .error(R.drawable.cry)
                .listener(object: RequestListener<String, GlideDrawable> {
                    /**
                     * Called when an exception occurs during a load. Will only be called if we currently want to display an image
                     * for the given model in the given target. It is recommended to create a single instance per activity/fragment
                     * rather than instantiate a new object for each call to `Glide.load()` to avoid object churn.
                     *
                     *
                     *
                     * It is safe to reload this or a different model or change what is displayed in the target at this point.
                     * For example:
                     * <pre>
                     * `public void onException(Exception e, T model, Target target, boolean isFirstResource) {
                     * target.setPlaceholder(R.drawable.a_specific_error_for_my_exception);
                     * Glide.load(model).into(target);
                     * }
                    ` *
                    </pre> *
                     *
                     *
                     *
                     *
                     * Note - if you want to reload this or any other model after an exception, you will need to include all
                     * relevant builder calls (like centerCrop, placeholder etc).
                     *
                     *
                     * @param e The exception, or null.
                     * @param model The model we were trying to load when the exception occurred.
                     * @param target The [Target] we were trying to load the image into.
                     * @param isFirstResource True if this exception is for the first resource to load.
                     * @return True if the listener has handled updating the target for the given exception, false to allow
                     * Glide's request to update the target.
                     */
                    override fun onException(e: Exception?, model: String?, target: Target<GlideDrawable>?, isFirstResource: Boolean): Boolean {
                        Log.d(TAG, e?.message)
                        return false
                    }

                    /**
                     * Called when a load completes successfully, immediately after
                     * [Target.onResourceReady].
                     *
                     * @param resource The resource that was loaded for the target.
                     * @param model The specific model that was used to load the image.
                     * @param target The target the model was loaded into.
                     * @param isFromMemoryCache True if the load completed synchronously (useful for determining whether or not to
                     * animate)
                     * @param isFirstResource True if this is the first resource to in this load to be loaded into the target. For
                     * example when loading a thumbnail and a fullsize image, this will be true for the first
                     * image to load and false for the second.
                     * @return True if the listener has handled setting the resource on the target (including any animations), false to
                     * allow Glide's request to update the target (again including animations).
                     */
                    override fun onResourceReady(resource: GlideDrawable?, model: String?, target: Target<GlideDrawable>?, isFromMemoryCache: Boolean, isFirstResource: Boolean): Boolean {
                        Log.d(TAG, model)
                        return false
                    }

                })
                .fitCenter()
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
        val mSpeakerImageView: ImageView = mView.cardviewSpeakerImage
        val mTitleView: TextView = mView.cardviewTalkTitle
        val mSpeakerView: TextView = mView.cardviewSpeakerName

        override fun toString(): String {
            return super.toString() + " '" + mTitleView.text + "'"
        }
    }
}
