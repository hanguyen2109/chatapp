package com.example.mvvmfirst.views.content.message

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.mvvmfirst.R
import com.example.mvvmfirst.services.model.ChatModel
import com.example.mvvmfirst.views.adapters.MessageAdapter
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.fragment_messagee.*

class MessageFragment : Fragment() {
    var searchUserArray: ArrayList<ChatModel> = ArrayList()
    var messageChatModelArray: ArrayList<ChatModel> = ArrayList()
    var messageAdapter: MessageAdapter? = null
    private lateinit var chatViewModel: ChatViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_messagee, container, false)
        chatViewModel =
            ViewModelProviders.of(requireActivity()).get<ChatViewModel>(ChatViewModel::class.java)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //invisibility keybroad
        val inputMethodManager =
            requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireView().windowToken, 0)

        //animation for recycleView
        val itemAnimation = recyclerMessage.itemAnimator as SimpleItemAnimator
        itemAnimation.supportsChangeAnimations = false

        //create adapter
        messageAdapter = context?.let { MessageAdapter(it, messageChatModelArray) }
        recyclerMessage.adapter = messageAdapter
        recyclerMessage.layoutManager = LinearLayoutManager(activity)

        messageAdapter?.setOnItemClickListener(object : MessageAdapter.OnItemClickListener {
            override fun onItemClick(userModel: ChatModel?) {
                val fragmentTransaction = parentFragmentManager.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                var chatFragment = parentFragmentManager.findFragmentByTag("chat")
                if (chatFragment == null) {
                    chatFragment = ChatFragment()
                    fragmentTransaction.add(R.id.frameLayoutChat, chatFragment, "chat")
                } else {
                    fragmentTransaction.replace(R.id.frameLayoutChat, chatFragment, "chat")
                }
                var bundle = Bundle()
                if (userModel != null) {
                    bundle.putString("idUser", userModel.getUserModel()?.getUserId())
                }
                chatFragment.arguments = bundle
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
                editTextSearchUserChat.text = null
            }

            override fun onClick(v: View?) {
            }
        })

        imageButtonDelete.setOnClickListener {
            editTextSearchUserChat.text = null
            imageButtonDelete.visibility = View.GONE
        }

        editTextSearchUserChat.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                chatViewModel.searchUserChat(s.toString(), searchUserArray)
                if (TextUtils.isEmpty(s.toString())) {
                    imageButtonDelete.visibility = View.GONE
                } else {
                    imageButtonDelete.visibility = View.VISIBLE
                }
            }
        })

        //listen observer
        chatViewModel.getAll()
        chatViewModel.userInfoUserChat.observe(viewLifecycleOwner, Observer {
            messageChatModelArray.clear()
            if (it.isEmpty()) {
                imageNotSearchMessage.visibility = View.VISIBLE
            } else {
                chatViewModel.sortArray(it)
                val count = chatViewModel.getCountNotReadMessage(it)
                if (count > 9) {
                    chatViewModel.countNotRead. value = "9+"
                }else{
                    chatViewModel.countNotRead.value = "" + count
                }
                imageNotSearchMessage.visibility = View.GONE
            }
            if(messageChatModelArray.isEmpty()){
                messageChatModelArray.addAll(it)
            }
            messageAdapter?.notifyDataSetChanged()
        })

        chatViewModel.arraySearchUser.observe(viewLifecycleOwner, Observer {
            searchUserArray = it
        })
    }
}
