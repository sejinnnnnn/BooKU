package com.example.mybook

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class StartLoadingActivity : AppCompatActivity() {

    lateinit var db: FirebaseFirestore//데이터베이스의 정보를 보여줌
    lateinit var user:ArrayList<User>//사용자의 정보를 담을 데이터베이스
    lateinit var feed:ArrayList<MyFeed>
    lateinit var letter:ArrayList<MyFeed>
    lateinit var category:ArrayList<MyCatg>
    lateinit var allwant:ArrayList<MyFeed>

    var check=0//2가되면 인텐트 바꿈
    var handler:Handler = Handler(){
        when(it.what){
            USER_STOP->{
                check+=1
                true
            }
            FEED_STOP->{
                check+=1
                true
            }
            LETTER_STOP->{
                check+=1
                true
            }
            CATG_STOP->{
                check+=1
                true
            }
            else->{
                false
            }
        }
        if(check>=4){
            nextActivity()
        }
        true
    }

    val USER_STOP:Int = 1111
    val FEED_STOP:Int = 2222
    val LETTER_STOP:Int = 3333
    val CATG_STOP:Int = 4444
    val WANT_STOP:Int = 5555

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_loading)
        init()
    }

    fun init(){
        db = FirebaseFirestore.getInstance()
        user = arrayListOf()
        feed = arrayListOf()
        letter = arrayListOf()
        category = arrayListOf()
        allwant = arrayListOf()
      var userThread = Thread(object:Runnable{//유저정보 스레드
          override fun run() {
             // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                readData()
                readFeed()
                readLetter()
                readCategory()
                readWant()
          }
      })
        userThread.start()

    }

    fun nextActivity(){
        var intent = Intent(this,MainActivity::class.java)
        intent.putParcelableArrayListExtra("USER_DATA",user)
        intent.putParcelableArrayListExtra("FEED_DATA",feed)
        intent.putParcelableArrayListExtra("LETTER_DATA",letter)
        intent.putParcelableArrayListExtra("CATEGORY",category)
        intent.putParcelableArrayListExtra("ALLWANT",allwant)
        startActivity(intent)
    }

    fun readData(){//데이터베이스의 정보를 불러옴
        db.collection("user").get().addOnCompleteListener {
            if(it.isSuccessful){//성공적이면
                for(document in it.result!!){
                    user.add(User(document.data["u_no"].toString().toInt(),document.data["u_id"].toString(),document.data["u_pass"].toString(),document.data["u_name"].toString(), Uri.parse(document.data["uri"].toString()),document.data["catg"].toString()))
                }
                //모든 데이터를 다 읽었을 때
                handler.sendEmptyMessage(USER_STOP)
            }else{//실패했으면
                Log.w("ERROR","Error getting documents"+it.exception)
            }
        }
    }

    fun readFeed(){//피드데이터 부름
        db.collection("post").get().addOnCompleteListener {
            if(it.isSuccessful){//성공적이면
               for(document in it.result!!){
                   Log.d("ORDER","READFeedStart")
                   feed.add(MyFeed(document.data["u_no"].toString().toInt(),document.data["uri"].toString(),document.data["bname"].toString(),document.data["author"].toString(),document.data["publisher"].toString(),document.data["content"].toString(),document.data["like_no"].toString(),document.data["imageLink"].toString(),document.data["isbn"].toString(),document.data["date"].toString()))
               }
                Log.d("ORDER","OUTFeed")
                handler.sendEmptyMessage(FEED_STOP)
            }else{//실패했으면
                Log.w("ERROR","Error getting documents"+it.exception)
            }
        }
    }

    fun readLetter(){//글귀를 불러옴
        db.collection("letter").get().addOnCompleteListener {
            if(it.isSuccessful){
                for(document in it.result!!){
                    letter.add(MyFeed(-1,"",document.data["bname"].toString(),"","",document.data["content"].toString(),"","","",""))
                }
                handler.sendEmptyMessage(LETTER_STOP)
            }
        }
    }

    fun readCategory() {//카테고리를 불러옴
        db.collection("catg").get().addOnCompleteListener {
            if (it.isSuccessful) {
                for (document in it.result!!) {
                    category.add(MyCatg(document.data["code"].toString(), document.data["name"].toString()))
                }
                handler.sendEmptyMessage(CATG_STOP)
            } else {//실패했으면
                Log.w("ERROR", "Error getting documents" + it.exception)
            }
        }
    }

    fun readWant(){//원하는 리스트를 가져옴
        db.collection("want").get().addOnCompleteListener {
            if(it.isSuccessful){
                for(document in it.result!!){
                    allwant.add(MyFeed(document.data["u_no"].toString().toInt(),"",document.data["bname"].toString(),document.data["author"].toString(),document.data["publisher"].toString(),"","",document.data["imageLink"].toString(),document.data["isbn"].toString(),""))
                }
                handler.sendEmptyMessage(WANT_STOP)
            }else{
                Log.d("ERROR","Error getting documents" + it.exception)
            }
        }
    }
}
