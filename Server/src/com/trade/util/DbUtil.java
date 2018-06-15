package com.trade.util;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.trade.model.Customer;
import com.trade.model.Deliver;
import com.trade.model.Finance;
import com.trade.model.FinanceAll;
import com.trade.model.Goods;
import com.trade.model.InBill;
import com.trade.model.OutBill;
import com.trade.model.OutBillDetail;
import com.trade.model.Repertory;
import com.trade.model.Supplier;

public class DbUtil {

	// ���ݿ�URL
	private String dbUrl = "jdbc:sqlserver://127.0.0.1:1433;DatabaseName=TradeManagement";
	// ���ݿ��û���
	private String dbUser = "sa";
	// ���ݿ�����
	private String dbPwd = "sa";
	// JDBC����
	private String jdbcName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

	private static final int SUCCESS = 1;
	private static final int GOODS_EXIST = 0;

	// ��ȡ���ݿ�����
	public Connection getCon() throws Exception {
		// �������ݿ�����
		Class.forName(jdbcName);
		// ��ȡ���ݿ�����
		Connection con = DriverManager.getConnection(dbUrl, dbUser, dbPwd);
		return con;
	}

	// �ر����ݿ�����
	public void closeCon(Connection con) {
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	// �رս����
	public void closeRs(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// �û���¼
	public String login(Connection con, String userName, String userPwd)
			throws Exception {
		String sql = "select userId from [user] where userPhone=? and userPassword=?";
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.setString(1, userName);
		pstmt.setString(2, userPwd);
		ResultSet rs = pstmt.executeQuery();
		if (rs.next())
			return rs.getString("userId");
		else
			return null;
	}
	public String login(Connection con, String userPhone)
			throws Exception {
		String sql = "select * from [user] where userPhone=?";
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.setString(1, userPhone);
		ResultSet rs = pstmt.executeQuery();
		if (rs.next())
			return rs.getString("userId");
		else
			return null;
	}
	// �û���¼��ע��˴���������Ҫ���� todo ע�� 
	public String register(Connection con, String userName, String userPwd)
			throws Exception {
		String sql = "select userId from [user] where userPhone=?";
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.setString(1, userName);
		ResultSet rs = pstmt.executeQuery();
		if (rs.next())
			return "";
		else {
			saveUser(con, userName, userPwd);
			return login(con, userName, userPwd);
		}
			
	}
	
	private void saveUser(Connection con, String userName, String userPwd)
			throws Exception {
		String sql = "insert into [user] values(?,?,?,?)";
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.setString(1, "");
		pstmt.setString(2, userName);
		pstmt.setString(3, userPwd);
		pstmt.setString(4, "");
		pstmt.execute();
	}
	/************************************ ��Ӧ�� ************************************************/
	// ��ѯ��Ӧ����Ϣ
	public List<Supplier> listSupplier(Connection con, String userId)
			throws Exception {

		List<Supplier> suppliers = new ArrayList<Supplier>();

		// ��ѯ��sql���
		String sql = "select supplier.supplierId,supplierName,supplierPhone,supplierAddress from supplier,coopSupplierGoods "
				+ "where userId=? and supplier.supplierId=coopSupplierGoods.supplierId and goodsId is null";
		// ��ȡPreparedStatement����
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.setString(1, userId);
		// ִ�в�ѯ
		ResultSet rs = pstmt.executeQuery();
		String supplierId = "";
		String supplierName = "";
		String supplierPhone = "";
		String supplierAddress = "";
		while (rs.next()) {
			supplierId = rs.getString("supplierId");
			supplierName = rs.getString("supplierName");
			supplierPhone = rs.getString("supplierPhone");
			supplierAddress = rs.getString("supplierAddress");
			Supplier supplier = new Supplier(supplierId, supplierName,
					supplierPhone, supplierAddress);
			suppliers.add(supplier);
		}
		this.closeRs(rs);
		return suppliers;
	}

	// ���湩Ӧ����Ϣ
	public void saveSupplier(Connection con, String userId, String phone,
			String name, String address) throws SQLException {

		String sql = "{call proSupplierSave(?,?,?,?,?)}";
		CallableStatement cstmt = con.prepareCall(sql);
		cstmt.setString(1, userId);
		cstmt.setString(2, phone);
		cstmt.setString(3, name);
		cstmt.setString(4, address);
		cstmt.registerOutParameter(5, Types.INTEGER);
		cstmt.execute();

		int result = cstmt.getInt(5);
		if (result == GOODS_EXIST) { // �Ѵ��ڸù�Ӧ��
			// TODO
		} else {

		}
	}

	// �޸Ĺ�Ӧ����Ϣ
	public void updateSupplier(Connection con, String supplierId,
			String supplierName, String supplierPhone, String supplierAddress)
			throws Exception {
		String sql = "update supplier set supplierName=?,supplierPhone=?,supplierAddress=? where supplierId=?";
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.setString(1, supplierName);
		pstmt.setString(2, supplierPhone);
		pstmt.setString(3, supplierAddress);
		pstmt.setString(4, supplierId);
		pstmt.executeUpdate();
	}

	// ɾ��user��Ӧ�Ĺ�Ӧ��
	public boolean removeSupplier(Connection con, String userId, String supplierId)
			throws Exception {
		String slq1 = "select * from coopSupplierGoods where userId = ? and supplierId = ? and goodsId is not null";
		PreparedStatement pstmt1 = con.prepareStatement(slq1);
		pstmt1.setString(1, userId);
		pstmt1.setString(2, supplierId);
		// ִ�в�ѯ
		ResultSet rs = pstmt1.executeQuery();
		if (rs.next()) {
			return false;
		} else {
			String sql = "delete coopSupplierGoods where userId=? and supplierId=?";
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setString(1, userId);
			pstmt.setString(2, supplierId);
			pstmt.executeUpdate();
			return true;
		}
	}

	/********************************************** �ͻ����� *******************************************************/
	// ��ѯ�ͻ���Ϣ
	public List<Customer> listCustomer(Connection con, String userId)
			throws Exception {

		List<Customer> customers = new ArrayList<Customer>();

		// ��ѯ��sql���
		String sql = "select customer.customerId,customerName,customerPhone,customerAddress from customer,coopCustomer "
				+ "where userId=? and customer.customerId=coopCustomer.customerId";
		// ��ȡPreparedStatement����
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.setString(1, userId);
		// ִ�в�ѯ
		ResultSet rs = pstmt.executeQuery();
		String customerId = "";
		String customerName = "";
		String customerPhone = "";
		String customerAddress = "";
		while (rs.next()) {
			customerId = rs.getString("customerId");
			customerName = rs.getString("customerName");
			customerPhone = rs.getString("customerPhone");
			customerAddress = rs.getString("customerAddress");
			Customer customer = new Customer(customerId, customerName,
					customerPhone, customerAddress);
			customers.add(customer);
		}
		this.closeRs(rs);
		return customers;
	}

	// ����ͻ���Ϣ
	public void saveCustomer(Connection con, String userId, String phone,
			String name, String address) throws SQLException {

		String sql = "{call proCustomerSave(?,?,?,?,?)}";
		CallableStatement cstmt = con.prepareCall(sql);
		cstmt.setString(1, userId);
		cstmt.setString(2, phone);
		cstmt.setString(3, name);
		cstmt.setString(4, address);
		cstmt.registerOutParameter(5, Types.INTEGER);
		cstmt.execute();

		int result = cstmt.getInt(5);
		if (result == GOODS_EXIST) { // �Ѵ��ڸÿͻ�
			// TODO
		} else {

		}
	}

	// �޸Ŀͻ���Ϣ
	public void updateCustomer(Connection con, String customerId,
			String customerName, String customerPhone, String customerAddress)
			throws Exception {
		String sql = "update customer set customerName=?,customerPhone=?,customerAddress=? where customerId=?";
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.setString(1, customerName);
		pstmt.setString(2, customerPhone);
		pstmt.setString(3, customerAddress);
		pstmt.setString(4, customerId);
		pstmt.executeUpdate();
	}

	// ɾ��user��Ӧ�Ŀͻ�
	public void removeCustomer(Connection con, String userId, String customerId)
			throws Exception {
		String sql = "delete coopCustomer where userId=? and customerId=?";
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.setString(1, userId);
		pstmt.setString(2, customerId);
		pstmt.executeUpdate();
	}

	/***************************************************** ��Ʒ *************************************************/

	// ���ô洢���̣�����Ʒ���в�ѯ
	public List<Goods> listGoods(Connection con, String userId)
			throws Exception {
		List<Goods> goodss = new ArrayList<Goods>();

		String sql = "{call proGoodsQuery(?)}";
		CallableStatement cstmt = con.prepareCall(sql);
		cstmt.setString(1, userId);
		ResultSet rs = cstmt.executeQuery();

		String goodsId = "";
		String goodsName = "";
		String goodsUnit = "";
		String goodsInUnitPrice = "";
		String goodsOutUnitPrice = "";
		String goodsImage = "";
		String goodsRepertory = "";
		String goodsSupplierId = "";
		String goodsSupplierName = "";
		String goodsSupplierPhone = "";
		String goodsSupplierAddress = "";

		while (rs.next()) {
			goodsId = rs.getString("goodsId");
			goodsName = rs.getString("goodsName");
			goodsUnit = rs.getString("goodsUnit");
			goodsInUnitPrice = rs.getString("goodsInUnitPrice");
			goodsOutUnitPrice = rs.getString("goodsOutUnitPrice");
			goodsImage = Constant.IMAGE_URL + rs.getString("goodsImage");
			goodsRepertory = getRepertoryCount(con, goodsId, userId);
			goodsSupplierId = rs.getString("supplierId");
			goodsSupplierName = rs.getString("supplierName");
			goodsSupplierPhone = rs.getString("supplierPhone");
			goodsSupplierAddress = rs.getString("supplierAddress");
			Goods goods = new Goods(goodsId, goodsName, goodsUnit,
					goodsInUnitPrice, goodsOutUnitPrice, goodsImage, goodsRepertory,
					goodsSupplierId, goodsSupplierName, goodsSupplierPhone,
					goodsSupplierAddress);
			goodss.add(goods);
		}
		this.closeRs(rs);
		return goodss;
	}

	private String getRepertoryCount(Connection con, String goodsId, String userId) throws SQLException {
		// ��ѯ��sql���
		String sql = "select repertoryGoodsCount from repertory,goods "
				+ "where repertoryUserId=? and repertoryGoodsId=?";
		// ��ȡPreparedStatement����
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.setString(1, userId);
		pstmt.setString(2, goodsId);
		// ִ�в�ѯ
		ResultSet rs = pstmt.executeQuery();
		String repertoryGoodsCount = "";
		if (rs.next()) {
			repertoryGoodsCount = rs.getString("repertoryGoodsCount");
		}
		this.closeRs(rs);
		return repertoryGoodsCount == null || repertoryGoodsCount.equals("") ? "0" : repertoryGoodsCount ;
	}

	public void saveGoods(Connection con, String userId, String goodsName,
			String goodsUnit, String goodsInUnitPrice,
			String goodsOutUnitPirce, String goodsImage, String supplierId)
			throws SQLException {

		String sql = "{call proGoodsSave(?,?,?,?,?,?,?,?)}";
		CallableStatement cstmt = con.prepareCall(sql);
		cstmt.setString(1, userId);
		cstmt.setString(2, goodsName);
		cstmt.setString(3, goodsUnit);
		cstmt.setString(4, goodsInUnitPrice);
		cstmt.setString(5, goodsOutUnitPirce);
		cstmt.setString(6, goodsImage);
		cstmt.setString(7, supplierId);
		cstmt.registerOutParameter(8, Types.INTEGER);
		cstmt.execute();

		int result = cstmt.getInt(8);
		if (result == GOODS_EXIST) {
			// TODO
		} else {

		}
	}

	public void updateGoods(Connection con, String userId, String goodsId,
			String goodsName, String goodsUnit, String goodsInUnitPrice,
			String goodsOutUnitPirce, String goodsImage, String supplierId)
			throws SQLException {

		String sql = "{call proGoodsUpdate(?,?,?,?,?,?,?,?)}";
		CallableStatement cstmt = con.prepareCall(sql);
		cstmt.setString(1, userId);
		cstmt.setString(2, goodsId);
		cstmt.setString(3, goodsName);
		cstmt.setString(4, goodsUnit);
		cstmt.setString(5, goodsInUnitPrice);
		cstmt.setString(6, goodsOutUnitPirce);
		cstmt.setString(7, goodsImage);
		cstmt.setString(8, supplierId);
		cstmt.execute();
		// TODO ������Ʒ��Ϣ�ɹ�
	}

	// ɾ��user��Ӧ����Ʒ
	public void removeGoods(Connection con, String userId, String supplierId,
			String goodsId) throws Exception {
		String sql = "delete coopSupplierGoods where userId=? and supplierId=? and goodsId=?";
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.setString(1, userId);
		pstmt.setString(2, supplierId);
		pstmt.setString(3, goodsId);
		pstmt.executeUpdate();
	}

	/***************************************************** �����˵� *************************************************/

	public String pageInBill(Connection con, String userId) throws Exception {
		String sql = "select count(*) from inBill where ibUserId=?";
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.setString(1, userId);
		ResultSet rs = pstmt.executeQuery();
		if (rs.next()) {
			return rs.getString(1);
		}
		return "0";
	}
	// ���ô洢���̣�����Ʒ���в�ѯ
	public List<InBill> listInBill(Connection con, String userId, String pageSize, String page)
			throws Exception {
		List<InBill> inBills = new ArrayList<InBill>();

		String sql = "{call proInBillQuery(?,?,?)}";
		CallableStatement cstmt = con.prepareCall(sql);
		cstmt.setString(1, userId);
		cstmt.setString(2, pageSize);
		cstmt.setString(3, page);
		ResultSet rs = cstmt.executeQuery();

		String ibId = "";
		String goodsId = "";
		String goodsName = "";
		String goodsUnit = "";
		String goodsInUnitPrice = "";
		String goodsImage = "";
		String goodsCount = "";
		String ibTotalPrice = "";
		String goodsSupplierId = "";
		String goodsSupplierName = "";
		String goodsSupplierPhone = "";
		String goodsSupplierAddress = "";
		String ibTime = "";

		while (rs.next()) {
			ibId = rs.getString("ibId");
			goodsId = rs.getString("goodsId");
			goodsName = rs.getString("goodsName");
			goodsUnit = rs.getString("goodsUnit");
			goodsInUnitPrice = rs.getString("goodsInUnitPrice");
			goodsImage = Constant.IMAGE_URL + rs.getString("goodsImage");
			goodsCount = rs.getString("ibGoodsCount");
			ibTotalPrice = rs.getFloat("ibTotalPrice") + "";
			goodsSupplierId = rs.getString("supplierId");
			goodsSupplierName = rs.getString("supplierName");
			goodsSupplierPhone = rs.getString("supplierPhone");
			goodsSupplierAddress = rs.getString("supplierAddress");
			ibTime = rs.getString("ibTime");
			InBill inBill = new InBill(ibId, goodsId, goodsName, goodsUnit,
					goodsImage, goodsInUnitPrice, goodsCount, ibTotalPrice,
					goodsSupplierId, goodsSupplierName, goodsSupplierPhone,
					goodsSupplierAddress, ibTime);

			inBills.add(inBill);
		}
		this.closeRs(rs);
		return inBills;
	}

	public void saveInBill(Connection con, String userId, String goodsId,
			String goodsCount, String supplierId, String time)
			throws SQLException {

		String sql = "{call proInBillSave(?,?,?,?,?)}";
		CallableStatement cstmt = con.prepareCall(sql);
		cstmt.setString(1, userId);
		cstmt.setString(2, goodsId);
		cstmt.setString(3, goodsCount);
		cstmt.setString(4, supplierId);
		cstmt.setString(5, time);
		cstmt.execute();
		// TODO success
	}

	public void updateInBill(Connection con, String userId, String ibId,
			String goodsId, String goodsCount, String supplierId)
			throws SQLException {

		String sql = "{call proInBillUpdate(?,?,?,?,?)}";
		CallableStatement cstmt = con.prepareCall(sql);
		cstmt.setString(1, userId);
		cstmt.setString(2, ibId);
		cstmt.setString(3, goodsId);
		cstmt.setString(4, goodsCount);
		cstmt.setString(5, supplierId);
		cstmt.execute();
		// TODO ���½����˵���Ϣ�ɹ�
	}

	/***************************************************** �����˵� *************************************************/

	// �����˵�������
	public String pageOutBill(Connection con, String userId) throws Exception {
		String sql = "select count(*) from outBill where obUserId=?";
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.setString(1, userId);
		ResultSet rs = pstmt.executeQuery();
		if (rs.next()) {
			return rs.getString(1);
		}
		return "0";
	}
	
	// ���ô洢���̣��Գ����˵����в�ѯ
	public List<OutBill> listOutBill(Connection con, String userId, String pageNo)
			throws Exception {
		List<OutBill> outBills = new ArrayList<OutBill>();
		String sql = "{call proOutBillQuery(?,?,?)}";
		CallableStatement cstmt = con.prepareCall(sql);
		cstmt.setString(1, userId);
		cstmt.setString(2, Constant.PAGE_SIZE);
		cstmt.setString(3, pageNo);
		ResultSet rs = cstmt.executeQuery();

		String obId = "";
		String goodsId = "";
		String goodsName = "";
		String goodsUnit = "";
		String goodsOutUnitPrice = "";
		String goodsImage = "";
		String goodsCount = "";
		String obTotalPrice = "";
		String customerId = "";
		String customerName = "";
		String customerPhone = "";
		String customerAddress = "";
		String obPayStatus = "";
		String deliverId = "";
		String deliverMan = "";
		String deliverPhone = "";
		String deliverManStatus = "";
		String deliverStatus = "";
		String obTime = "";

		while (rs.next()) {
			obId = rs.getString("obId");
			goodsId = rs.getString("goodsId");
			goodsName = rs.getString("goodsName");
			goodsUnit = rs.getString("goodsUnit");
			goodsOutUnitPrice = rs.getString("goodsOutUnitPrice");
			goodsImage = Constant.IMAGE_URL + rs.getString("goodsImage");
			goodsCount = rs.getString("obGoodsCount");
			obTotalPrice = rs.getFloat("obTotalPrice") + "";
			customerId = rs.getString("customerId");
			customerName = rs.getString("customerName");
			customerPhone = rs.getString("customerPhone");
			customerAddress = rs.getString("customerAddress");
			obPayStatus = rs.getString("obPayStatus");
			deliverId = rs.getString("deliverId");
			deliverMan = rs.getString("deliverMan");
			deliverPhone = rs.getString("deliverPhone");
			deliverManStatus = rs.getString("deliverManStatus");

			deliverStatus = rs.getString("obDeliverStatus");
			if (deliverStatus.equals("-1")) {
				deliverStatus = "δ����";
			} else if (deliverStatus.equals("0")) {
				deliverStatus = "������";
			} else {
				deliverStatus = "�������";
			}
			obTime = rs.getString("obTime");
			OutBill outBill = new OutBill(obId, goodsId, goodsName, goodsUnit,
					goodsOutUnitPrice, goodsImage, goodsCount, obTotalPrice,
					customerId, customerName, customerPhone, customerAddress,
					obPayStatus, deliverId, deliverMan, deliverPhone,
					deliverManStatus, deliverStatus, obTime);

			outBills.add(outBill);
		}
		this.closeRs(rs);
		return outBills;
	}
	
	public OutBill detailOutBill(Connection con, String userId, String obId)
			throws Exception {
		String sql = "select obId,goodsId,goodsName,goodsUnit,goodsOutUnitPrice,goodsImage,obGoodsCount," +
				"goodsOutUnitPrice*obGoodsCount obTotalPrice,customerId,customerName,customerPhone,customerAddress," +
				"obPayStatus,deliverId,deliverMan,deliverPhone,deliverManStatus,obDeliverStatus,obTime from outBill," +
				"goods,customer,deliver where obUserId = ? and obId = ? and goodsId = obGoodsId and customerId = obCustomerId";
		CallableStatement cstmt = con.prepareCall(sql);
		cstmt.setString(1, userId);
		cstmt.setString(2, obId);
		ResultSet rs = cstmt.executeQuery();

//		String obId = "";
		String goodsId = "";
		String goodsName = "";
		String goodsUnit = "";
		String goodsOutUnitPrice = "";
		String goodsImage = "";
		String goodsCount = "";
		String obTotalPrice = "";
		String customerId = "";
		String customerName = "";
		String customerPhone = "";
		String customerAddress = "";
		String obPayStatus = "";
		String deliverId = "";
		String deliverMan = "";
		String deliverPhone = "";
		String deliverManStatus = "";
		String deliverStatus = "";
		String obTime = "";

		if (rs.next()) {
			obId = rs.getString("obId");
			goodsId = rs.getString("goodsId");
			goodsName = rs.getString("goodsName");
			goodsUnit = rs.getString("goodsUnit");
			goodsOutUnitPrice = rs.getString("goodsOutUnitPrice");
			goodsImage = Constant.IMAGE_URL_LOCAL + rs.getString("goodsImage");
			goodsCount = rs.getString("obGoodsCount");
			obTotalPrice = rs.getFloat("obTotalPrice") + "";
			customerId = rs.getString("customerId");
			customerName = rs.getString("customerName");
			customerPhone = rs.getString("customerPhone");
			customerAddress = rs.getString("customerAddress");
			obPayStatus = rs.getString("obPayStatus");
			deliverId = rs.getString("deliverId");
			deliverMan = rs.getString("deliverMan");
			deliverPhone = rs.getString("deliverPhone");
			deliverManStatus = rs.getString("deliverManStatus");

			deliverStatus = rs.getString("obDeliverStatus");
			if (deliverStatus.equals("-1")) {
				deliverStatus = "δ����";
			} else if (deliverStatus.equals("0")) {
				deliverStatus = "������";
			} else {
				deliverStatus = "�������";
			}
			obTime = rs.getString("obTime");
			OutBill outBill = new OutBill(obId, goodsId, goodsName, goodsUnit,
					goodsOutUnitPrice, goodsImage, goodsCount, obTotalPrice,
					customerId, customerName, customerPhone, customerAddress,
					obPayStatus, deliverId, deliverMan, deliverPhone,
					deliverManStatus, deliverStatus, obTime);

//			outBills.add(outBill);
			return outBill;
		}
		this.closeRs(rs);
		return null;
	}
	
	public void saveOutBill(Connection con, String userId, String goodsId,
			String goodsCount, String customerId, String payStatus,
			String deliverId, String deliverStatus, String time)
			throws SQLException {

		String sql = "{call proOutBillSave(?,?,?,?,?,?,?,?)}";
		CallableStatement cstmt = con.prepareCall(sql);
		cstmt.setString(1, userId);
		cstmt.setString(2, goodsId);
		cstmt.setString(3, goodsCount);
		cstmt.setString(4, customerId);
		cstmt.setString(5, payStatus);
		cstmt.setString(6, deliverId);
		cstmt.setString(7, deliverStatus);
		cstmt.setString(8, time);
		cstmt.execute();
		// TODO success
	}

	public void updateOutBill(Connection con, String userId, String obId,
			String goodsId, String goodsCount, String customerId,
			String payStatus, String deliverId, String deliverStatus)
			throws SQLException {

		String sql = "{call proOutBillUpdate(?,?,?,?,?,?,?,?)}";
		CallableStatement cstmt = con.prepareCall(sql);
		cstmt.setString(1, userId);
		cstmt.setString(2, obId);
		cstmt.setString(3, goodsId);
		cstmt.setString(4, goodsCount);
		cstmt.setString(5, customerId);
		cstmt.setString(6, payStatus);
		cstmt.setString(7, deliverId);
		cstmt.setString(8, deliverStatus);
		cstmt.execute();
		// TODO ���½����˵���Ϣ�ɹ�
	}

	// TODO ���ӿ�
	/*********************************************** ��� ********************************/
	// ��ѯ�����Ϣ
	public List<Repertory> listRepertory(Connection con, String userId)
			throws Exception {

		List<Repertory> Repertories = new ArrayList<Repertory>();

		// ��ѯ��sql���
		String sql = "select repertoryId,goodsId,goodsName,goodsUnit,repertoryGoodsCount,goodsImage from repertory,goods "
				+ "where repertoryUserId=? and repertoryGoodsId=goodsId";
		// ��ȡPreparedStatement����
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.setString(1, userId);
		// ִ�в�ѯ
		ResultSet rs = pstmt.executeQuery();
		String repertoryId = "";
		String repertoryGoodsId = "";
		String repertoryGoodsName = "";
		String repertoryGoodsUnit = "";
		String repertoryGoodsCount = "";
		String repertoryGoodsImage = "";
		while (rs.next()) {
			repertoryId = rs.getString("repertoryId");
			repertoryGoodsId = rs.getString("goodsId");
			repertoryGoodsName = rs.getString("goodsName");
			repertoryGoodsUnit = rs.getString("goodsUnit");
			repertoryGoodsCount = rs.getString("repertoryGoodsCount");
			repertoryGoodsImage = Constant.IMAGE_URL + rs.getString("goodsImage");
			Repertory repertory = new Repertory(repertoryId, repertoryGoodsId,
					repertoryGoodsName, repertoryGoodsUnit,
					repertoryGoodsCount, repertoryGoodsImage);
			Repertories.add(repertory);
		}
		this.closeRs(rs);
		return Repertories;
	}

	/*********************************************** �ͻ�Ա ********************************/
	
	public String loginDeliver(Connection con, String userName, String userPwd)
			throws Exception {
		String sql = "select deliverId from deliver where deliverPhone=? and deliverPassword=?";
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.setString(1, userName);
		pstmt.setString(2, userPwd);
		ResultSet rs = pstmt.executeQuery();
		if (rs.next())
			return rs.getString("deliverId");
		else
			return null;
	}
	public OutBillDetail missionDeliver(Connection con, String deliverId)
			throws Exception {

		String sql = "select obId,goodsId,goodsName,goodsUnit,goodsOutUnitPrice,goodsImage,obGoodsCount,"+
		"goodsOutUnitPrice*obGoodsCount obTotalPrice,customerId,customerName,customerPhone,"+
		"customerAddress,obPayStatus,deliverId,deliverMan,deliverPhone,deliverManStatus,obDeliverStatus,obUserId,obTime from"+ 
		" outBill,goods,customer,deliver where obDeliverStatus = 0 and obDeliverId = ? and obGoodsId = goodsId"+
		" and obCustomerId = customerId and obDeliverId = deliverId";
		
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.setString(1, deliverId);
		ResultSet rs = pstmt.executeQuery();

		String obId = "";
		String goodsId = "";
		String goodsName = "";
		String goodsUnit = "";
		String goodsOutUnitPrice = "";
		String goodsImage = "";
		String goodsCount = "";
		String obTotalPrice = "";
		String customerId = "";
		String customerName = "";
		String customerPhone = "";
		String customerAddress = "";
		String obPayStatus = "";
		String deliverMan = "";
		String deliverPhone = "";
		String deliverManStatus = "";
		String deliverStatus = "";
		String userId = "";
		String obTime = "";

		if (rs.next()) {
			obId = rs.getString("obId");
			goodsId = rs.getString("goodsId");
			goodsName = rs.getString("goodsName");
			goodsUnit = rs.getString("goodsUnit");
			goodsOutUnitPrice = rs.getString("goodsOutUnitPrice");
			goodsImage = Constant.IMAGE_URL_LOCAL + rs.getString("goodsImage");
			goodsCount = rs.getString("obGoodsCount");
			obTotalPrice = rs.getString("obTotalPrice");
			customerId = rs.getString("customerId");
			customerName = rs.getString("customerName");
			customerPhone = rs.getString("customerPhone");
			customerAddress = rs.getString("customerAddress");
			obPayStatus = rs.getString("obPayStatus");
			deliverId = rs.getString("deliverId");
			deliverMan = rs.getString("deliverMan");
			deliverPhone = rs.getString("deliverPhone");
			deliverManStatus = rs.getString("deliverManStatus");

			deliverStatus = rs.getString("obDeliverStatus");
			if (deliverStatus.equals("-1")) {
				deliverStatus = "δ����";
			} else if (deliverStatus.equals("0")) {
				deliverStatus = "������";
			} else {
				deliverStatus = "�������";
			}
			userId = rs.getString("obUserId");
			obTime = rs.getString("obTime");
			OutBillDetail outBill = new OutBillDetail(obId, goodsId, goodsName, goodsUnit,
					goodsOutUnitPrice, goodsImage, goodsCount, obTotalPrice,
					customerId, customerName, customerPhone, customerAddress,
					obPayStatus, deliverId, deliverMan, deliverPhone,
					deliverManStatus, deliverStatus, userId, obTime);
			return outBill;
		} else {
			return null;
		}
	}
	// ��ѯ�ͻ�Ա��Ϣ
	public List<Deliver> listDeliver(Connection con, String userId)
			throws Exception {

		List<Deliver> delivers = new ArrayList<Deliver>();

		// ��ѯ��sql���
		String sql = "select deliverId,deliverMan,deliverPhone,deliverManStatus from deliver "
				+ "where userId=?";
		// ��ȡPreparedStatement����
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.setString(1, userId);
		// ִ�в�ѯ
		ResultSet rs = pstmt.executeQuery();
		String deliverId = "";
		String deliverMan = "";
		String deliverPhone = "";
		String deliverManStatus = "";
		while (rs.next()) {
			deliverId = rs.getString("deliverId");
			deliverMan = rs.getString("deliverMan");
			deliverPhone = rs.getString("deliverPhone");
			deliverManStatus = rs.getString("deliverManStatus");
			Deliver deliver = new Deliver(deliverId, deliverMan, deliverPhone,
					deliverManStatus);
			delivers.add(deliver);
		}
		this.closeRs(rs);
		return delivers;
	}

	// ��ѯ�����ͻ�Ա��Ϣ
	public List<Deliver> listDeliverFree(Connection con, String userId)
			throws Exception {

		List<Deliver> delivers = new ArrayList<Deliver>();

		// ��ѯ��sql���
		String sql = "select deliverId,deliverMan,deliverPhone,deliverManStatus from deliver "
				+ "where userId=? and deliverManStatus = 0";
		// ��ȡPreparedStatement����
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.setString(1, userId);
		// ִ�в�ѯ
		ResultSet rs = pstmt.executeQuery();
		String deliverId = "";
		String deliverMan = "";
		String deliverPhone = "";
		String deliverManStatus = "";
		while (rs.next()) {
			deliverId = rs.getString("deliverId");
			deliverMan = rs.getString("deliverMan");
			deliverPhone = rs.getString("deliverPhone");
			deliverManStatus = rs.getString("deliverManStatus");
			Deliver deliver = new Deliver(deliverId, deliverMan, deliverPhone,
					deliverManStatus);
			delivers.add(deliver);
		}
		this.closeRs(rs);
		return delivers;
	}

	/************************************** ���񱨱� *******************************************/
	public List<FinanceAll> listFinanceMonthDay(Connection con, String userId,
			String year) throws SQLException {
		List<FinanceAll> finances = new ArrayList<FinanceAll>();

		float inBillPrice = 0;
		float outBillPrice = 0;
		float profit = 0;
		for (int i = 1; i <= 12; i++) {
			Map<String, String> hm = Util.getTimeInterval(year, "" + i);
			String startTime = hm.get(Util.KEY_START_TIME);
			String endTime = hm.get(Util.KEY_END_TIME);
			inBillPrice = queryInBillPrice(con, userId, startTime, endTime);
			outBillPrice = queryOutBillPrice(con, userId, startTime, endTime);
			profit = outBillPrice - inBillPrice;
			FinanceAll financeAll = new FinanceAll(inBillPrice, outBillPrice, profit);
			
			List<Finance> list = listFinanceDay(con, userId, year, i + "");
			financeAll.setFinances(list);
			finances.add(financeAll);
		}
		return finances;
	}
	public List<Finance> listFinanceMonth(Connection con, String userId,
			String year) throws SQLException {
		List<Finance> finances = new ArrayList<Finance>();

		float inBillPrice = 0;
		float outBillPrice = 0;
		float profit = 0;
		for (int i = 1; i <= 12; i++) {
			Map<String, String> hm = Util.getTimeInterval(year, "" + i);
			String startTime = hm.get(Util.KEY_START_TIME);
			String endTime = hm.get(Util.KEY_END_TIME);
			inBillPrice = queryInBillPrice(con, userId, startTime, endTime);
			outBillPrice = queryOutBillPrice(con, userId, startTime, endTime);
			profit = outBillPrice - inBillPrice;
			Finance finance = new Finance(inBillPrice, outBillPrice, profit);
			finances.add(finance);
		}
		return finances;
	}
	// ���»�ȡÿ��Ĳ��񱨱�
	public List<Finance> listFinanceDay(Connection con, String userId,
			String year, String month) throws SQLException {
		List<Finance> finances = new ArrayList<Finance>();
		Map<String, String> interval = Util.getTimeInterval(year, month);

		int days = Util.getLastDayInMonth(year, month);
		float inBillPrice = 0;
		float outBillPrice = 0;
		float profit = 0;
		for (int i = 1; i <= days; i++) {
			Map<String, String> hm = Util.getTimeInterval(year, month, "" + i);
			String startTime = hm.get(Util.KEY_START_TIME);
			String endTime = hm.get(Util.KEY_END_TIME);
			inBillPrice = queryInBillPrice(con, userId, startTime, endTime);
			outBillPrice = queryOutBillPrice(con, userId, startTime, endTime);
			profit = outBillPrice - inBillPrice;
			Finance finance = new Finance(inBillPrice, outBillPrice, profit);
			finances.add(finance);
		}
		return finances;
	}

	// ʱ�������ڳ����ܼ۸�
	private float queryOutBillPrice(Connection con, String userId,
			String startTime, String endTime) throws SQLException {
		System.out.println("out start = " + startTime + "   end = " + endTime);
		float price = 0;
		// ��ѯ��sql���
		String sql = "select sum(goodsOutUnitPrice*obGoodsCount) totalPrice from outBill,goods where obUserId = ? and "
				+ "obTime > ? and obTime < ? and goods.goodsId = outBill.obGoodsId";
		// ��ȡPreparedStatement����
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.setString(1, userId);
		pstmt.setString(2, startTime);
		pstmt.setString(3, endTime);
		// ִ�в�ѯ
		ResultSet rs = pstmt.executeQuery();
		if (rs.next()) {
			price = rs.getFloat("totalPrice");
			System.out.println("-----out-----" + price + "------out------");
		}
		return price;
	}

	// ʱ�������ڽ����ܼ۸�
	private float queryInBillPrice(Connection con, String userId,
			String startTime, String endTime) throws SQLException {
		System.out.println("in start = " + startTime + "   end = " + endTime);
		float price = 0;
		// ��ѯ��sql���
		String sql = "select sum(goodsInUnitPrice*ibGoodsCount) totalPrice from inBill,goods where ibUserId = ? and "
				+ "ibTime > ? and ibTime < ? and goods.goodsId = inBill.ibGoodsId";
		// ��ȡPreparedStatement����
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.setString(1, userId);
		pstmt.setString(2, startTime);
		pstmt.setString(3, endTime);
		// ִ�в�ѯ
		ResultSet rs = pstmt.executeQuery();
		if (rs.next()) {
			price = rs.getFloat("totalPrice");
			System.out.println("----in------" + price + "-------in-----");
		}
		return price;
	}

	// �������
	public void updateDeliverStatus(Connection con, String userId, String obId, String deliverId) throws Exception{
		String sql = "{call proDeliverUpdate(?,?,?)}";
		CallableStatement cstmt = con.prepareCall(sql);
		cstmt.setString(1, userId);
		cstmt.setString(2, obId);
		cstmt.setString(3, deliverId);
		cstmt.execute();
	}

	public void saveDeliver(Connection con, String userId, String phone,
			String name, String password) throws Exception {
		String sql = "insert into deliver values(?,?,?,0,?)";
		// ��ȡPreparedStatement����
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.setString(1, name);
		pstmt.setString(2, phone);
		pstmt.setString(3, password);
		pstmt.setString(4, userId);
		pstmt.execute();
	}

}
