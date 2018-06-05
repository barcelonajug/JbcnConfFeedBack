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
import java.util.*


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
                /*
                .bitmapTransform(object : BitmapTransformation(context) {
                    /**
                     *
                     * NOT WORKING WITH PLACEHOLDER
                     *
                     * A method to get a unique identifier for this particular transformation that can be used as part of a cache key.
                     * The fully qualified class name for this class is appropriate if written out, but getClass().getName() is not
                     * because the name may be changed by proguard.
                     *
                     *
                     *
                     * If this transformation does not affect the data that will be stored in cache, returning an empty string here
                     * is acceptable.
                     *
                     *
                     * @return A string that uniquely identifies this transformation.
                     */
                    override fun getId(): String {
                        return "Rounded Transformation"
                    }

                    /**
                     * Transforms the given [android.graphics.Bitmap] based on the given dimensions and returns the transformed
                     * result.
                     *
                     *
                     *
                     * The provided Bitmap, toTransform, should not be recycled or returned to the pool. Glide will automatically
                     * recycle and/or reuse toTransform if the transformation returns a different Bitmap. Similarly implementations
                     * should never recycle or return Bitmaps that are returned as the result of this method. Recycling or returning
                     * the provided and/or the returned Bitmap to the pool will lead to a variety of runtime exceptions and drawing
                     * errors. See #408 for an example. If the implementation obtains and discards intermediate Bitmaps, they may
                     * safely be returned to the BitmapPool and/or recycled.
                     *
                     *
                     *
                     *
                     * outWidth and outHeight will never be [com.bumptech.glide.request.target.Target.SIZE_ORIGINAL], this
                     * class converts them to be the size of the Bitmap we're going to transform before calling this method.
                     *
                     *
                     * @param pool A [com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool] that can be used to obtain and
                     * return intermediate [Bitmap]s used in this transformation. For every
                     * [android.graphics.Bitmap] obtained from the pool during this transformation, a
                     * [android.graphics.Bitmap] must also be returned.
                     * @param toTransform The [android.graphics.Bitmap] to transform.
                     * @param outWidth The ideal width of the transformed bitmap (the transformed width does not need to match exactly).
                     * @param outHeight The ideal height of the transformed bitmap (the transformed heightdoes not need to match
                     * exactly).
                     */
                    override fun transform(pool: BitmapPool?, toTransform: Bitmap?, outWidth: Int, outHeight: Int): Bitmap {

                        val size = Math.min(toTransform!!.width, toTransform!!.height)
                        val x = (toTransform.width - size) / 2
                        val y = (toTransform.height - size) / 2
                        val squared = Bitmap.createBitmap(toTransform, x, y, size, size)
//                        val result: Bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
                        val result: Bitmap = pool!!.get(size, size, Bitmap.Config.ARGB_8888)
                        val canvas = Canvas(result)
                        val paint = Paint()
                        paint.shader = BitmapShader(squared, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
                        paint.isAntiAlias = true
                        val r = size / 2F
                        canvas.drawCircle(r, r, r, paint)
                        return
                    }

                })
                */
                //.override(65, 43)
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
        val session = SessionsTimes.valueOf("${scheduleId.substring(1, 4)}_${scheduleId.substring(9, 12)}")
        val location = TalksLocations.valueOf("${scheduleId.substring(1, 4)}_${scheduleId.substring(5, 8)}")

//        val simpleDateFormat = SimpleDateFormat("dd/MM/yyy hh:mm:ss")
        val simpleDateFormat = SimpleDateFormat("hh:mm")
        val startTime = simpleDateFormat.format(session.getStartTime().time)
        val endTime = simpleDateFormat.format(session.getEndTime().time)
//        simpleDateFormat.applyPattern("EEEE d MMM yyyy")
        simpleDateFormat.applyPattern("EEEE dd MMMM")
        val due = simpleDateFormat.format(session.getStartTime().time)
//        holder.mScheduleId.text = "Code: ${item.talk.scheduleId}. Starting: ${startTime}. Ending: ${endTime}. Location: ${location.getRoomName()}"
        holder.mScheduleId.text = "Due on $due from $startTime to $endTime in '${location.getRoomName()}'"
        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size

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
