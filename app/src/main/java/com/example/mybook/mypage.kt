package com.example.mybook


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_mypage.*


class mypage : Fragment() {


    lateinit var my:User
    lateinit var myBook:ArrayList<MyFeed>//나의 책 목록
    lateinit var adapter:sbookAdapter//어댑터
    val SELECT_IMAGE = 9999
    lateinit var db:FirebaseFirestore
    lateinit var myF:mypage
    lateinit var myBookList:ArrayList<Book>
    var check = false

    companion object{
        val myFrag = mypage()
        fun makeProfile(user:User,book:ArrayList<MyFeed>):mypage{
            myFrag.myF = mypage()
            myFrag.my = user
            myFrag.myBook = book
            return myFrag
        }

        fun send(item:ArrayList<MyFeed>){
            myFrag.myBook = item
            Log.d("번들",myFrag.myBook.size.toString())
            myFrag.myBookList = arrayListOf()
            myFrag.myBookList.clear()
            for(i in 0 until item.size){
                myFrag.myBookList.add(Book(myFrag.myBook[i].bname,myFrag.myBook[i].author,0,0,myFrag.myBook[i].publisher,"null","null","null","null",myFrag.myBook[i].imageLink))
            }
            Log.d("호출0",myFrag.myBook.size.toString())
            Log.d("호출1",myFrag.myBookList.size.toString())
            myFrag.check = true
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mypage, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        db = FirebaseFirestore.getInstance()//프로필사진 바꿨을 때 업데이트 하기 위해
        Log.d("호출","호출")
        makeMyInfo()
        makeMyList()
    }

    fun makeMyInfo(){
        my_user_name.text = myFrag.my.name//이름 설정
        my_photo.setImageURI(myFrag.my.uri)
        user_book.text = myFrag.my.name+"님이 찜한 책"

        my_photo.setOnClickListener {
            //프로필 사진 바꾸기
            var intent = Intent(Intent.ACTION_PICK)
            intent.type=android.provider.MediaStore.Images.Media.CONTENT_TYPE
            intent.data = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            startActivityForResult(intent,SELECT_IMAGE)
        }
    }

    fun makeMyList(){//나의 책정보 리스트 만들기
        var b:Book
        b = Book("","",0,0,"","","","","","")
        if(check){
            b = myFrag.myBookList[myFrag.myBookList.size-1]
        }


        myBookList = arrayListOf()
        myBookList.clear()
        if(check){
             myBookList.add(b)
        }

        Log.d("책SIZE",myFrag.myBook.size.toString())
        for(i in 0 until myBook.size){
            myBookList.add(Book(myBook[i].bname,myBook[i].author,0,0,myBook[i].publisher,"null","null","null","null",myBook[i].imageLink))
        }
        Log.d("책SIZE",myBookList.size.toString())
        adapter=sbookAdapter(myBookList,nothing)//나의 책 정보가 담긴 책 리스트
        val layoutManager= LinearLayoutManager(activity, LinearLayoutManager.VERTICAL,false)
        readList.layoutManager=layoutManager
        readList.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    val nothing:(Book) -> Unit = {//아무것도 아님
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {//프로필사진 바꾸는 인텐트
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == SELECT_IMAGE){
            if(resultCode == Activity.RESULT_OK){
                Log.d("MYURI",my.uri.toString())
                var uri = data!!.data as Uri
                my.uri = uri
                Log.d("MYURI",uri.toString())
                my_photo.setImageURI(my.uri)
                updateProfile()
            }
        }
    }
    fun findProfile(){

    }

    fun updateProfile(){
        var input:MutableMap<String,String>
        input = mutableMapOf()
        input.put("u_no",my.no.toString())
        input.put("u_id",my.eEmail)
        input.put("u_pass",my.pass)
        input.put("u_name",my.name)
        input.put("uri",my.uri.toString())
        input.put("catg",my.catg)

        var document_name="" as String
        db.collection("user").get()
            .addOnCompleteListener {
                if(it.isSuccessful){
                    for(document in it.result!!){
                        if(document.data["u_no"].toString() == my.no.toString()){//만약 내 정보가 같으면
                            document_name = document.id
                            break
                        }
                    }
                    db.collection("user").document(document_name).set(input as MutableMap<String, Any>)
                        .addOnSuccessListener {
                            Log.d("UPDATE","성공")
                        }
                        .addOnFailureListener {
                            Log.e("UPDATE","실패")
                        }
                }
            }
    }

}
