package com.example.mybook

import android.util.Log
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStreamReader
import java.net.URL
import java.net.URLEncoder
import javax.net.ssl.HttpsURLConnection


class BookAPI(){
    companion object {
        fun callAPI(search :String): MutableList<Book>{
            val clientId = "6Tk2M7YOBlfy1Xt53ULL"//애플리케이션 클라이언트 아이디값";
            val clientSecret = "PyhhT2cCj2"//애플리케이션 클라이언트 시크릿값";

            try {
                val text = URLEncoder.encode(search, "UTF-8")
                val apiURL = "https://openapi.naver.com/v1/search/book.xml?query=$text" //
                //String apiURL = "https://openapi.naver.com/v1/search/blog.xml?query="+ text; // xml 결과
                val url = URL(apiURL)
                val con = url.openConnection() as HttpsURLConnection
                con.requestMethod = "GET"
                con.setRequestProperty("X-Naver-Client-Id", clientId)
                con.setRequestProperty  ("X-Naver-Client-Secret", clientSecret)
                val responseCode = con.responseCode
                val factory = XmlPullParserFactory.newInstance()
                val xpp = factory.newPullParser();

                if (responseCode == 200) { // 정상 호출
                    xpp.setInput(InputStreamReader(con.inputStream, "UTF-8"))
                } else {  // 에러 발생
                    xpp.setInput(InputStreamReader(con.errorStream, "UTF-8"))
                }
                var eventType = xpp.eventType
                var startTag = ""
                var endTag = ""
                val list = mutableListOf<Book>()
                var title =""
                var link = ""
                var image = ""
                var author = ""
                var price = 0
                var discount = 0
                var publisher = ""
                var pubdate = ""
                var isbn = ""
                var description = ""
                while (eventType != XmlPullParser.END_DOCUMENT){
                    xpp.next()
                    eventType = xpp.eventType
                    when(eventType){
                        XmlPullParser.START_TAG->{
                            startTag = xpp.name

                        }
                        XmlPullParser.END_TAG ->{
                            endTag = xpp.name
                            if(endTag == "item")
                                list.add(Book(
                                    title = title,
                                    link = link,
                                    imageLink = image,
                                    author = author,
                                    price = price,
                                    discount = discount,
                                    publisher = publisher,
                                    pubdate = pubdate,
                                    isbn = isbn,
                                    description = description
                                ))
                        }
                        XmlPullParser.TEXT ->{
                            val text = xpp.text
                            when(startTag){
                                "title" -> title = text
                                "link" -> link = text
                                "image" -> image = text
                                "author" -> author = text
                                "price" -> price = text.toInt()
                                "discount" -> discount = text.toInt()
                                "publisher" -> publisher = text
                                "pubdate" -> pubdate = text
                                "isbn" -> isbn = text
                                "description" -> description = text
                            }
                        }
                    }
                }
                return list


            } catch (e: Exception) {
                println(e.toString())
                return mutableListOf()
            }


        }
        fun calladvAPI(
            search :String? = null,//책 검색
            d_titl: String? = null,//책 제목 검색
            d_auth: String? = null,//저자명 검색
            d_count: String? = null,//목차 검색
            d_isbn: String? = null,//isbn 검색
            d_publ: String? = null,
            d_dafr: String? = null,//출간 시작일
            d_dato: String? = null,//출간 종료일
            d_catg: String? = null//책 카테고리
        ): MutableList<Book>{
            val clientId = "6Tk2M7YOBlfy1Xt53ULL"//애플리케이션 클라이언트 아이디값";
            val clientSecret = "PyhhT2cCj2"//애플리케이션 클라이언트 시크릿값";

            try {


                var apiURL = "https://openapi.naver.com/v1/search/book_adv.xml?display=100" //
                if(search != null) apiURL += "&query=" + URLEncoder.encode(search, "UTF-8")
                if(d_titl != null) apiURL += "&d_titl=" + URLEncoder.encode(d_titl, "UTF-8")
                if(d_auth != null) apiURL += "&d_auth=" + URLEncoder.encode(d_auth, "UTF-8")
                if(d_count != null) apiURL += "&d_count=" + URLEncoder.encode(d_count, "UTF-8")
                if(d_isbn != null) apiURL += "&d_isbn=" + URLEncoder.encode(d_isbn, "UTF-8")
                if(d_publ != null) apiURL += "&d_publ=" + URLEncoder.encode(d_publ, "UTF-8")
                if(d_dafr != null) apiURL += "&d_dafr=" + URLEncoder.encode(d_dafr, "UTF-8")
                if(d_dato != null) apiURL += "&d_dato=" + URLEncoder.encode(d_dato, "UTF-8")
                if(d_catg != null) apiURL += "&d_catg=" + URLEncoder.encode(d_catg, "UTF-8")

                Log.e("url",apiURL)
                //String apiURL = "https://openapi.naver.com/v1/search/blog.xml?query="+ text; // xml 결과
                val url = URL(apiURL)
                val con = url.openConnection() as HttpsURLConnection
                con.requestMethod = "GET"
                con.setRequestProperty("X-Naver-Client-Id", clientId)
                con.setRequestProperty  ("X-Naver-Client-Secret", clientSecret)
                val responseCode = con.responseCode
                val factory = XmlPullParserFactory.newInstance()
                val xpp = factory.newPullParser();

                if (responseCode == 200) { // 정상 호출
                    xpp.setInput(InputStreamReader(con.inputStream, "UTF-8"))
                } else {  // 에러 발생
                    xpp.setInput(InputStreamReader(con.errorStream, "UTF-8"))
                }
                var eventType = xpp.eventType
                var startTag = ""
                var endTag = ""
                val list = mutableListOf<Book>()
                var title =""
                var link = ""
                var image = ""
                var author = ""
                var price = 0
                var discount = 0
                var publisher = ""
                var pubdate = ""
                var isbn = ""
                var description = ""
                while (eventType != XmlPullParser.END_DOCUMENT){
                    xpp.next()
                    eventType = xpp.eventType
                    when(eventType){
                        XmlPullParser.START_TAG->{
                            startTag = xpp.name

                        }
                        XmlPullParser.END_TAG ->{
                            endTag = xpp.name
                            if(endTag == "item")
                                list.add(Book(
                                    title = title,
                                    link = link,
                                    imageLink = image,
                                    author = author,
                                    price = price,
                                    discount = discount,
                                    publisher = publisher,
                                    pubdate = pubdate,
                                    isbn = isbn,
                                    description = description
                                ))
                        }
                        XmlPullParser.TEXT ->{
                            val text = xpp.text
                            when(startTag){
                                "title" -> title = text
                                "link" -> link = text
                                "image" -> image = text
                                "author" -> author = text
                                "price" -> price = text.toInt()
                                "discount" -> discount = text.toInt()
                                "publisher" -> publisher = text
                                "pubdate" -> pubdate = text
                                "isbn" -> isbn = text
                                "description" -> description = text
                            }
                        }
                    }
                }
                return list


            } catch (e: Exception) {
                println(e.toString())
                return mutableListOf()
            }


        }

    }
}