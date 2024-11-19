package com.imbos.chat.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.database.sqlite.SQLiteCursor;

import com.imbos.chat.app.ChatApp;
import com.imbos.chat.base.BaseApplication;
import com.imbos.chat.util.StringUtil;

public class DbManager {
	
	public static void saveSession(String uid,String lastMessage,String lastDate){
		
		String sql_save = " REPLACE INTO SESSIONS (LAST_MESSAGE,LAST_DATE,UID) VALUES (?,?,?)";
		BaseApplication.getDbHelper().execSQL(sql_save, new String[]{lastMessage,lastDate,uid});
	}
	public static void delSession(String uid){
		String sql_save = " DELETE FROM SESSIONS WHERE UID=?";
		BaseApplication.getDbHelper().execSQL(sql_save, new String[]{uid});
	}
	
	public static void topSession(String uid){
		String sql_save = " UPDATE SESSIONS SET ORDER_NUM = ? WHERE UID=?";
		BaseApplication.getDbHelper().execSQL(sql_save, new String[]{String.valueOf(new Date().getTime()),uid});
	}

	/**
	 * 保存好友信息
	 * @param uid
	 * @param name
	 * @param head
	 * @param start
	 * @param real
	 * @param doing
	 * @param pingy
	 * @param alias
	 */
	public static void saveFriends(String uid, String name, String head,
			String start, String real, String doing, String pingy, String alias) {
		String sql_save = " REPLACE INTO FRIEND_ALL (UID,NAME,HEAD,STAR,REAL,DOING,PINGYIN,ALIAS) " +
				" VALUES (?,?,?,?,?,?,?,?)";
		BaseApplication.getDbHelper().execSQL(
				sql_save,
				new String[] { uid, name, head, start, real, doing, pingy,
						alias });
	}
	
	/**
	 * 修改好友备注
	 * 修改者名字 
	 * 修改日期 
	 * 修改内容
	 * @author wanxianze@gmail.com 2013-4-8
	 * @param uid
	 * @param alias
	 * void
	 */
	public static void setFriendAlias(String uid,String alias){
		
		String sql ="UPDATE FRIEND_ALL SET ALIAS=? WHERE UID = ?" ;
		BaseApplication.getDbHelper().execSQL(sql,new String[]{alias,uid});
	}
	public static void setFriendStar(String uid,int star){
		String sql1 ="UPDATE FRIEND_ALL SET STAR=? WHERE UID = ?" ;
		String sql2 ="UPDATE MEMBER_DETAIL SET STAR=? WHERE UID = ?" ;
		BaseApplication.getDbHelper().execSQL(sql1,new Object[]{star,uid});
		BaseApplication.getDbHelper().execSQL(sql2,new Object[]{star,uid});
	}
	
	/**
	 * 删除好友
	 * 修改者名字 
	 * 修改日期 
	 * 修改内容
	 * @author wanxianze@gmail.com 2013-4-8
	 * @param uid
	 * void
	 */
	public static void delFriend(String uid){
		String sql =" DELETE FROM FRIEND_ALL WHERE UID = ?" ;
		BaseApplication.getDbHelper().execSQL(sql,new String[]{uid});
	}
	
	
	/**
	 * 
	 * @param args[UID,NAME,HEAD,START,REAL,DOING,PINGYIN,ALIAS]
	 */
	public static void saveFriends(String[] args){
		String sql_save = " REPLACE INTO FRIEND_ALL (UID,NAME,HEAD,STAR,REAL,DOING,PINGYIN,ALIAS) " +
				" VALUES (?,?,?,?,?,?,?,?)";
		BaseApplication.getDbHelper().execSQL(
				sql_save,args);
	}
	/**
	 * 
	 * @return [UID,NAME,HEAD,STAR,REAL,DOING,PINGYIN,ALIAS,ALIAS_PINGYIN,NOTE]
	 */
	public static SQLiteCursor queryFriends(int isRecommend){
		String sql = 
				" SELECT UID as _id,ifnull(ALIAS,NAME) AS NAME,HEAD,STAR,REAL,DOING,\n" +
				"	CASE WHEN ALIAS ISNULL THEN PINGYIN ELSE ALIAS_PINGYIN END AS PY,\n" +
				" ALIAS,ALIAS_PINGYIN,NOTE\n" +
				" FROM FRIEND_ALL WHERE STATUS = ? ORDER BY STAR DESC,PY ASC";
		return BaseApplication.getDbHelper().query(sql,new String[]{Integer.toString(isRecommend)});
	
	}
	
	public static SQLiteCursor queryFriend(String uid){
		String sql = "SELECT UID as _id,ifnull(ALIAS,NAME) AS NAME,HEAD,STAR,REAL,DOING,PINGYIN,ALIAS,ALIAS_PINGYIN " +
				" FROM FRIEND_ALL WHERE UID = ? ORDER BY PINGYIN";
		return BaseApplication.getDbHelper().query(sql,new String[]{uid});
	}
	
	public static SQLiteCursor queryFriends(){
		return queryFriends(1);
	}
	/**
	 * 
	 * @return [UID,NAME,HEAD,STAR,REAL,DOING,PY,ALIAS,ALIAS_PINGYIN,NOTE]
	 */
	public static SQLiteCursor queryRecommend(){
		return queryFriends(0);
	}
	
	/**
	 * 
	 * @return [UID,HEAD,NAME,LAST_MESSAGE,LAST_DATE,UNREAER]
	 */
	public static SQLiteCursor querySessions(){
		String sql = "SELECT S.UID,F.HEAD,ifnull(F.ALIAS,F.NAME) AS NAME,LAST_MESSAGE,LAST_DATE,COUNT(M.TOS) AS UNREAER" +
				" FROM SESSIONS S" +
				" LEFT JOIN FRIEND_ALL F ON S.UID = F.UID " +
				" LEFT JOIN MESSAGES M ON M.FROMS = S.UID AND READER=0 " +
				" GROUP BY S.UID " +
				" ORDER BY ORDER_NUM DESC,LAST_DATE DESC LIMIT 100";
		return BaseApplication.getDbHelper().query(sql);
		
	}
	
	public static void clearAllMessages(){
		ChatApp.getDbHelper().execSQL("DELETE FROM MESSAGES");
		ChatApp.getDbHelper().execSQL("DELETE FROM SESSIONS");
	}
	
	public static String findUserName(String id){
		String sql = " SELECT ifnull(F.ALIAS,F.NAME) as NAME FROM FRIEND_ALL F WHERE F.UID = ?"+
					 " UNION "+
					 " SELECT NAME FROM MEMBER_DETAIL M WHERE M.UID = ? ";
		return ChatApp.getDbHelper().executeScalar(sql, new String[]{id,id});
	}
	
	/**
	 * 搜索企业好友
	 * 函数功能说明 
	 * 修改者名字 
	 * 修改日期 
	 * 修改内容
	 * @author wanxianze@gmail.com 2013-6-6
	 * void
	 */
	public static SQLiteCursor searchEPMember(){
		String sql = " SELECT EID,NAME,LOGO,VIP,REAL,INDUSTRY " +
				" FROM ENTERPRISE_FINDID";
		return ChatApp.getDbHelper().query(sql);
	}
	/**
	 * 
	 * @return [_id,NAME,HEAD,STAR,REAL,DOING,ALIAS ,SEX ,ENTERPRISE ,ISFRIEND ,
	 *  BIRTH ,GRADUATE ,INDUSTRY ,PROFESSION ,POST ,INTEREST ,AREA ,DISTANCE ,UNUM]
	 */
	public static SQLiteCursor queryMembers(String action){
		
		String sql = "SELECT UID as _id,NAME,HEAD,STAR,REAL,DOING,ALIAS ,SEX ,ENTERPRISE ,ISFRIEND ,"
					+" BIRTH ,GRADUATE ,INDUSTRY ,PROFESSION ,POST ,INTEREST ,AREA ,DISTANCE ,UNUM" 
					+" FROM MEMBER_DETAIL WHERE 1=1 ";
		
		if(!StringUtil.isEmpty(action)){
			sql += " AND SRC_ACTION= ? ";
			return ChatApp.getDbHelper().query(sql,new String[]{action});
		}else{
			return ChatApp.getDbHelper().query(sql);
		}
		
	}
	
	public static void delMembers(String action){
		delTable("MEMBER_DETAIL", action);
	}
	/**
	 * 
	 * @return [UID AS _id, NAME ,HEAD,STAR ,REAL ,DOING ,ALIAS ,SEX ,ENTERPRISE ,ISFRIEND ,
	 *  BIRTH ,GRADUATE ,INDUSTRY ,PROFESSION ,POST ,INTEREST ,AREA ,DISTANCE ,UNUM]
	 */
	public static SQLiteCursor queryMemberDetail(String uid){
		String sql = "SELECT UID AS _id, NAME ,HEAD,STAR ,REAL ,DOING ,ALIAS ,SEX ,ENTERPRISE ,ISFRIEND ,"
					+" BIRTH ,GRADUATE ,INDUSTRY ,PROFESSION ,POST ,INTEREST ,AREA ,DISTANCE ,UNUM "
					+" FROM MEMBER_DETAIL WHERE UID = ?";
		return ChatApp.getDbHelper().query(sql,new String[]{uid});
	}
	
	
	public static void saveMessage(String id,String froms,String tos,String content,String createDate,int status){
		String sql = "REPLACE INTO MESSAGES (ID,FROMS,TOS,CONTENT,CREATE_DATE,RECEIVE_DATE,STATUS) " +
				"	VALUES (?,?,?,?,?,?,?)";
		//插入消息表
		ChatApp.getDbHelper().execSQL(sql, new Object[]{id,froms,tos,content,createDate,createDate,status});
	
	}
	
	/**
	 * 
	 * @param uid
	 * @return [_id,FROMS,TOS,CONTENT,CREATE_DATE,DIRECT]co
	 */
	public static SQLiteCursor queryMessages(String uid){
		String sql = " SELECT ID as _id,FROMS,TOS,CONTENT,M.RECEIVE_DATE,CASE WHEN M.FROMS = ? THEN 1 " +
				" ELSE 0 END AS DIRECT " +
				" FROM MESSAGES M "+
				" LEFT JOIN FRIEND_ALL F1 ON F1.UID=M.FROMS "+
				" LEFT JOIN FRIEND_ALL F2 ON F2.UID=M.TOS "+
				" WHERE 1 = 1 "+
				" AND (M.FROMS = ? OR M.TOS = ?) "+
				" AND (M.CREATE_DATE BETWEEN date('now','-7 day') AND date('now','+1 day') OR "+
						" M.RECEIVE_DATE BETWEEN date('now','-7 day') AND date('now','+1 day'))"+
				" ORDER BY M.RECEIVE_DATE DESC LIMIT 20";
		return ChatApp.getDbHelper().query(sql, new String[]{uid,uid,uid});
	}
	
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public static SQLiteCursor queryMessage(String id){
		String sql = "SELECT ID AS _id,FROM,TOS,CONTENT,M.CREATE_DATE,FROM_NAME,TO_NAME " +
				" FROM MESSAGES M" +
				" LEFT JOING FRIEND_ALL ON F1.UID=M.FROMS WHERE ID=?";
		return ChatApp.getDbHelper().query(sql, new String[]{id});
		
	}
	public static String queryMessageFroms(String id){
		String sql = "SELECT ifnull(F.ALIAS,F.NAME) AS NAME " +
				" FROM MESSAGES M" +
				" LEFT JOIN FRIEND_ALL F ON M.FROMS = F.UID"+
				" WHERE M.FROMS = ?";
		return ChatApp.getDbHelper().executeScalar(sql, new String[]{id});
	}
	public static void updateMessageRead(String id){
		String sql =" UPDATE MESSAGES SET READER = ? WHERE ID=?";
		ChatApp.getDbHelper().execSQL(sql, new Object[]{1,id});
	}
	public static void updateChatMessage(String froms){
		String sql ="UPDATE MESSAGES SET READER = ? WHERE FROMS = ? ";
		ChatApp.getDbHelper().execSQL(sql, new Object[]{1,froms});
	}
	public static void updateMessage(String id,int status,String receiveDate){
		String sql ="UPDATE MESSAGES SET STATUS = ?,RECEIVE_DATE=?  WHERE ID=?";
		ChatApp.getDbHelper().execSQL(sql, new Object[]{status,receiveDate,id});
	}
	public static void updateMessage(String id,int status,String content,String receiveDate){
		String sql ="UPDATE MESSAGES SET STATUS = ?,CONTENT=?,RECEIVE_DATE=?  WHERE ID=?";
		ChatApp.getDbHelper().execSQL(sql, new Object[]{status,content,receiveDate,id});
	}

	public static void deleteFriends() {
		String sql ="DELETE FROM FRIEND_ALL ";
		ChatApp.getDbHelper().execSQL(sql);
	}
	
	public static List<Map<String,?>> queryRegion(String parentId){
		return queryDicItems("area",parentId);
	}
	public static List<Map<String,?>> queryDicItems(String dicName,String parentId){
		String sql = "SELECT D.ID,D.NAME,D.PARENT_ID,count(D.ID) AS SUB \n" +
			"FROM DIC D \n" +
			"LEFT JOIN DIC S ON S.PARENT_ID = D.ID \n" +
			"WHERE 1=1 ";
			if(StringUtil.isEmpty(parentId)){
				sql += " AND D.PARENT_ID = (SELECT ID FROM DIC WHERE EN_NAME = '"+dicName+"')";
			}else{
				sql += " AND D.PARENT_ID = "+parentId;
			}
			sql += " GROUP BY D.ID";
		return ChatApp.getDbHelper().rawQuery(sql,new String[]{});
	}
	public static List<Map<String,?>> queryDicCategoryItems(String dicName){
		String sql = "SELECT D.ID AS ID,D.NAME AS NAME,D.PARENT_ID,T.NAME as CATEGORY\n" +
				" FROM DIC D INNER JOIN (\n" +
				" 	SELECT S.ID,S.NAME,S.PARENT_ID FROM DIC A \n" +
				"	LEFT JOIN DIC S ON S.PARENT_ID = A.ID WHERE A.EN_NAME = ? \n" +
				" ) AS T\n" +
				"ON D.PARENT_ID = T.ID \n" +
				"ORDER BY D.PARENT_ID,D.ORD";
		return ChatApp.getDbHelper().rawQuery(sql,new String[]{dicName});
	}
	
	
	public static int updateTable(String tabName){
		String sql_del = "DELETE FROM "+tabName +
				" WHERE UPDATE_DATE < (SELECT UPDATE_DATE FROM "+tabName+" ORDER BY UPDATE_DATE DESC LIMIT 1)";
		String sql_query = "SELECT COUNT(*) FROM "+tabName +
				" WHERE UPDATE_DATE < (SELECT UPDATE_DATE FROM "+tabName+" ORDER BY UPDATE_DATE DESC LIMIT 1)";
		String result = ChatApp.getDbHelper().executeScalar(sql_query, new String[]{});
		ChatApp.getDbHelper().execSQL(sql_del,new Object[]{});
		return StringUtil.parseInt(result);
	}
	public static void delTable(String tabName,String action){
		String sql = "DELETE FROM "+tabName+" WHERE 1=1 ";
		if(!StringUtil.isEmpty(action)){
			sql += " AND SRC_ACTION = ?";
			ChatApp.getDbHelper().execSQL(sql,new Object[]{action});
		}else{
			ChatApp.getDbHelper().execSQL(sql,new Object[]{});
		}
		
		
	}
	
	/**
	 * 删除企业找资金
	 */
	public static void delFind_funds() {
		String sql = "DELETE FROM ENTERPRISE_FINDFUND";
		ChatApp.getDbHelper().execSQL(sql);
	}
	
	/**
	 * 删除企业找项目
	 */
	public static void delFind_subject() {
		String sql = "DELETE FROM ENTERPRISE_FINDPROJECT";
		ChatApp.getDbHelper().execSQL(sql);
	}
	
	/**
	 * 删除企业找商品
	 */
	public static void delFind_gs() {
		String sql = "DELETE FROM ENTERPRISE_FINDGS";
		ChatApp.getDbHelper().execSQL(sql);
	}
	
	/**
	 * 删除企业找好友
	 */
	public static void delEnterprise_FindID() {
		String sql = "DELETE FROM ENTERPRISESETTING_FINDENTERPRISEDETAIL";
		ChatApp.getDbHelper().execSQL(sql);
	}
	
	/**
	 * 查询企业找资金
	 */
	public static ArrayList<ArrayList<String>> getFind_fundsList(String currentTime) {
		String sql = "SELECT" +
				" FUNDNAME," +
				" '投资方式：' || d.NAME," +
				" '资金实力：' || i.name," +
				" '投资领域：' || c.name," +
				" FUNDINTRO," +
				" FUNDID," +
				" INVESTMENTWAY," +
				" FINANCIALSTRENGTH," +
				" INVESTMENTFIELD," +
				" e.NAME," +
				" EID" +
				" FROM" +
				" ENTERPRISE_FINDFUND e" +
				" INNER JOIN dic d ON d.[id] = INVESTMENTWAY" +
				" INNER JOIN dic i ON i.[id] = FINANCIALSTRENGTH" +
				" INNER JOIN dic c ON c.[id] = INVESTMENTFIELD" +
				" where e.UPDATE_DATE >= '" + currentTime + "'";
		return ChatApp.getDbHelper().queryData(sql.toString(), new String[]{});
	}
	
	/**
	 * 查询企业找项目
	 */
	public static ArrayList<ArrayList<String>> getFind_subjectList(String currentTime) {
		String sql = "SELECT" +
				" PROJECTNAME," +
				" '融资方式：' || d.NAME," +
				" '行业分类：' || i.name," +
				" '资产价值：' || c.name," +
				" '融资金额：' || di.name," +
				" PROJECTINTRO," +
				" PROJECTID," +
				" FINANCINGWAY," +
				" INDUSTRYCLASSIFICATION," +
				" ASSETVALUE," +
				" FINANCINGAMOUNT," +
				" e.NAME," +
				" EID" +
				" FROM" +
				" ENTERPRISE_FINDPROJECT e" +
				" LEFT JOIN DIC d ON d.[id] = e.FINANCINGWAY" +
				" LEFT JOIN DIC i ON i.[id] = e.INDUSTRYCLASSIFICATION" +
				" LEFT JOIN DIC c ON c.[id] = e.ASSETVALUE" +
				" LEFT JOIN DIC di ON di.[id] = e.FINANCINGAMOUNT" +
				" where e.UPDATE_DATE >= '" + currentTime + "'";
		return ChatApp.getDbHelper().queryData(sql.toString(), new String[]{});
	}
	
	/**
	 * 查询企业找商品服务
	 */
	public static ArrayList<ArrayList<String>> getFind_gs(String currentTime) {
		String sql = "SELECT" +
				" GSNAME," +
				" '所属行业：' || d.NAME," +
				" '产品分类：' || c.name || '-' || i.name," +
				" '所在地区：' || AREA," +
				" GSINTRO," +
				" GSID," +
				" INDUSTRYINVOLVED," +
				" PRODUCTCLASSIFICATION," +
				" e.NAME," +
				" EID" +
				" FROM" +
				" ENTERPRISE_FINDGS e" +
				" INNER JOIN DIC d ON d.[id] = e.INDUSTRYINVOLVED" +
				" INNER JOIN DIC i ON i.[id] = e.PRODUCTCLASSIFICATION" +
				" INNER JOIN DIC c ON c.[id] = (select PARENT_ID from DIC where id = e.PRODUCTCLASSIFICATION)" +
				" where e.UPDATE_DATE >= '" + currentTime + "'";
		return ChatApp.getDbHelper().queryData(sql.toString(), new String[]{});
	}
	
	/**
	 * 查询企业找好友
	 */
	public static ArrayList<ArrayList<String>> getEnterprise_FindID(String currentTime) {
		String sql = "SELECT" +
				" EID," +
				" e.NAME," +
				" d.NAME," +
				" LOGO," +
				" VIP," +
				" REAL," +
				" INDUSTRY," +
				" LONGITUDE," +
				" LATITUDE," +
				" DISTANCE" +
				" FROM" +
				" ENTERPRISESETTING_FINDENTERPRISEDETAIL e" +
				" LEFT JOIN DIC d ON d.[id] = e.INDUSTRY" +
				" where e.UPDATE_DATE >= '" + currentTime + "'";
		return ChatApp.getDbHelper().queryData(sql.toString(), new String[]{});
	}
	
	/**
	 * 插入查询历史数据
	 * @param args
	 */
	public static void insertQueryHistroy(Object[] args) {
		String sql = "insert into QUERY_HISTROY (QUERY_DATE, QUERY_CONDITION, QUERY_ID, TYPE, ID, QUERY_CONDITION_SHOW) values (?, ?, ?, ?, ?, ?)";
		ChatApp.getDbHelper().execSQL(sql, args);
	}
	
	/**
	 * 获取查询历史数据
	 * @param type
	 */
	public static ArrayList<ArrayList<String>> getQueryHistroy(String type) {
		String sql = "select QUERY_DATE, QUERY_CONDITION, QUERY_ID, TYPE, QUERY_CONDITION_SHOW from QUERY_HISTROY where TYPE = '" + type + "' order by query_date desc";
		return ChatApp.getDbHelper().queryData(sql.toString(), new String[]{});
	}
	
	/**
	 * 删除多余的查询历史数据
	 * @param type
	 */
	public static void delUnnecessaryQueryHistroy(String type, int count) {
		String sql = "delete from query_histroy where ID not in (select ID from query_histroy where type = '" + type 
				+ "' order by query_date desc limit " + count + ") and TYPE = '" + type + "'";
		ChatApp.getDbHelper().execSQL(sql);
	}
	
	/**
	 * 获取企业用户
	 * 函数功能说明 
	 * 修改者名字 
	 * 修改日期 
	 * 修改内容
	 * @author wanxianze@gmail.com 2013-6-6
	 * @return 
	 * SQLiteCursor [EID,NAME,LOGO,VIP,REAL,INDUSTRY]
	 */
	public static SQLiteCursor queryEPMembers() {
		String sql = "SELECT EID,NAME,LOGO,VIP,REAL,INDUSTRY FROM MEMBERSETTING_FINDATTENTION ";
		return ChatApp.getDbHelper().query(sql);
	}
	
	/**
	 * 
	 * @param uid
	 * @return [id,USERNAME,NAME,HEAD,BIRTH,AREA,C_DOING,CREDIT,C_ENTERPRISE,C_INDUSTRY ,
	 *  C_GRADUATE,C_PROFESSION,C_INTEREST,C_PROFESSION,C_POST,C_INTEREST,C_TYPE]
	 */
	public static Map<String,?> queryUserCard(String uid){
		
		
		String sql = "SELECT UID as _id,USERNAME,NAME,HEAD,SEX,BIRTH,AREA,C_DOING,CREDIT,C_ENTERPRISE,C_INDUSTRY, " +
				" C_GRADUATE,C_PROFESSION,C_INTEREST,C_PROFESSION,C_POST,C_INTEREST,C_TYPE " +
				" FROM MEMBERSETTING_FINDHOMEPAGES WHERE UID = ?";
		return ChatApp.getDbHelper().rawQueryTop(sql, new String[]{uid});
	}
	/**
	 * 
	 * @param uid
	 * @return [UID as _id,UNUM,EMAIL,EMAILCHECK,IDCARD,IDCARDCHECK,MOBILE,MOBILECHECK]
	 */
	public static Map<String,?> queryUserAccount(String uid){
		
		
		String sql = "SELECT UID as _id,UNUM,EMAIL,EMAILCHECK,IDCARD,IDCARDCHECK,MOBILE,MOBILECHECK" +
				" FROM MEMBERSETTING_FINDACCOUNT WHERE UID = ?";
		return ChatApp.getDbHelper().rawQueryTop(sql, new String[]{uid});
	}
	
	public static String getDicName(String ids) {
		if(StringUtil.isEmpty(ids)) {
			return "";
		}
		String[] idArray = ids.split(",");
		int length = idArray.length;
		StringBuffer name = new StringBuffer();
		String sql = "select NAME from DIC where ID = ?";
		SQLiteCursor cursor = null;
		for(int i = 0; i < length; i++) {
			cursor = ChatApp.getDbHelper().query(sql, new String[]{idArray[i]});
			if(cursor.getCount() > 0) {
				while(cursor.moveToNext()) {
					name.append(cursor.getString(0)).append(",");
				}
			}
		}
		String result = name.toString();
		if(name.toString().length() > 0) {
			result = name.substring(0, name.length() - 1);
		}
		return result;
	}
	
	/**
	 * 查询企业找资金
	 */
	public static ArrayList<ArrayList<String>> getEPCommentsList() {
		String sql = "SELECT" +
				" FUNDNAME," +
				" '投资方式：' || d.NAME," +
				" '资金实力：' || i.name," +
				" '投资领域：' || c.name," +
				" FUNDINTRO," +
				" FUNDID," +
				" INVESTMENTWAY," +
				" FINANCIALSTRENGTH," +
				" INVESTMENTFIELD," +
				" e.NAME," +
				" EID" +
				" FROM" +
				" ENTERPRISE_FINDFUND e" +
				" INNER JOIN DIC d ON d.[id] = INVESTMENTWAY" +
				" INNER JOIN DIC i ON i.[id] = FINANCIALSTRENGTH" +
				" INNER JOIN DIC c ON c.[id] = INVESTMENTFIELD";
		return ChatApp.getDbHelper().queryData(sql.toString(), new String[]{});
	}
	
	public static SQLiteCursor queryPhoneContact(){
		String sql = "SELECT UID,PHONE,LOCAL_NAME FROM MEMBER_CONTRAST ORDER BY UID DESC";
		return ChatApp.getDbHelper().query(sql);
	}
}
