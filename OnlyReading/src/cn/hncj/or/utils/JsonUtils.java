package cn.hncj.or.utils;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.hncj.or.db.Book;

public class JsonUtils {
	public static List<Book> JsonTools(String jsonString) {
		List<Book> books=new ArrayList<Book>();
		try {
			JSONArray array =new JSONArray(jsonString);
			for(int i=0;i<array.length();++i){
				JSONObject jsonbook=array.getJSONObject(i);
				 Book book=new Book();
				 book.setId(jsonbook.getInt("bookId"));
				 book.setBookImage(jsonbook.getString("bookImage"));
				 book.setBookName(jsonbook.getString("bookName"));
				 book.setBookpath(jsonbook.getString("bookpath"));
				 book.setBookPower(jsonbook.getString("bookPower"));
				 book.setBookType(jsonbook.getString("bookType"));
				 book.setDescribe(jsonbook.getString("describe"));
				 books.add(book);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return books;
	}
}
