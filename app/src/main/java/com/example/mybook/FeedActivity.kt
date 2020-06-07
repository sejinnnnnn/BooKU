package com.example.mybook

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import me.relex.circleindicator.CircleIndicator

class FeedActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var adapterVeiwPager: FragmentPagerAdapter
    lateinit var my:User
    lateinit var user:ArrayList<User>//모든 피드
    lateinit var allFeed:ArrayList<MyFeed>//모든 피드
    lateinit var myFeedArr:ArrayList<MyFeed>
    lateinit var myBook:ArrayList<MyFeed>//나의 책 목록
    lateinit var letter:ArrayList<MyFeed>//글귀 목록
    lateinit var allwant:ArrayList<MyFeed>//모든 사람들의 찜 목록
    lateinit var want:ArrayList<MyFeed>
    lateinit var vpPager: ViewPager
    lateinit var fab_open: Animation
    lateinit var fab_close: Animation

    lateinit var fab: FloatingActionButton
    lateinit var fab1: FloatingActionButton
    lateinit var fab2: FloatingActionButton

    private var isFabOpen = false

    val SELECT_IMAGE = 9999
    val USE_INTERNET = 8888
    val REQ_CODE_SPEECH_INPUT = 5555
    var WRITE_CODE = 7654
    val LETTER_CODE = 3678

    lateinit var myFeedMake:mainfeed
    lateinit var myProfile:mypage
    lateinit var searchMake:searchpage
    lateinit var calendar:CalendarFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

        fab_open= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_open)
        fab_close= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close)

        fab=findViewById(R.id.fab)
        fab1=findViewById(R.id.fab1)
        fab2 = findViewById(R.id.fab2)

        fab.setOnClickListener(this)
        fab1.setOnClickListener(this)
        fab2.setOnClickListener(this)


        vpPager= findViewById(R.id.vpPager)
        adapterVeiwPager = MyPagerAdapter(supportFragmentManager)
        vpPager.adapter=adapterVeiwPager

        //인디케이터 생성 부분
        var indicator: CircleIndicator = findViewById(R.id.indicator)

        indicator.setViewPager(vpPager)
        //사진 접근 허용,마이크 및 인터넷
        initPermission()
        init()
    }

    fun init(){
        my = getIntent().getParcelableExtra("MY")
        user = getIntent().getParcelableArrayListExtra("USER")
        allFeed = getIntent().getParcelableArrayListExtra("FEED")//모든 피드정보
        myFeedArr = getIntent().getParcelableArrayListExtra("MYFEED")//나의 피드 정보
        myBook = getIntent().getParcelableArrayListExtra("MYBOOK")//나의 책 정보
        letter = getIntent().getParcelableArrayListExtra("LETTER")//글귀정보
        allwant = getIntent().getParcelableArrayListExtra("ALLWANT")//모든 사람들의 찜 목록
        want = getIntent().getParcelableArrayListExtra("WANT")//내가 찜한 목록

        myFeedMake = mainfeed()
        myProfile=mypage()
        searchMake=searchpage()
        calendar= CalendarFragment()

        Log.d("USERINFO",my.name)
    }

    fun initPermission(){//퍼미션 체크
        if(checkAppPermission(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE))){//사진 체크
            Toast.makeText(this,"권한체크됨",Toast.LENGTH_SHORT).show()
        }else{
            askPermission(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),SELECT_IMAGE)
        }
        if(checkAppPermission(arrayOf(android.Manifest.permission.INTERNET))){//인터넷체크

        }else{
            askPermission(arrayOf(android.Manifest.permission.INTERNET),USE_INTERNET)
        }
        if(checkAppPermission(arrayOf(android.Manifest.permission.RECORD_AUDIO))){

        }else{
            askPermission(arrayOf(android.Manifest.permission.RECORD_AUDIO),REQ_CODE_SPEECH_INPUT)
        }
    }

    fun checkAppPermission(requestPermission:Array<String>):Boolean{
        val requestResult = BooleanArray(requestPermission.size)
        for(i in requestResult.indices){
            requestResult[i] = ContextCompat.checkSelfPermission(this,requestPermission[i]) == PackageManager.PERMISSION_GRANTED
            if(!requestResult[i]){
                return false
            }
        }
        return true
    }

    fun askPermission(requestPermission: Array<String>, REQ_PERMISSION:Int){
        ActivityCompat.requestPermissions(this,requestPermission,REQ_PERMISSION)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            SELECT_IMAGE->{
                if(checkAppPermission(permissions)){//허용이 된 경우
                    Toast.makeText(applicationContext,"권한을 허락함",Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(applicationContext,"권한을 허락안함",Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            USE_INTERNET->{
                if(checkAppPermission(permissions)){//허용이 된 경우
                    Toast.makeText(applicationContext,"권한을 허락함",Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(applicationContext,"권한을 허락안함",Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            REQ_CODE_SPEECH_INPUT->{
                if(checkAppPermission(permissions)){//허용이 된 경우
                    Toast.makeText(applicationContext,"권한을 허락함",Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(applicationContext,"권한을 허락안함",Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }


    override fun onClick(p0: View?) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        var id=p0!!.getId()
        when(id){
            R.id.fab-> {
                anim()
            }
            R.id.fab1->{
                anim()//글쓰기 인텐트로 넘어감
                var intent= Intent(getApplicationContext(),WriteActivity::class.java)
                intent.putExtra("USERNO",my.no)
                startActivityForResult(intent,WRITE_CODE)
            }
            R.id.fab2->{
                anim()
                //글귀저장 인텐트
                var intent = Intent(applicationContext,WritingActivity::class.java)
                intent.putExtra("USERNO",my.no)
                startActivityForResult(intent,LETTER_CODE)
            }
        }
    }
    fun anim() {
        if (isFabOpen) {
            fab1.startAnimation(fab_close)
            fab1.isClickable = false
            fab2.startAnimation(fab_close)
            fab2.isClickable = false
            isFabOpen = false
        } else {
            fab1.startAnimation(fab_open)
            fab1.isClickable = true
            fab2.startAnimation(fab_open)
            fab2.isClickable = true
            isFabOpen = true
        }
    }

    inner class MyPagerAdapter(fm: FragmentManager?): FragmentPagerAdapter(fm)
    {
        var num=4;

        override fun getItem(p0: Int): Fragment? {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            when(p0){
                //search
                0->{
                    Log.d("PAGER",vpPager.currentItem.toString())
                    searchMake=searchpage.makeSearch(allFeed,my,want,allwant)
                    return searchMake
                }
                //mainfeed
                1->{
                    Log.d("PAGER",vpPager.currentItem.toString())
                    myFeedMake = mainfeed.makeFeed(myFeedArr,my)
                    return myFeedMake
                }
                //mypage
                2->{
                    Log.d("PAGER",vpPager.currentItem.toString())
                     myProfile = mypage.makeProfile(my,want)//나의 프로필 정보 ,내가 찜한 책 정보를
                    return myProfile
                }
                3->{//캘린더
                    calendar = CalendarFragment.makeCalendar(myBook)//나의 책 정보 넘겨줌
                    return calendar
                }
            }
            return null
        }

        override fun getCount(): Int {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            return num
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {//글쓰기 종료되면
        super.onActivityResult(requestCode, resultCode, data)
        var myfeed:MyFeed
        if(requestCode==WRITE_CODE){//게시글추가
            if(resultCode==RESULT_OK){
               myfeed = data!!.getParcelableExtra<MyFeed>("UPLOAD") as MyFeed
                allFeed.add(myfeed)
                //나의 책목록 다시 생성
                myBook.clear()
                for(i in 0 until allFeed.size){
                    if(allFeed[i].no == my.no){
                        myBook.add(allFeed[i])//나의 책 목록
                    }
                }
                myFeedArr.clear()
                makeFeed()
            }
        }else if(requestCode == LETTER_CODE){///글귀추가 했을 때
            if(requestCode==RESULT_OK){
               myfeed = data!!.getParcelableExtra<MyFeed>("UPLOAD_LETTER") as MyFeed
                letter.add(myfeed)
                myFeedArr.clear()
                makeFeed()
            }
        }

    }

    fun makeFeed(){//나에게 맞춰 피드를 만들어줌
        //나의 카테고리정보
        var catg = my.catg
        var catg_arr = catg.split(" ")//공백으로 스플릿

        for(i in 0 until user.size){
            var feedNo = -1
            var u_catg = user[i].catg
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
                for(u in 0 until allFeed.size){
                    if(allFeed[u].no == feedNo){
                        myFeedArr.add(allFeed[u])//나와 비슷한 카테고리의 피드를 담음
                    }
                }
            }
        }
        //글귀 넣어주기
        var j = 0
        for(i in 0 until myFeedArr.size){
            if(i!=0&&i % 3 == 0 && j <= (letter.size-1)){
                myFeedArr.add(i,letter[j++])
            }
        }

        myFeedMake.initlayout()
        //myFeedMake.adapter.notifyDataSetChanged
        //책정보 다시 넘겨주기
        searchMake.post = allFeed
        //캘린더에 내 책정보 넘겨주기
        calendar.myBook = myBook
    }
}
