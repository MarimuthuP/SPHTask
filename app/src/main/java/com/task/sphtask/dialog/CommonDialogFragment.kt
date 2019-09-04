package com.task.sphtask.dialog

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.task.sphtask.R
import com.task.sphtask.pojo.RecordPojo
import com.task.sphtask.pojo.TotalUsagePojo

/**
 * Created by Marimuthu on 2019-09-02.
 */
class CommonDialogFragment : DialogFragment() {

    private var title: String? = null
    private var content: String? = null
    private var flag: Boolean? = null
    private var totalUsagePojo: TotalUsagePojo? = null

    private var DURATION: Int = 0

    private var textViewTitle: TextView? = null
    private var textViewContent: TextView? = null
    private var imageViewLogo: ImageView? = null
    private var buttonOk: Button? = null
    private var viewIncludeLayout: View? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        flag = arguments?.getBoolean(FLAG_DETAILED_VIEW)
        when (flag) {
            true -> {
                title = arguments?.getString(FLAG_TITLE)
                totalUsagePojo = arguments?.getSerializable(FLAG_VOLUME) as TotalUsagePojo
            }
            false -> {
                title = arguments?.getString(FLAG_TITLE)
                content = arguments?.getString(FLAG_CONTENT)
            }
        }

        // Pick a style based on the num.
        val style = DialogFragment.STYLE_NO_FRAME
        val theme = R.style.DialogTheme
        setStyle(style, theme)
    }

    override fun onStart() {
        super.onStart()
        DURATION = 500
        startAnimation()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_common_layout, container, false)

        buttonOk = view.findViewById<View>(R.id.btn_ok) as Button

        imageViewLogo = view.findViewById<View>(R.id.iv_alert_img) as ImageView
        textViewTitle = view.findViewById<View>(R.id.tv_alert_title) as TextView
        textViewContent = view.findViewById<View>(R.id.tv_alert_content) as TextView
        viewIncludeLayout = view.findViewById<View>(R.id.include_volume_details) as View

        textViewTitle!!.text = title
        if(!flag!!){
            textViewContent!!.text = content
            textViewContent!!.isVisible = true
            viewIncludeLayout!!.isVisible = false
        }
        else{
            textViewContent!!.isVisible = false
            viewIncludeLayout!!.isVisible = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                imageViewLogo!!.background = context?.getDrawable(R.drawable.ic_trending_down)
            }

            val textViewInfo = view.findViewById<View>(R.id.textView_desc) as TextView
            val textViewQ1 = view.findViewById<View>(R.id.textView_q1) as TextView
            val textViewQ2 = view.findViewById<View>(R.id.textView_q2) as TextView
            val textViewQ3 = view.findViewById<View>(R.id.textView_q3) as TextView
            val textViewQ4 = view.findViewById<View>(R.id.textView_q4) as TextView

            textViewInfo.text = context!!.resources.getString(R.string.volume_desc)

            val recordPojo = totalUsagePojo!!.usageDetails
            val size = totalUsagePojo!!.usageDetails.size
            when(size){
                2-> {
                    setValuesIntoView(textViewQ1, recordPojo.get(0))
                    setValuesIntoView(textViewQ2, recordPojo.get(1))
                    textViewQ3.isVisible = false
                    textViewQ4.isVisible = false
                }
                3-> {
                    setValuesIntoView(textViewQ1, recordPojo.get(0))
                    setValuesIntoView(textViewQ2, recordPojo.get(1))
                    setValuesIntoView(textViewQ3, recordPojo.get(2))
                    textViewQ4.isVisible = false
                }
                4-> {
                    setValuesIntoView(textViewQ1, recordPojo.get(0))
                    setValuesIntoView(textViewQ2, recordPojo.get(1))
                    setValuesIntoView(textViewQ3, recordPojo.get(2))
                    setValuesIntoView(textViewQ4, recordPojo.get(3))
                }
            }
        }

        buttonOk!!.setOnClickListener {
            //Toast.makeText(activity, "Okay", Toast.LENGTH_SHORT).show()
            applyAnimation()
        }
        return view
    }

    private fun setValuesIntoView(textView: TextView, records: RecordPojo) {
        textView.text = "${records.quarter} - ${records.data_volume}"
        if (records.isDown) highlightQuarter(textView)
    }

    private fun highlightQuarter(textView: TextView) {
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD_ITALIC)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textView.setTextColor(context!!.getColor(android.R.color.holo_red_dark))
        }
    }

    private fun startAnimation() {
        val decorView = dialog.window?.decorView

        val scaleDown = ObjectAnimator.ofPropertyValuesHolder(
            decorView,
            PropertyValuesHolder.ofFloat("scaleX", 0.0f, 1.0f),
            PropertyValuesHolder.ofFloat("scaleY", 0.0f, 1.0f),
            PropertyValuesHolder.ofFloat("alpha", 0.0f, 1.0f)
        )
        scaleDown.duration = DURATION.toLong()
        scaleDown.start()
    }

    private fun applyAnimation() {
        val decorView = dialog.window?.decorView

        val scaleDown = ObjectAnimator.ofPropertyValuesHolder(
            decorView,
            PropertyValuesHolder.ofFloat("scaleX", 1.0f, 0.0f),
            PropertyValuesHolder.ofFloat("scaleY", 1.0f, 0.0f),
            PropertyValuesHolder.ofFloat("alpha", 1.0f, 0.0f)
        )
        scaleDown.addListener(object : Animator.AnimatorListener {
            override fun onAnimationEnd(animation: Animator) {
                dismiss()
            }

            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationCancel(animation: Animator) {}

            override fun onAnimationRepeat(animation: Animator) {}
        })
        scaleDown.duration = DURATION.toLong()
        scaleDown.start()
    }

    companion object {
        /**
         * Create a new instance of CustomDialogFragment, providing "num" as an
         * argument.
         */
        val FLAG_VOLUME = "VOLUME_DATA"
        val FLAG_TITLE = "TITLE"
        val FLAG_CONTENT = "CONTENT"
        val FLAG_DETAILED_VIEW = "DETAIL_VIEW"

        fun newInstance(title: String, data_volume: TotalUsagePojo): CommonDialogFragment {
            val f = CommonDialogFragment()

            // Supply num input as an argument.
            val args = Bundle()
            args.putString(FLAG_TITLE, title)
            args.putSerializable(FLAG_VOLUME, data_volume)
            args.putBoolean(FLAG_DETAILED_VIEW, true)
            f.arguments = args

            return f
        }

        fun newInstance(title: String, content: String): CommonDialogFragment {
            val f = CommonDialogFragment()

            // Supply num input as an argument.
            val args = Bundle()
            args.putString(FLAG_TITLE, title)
            args.putString(FLAG_CONTENT, content)
            args.putBoolean(FLAG_DETAILED_VIEW, false)
            f.arguments = args

            return f
        }
    }
}