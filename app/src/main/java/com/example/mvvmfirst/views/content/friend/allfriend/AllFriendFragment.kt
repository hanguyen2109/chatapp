package com.example.mvvmfirst.views.content.friend.allfriend

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.mvvmfirst.R
import com.example.mvvmfirst.services.model.AllUserModel
import com.example.mvvmfirst.views.adapters.AllFriendAdapter
import com.example.mvvmfirst.views.adapters.MessageAdapter
import com.example.mvvmfirst.views.content.friend.DialogFriendFragment
import com.example.mvvmfirst.views.content.friend.FriendViewModel
import com.example.mvvmfirst.views.content.message.ChatFragment
import kotlinx.android.synthetic.main.fragment_allfriend.*
import java.util.*
import kotlin.collections.ArrayList

class AllFriendFragment : Fragment() {
    private var allFriendAdapter: AllFriendAdapter? = null
    private lateinit var friendViewModel: FriendViewModel
    private var getFriendList: ArrayList<AllUserModel> = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_allfriend, container, false)
        friendViewModel = ViewModelProviders.of(requireActivity())
            .get<FriendViewModel>(FriendViewModel::class.java)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val itemAnimator: SimpleItemAnimator =
            (recyclerAllFriend.itemAnimator as SimpleItemAnimator)
        itemAnimator.supportsChangeAnimations = false
        allFriendAdapter = AllFriendAdapter((requireActivity()))
        recyclerAllFriend.adapter = allFriendAdapter
        recyclerAllFriend.layoutManager = LinearLayoutManager(activity)
        allFriendAdapter?.setOnItemClickListener(object : AllFriendAdapter.OnItemClickListener {
            override fun onItemClick(userModel: AllUserModel) {
                if (userModel.userType == "friend") {
                    var fragmentTransaction = parentFragmentManager.beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    val chatFragment = ChatFragment()
                    val bundle = Bundle()
                    bundle.putString("idUser", userModel.userID)
                    chatFragment.arguments = bundle
                    fragmentTransaction.add(R.id.frameLayoutChat, chatFragment, null)
                    fragmentTransaction.commit()
                }else{
                    val dialogFriendFragment = DialogFriendFragment()
                    dialogFriendFragment.show(parentFragmentManager, null)
                }
            }

        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        friendViewModel.getUser()
        friendViewModel.allUserArray.observe(viewLifecycleOwner, Observer {
            var allUserModelList: ArrayList<AllUserModel> = ArrayList()
            allUserModelList.clear()
            getFriendList.clear()
            allUserModelList = it
            for (i in allUserModelList.indices) {
                if (allUserModelList[i].userType == "friendRequest") {
                    Log.d("s", "s")
                } else {
                    getFriendList.add(allUserModelList[i])
                }

                Log.d("hanguyen", allUserModelList[i].userID)
            }
            if (allUserModelList.isEmpty()) {
                imageNoResultAllFriend.visibility = View.VISIBLE
            } else {
                imageNoResultAllFriend.visibility = View.GONE
            }
            allFriendAdapter?.submitList(getFriendList)
//            (recyclerAllFriend.adapter as AllFriendAdapter).notifyDataSetChanged()
        })
    }
}



