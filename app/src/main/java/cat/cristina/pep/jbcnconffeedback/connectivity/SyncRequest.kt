package cat.cristina.pep.jbcnconffeedback.connectivity

import android.os.AsyncTask
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.RequestFuture
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class SyncRequest(val requestQueue: RequestQueue): AsyncTask<String, Int, JSONObject>() {
    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to [.execute]
     * by the caller of this task.
     *
     * This method can call [.publishProgress] to publish updates
     * on the UI thread.
     *
     * @param params The parameters of the task.
     *
     * @return A result, defined by the subclass of this task.
     *
     * @see .onPreExecute
     * @see .onPostExecute
     *
     * @see .publishProgress
     */
    override fun doInBackground(vararg params: String?): JSONObject {
        val requestFuture: RequestFuture<JSONObject> = RequestFuture.newFuture()
        val jsonObjectRequest: JsonObjectRequest = JsonObjectRequest(params[0], null, requestFuture, requestFuture)
        requestQueue.add(jsonObjectRequest)
        /* La llamada a get() es síncrona por eso hay un timeout */
        val jsonObject: JSONObject = requestFuture.get(10, TimeUnit.SECONDS)
        return jsonObject
    }

    override fun onPostExecute(result: JSONObject?) {
        super.onPostExecute(result)
    }
}