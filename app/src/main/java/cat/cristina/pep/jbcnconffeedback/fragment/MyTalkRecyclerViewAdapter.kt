package cat.cristina.pep.jbcnconffeedback.fragment


import android.content.Context
import android.content.res.Configuration
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import cat.cristina.pep.jbcnconffeedback.R
import cat.cristina.pep.jbcnconffeedback.activity.MainActivity.Companion.URL_SPEAKERS_IMAGES
import cat.cristina.pep.jbcnconffeedback.fragment.ChooseTalkFragment.OnChooseTalkListener
import cat.cristina.pep.jbcnconffeedback.fragment.provider.TalkContent.TalkItem
import cat.cristina.pep.jbcnconffeedback.utils.SessionsTimes
import cat.cristina.pep.jbcnconffeedback.utils.TalksLocations
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.fragment_choose_talk.view.*
import java.lang.Exception
import java.text.SimpleDateFormat


/**
 * Adapters provide a binding from an app-specific data set to views that are displayed within a
 * RecyclerView
 *
 */

private val TAG = MyTalkRecyclerViewAdapter::class.java.name

class MyTalkRecyclerViewAdapter(
        private val mValues: List<TalkItem>,
        private val mListener: OnChooseTalkListener?,
        private val context: Context)
    : RecyclerView.Adapter<MyTalkRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener
    private val mOnLongClickListener: View.OnLongClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as TalkItem
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onChooseTalk(item)
        }

        mOnLongClickListener = View.OnLongClickListener { v ->
            val item = v.tag as TalkItem
            mListener?.onLongChooseTalk(item)
            true
        }
    }

    /*
    * Called when RecyclerView needs a new RecyclerView.ViewHolder of the given type to
    * represent an item.
    *
    * */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_choose_talk, parent, false)
        return ViewHolder(view)
    }

    /*
    * Called by RecyclerView to display the data at the specified position.
    * This method should update the contents of the itemView to reflect the item at the
    * given position.
    *
    * */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.mIdView.text = item.id
        val imageUrl: String = URL_SPEAKERS_IMAGES + item.speaker.image
        Glide
                .with(context)
                .load(imageUrl)
                .error(R.drawable.missing_photo)
                .listener(object : RequestListener<String, GlideDrawable> {
                    /**
                     * Called when an exception occurs during a load. Will only be called if we currently want to display an image
                     * for the given model in the given target. It is recommended to create a single instance per activity/fragment
                     * rather than instantiate a new object for each call to `Glide.load()` to avoid object churn.
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

        // Small screens
        if (context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK < Configuration.SCREENLAYOUT_SIZE_LARGE) {
            holder.mTitleView.text =
                    if (item.talk.title.length > 35) item.talk.title.substring(0, 35).let {
                        "$it..."
                    } else item.talk.title
        } else { // Big screens
            holder.mTitleView.text =
                    if (item.talk.title.length > 60) item.talk.title.substring(0, 60).let {
                        "$it..."
                    } else item.talk.title
        }
        holder.mSpeakerView.text = item.speaker.name

        val scheduleId = item.talk.scheduleId
        if(scheduleId.isNotEmpty()) {
            val session = SessionsTimes.valueOf("${scheduleId.substring(1, 4)}_${scheduleId.substring(9, 12)}")
            val location = TalksLocations.valueOf("${scheduleId.substring(1, 4)}_${scheduleId.substring(5, 8)}")

            val simpleTimeFormat = SimpleDateFormat("HH:mm")
            val startTime = simpleTimeFormat.format(session.getStartTalkDateTime().time)
            val endTime = simpleTimeFormat.format(session.getEndTalkDateTime().time)
            // eg. Monday 11 March
            simpleTimeFormat.applyPattern("EEEE dd MMMM")
            val due = simpleTimeFormat.format(session.getStartTalkDateTime().time)
//        holder.mScheduleId.text = "Code: ${item.talk.scheduleId}. Starting: ${startTime}. Ending: ${endTime}. Location: ${location.getRoomName()}"
            holder.mScheduleId.text =
                "Due on $due from $startTime to $endTime in '${location.getRoomName()}'"
            with(holder.mView) {
                tag = item
                setOnClickListener(mOnClickListener)
                setOnLongClickListener(mOnLongClickListener)
            }
        }
    }

    /*
    * Returns the total number of items in the data set held by the adapter.
    *
    * */
    override fun getItemCount(): Int = mValues.size

    /*
    * This is the content of every card in the ChooseTalkFragment
    *
    * */
    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {

        val mIdView: TextView = mView.cardViewItemNumber
        val mSpeakerImageView: ImageView = mView.cardviewSpeakerImage
        val mTitleView: TextView = mView.cardviewTalkTitle
        val mSpeakerView: TextView = mView.cardviewSpeakerName
        val mScheduleId: TextView = mView.cardviewScheduleId

        override fun toString(): String {
            return super.toString() + " '" + mTitleView.text + "'"
        }
    }
}
