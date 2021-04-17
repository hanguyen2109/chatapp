package com.example.mvvmfirst.views.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.mvvmfirst.views.content.friend.FriendFragment
import com.example.mvvmfirst.views.content.message.MessageFragment
import com.example.mvvmfirst.views.content.profile.ProfileFragment

class ViewPagerAdapter(private val myContext: Context, fm: FragmentManager, var totalTabs: Int) :
    FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                MessageFragment()
            }
            1 -> {
                FriendFragment()
            }
            else -> {
                ProfileFragment()
            }
        }

    }

    override fun getCount(): Int {
        return totalTabs
    }
}
