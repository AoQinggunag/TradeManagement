package com.trade.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Util {

	public static final String KEY_START_TIME = "start";
	public static final String KEY_END_TIME = "end";

	public static String getDate() {
		Date date = new Date();
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = format.format(date);
		return time;
	}
	public static String getDateSimple() {
		Date date = new Date();
		DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		String time = format.format(date);
		return time;
	}
	// ��month��
	public static String getDate(int month) {
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, month);
		return f.format(c.getTime());
	}

	// ��year��
	public static String getYearDate(int year) {
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Calendar c = Calendar.getInstance();
		c.add(Calendar.YEAR, year);
		return f.format(c.getTime());
	}

	// ͨ�����»�ȡ���µ�ʱ������
	public static Map<String, String> getTimeInterval(int year, int month) {
		Map<String, String> map = new HashMap<String, String>();
		String start = new StringBuilder().append(year).append("-")
				.append(month < 10 ? "0" + month : month).append("-01")
				.append(" 00:00:00").toString();
		String end = new StringBuilder().append(year).append("-")
				.append(month < 10 ? "0" + month : month).append("-")
				.append(getLastDayInMonth(year, month)).append(" 23:59:59")
				.toString();
		map.put(KEY_START_TIME, start);
		map.put(KEY_END_TIME, end);
		return map;
	}

	// ͨ�������ջ�ȡ���յ�ʱ������
	public static Map<String, String> getTimeInterval(int year, int month, int day) {
		Map<String, String> map = new HashMap<String, String>();
		String start = new StringBuilder().append(year).append("-")
				.append(month < 10 ? "0" + month : month).append("-").append(day < 10 ? "0" + day : day)
				.append(" 00:00:00").toString();
		String end = new StringBuilder().append(year).append("-")
				.append(month < 10 ? "0" + month : month).append("-")
				.append(day < 10 ? "0" + day : day).append(" 23:59:59")
				.toString();
		map.put(KEY_START_TIME, start);
		map.put(KEY_END_TIME, end);
		return map;
	}
	
	public static Map<String, String> getTimeInterval(String year, String month, String day) {
		return getTimeInterval(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
	}

	// ͨ�����»�ȡ���µ�ʱ������
	public static Map<String, String> getTimeInterval(String year, String month) {
		return getTimeInterval(Integer.parseInt(year), Integer.parseInt(month));
	}

	public static int getLastDayInMonth(String year, String month) {
		return getLastDayInMonth(Integer.parseInt(year),
				Integer.parseInt(month));
	}

	public static int getLastDayInMonth(int year, int month) {
		boolean isRunNian = year % 4 == 0 && year % 100 != 0;
		if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8
				|| month == 10 || month == 12) {
			return 31;
		} else if (month == 2) {
			if (isRunNian)
				return 29;
			else
				return 28;
		} else {
			return 30;
		}
	}

	public static double dateDiff(String startTime, String endTime)
			throws ParseException {
		// ���մ���ĸ�ʽ����һ��simpledateformate����
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long nd = 1000 * 24 * 60 * 60;// һ��ĺ�����
		long nh = 1000 * 60 * 60;// һСʱ�ĺ�����
		long nm = 1000 * 60;// һ���ӵĺ�����
		long ns = 1000;// һ���ӵĺ�����long diff;try {
		// �������ʱ��ĺ���ʱ�����
		long diff = sd.parse(endTime).getTime() - sd.parse(startTime).getTime();
		long day = diff / nd;// ����������
		long min = diff % nd % nh / nm;// �������ٷ���
		double hour = day * 24 + diff % nd / nh + min / 60;// ��������Сʱ
		long sec = diff % nd % nh % nm / ns;// ����������//������
		System.out.println("ʱ����" + day + "��" + hour + "Сʱ" + min + "����" + sec
				+ "�롣");
		return hour;
	}
}
