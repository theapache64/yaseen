package com.github.theapache64.yaseen

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2

val yaseen = arrayOf(
    R.drawable.page_1,
    R.drawable.page_2,
    R.drawable.page_3,
    R.drawable.page_4,
    R.drawable.page_5,
    R.drawable.page_6,
    R.drawable.page_7,
    R.drawable.page_8,
)


class MainActivity : FragmentActivity() {

    private val vpYaseen by lazy {
        findViewById<ViewPager2>(R.id.vp_yaseen)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_main)

        val adapter = object : FragmentStateAdapter(this) {
            override fun createFragment(position: Int): Fragment {
                return PageFragment.newInstance(position)
            }

            override fun getItemCount(): Int = yaseen.size
        }

        vpYaseen.apply {
            this.adapter = adapter
            this.layoutDirection = ViewPager2.LAYOUT_DIRECTION_RTL
        }
    }

    fun goToFirstPage() {
        vpYaseen.setCurrentItem(0, true)
    }
}

class PageFragment() : Fragment() {
    companion object {
        private const val KEY_PAGE = "page"
        fun newInstance(page: Int): PageFragment {
            return PageFragment().apply {
                arguments = Bundle().apply {
                    putInt(KEY_PAGE, page)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val position = arguments?.getInt(KEY_PAGE) ?: -1
        val layout = context?.let { ctx ->
            val ivPage = ImageView(ctx).apply {
                // full screen
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                scaleType = ImageView.ScaleType.FIT_XY
                scaleX = 1.1f

                // image
                setImageResource(yaseen[position])

            }

            val btn = if (position == yaseen.lastIndex) {
                Button(ctx).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply {
                        gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
                    }

                    setOnClickListener {
                        (activity as MainActivity).goToFirstPage()
                    }

                    text = "GO TO FIRST PAGE"
                }
            } else {
                null
            }

            // root
            FrameLayout(ctx).apply {
                addView(ivPage)
                if (btn != null) {
                    addView(btn)
                }
            }
        }

        return layout
    }
}