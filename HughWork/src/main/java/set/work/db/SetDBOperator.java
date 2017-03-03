package set.work.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import set.work.bean.RequestListBean;
import set.work.utils.LogUtil;


public class SetDBOperator {
	private SQLiteDatabase db;
	public final int VERSION = 1;
	private static SetDBOperator operator;
	public static String DB_NAME = "Hugh.db";
	private static String TABLE_NAME = "TableInSubmit";
	private static String SUBMIT_TIME = "operatetime";
	private static String SUBMIT_VALUE = "submitvalue";
	private static String SUBMIT_ID = "method";

	public static SetDBOperator getInstance(Context context) {
		if (operator == null) {
			operator = new SetDBOperator(context);
		}
		return operator;
	}
	
	public static SetDBOperator getInstance(String dbName, Context context) {
		DB_NAME = dbName;
		if (operator == null) {
			operator = new SetDBOperator(context);
		}
		return operator;
	}
	public SetDBOperator(Context context) {
		SqliteDbHelper myDBHelper = new SqliteDbHelper(context, DB_NAME,
				VERSION);
		db = myDBHelper.getWritableDatabase();
		createSubmitTable();
	}

	private void createSubmitTable() {
		// 创建表SQL语句
		String stu_table = "create table if not exists " + TABLE_NAME + "("
				+ SUBMIT_TIME + " DATETIME, " + SUBMIT_VALUE + " TEXT,"
				+ SUBMIT_ID + " TEXT)";
		// 执行SQL语句
		db.execSQL(stu_table);
	}

	public void insertDataSubmit(String methodName, RequestListBean params) {
		try {
			Date currentTime = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			String curtime = formatter.format(currentTime);
			// 实例化常量�?
			ContentValues cValue = new ContentValues();
			// 添加用户�?
			cValue.put(SUBMIT_TIME, curtime);
			// 添加密码
			cValue.put(SUBMIT_VALUE, params.toString());
			cValue.put(SUBMIT_ID, methodName);
			// 调用insert()方法插入数据
			db.insert(TABLE_NAME, null, cValue);
		} catch (Exception e) {
			LogUtil.Debug(this, "保存本地数据出错！！");
		}

	}

	public void delete(String methadName, String inputTime) {
		// 删除条件
		String whereClause = SUBMIT_TIME + "=? and " + SUBMIT_ID + "=?";
		// 删除条件参数
		String[] whereArgs = { inputTime, methadName };
		// 执行删除
		db.delete(TABLE_NAME, whereClause, whereArgs);
	}

	public List<Map<String, String>> getAllNeedSubList() {
		String sqlstr = "select * from " + TABLE_NAME;
		Cursor cursor = db.rawQuery(sqlstr, null);
		List<Map<String, String>> retulist = formatCursorToList(cursor);
		return retulist;
	}

	private List<Map<String, String>> formatCursorToList(Cursor cursor) {
		List<Map<String, String>> retulist = null;
		if (cursor != null) {
			retulist = new LinkedList<Map<String, String>>();
			int columncount = cursor.getColumnCount();
			while (cursor.moveToNext()) {
				Map<String, String> currow = new LinkedHashMap<String, String>();
				for (int i = 0; i < columncount; i++) {
					String clumnName = cursor.getColumnName(i);
					String valueString = cursor.getString(i);
					currow.put(clumnName, valueString);
				}
				retulist.add(currow);
			}
		}
		return retulist;
	}

	class SqliteDbHelper extends SQLiteOpenHelper {
		public SqliteDbHelper(Context context, String name, int version) {
			super(context, name, null, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}
}
