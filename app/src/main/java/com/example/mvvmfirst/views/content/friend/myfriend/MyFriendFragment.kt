package com.example.mvvmfirst.views.content.friend.myfriend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mvvmfirst.R
import com.example.mvvmfirst.services.model.AllUserModel
import com.example.mvvmfirst.views.adapters.MyfriendsAdapter
import com.example.mvvmfirst.views.content.friend.FriendViewModel
import com.example.mvvmfirst.views.content.message.ChatFragment
import kotlinx.android.synthetic.main.fragment_myfriend.*

class MyFriendFragment : Fragment() {

    lateinit var myFriendAdapter: MyfriendsAdapter
    lateinit private var friendViewModel: FriendViewModel
    private var getFriendList: ArrayList<AllUserModel> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_myfriend, container, false)
        friendViewModel = ViewModelProviders.of(requireActivity())
            .get<FriendViewModel>(FriendViewModel::class.java)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //add adapter
        myFriendAdapter = MyfriendsAdapter(requireContext())
        recyclerViewMyFriend.adapter = myFriendAdapter
        recyclerViewMyFriend.layoutManager = LinearLayoutManager(activity)
        myFriendAdapter.setOnItemClickListener(object : MyfriendsAdapter.OnItemClickListener {
            override fun onItemClick(allUserModel: AllUserModel?) {
//                Toast.makeText(requireActivity(), "click", Toast.LENGTH_SHORT).show()
                val fragmentTransaction = parentFragmentManager.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                var chatFragment = parentFragmentManager.findFragmentByTag("fragmentChat")
                if(chatFragment == null){
                    chatFragment = ChatFragment()
                    fragmentTransaction.add(R.id.frameLayoutChat, chatFragment, "fragmentChat")
                }else{
                    fragmentTransaction.replace(R.id.frameLayoutChat, chatFragment, "fragment")
                }
                val bundle = Bundle()
                if (allUserModel != null) {
                    bundle.putString("idUser", allUserModel.userID)
                    chatFragment.arguments = bundle
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.commit()
                }
            }

            override fun onClick(v: View?) {
            }

        })
        friendViewModel.getUser()
        friendViewModel.allUserArray.observe(viewLifecycleOwner, Observer {
            var allUserModelList: ArrayList<AllUserModel> = ArrayList()
            allUserModelList.clear()
            getFriendList.clear()
            allUserModelList = it
            for (i in allUserModelList.indices) {
                if (allUserModelList[i].userType == "friend") {
                    getFriendList.add(it[i])
                }
            }
            if (getFriendList.isEmpty()) {
                imageNoResultMyFriend.visibility = View.VISIBLE
            } else {
                imageNoResultMyFriend.visibility = View.GONE
            }
            myFriendAdapter.submitList(getFriendList)
        })
    }
}
