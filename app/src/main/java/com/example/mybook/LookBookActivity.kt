package com.example.mybook

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.koushikdutta.ion.Ion
import kotlinx.android.synthetic.main.activity_look_book.*

class LookBookActivity : AppCompatActivity() {

    lateinit var searchedBook:Book
    lateinit var allPost:ArrayList<MyFeed>
    lateinit var my:User
    lateinit var want:ArrayList<MyFeed>
    lateinit var allwant:ArrayList<MyFeed>
    var postlist:ArrayList<MyFeed> = ArrayList()
    lateinit var padapter:MyFeedAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_look_book)
        getdata()
    }
    //우선 테스트로 출력해놓음
    //가져온 책 정보로 post DB에서 동일한 isbn인 책 포스트만 가져오기
    fun getdata(){
        searchedBook= intent.extras.getSerializable("sbook") as Book//현재 책 정보
        allPost = getIntent().getParcelableArrayListExtra("ALLPOST")//모든 피드의 정보
        my = getIntent().getParcelableExtra("MY")//나의 정보
        want = getIntent().getParcelableArrayListExtra("WANT")//나의 찜 정보
        allwant = getIntent().getParcelableArrayListExtra("ALLWANT")
        setlayout()
    }
    fun setlayout(){
        Ion.with(lb_img).load(searchedBook.imageLink)
        lb_title.text=searchedBook.title
        lb_author.text=searchedBook.author
        lb_publish.text=searchedBook.publisher

        setsearchre.text="\""+searchedBook.title+"\" 일치하는 POST 검색 결과"
        val layoutManager= LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        lb_rview.layoutManager=layoutManager
        getpost()
    }
    fun getpost(){
        for(i in 0 until allPost.size){
            if(allPost[i].isbn==searchedBook.isbn){
                //검색한 책의 isbn과 post DB에 있는 isbn이 일치하면 리스트에 붙이기
                 postlist.add(allPost[i])
                //들어가는 거 확인함
            }
        }
        padapter= MyFeedAdapter(postlist)
        lb_rview.adapter=padapter
    }

    fun addwant(view: View?){
        //내가 찜한 목록에 있는지 비교
        var check = true
        for(i in 0 until want.size){
            if(want[i].isbn == searchedBook.isbn ){
                check = false//같은 책이 이미 있음
                Toast.makeText(this,"이미 찜한 책입니다!",Toast.LENGTH_SHORT).show()
            }
        }
        if(check){//같은 책이 없음
            Toast.makeText(this,"♥",Toast.LENGTH_SHORT).show()
            var db = FirebaseFirestore.getInstance()

            var added:Map<String,String>
            added = hashMapOf()
            added.put("author",searchedBook.author)
            added.put("bname",searchedBook.title)
            added.put("imageLink",searchedBook.imageLink)
            added.put("isbn",searchedBook.isbn)
            added.put("publisher",searchedBook.publisher)
            added.put("u_no",my.no.toString())

            allwant.add(MyFeed(my.no,searchedBook.link,searchedBook.title,searchedBook.author,searchedBook.publisher, "","",searchedBook.imageLink,searchedBook.isbn,""))
            want.add(MyFeed(my.no,searchedBook.link,searchedBook.title,searchedBook.author,searchedBook.publisher, "","",searchedBook.imageLink,searchedBook.isbn,""))

            db.collection("want").add(added).addOnSuccessListener {
                Log.d("WANT추가 성공","WANT 추가 ID : "+it.id)
            }.addOnFailureListener {
                Log.e("WANT추가 실패","WANT 추가 실패 :"+it)
            }


          //  var mypage=mypage()
           // var bundle:Bundle=Bundle()
           // bundle.putParcelableArrayList("WANT_DATA",want)
            mypage.send(want)
          //  Log.d("번들비지라마",bundle.toString())
           // mypage.setArguments(bundle)

        }
    }
}