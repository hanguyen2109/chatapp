package com.example.mvvmfirst.views.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class FriendViewpagerAdapter(fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private val arrayFragment: ArrayList<Fragment> = ArrayList()
    private val arrayTitle: ArrayList<String> = ArrayList()
    override fun getItem(position: Int): Fragment {
        return arrayFragment[position]
    }

    override fun getCount(): Int {
        return arrayFragment.size
    }
    fun AddFragment(
        fragment: Fragment?,
        title: String?
    ) {
        arrayFragment.add(fragment!!)
        arrayTitle.add(title!!)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return arrayTitle[position] //tra ve title cua fragment
    }
}
