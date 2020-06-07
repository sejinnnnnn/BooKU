package com.example.mybook

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.InputType
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_enroll.*

class EnrollActivity : AppCompatActivity() {//회원가입 액티비티

    lateinit var tmp_user:ArrayList<User>//전 액티비티에서 받은 유저 정보
    lateinit var category:ArrayList<MyCatg>//전체 카테고리 목록
    lateinit var selected_catg:ArrayList<MyCatg>//내가 선택한 카테고리
    lateinit var my:User
    lateinit var adapter: CatgAdapter//카테고리 어댑터
    var id:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enroll)
        init()
    }

    fun init(){
        tmp_user = getIntent().getSerializableExtra("USER") as ArrayList<User>
        category = getIntent().getParcelableArrayListExtra("CATEGORY")
        my = getIntent().getParcelableExtra("MY")
        if(my.eEmail!=""){//카카오 등록 유저이면
            input_email.setText(my.eEmail)
            input_pass.setText(my.pass)
            input_name.setText(my.name)
            input_email.setInputType(InputType.TYPE_NULL)
            input_pass.setInputType(InputType.TYPE_NULL)
            input_name.setInputType(InputType.TYPE_NULL)
        }
        selected_catg = arrayListOf()
        adapter = CatgAdapter(category)

        addCatg()
    }

    fun clickEnroll(view: View?){
        if(checkUser()){//등록된 아이디가 없으면 성공
            Toast.makeText(this,"성공",Toast.LENGTH_SHORT).show()
            var catg = "" as String
            for(i in 0 until selected_catg.size){
                for(j in 0 until category.size){
                    if(selected_catg[i].name == category[j].name){
                        catg+=selected_catg[i].code+" "
                    }
                }

            }
            tmp_user.add(User(tmp_user.size,id,input_pass.text.toString(),input_name.text.toString(), Uri.parse(getDrawable(R.drawable.background).toString()),catg))
            val result = Intent()
            result.putParcelableArrayListExtra("USER_INFO",tmp_user)
            setResult(RESULT_OK,result)
            finish()
        }else{//등록된 아이디가 있으면 다시 입력
            Toast.makeText(this,"이미 등록된 아이디가 있습니다",Toast.LENGTH_SHORT).show()
            input_email.text.clear()
            input_name.text.clear()
            input_pass.text.clear()
        }
    }

    fun checkUser():Boolean{//등록된 아이디가 있는지 비교
        id = input_email.text.toString()
        for(i in 0 until tmp_user.size){
            if(tmp_user[i].eEmail == id){
                return false
            }
        }
        return true
    }

    fun addCatg(){

        catg_list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        catg_list.adapter = adapter

        adapter.itemClickListener = object : CatgAdapter.OnItemClickListener{
            override fun OnItemClick(holder: CatgAdapter.ViewHolder, view: View, data: MyCatg, position: Int) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                Toast.makeText(view.context,data.name,Toast.LENGTH_SHORT).show()
                selected_catg.add(data)
            }
         }
    }
}
