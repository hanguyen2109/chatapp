package com.example.mvvmfirst.views.content

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.mvvmfirst.R
import com.example.mvvmfirst.services.model.ChatModel
import com.example.mvvmfirst.views.adapters.ViewPagerAdapter
import com.example.mvvmfirst.views.content.friend.FriendViewModel
import com.example.mvvmfirst.views.content.message.ChatViewModel
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_messagee.*


class MainFragment : Fragment() {
    //    private var tabLayout: TabLayout? = null
//    var viewPager: ViewPager? = null
    var currentTab: Int = 0

    //    var friendViewModel: FriendViewModel? = null
    var chatViewModel: ChatViewModel? = null

    //    var chatViewModel: ChatViewModel? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        chatViewModel =
            ViewModelProviders.of(this, ViewModelProviderFactory())
                .get<ChatViewModel>(ChatViewModel::class.java)
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //add tablayout
        tabLayoutFragmentMain.addTab(
            tabLayoutFragmentMain.newTab().setCustomView(R.layout.tablayout_main_message)
        )
        tabLayoutFragmentMain.addTab(
            tabLayoutFragmentMain.newTab().setCustomView(R.layout.tablayout_main_friend)
        )
        tabLayoutFragmentMain.addTab(
            tabLayoutFragmentMain.newTab().setCustomView(R.layout.tablayout_main_profile)
        )
        tabLayoutFragmentMain.tabGravity = TabLayout.GRAVITY_FILL
        var viewPagerAdapter = parentFragmentManager.let {
            context?.let { it1 ->
                ViewPagerAdapter(it1, it, tabLayoutFragmentMain.tabCount)
            }
        }
        viewPager?.adapter = viewPagerAdapter
        viewPager!!.addOnPageChangeListener(
            TabLayout.TabLayoutOnPageChangeListener(
                tabLayoutFragmentMain
            )
        )
        tabLayoutFragmentMain.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                val viewTab = tab?.customView
                viewTab?.findViewById<TextView>(R.id.textTab)
                    ?.setTextColor(ContextCompat.getColor(context!!, R.color.line))
                viewTab?.findViewById<ImageView>(R.id.imageTab)?.colorFilter =
                    PorterDuffColorFilter(
                        ContextCompat.getColor(context!!, R.color.line),
                        PorterDuff.Mode.SRC_ATOP
                    )
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager.currentItem = tab!!.position
                currentTab = tab.position
                val viewTab = tab.customView
                viewTab?.findViewById<TextView>(R.id.textTab)
                    ?.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
                viewTab?.findViewById<ImageView>(R.id.imageTab)?.colorFilter =
                    PorterDuffColorFilter(
                        ContextCompat.getColor(context!!, R.color.blue),
                        PorterDuff.Mode.SRC_ATOP
                    )
            }
        })

        chatViewModel?.getAll()
        chatViewModel?.userInfoUserChat?.observe(viewLifecycleOwner, Observer {
            val viewRequest: View? =
                tabLayoutFragmentMain.getTabAt(0)?.customView
            val count1 = viewRequest?.findViewById<View>(R.id.notificationTab) as TextView
//                chatViewModel?.sortArray(it)
            val count = chatViewModel?.getCountNotReadMessage(it)
            if (count == 0) {
                count1.visibility = View.GONE
            } else {

                count1.visibility = View.VISIBLE
                count1.text = count.toString()
            }

        })


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }
}
