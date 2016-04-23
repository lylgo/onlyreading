package cn.hncj.or.read;

import java.util.ArrayList;
import java.util.List;

import com.hncj.activity.R;
import com.lidroid.xutils.db.converter.SqlDateColumnConverter;

import cn.hncj.or.config.Const;
import cn.hncj.or.db.BooKMark;
import cn.hncj.or.db.Book;
import cn.hncj.or.db.BookDb;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class BookMarkAdapter extends BaseAdapter {
	private Context context;
	private BookDb bookDb;
	private List<BooKMark> list;

	public BookMarkAdapter(Context context, String path) {
		this.context = context;
		bookDb = new BookDb(context);
		SQLiteDatabase db = bookDb.getReadableDatabase();
		Cursor cursor = db.query(Const.DB_TMARK, null, "path=?",
				new String[] { path }, null, null, null);
		list = new ArrayList<BooKMark>();
		while (cursor.moveToNext()) {
			BooKMark book = new BooKMark();
			book.setBegin(cursor.getInt(cursor.getColumnIndex("begin")));
			book.setWord(cursor.getString(cursor.getColumnIndex("word")));
			book.setTime(cursor.getString(cursor.getColumnIndex("time")));
			list.add(book);
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view;
		if (convertView == null) {
			view = View.inflate(context, R.layout.item_mark, null);
		} else {
			view = convertView;
		}
		TextView markword = (TextView) view.findViewById(R.id.markword);
		TextView marktime = (TextView) view.findViewById(R.id.marktime);
		ImageButton delectButton = (ImageButton) view
				.findViewById(R.id.markdelect);
		marktime.setText(list.get(position).getTime());
		markword.setText(list.get(position).getWord());
		final int id = position;
		delectButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SQLiteDatabase db = bookDb.getWritableDatabase();
				int flag = db
						.delete(Const.DB_TMARK, "begin=?",
								new String[] { String.valueOf(list.get(id)
										.getBegin()) });
				if (flag > 0) {
					list.remove(id);
					Intent intent=new Intent("BookMarkAdapter.Change");
					context.sendBroadcast(intent);
				} else {
					Toast.makeText(context, "…æ≥˝ È«© ß∞‹", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
		return view;
	}
	public List<BooKMark> getMarklist(){
		return list;
	}
}
