package com.example.mvvmfirst.views.content.friend.requestfriend

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.mvvmfirst.R
import com.example.mvvmfirst.services.model.AllUserModel
import com.example.mvvmfirst.views.adapters.AllFriendAdapter
import com.example.mvvmfirst.views.adapters.RequestFriendAdapter
import com.example.mvvmfirst.views.content.friend.DialogFriendFragment
import com.example.mvvmfirst.views.content.friend.FriendViewModel
import kotlinx.android.synthetic.main.fragment_allfriend.*
import kotlinx.android.synthetic.main.fragment_requestfriend.*

class RequestFriendFragment: Fragment() {

    private var allUserRequestArray: ArrayList<AllUserModel> = ArrayList()//array user request
    private var requestFriendAdapter: RequestFriendAdapter? = null
    private lateinit var friendViewModel: FriendViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_requestfriend, container, false)
        friendViewModel = ViewModelProviders.of(requireActivity())
            .get<FriendViewModel>(FriendViewModel::class.java)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val itemAnimator: SimpleItemAnimator =
            (recyclerRequestFriend.itemAnimator as SimpleItemAnimator)
        itemAnimator.supportsChangeAnimations = false
        requestFriendAdapter = RequestFriendAdapter((requireActivity()))
        recyclerRequestFriend.adapter = requestFriendAdapter
        recyclerRequestFriend.layoutManager = LinearLayoutManager(activity)
        requestFriendAdapter?.setOnItemClickListener(object: RequestFriendAdapter.OnItemClickListener{
            override fun onItemClick(userModel: AllUserModel) {
                val dialog = DialogFriendFragment()
                dialog.show(parentFragmentManager, null)
            }

        })
        friendViewModel.getUser()
        friendViewModel.allUserArray.observe(viewLifecycleOwner, Observer {
            var allUser: ArrayList<AllUserModel> = ArrayList()
            allUser.clear()
            allUserRequestArray.clear()
            allUser = it
            for (i in allUser.indices) {
                if (allUser[i].userType == "friend"|| allUser[i].userType =="friendRequest") {
                    allUserRequestArray.add(allUser[i])
                }
            }
//            for(i in allUserRequestArray.indices){
//                Log.d("cuong", allUserRequestArray[i].userID)
//            }
            if(allUserRequestArray.isEmpty()){
                imageNoResultRequestFriend.visibility = View.VISIBLE
            }else{
                imageNoResultRequestFriend.visibility = View.GONE
            }
            requestFriendAdapter?.submitList(allUserRequestArray)
//            (recyclerRequestFriend.adapter as RequestFriendAdapter).notifyDataSetChanged()
        })
    }
}
