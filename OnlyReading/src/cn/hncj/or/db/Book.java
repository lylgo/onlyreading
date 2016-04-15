package cn.hncj.or.db;

public class Book {
	private String bookImage;
	private String bookName;
	private String bookpath;
	private String bookType;
	private String bookPower;
	private String describe;
	private int id;
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}
	public Book(String bookImage, String bookName, String bookpath,
			String bookType, String bookPower, String describe,int id) {
		this.bookImage = bookImage;
		this.bookName = bookName;
		this.bookpath = bookpath;
		this.bookType = bookType;
		this.bookPower = bookPower;
		this.describe = describe;
		this.id=id;
	}
	public Book() {
		super();
		// TODO Auto-generated constructor stub
	}


	public String getBookImage() {
		return bookImage;
	}

	public void setBookImage(String bookImage) {
		this.bookImage = bookImage;
	}

	public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

	public String getBookpath() {
		return bookpath;
	}

	public void setBookpath(String bookpath) {
		this.bookpath = bookpath;
	}

	public String getBookType() {
		return bookType;
	}

	public void setBookType(String bookType) {
		this.bookType = bookType;
	}

	public String getBookPower() {
		return bookPower;
	}

	public void setBookPower(String bookPower) {
		this.bookPower = bookPower;
	}

}
