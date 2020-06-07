package com.example.mybook


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_calendar.*
import java.text.SimpleDateFormat
import java.util.*



class CalendarFragment : Fragment() {

    lateinit var myBook:ArrayList<MyFeed>//내가 읽은 책 목록
    lateinit var this_year:String
    lateinit var this_month:String
    lateinit var this_day:String
    lateinit var adapter:sbookAdapter//어댑터
    lateinit var myBookList:ArrayList<Book>//나의 책정보를 Book객체로 반환

    companion object{
        fun makeCalendar(my:ArrayList<MyFeed>):CalendarFragment{
            var cf = CalendarFragment()
            cf.myBook = my



            return cf
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
        makeMyList()
    }

    fun init(){

        var now = System.currentTimeMillis()//현재 시간
        var date:Date = Date(now)//현재 날짜

        var yearFormat:SimpleDateFormat  = SimpleDateFormat("yyyy",Locale.getDefault())//현재 지역의 년도
        var monthFormat:SimpleDateFormat = SimpleDateFormat("MM",Locale.getDefault())//현재 지역의 달
        var dayFormat:SimpleDateFormat = SimpleDateFormat("dd",Locale.getDefault())//현재 몇일

        var year = yearFormat.format(date)
        var month = monthFormat.format(date)
        var day = dayFormat.format(date)

        this_year = year
        this_month = month.toString()
        this_day = day

        date_book.text = this_year+"년 "+this_month+"월에 읽은 책"

        this.calendar.setOnDateChangeListener { view, year, month, dayOfMonth ->
            this_year = year.toString()
            if(month<10){
                this_month="0"+(month+1).toString()
            }else{
                this_month = (month+1).toString()
            }
            if(dayOfMonth<10){
                this_day = "0"+dayOfMonth.toString()
            }else{
                this_day = dayOfMonth.toString()
            }
            date_book.text = year.toString()+"년 "+(month+1)+"월 "+dayOfMonth+"일에 읽은 책"
            makeMyList()
        }
    }

    fun markBook(){//책이 기록된 날짜를 표시해준다
        for(i in 0 until myBook.size) {
            var year = myBook[i].date.toInt()/10000
            var month = (myBook[i].date.toInt()%10000)/100
            //정확한 날짜가지 선택된 경우
            var day = (myBook[i].date.toInt()%10000)%100
            var cal:Calendar = Calendar.getInstance()

        }
    }

    fun makeMyList(){//나의 책정보 리스트 만들기
        myBookList = arrayListOf()
        myBookList.clear()
        Log.d("SIZE",myBook.size.toString())
        for(i in 0 until myBook.size){
            var year = myBook[i].date.toInt()/10000
            var month = (myBook[i].date.toInt()%10000)/100
            //정확한 날짜가지 선택된 경우
            var day = (myBook[i].date.toInt()%10000)%100
                if(month == this_month.toInt() && year == this_year.toInt() && day == this_day.toInt()){
                    myBookList.add(Book(myBook[i].bname,myBook[i].author,0,0,myBook[i].publisher,"null","null","null","null",myBook[i].imageLink))
                }
        }
        makeLayout()
    }

    fun makeLayout(){
        val layoutManager= LinearLayoutManager(activity, LinearLayoutManager.VERTICAL,true)
        user_read_book_date.layoutManager=layoutManager
        adapter=sbookAdapter(myBookList,nothing)//나의 책 정보가 담긴 책 리스트
        user_read_book_date.adapter = adapter
    }

    val nothing:(Book) -> Unit = {//아무것도 아님
    }


}
