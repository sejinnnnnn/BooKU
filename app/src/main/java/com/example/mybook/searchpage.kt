package com.example.mybook

import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.speech.RecognizerIntent
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_searchpage.*
import java.util.*
import kotlin.collections.ArrayList

class searchpage : Fragment() {

    var searchlist:ArrayList<Book> = ArrayList()
    lateinit var post:ArrayList<MyFeed>
    lateinit var fadapter:sbookAdapter
    lateinit var searchingbook:Book//찾는 책
    lateinit var want:ArrayList<MyFeed>
    lateinit var allwant:ArrayList<MyFeed>
    lateinit var my:User
    val REQ_CODE_SPEECH_INPUT = 5555

    companion object{
        fun makeSearch(post:ArrayList<MyFeed>,u:User,w:ArrayList<MyFeed>,all:ArrayList<MyFeed>):searchpage{
            val searchfrag=searchpage()
            searchfrag.post = post
            searchfrag.my = u
            searchfrag.want = w
            searchfrag.allwant = all
            return searchfrag
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_searchpage, container, false)
        return view
    }
    val clickFun:(Book) -> Unit = {//하나의 책을 클릭했을 때
        searchingbook=Book(it.title,it.author,it.price,it.discount,it.publisher,it.pubdate,it.isbn,it.link,it.description,it.imageLink)

        var intent= Intent(this.context,LookBookActivity::class.java)
        intent.putExtra("sbook",searchingbook)
        intent.putParcelableArrayListExtra("ALLPOST",post)//모든 포스트의 정보를 넘겨줌
        intent.putExtra("MY",my)
        intent.putParcelableArrayListExtra("WANT",want)//내가 찜한 목록
        intent.putParcelableArrayListExtra("ALLWANT",allwant)
        startActivity(intent)
    }

    fun init(){
        fadapter= sbookAdapter(searchlist,clickFun)
        initlay()
    }
    fun initlay(){
        //여기 context 맞음?
        val lm=LinearLayoutManager(this.context,LinearLayoutManager.VERTICAL,false)
        searchview.layoutManager=lm
        searchview.adapter=fadapter
    }
    val handler = Handler() {
        fadapter= sbookAdapter(searchlist,clickFun)
        initlay()
        true
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        voice_search.setOnClickListener {//음성검색
            promptSpeechInput()
        }
        Log.d("프래그먼트","서치페이지")
        sbtn.setOnClickListener {//클릭했을 때
            searchlist.clear()
            var search=search_book.text.toString()

            var bl=""
            var bt=""
            var ba=""
            var bp=""

            val run = Runnable {
                lateinit var apiReturnList:MutableList<Book>
                //나중에 수정
                apiReturnList=BookAPI.calladvAPI(d_titl = search)

                for (book in apiReturnList) {
                    bl=book.imageLink
                    Log.d("이미지링크",bl)

                    bt=book.title
                    bt=bt.replace("<b>","")
                    bt=bt.replace("</b>","")

                    ba=book.author
                    ba=ba.replace("<b>","")
                    ba=ba.replace("</b>","")

                    bp=book.publisher
                    bp=bp.replace("<b>","")
                    bp=bp.replace("</b>","")

                    searchlist.add(Book(bt,ba,book.price,book.discount,bp,book.pubdate,book.isbn,book.link,book.description,book.imageLink))
                }
                val msg = handler.obtainMessage()
                handler.sendMessage(msg)
            }
            val th = Thread(run)
            th.start()
            init()
        }
    }
    fun promptSpeechInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say Something!")

        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT)
        } catch (a: ActivityNotFoundException) {
           Log.e("ERROR","마이크 에러")
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {//마이크
            REQ_CODE_SPEECH_INPUT -> {
                if (resultCode == RESULT_OK && data != null) {
                    //이벤트 처리
                    val result : ArrayList<String> = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    search_book.setText(result[0])
                }
            }
        }
    }

}