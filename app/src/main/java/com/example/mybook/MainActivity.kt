package com.example.mybook



import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import com.kakao.util.exception.KakaoException
import com.kakao.util.helper.Utility.getPackageInfo
import kotlinx.android.synthetic.main.activity_main.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import com.kakao.usermgmt.callback.MeResponseCallback as MeResponseCallback1
import java.lang.Thread as Thread1

class MainActivity : AppCompatActivity() {//로그인 액티비티

    var id:String=""//사용자의 이메일
    var pass:String=""//사용자의 패스워드
    var name:String=""//사용자의 이름
    var userNo:Int = -1
    lateinit var user:ArrayList<User>
    lateinit var user_map:MutableMap<Int,User>
    lateinit var feed:ArrayList<MyFeed>
    lateinit var letter:ArrayList<MyFeed>//글귀
    lateinit var category:ArrayList<MyCatg>
    lateinit var allwant:ArrayList<MyFeed>
    lateinit var db: FirebaseFirestore
    lateinit var tmp:User
    var CODE = 1234

    //카카오 콜백
    lateinit var kakaoCallback: SessionCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //해시키 얻는 부분
        val info = getPackageInfo(this, PackageManager.GET_SIGNATURES)
        if(info == null){
            Toast.makeText(this,"안됨", Toast.LENGTH_SHORT).show()
            Log.d("thisd","안됨")
        }

        for(signature in info!!.signatures){
            try {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d("HASH_KEY", Base64.encodeToString(md.digest(), Base64.NO_WRAP))
            }catch (e: NoSuchAlgorithmException){
                Log.w("해시키오류","Unable to get MessageDigest. signature =$signature",e)
            }
        }
        //여기까지 카카오부분
        init()
        //카카오 콜백(세션)
        kakaoCallback = SessionCallback()
        Session.getCurrentSession().addCallback(kakaoCallback)
        Log.d("KAKAOSESSION","SESSION")
        Session.getCurrentSession().checkAndImplicitOpen()
        Log.d("KAKAOSESSION","SESSION")
    }

    fun init(){//파이어베이스의 값을 가져옴
        Log.d("ORDER","init")
        user_map = mutableMapOf()//키값은 인덱스 번호 데이터는 유저데이터
        user = getIntent().getParcelableArrayListExtra("USER_DATA")
        feed = getIntent().getParcelableArrayListExtra("FEED_DATA")//피드의 정볼르 가져옴
        letter = getIntent().getParcelableArrayListExtra("LETTER_DATA")//글귀의 정보를 가져옴
        category = getIntent().getParcelableArrayListExtra("CATEGORY")
        allwant = getIntent().getParcelableArrayListExtra("ALLWANT")

        for(i in 0 until user.size){
            user_map.put(user[i].no,user[i])
        }

        enroll_page.setOnClickListener {
            clickJoin()
        }
    }

    fun clickLogin(view:View?){//로그인 버튼을 눌렀을 때
        id = user_id.text.toString()//사용자가 입력한 아이디
        pass = user_pass.text.toString()//사용자가 입력한 비밀번호
        Log.d("USER_SIZE",user.size.toString())
        for(i in 0..user.size-1){
            if(id == user[i].eEmail && pass == user[i].pass){//만약 사용자의 정보가 있으면
                Toast.makeText(this,"로그인 중...", Toast.LENGTH_SHORT).show()
                userNo = user[i].no
                makeFeed()
            }
        }
        //로그인 실패 했을 때 회원가입 액티비티로 넘어가야됨
    }

    fun clickJoin(){//회원가입 버튼 눌렀을 때 액티비티전환
        var intent = Intent(this, EnrollActivity::class.java)
        intent.putExtra("MY",User(-1,id,pass,name,Uri.parse(""),""))
        intent.putExtra("USER",user)
        intent.putParcelableArrayListExtra("CATEGORY",category)
        startActivityForResult(intent,CODE)
    }

    fun updateUser(id:String, pass:String, name:String){//정보가 없을 경우 추가해줌
        var input:MutableMap<String,Object>
        input = mutableMapOf()
        input.put("u_no",(user.size) as Object)
        input.put("u_id",id as Object)
        input.put("u_pass",pass as Object)
        input.put("u_name",name as Object)
        input.put("uri",Uri.parse(getDrawable(R.drawable.background).toString()).toString() as Object)
        input.put("catg", tmp.catg as Object)

        user.add(User(user.size,id,pass,name,Uri.parse(getDrawable(R.drawable.background).toString()),tmp.catg))
        user_map.put(user.size-1,user[user.size-1])
        userNo = user.size-1

        db = FirebaseFirestore.getInstance()
        //디비에 추가
        db.collection("user").add(input as HashMap<String, Any>).addOnSuccessListener {
            Log.d("USER UDATE SUCCESSFUL","Document Snapshot added with ID : "+it.id)
        }.addOnFailureListener {
            Log.e("USER UDATE FAILURE","Error adding document :"+it)
        }
        makeFeed()
    }

    fun kakaoLogin(){
        //카카오 등록된 정보가 없으면
        Log.d("KAKAO","카카오톡 로그인")
        var check = true
        for(i in 0..user.size-1){
            if(id== user[i].eEmail&& user[i]!!.pass=="kakaoUser"){
                Toast.makeText(this,"카카오 로그인 중...",Toast.LENGTH_SHORT).show()
                userNo = user[i].no
                makeFeed()
                return
            }
        }
        if(check){
            Log.d("KAKAO","카카오톡 업데이트")
            clickJoin()
        }

    }

    fun makeFeed(){//나에게 맞춰 피드를 만들어줌
        var myBook:ArrayList<MyFeed>//나의 책 목록
        var myFeedArr:ArrayList<MyFeed>

        var want:ArrayList<MyFeed>//내가 찜한 책 목록

        myBook = arrayListOf()
        myFeedArr = arrayListOf()
        want = arrayListOf()

        //나의 카테고리정보
        var catg = user_map[userNo]!!.catg//현재 사용자의 카테고리 정보
        var catg_arr = catg.split(" ")//공백으로 스플릿

        for(i in 0 until feed.size){//나의 책 목록을 만든다
            if(feed[i].no == userNo){
                myBook.add(feed[i])//나의 책 목록
            }
        }

        for(i in 0 until user.size){//내 피드 정보를 만든다
            var feedNo = -1
            var u_catg = user_map[user[i].no]!!.catg
            var u_catg_arr = u_catg.split(" ")//다른 사람의 카테고리
            var same_catg = false
            brakPoint@for(j in 0 until u_catg_arr.size){
                for(k in 0 until catg_arr.size){
                    if(u_catg_arr[j] == catg_arr[k] && catg_arr[k]!=""){//같은 카테고리가 있으면 보여줌
                        feedNo = user[i].no//나와 비슷한 카테고리를 가진 사람의 인덱스 번호
                        same_catg = true
                        break@brakPoint
                    }
                }
            }
            if(same_catg){
                for(u in 0 until feed.size){
                    if(feed[u].no == feedNo){
                        myFeedArr.add(feed[u])//나와 비슷한 카테고리의 피드를 담음
                    }
                }
            }
        }

        //글귀는 게시글 3개당 한개씩 끼워넣는다
        var j = 0
        for(i in 0 until myFeedArr.size){
            if(i!=0&&i % 3 == 0 && j <= (letter.size-1)){
                myFeedArr.add(i,letter[j++])
            }
        }

        for(i in 0 until allwant.size){
            if(allwant[i].no == userNo){//나인경우
                want.add(allwant[i])
            }
        }

        var intent = Intent(this,FeedActivity::class.java)
        intent.putParcelableArrayListExtra("MYFEED",myFeedArr)//피드정보를 넘겨줌
        intent.putParcelableArrayListExtra("FEED",feed)//모든피드
        intent.putParcelableArrayListExtra("MYBOOK",myBook)//나의 책 정보를 넘겨줌
        intent.putParcelableArrayListExtra("USER",user)//유저의 정보 넘겨줌(피드 정리할때)
        intent.putParcelableArrayListExtra("LETTER",letter)//글귀 정보를 넘겨줌
        intent.putExtra("MY",user_map[userNo])//나의 정보를 넘겨줌
        intent.putParcelableArrayListExtra("ALLWANT",allwant)//모든 사람들의 찜 목록을 넘겨줌
        intent.putParcelableArrayListExtra("WANT",want)//내가 찜한 목록을 넘겨줌
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {//회원가입후 넘어온 정보를 받아옴
        super.onActivityResult(requestCode, resultCode, data)
       if(requestCode == CODE){
           if(resultCode == Activity.RESULT_OK){
               var tmp_arr = data!!.getParcelableArrayListExtra<User>("USER_INFO") as ArrayList<User>
              tmp = tmp_arr[tmp_arr.size-1]
               id = tmp.eEmail
               pass = tmp.pass
               name = tmp.name
              updateUser(tmp.eEmail,tmp.pass,tmp.name)
           }
       }
    }


    fun request(){
        var keys:ArrayList<String> =  ArrayList()
        UserManagement.getInstance().me(keys, object: MeV2ResponseCallback(){
            override fun onSuccess(result: MeV2Response?) {
                // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                Log.d("KAKAO","리퀘스트요청")
                Log.d("ORDER","kakao reauest")
                id = result!!.id.toString()
                pass = "kakaoUser"
                name = result!!.nickname.toString()

               kakaoLogin()
            }

            override fun onSessionClosed(errorResult: ErrorResult?) {
                //  TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                Log.d("error","Session closed error is "+errorResult.toString())
            }

        })
    }

    inner class SessionCallback : ISessionCallback {

        override fun onSessionOpenFailed(exception: KakaoException?) {
            Log.d("KAKAOSESSION","콜백실패 "+exception!!.errorType)
           // request()
        }

        override fun onSessionOpened() {
            //  TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            Log.d("KAKAOSESSION","SessionCallbackOpened")
            request()
        }
    }
}
