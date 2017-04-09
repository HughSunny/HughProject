package set.work.utils;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

import java.io.File;
import java.util.Date;

public class LoggerFileTool {
	private static Logger gLogger = null;
	public static boolean isprint = true;
	private static String FILE_PATH = EquipmentInfo.getSDCardSavePath()
			+ File.separator + "LOG" + File.separator;
	public LoggerFileTool(Object object) {
		Date data = new Date();
		String fileDate = ConvertUtil.dateYMD.format(data);
		String fileNameString = FILE_PATH + fileDate + ".txt";
		gLogger = Logger.getLogger(EncodeUtil.encodeUtf_8String(object.getClass().getName()));
		try {
			// 清空Appender。特別是不想使用現存實例時一定要初期�?
			gLogger.removeAllAppenders();
			// 設定Logger級別�
			gLogger.setLevel(Level.DEBUG);
			// 設定是否繼承父Logger�?
			// 默認為true。繼承root輸出�?
			// 設定false後將不輸出root�?
			gLogger.setAdditivity(true);
			// 生成新的Appender
			RollingFileAppender appender = new RollingFileAppender();
			PatternLayout layout = new PatternLayout();
			// log的输出形�?
			String conversionPattern = "[%d] %p %t %c - %m%n";
			layout.setConversionPattern(conversionPattern);
			appender.setLayout(layout);
			// log输出路径
			appender.setFile(fileNameString);

			// appender.setMaximumFileSize(1024 * 1024);
			appender.setMaxBackupIndex(5);
			// log的文字码
			appender.setEncoding("UTF-8");
			// true:在已存在log文件后面追加 false:新log覆盖以前的log
			appender.setAppend(true);
			// 适用当前配置
			appender.activateOptions();
			gLogger.addAppender(appender);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void Debug(String message) {
		if (isprint) {
			if (message != null) {
				try {
					gLogger.debug(EncodeUtil.encodeUtf_8String(message));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void Info(String message) {
		if (isprint) {
			if (message != null) {
				gLogger.info(EncodeUtil.encodeUtf_8String(message));
			}
		}
	}

	public void Error(String message) {
		if (message != null) {
			gLogger.error(EncodeUtil.encodeUtf_8String(message));
		}
	}
}
