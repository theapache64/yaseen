package com.github.theapache64.yaseen

import android.R.attr.text
import android.annotation.SuppressLint
import android.graphics.ColorMatrixColorFilter
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
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

    private lateinit var pagerAdapter: PagerAdapter
    private val vpYaseen by lazy {
        findViewById<ViewPager2>(R.id.vp_yaseen)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_main)

        this.pagerAdapter = PagerAdapter(this)
        vpYaseen.apply {
            this.adapter = pagerAdapter
            this.layoutDirection = ViewPager2.LAYOUT_DIRECTION_RTL
        }
    }

    fun goToFirstPage() {
        vpYaseen.setCurrentItem(0, true)
    }

    fun toggleNightMode() {
        println("QuickTag: MainActivity:toggleNightMode: Toggling night mode")
        // Cycle through all filter types
        pagerAdapter.filterType = when (pagerAdapter.filterType) {
            FilterType.NONE -> FilterType.INVERT
            FilterType.INVERT -> FilterType.SEPIA
            FilterType.SEPIA -> FilterType.WARM
            FilterType.WARM -> FilterType.NONE
        }
        supportFragmentManager.fragments.forEach { fragment ->
            if (fragment is PageFragment) {
                fragment.updateFilter(pagerAdapter.filterType)
            }
        }
    }
}

class PagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    // Default based on time
    var filterType: FilterType = FilterType.NONE

    init {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        // night invert, day none, evening sepia
        filterType = when (hour) {
            in 6..17 -> FilterType.NONE
            in 18..20 -> FilterType.SEPIA
            else -> FilterType.INVERT
        }
    }

    override fun createFragment(position: Int): Fragment {
        return PageFragment.newInstance(position, filterType)
    }

    override fun getItemCount(): Int = yaseen.size
}

class PageFragment() : Fragment() {
    private  var ivPage: ImageView? = null

    companion object {
        private const val KEY_PAGE = "page"
        private const val KEY_FILTER_TYPE = "filter_type"
        fun newInstance(page: Int, filterType: FilterType): PageFragment {
            return PageFragment().apply {
                arguments = Bundle().apply {
                    putInt(KEY_PAGE, page)
                    putString(KEY_FILTER_TYPE, filterType.name)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val position = arguments?.getInt(KEY_PAGE) ?: -1
        val filterType = arguments?.getString(KEY_FILTER_TYPE) ?: FilterType.NONE.name
        val layout = context?.let { ctx ->
            this.ivPage = ImageView(ctx).apply {
                // full screen
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                )
                scaleType = ImageView.ScaleType.FIT_XY
                scaleX = 1.1f

                // image
                setImageResource(yaseen[position])

                setOnClickListener {
                    (activity as MainActivity).toggleNightMode()
                    true
                }

            }
            updateFilter(FilterType.valueOf(filterType))

            val tvPagePosition = TextView(ctx).apply {
                text = "${position + 1}/${yaseen.size}"
            }

            val btn = if (position == yaseen.lastIndex) {
                Button(ctx).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
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
                addView(tvPagePosition)
                if (btn != null) {
                    addView(btn)
                }
            }
        }

        return layout
    }

    fun updateFilter(filterType: FilterType) {
        ivPage?.let { imageView ->
            when (filterType) {
                FilterType.INVERT -> {
                    imageView.invertColors()
                }

                FilterType.SEPIA -> {
                    imageView.applySepia()
                }

                FilterType.WARM -> {
                    imageView.applyWarmFilter()
                }

                FilterType.NONE -> {
                    imageView.clearColorFilter()
                }
            }
        }
    }

}

enum class FilterType {
    NONE,
    INVERT,
    SEPIA,
    WARM
}


fun ImageView.invertColors() {
    val invertMatrix = floatArrayOf(
        -1.0f, 0.0f, 0.0f, 0.0f, 255.0f,
        0.0f, -1.0f, 0.0f, 0.0f, 255.0f,
        0.0f, 0.0f, -1.0f, 0.0f, 255.0f,
        0.0f, 0.0f, 0.0f, 1.0f, 0.0f
    )
    val colorFilter = ColorMatrixColorFilter(invertMatrix)
    this.colorFilter = colorFilter
}

fun ImageView.applySepia() {
    val sepiaMatrix = floatArrayOf(
        0.393f, 0.769f, 0.189f, 0f, 0f,
        0.349f, 0.686f, 0.168f, 0f, 0f,
        0.272f, 0.534f, 0.131f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f
    )
    this.colorFilter = ColorMatrixColorFilter(sepiaMatrix)
}

fun ImageView.applyWarmFilter(intensity: Float = 0.3f) {
    val warmMatrix = floatArrayOf(
        1f, 0f, 0f, 0f, 0f,
        0f, 1f - intensity * 0.2f, 0f, 0f, 0f,
        0f, 0f, 1f - intensity, 0f, 0f,
        0f, 0f, 0f, 1f, 0f
    )
    this.colorFilter = ColorMatrixColorFilter(warmMatrix)
}