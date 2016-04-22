package cn.hncj.or.db;

/**
 * 记录书签的各种数据
 * 
 * @author
 * 
 */
public class BooKMark {
	private String path;
	private int begin;
	private String time;
	private String word;
	public BooKMark(String path, int begin, String word, String time) {
		super();
		this.path = path;
		this.begin = begin;
		this.word = word;
		this.time = time;
	}

	public BooKMark() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getBegin() {
		return begin;
	}

	public void setBegin(int begin) {
		this.begin = begin;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
}
