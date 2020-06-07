package com.example.mybook

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.widget.LinearLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionCloudTextRecognizerOptions
import com.google.firebase.ml.vision.text.FirebaseVisionText
import kotlinx.android.synthetic.main.activity_writing.*
import java.util.*

class WritingActivity : AppCompatActivity() {

    val SELECT_IMAGE = 1024

    val btnClick:(String) -> Unit = {
        editContents.append(it)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_writing)
        init()
    }

    fun init(){
        recyclerOCR.layoutManager = LinearLayoutManager(this,LinearLayout.VERTICAL,false)
        btnUploadWriting.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = android.provider.MediaStore.Images.Media.CONTENT_TYPE
            intent.data = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            startActivityForResult(intent, SELECT_IMAGE)
            recyclerOCR.visibility = GONE
            pgb.visibility = View.VISIBLE
            pgb.getIndeterminateDrawable().setColorFilter(Color.rgb(234, 111, 113), android.graphics.PorterDuff.Mode.MULTIPLY)
        }
        btnSubmit.setOnClickListener {
            //제출 부분
            val contents = editContents.text.toString()
            val writingTitle = editWritingTitle.text.toString()

            var tmpFeed = MyFeed(-1,"",writingTitle,"","",contents,"","","","")
            var db= FirebaseFirestore.getInstance()
            var input:Map<String,String>
            input = HashMap()
            input.put("content",tmpFeed.contents)
            input.put("bname",tmpFeed.bname)

            db.collection("letter").add(input)
                .addOnSuccessListener {
                    Log.d("SUCCESS","게시글 등록 성공")
                }
                .addOnFailureListener {
                    Log.d("FAULURE","게시글 등록 실패")
                }
            var intent = Intent(this,FeedActivity::class.java)
            //put해주기 나중에
            intent.putExtra("UPLOAD_LETTER",tmpFeed)//현재 업로드한 글귀 추가
            setResult(RESULT_OK,intent)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            SELECT_IMAGE ->{
                if(resultCode == Activity.RESULT_OK){
                    var image = FirebaseVisionImage.fromFilePath(applicationContext, data!!.data)
                    val options = FirebaseVisionCloudTextRecognizerOptions.Builder()
                        .setLanguageHints(Arrays.asList("en", "ko"))
                        .build()

                    val detector = FirebaseVision.getInstance().getCloudTextRecognizer(options)
                    lateinit var result: FirebaseVisionText
                    detector.processImage(image)
                        .addOnSuccessListener { firebaseVisionText ->
                            result = firebaseVisionText
                            val resultText = result.text
                            val list = resultText.split("\n").toMutableList()

                            recyclerOCR.adapter = OCRTextAdapter(list,btnClick)

                            pgb.visibility = GONE
                            recyclerOCR.visibility = View.VISIBLE

                        }
                        .addOnFailureListener {
                            // Task failed with an exception
                            // ...
                        }
                }
            }
        }
    }
}
