package com.example.mvvmfirst.views.content.friend

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.mvvmfirst.BuildConfig
import com.example.mvvmfirst.R
import com.example.mvvmfirst.services.model.AllUserModel
import com.example.mvvmfirst.views.adapters.FriendViewpagerAdapter
import com.example.mvvmfirst.views.content.friend.allfriend.AllFriendFragment
import com.example.mvvmfirst.views.content.friend.myfriend.MyFriendFragment
import com.example.mvvmfirst.views.content.friend.requestfriend.RequestFriendFragment
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_friend.*
import kotlinx.android.synthetic.main.tablayout_friend_fragment.view.*
import java.lang.String

class FriendFragment : Fragment() {
    private lateinit var friendViewModel: FriendViewModel
    var userAllArrayList: ArrayList<AllUserModel> = ArrayList()
    var tabRequestFriend: TabLayout.Tab? = null
    var tabFriend: TabLayout.Tab? = null
    var tabAllFriend: TabLayout.Tab? = null
    var countNotifi: TextView? = null
    var textRequest: View? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_friend, container, false)
        friendViewModel = ViewModelProviders.of(requireActivity())
            .get<FriendViewModel>(FriendViewModel::class.java)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //hide keybroad
        val inputMethodManager =
            requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireView().windowToken, 0)
        //add fragment in tablayout
        var friendViewpagerAdapter = FriendViewpagerAdapter(parentFragmentManager)
        friendViewpagerAdapter.AddFragment(
            MyFriendFragment(),
            String.valueOf(R.string.txt_title_friend)
        )
        friendViewpagerAdapter.AddFragment(
            AllFriendFragment(),
            String.valueOf(R.string.txt_all_friend_title)
        )
        friendViewpagerAdapter.AddFragment(
            RequestFriendFragment(),
            String.valueOf(R.string.txt_request_friend_title)
        )
        viewpagerFriend.adapter = friendViewpagerAdapter
        tablayoutFriend.setupWithViewPager(viewpagerFriend)
        val textTabFriend: View =
            LayoutInflater.from(context).inflate(R.layout.tablayout_friend_fragment, null)
        textTabFriend.textTitle.setText(R.string.txt_title_friend)
        tabFriend = tablayoutFriend.getTabAt(0)
        assert(tabFriend != null)
        tabFriend?.customView = textTabFriend
        val textTabAllFriend: View =
            LayoutInflater.from(context).inflate(R.layout.tablayout_friend_fragment, null)
        textTabAllFriend.textTitle.setText(R.string.txt_all_friend_title)
        tabAllFriend = tablayoutFriend.getTabAt(1)
        if (BuildConfig.DEBUG && tabAllFriend == null) {
            error("Assertion failed")
        }
        tabAllFriend?.customView = textTabAllFriend
        val textTabRequestFriend: View =
            LayoutInflater.from(context).inflate(R.layout.tablayout_friend_fragment, null)
        textTabRequestFriend.textTitle.setText(R.string.txt_request_friend_title    )
        tabRequestFriend = tablayoutFriend.getTabAt(2)
        if (BuildConfig.DEBUG && tabRequestFriend == null) {
            error("Assertion failed")
        }
        tabRequestFriend?.customView = textTabRequestFriend
        imageButtonDeleteSearchFriend.setOnClickListener {
            editTextSearchFriend.text = null
            imageButtonDeleteSearchFriend.visibility = View.INVISIBLE
        }
        editTextSearchFriend.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                friendViewModel.searchFriend(s.toString(), userAllArrayList)
                if (TextUtils.isEmpty(s.toString())) {
                    imageButtonDeleteSearchFriend.visibility = View.GONE
                } else {
                    imageButtonDeleteSearchFriend.visibility = View.VISIBLE
                }
            }

        })

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        friendViewModel.getUser()
        friendViewModel.getUserArray.observe(viewLifecycleOwner,
            androidx.lifecycle.Observer {
                userAllArrayList = it
            })
        friendViewModel.countRequest.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            val viewRequest: View? = tablayoutFriend.getTabAt(2)?.customView
            val count = viewRequest?.findViewById<View>(R.id.textNotifi) as TextView
            if (it == "0") count.visibility = View.INVISIBLE
            else {
                count.visibility = View.VISIBLE
                count.text = it
            }
        })
    }
}
