package com.ischoolbar.programmer.dao;

import java.io.ObjectInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ischoolbar.programmer.annotation.Column;
import com.ischoolbar.programmer.annotation.Table;
import com.ischoolbar.programmer.entity.BaseEntity;
import com.ischoolbar.programmer.page.Operator;
import com.ischoolbar.programmer.page.Page;
import com.ischoolbar.programmer.page.SearchProperty;
import com.ischoolbar.programmer.util.DbUtil;
import com.ischoolbar.programmer.util.StringUtil;

/**
 * 基类dao，抽象封装所有的操作
 * @author llq
 *
 */
public class BaseDao<T> {
	private DbUtil dbUtil = DbUtil.getInstance();
	//public Connection connection = dbUtil.getConnection();
	private final static int CURD_ADD = 0;
	private final static int CURD_UPDATE = 1;
	private final static int CURD_FIND = 2;
	private final static int CURD_SELECT = 3;
	private final static int CURD_DELETE = 4;
	private final static int CURD_COUNT = 5;
	private Class<T> t;
	
	
	public BaseDao(){
		Type genericSuperclass = this.getClass().getGenericSuperclass();
		if(genericSuperclass instanceof ParameterizedType){
			Type[] actualTypeArguments = ((ParameterizedType)genericSuperclass).getActualTypeArguments();
			if(actualTypeArguments != null && actualTypeArguments.length > 0){
				t = (Class<T>) actualTypeArguments[0];
			}
		}
		//System.out.println(t.getSimpleName());
	}
	
	
	/**
	 * 抽象封装数据库添加操作
	 * @param t
	 * @return
	 */
	public boolean add(T t){
		String sql = buildSql(CURD_ADD);
		try {
			PreparedStatement prepareStatement = dbUtil.getConnection().prepareStatement(sql);
			prepareStatement = setPreparedStatement(t,prepareStatement,CURD_ADD);
			int rst = prepareStatement.executeUpdate();
			dbUtil.releaseConnection();
			return rst > 0;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 数据库更新操作抽象封装
	 * @param t
	 * @return
	 */
	public boolean update(T t){
		String sql = buildSql( CURD_UPDATE);
		try {
			PreparedStatement prepareStatement = dbUtil.getConnection().prepareStatement(sql);
			prepareStatement = setPreparedStatement(t,prepareStatement,CURD_UPDATE);
			int rst = prepareStatement.executeUpdate();
			dbUtil.releaseConnection();
			return rst > 0;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 数据库查询单个实体抽象封装
	 * @param id
	 * @return
	 */
	public T find(int id){
		String sql = buildSql(CURD_FIND);
		T newInstance = null;
		try {
			PreparedStatement prepareStatement = dbUtil.getConnection().prepareStatement(sql);
			prepareStatement.setObject(1, id);
			ResultSet executeQuery = prepareStatement.executeQuery();
			if(executeQuery.next()){
				newInstance = (T) t.newInstance();
				newInstance = setParams(newInstance,executeQuery);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dbUtil.releaseConnection();
		return newInstance;
	}
	
	/**
	 * 抽象分装多条件分页查询列表操作
	 * @param page
	 * @return
	 */
	public Page<T> findList(Page<T> page){
		String sql = buildSql(CURD_SELECT);
		sql += buildSearchSql(page);
		sql += " limit " + page.getOffset() + "," + page.getPageSize();
		try {
			PreparedStatement prepareStatement = dbUtil.getConnection().prepareStatement(sql);
			prepareStatement = setSearchPrepareStatement(prepareStatement,page);
			ResultSet executeQuery = prepareStatement.executeQuery();
			while(executeQuery.next()){
				T newInstance = (T) t.newInstance();
				newInstance = setParams(newInstance,executeQuery);
				page.getContent().add(newInstance);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		page.setTotal(getTotal(buildSql(CURD_COUNT) + buildSearchSql(page), page));
		int totalPage = 0;
		if(page.getTotal() % page.getPageSize() == 0){
			totalPage = page.getTotal() / page.getPageSize();
		}else{
			totalPage = page.getTotal() / page.getPageSize() + 1;
		}
		page.setTotalPage(totalPage);
		dbUtil.releaseConnection();
		return page;
	}
	
	/**
	 * 抽象封装删除操作，支持单个删除和批量删除
	 * @param ids
	 * @return
	 */
	public boolean delete(int... ids){
		String sql = buildSql(CURD_DELETE);
		String idsStr = "";
		for(int i=0;i<ids.length;i++){
			idsStr += ids[i] + ",";
		}
		if(!"".equals(idsStr))
			idsStr = idsStr.substring(0,idsStr.length()-1);
		sql = sql.replace("?", idsStr);
		try {
			PreparedStatement prepareStatement = dbUtil.getConnection().prepareStatement(sql);
			int rst = prepareStatement.executeUpdate();
			dbUtil.releaseConnection();
			return rst > 0;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 返回总的记录数
	 * @param sql
	 * @return
	 */
	private int getTotal(String sql,Page<T> page){
		PreparedStatement prepareStatement;
		try {
			prepareStatement = dbUtil.getConnection().prepareStatement(sql);
			prepareStatement = setSearchPrepareStatement(prepareStatement,page);
			ResultSet executeQuery = prepareStatement.executeQuery();
			if(executeQuery.next()){
				int total = executeQuery.getInt("total");
				dbUtil.releaseConnection();
				return total;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * 多条件组合查询赋值
	 * @param prepareStatement
	 * @param page
	 * @return
	 */
	private PreparedStatement setSearchPrepareStatement(
			PreparedStatement prepareStatement, Page<T> page) {
		// TODO Auto-generated method stub
		List<SearchProperty> searchProporties = page.getSearchProporties();
		int index = 1;
		for(SearchProperty searchProperty:searchProporties){
			try {
				if(searchProperty.getOperator() != Operator.IN)
					prepareStatement.setObject(index++, searchProperty.getValue());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return prepareStatement;
	}


	/**
	 * 构造多条件查询的条件sql语句
	 * @param page
	 * @return
	 */
	private String buildSearchSql(Page<T> page) {
		// TODO Auto-generated method stub
		String searchSql = "";
		List<SearchProperty> searchProporties = page.getSearchProporties();
		for(SearchProperty searchProperty:searchProporties){
			switch (searchProperty.getOperator()) {
				case GT:{//大于
					searchSql += " and " + searchProperty.getKey() + " > ?";
					break;
				}
				case GTE:{//大于等于
					searchSql += " and " + searchProperty.getKey() + " >= ?";
					break;
				}
				case EQ:{//等于
					searchSql += " and " + searchProperty.getKey() + " = ?";
					break;
				}
				case LT:{//小于
					searchSql += " and " + searchProperty.getKey() + " < ?";
					break;
				}
				case LTE:{//小于等于
					searchSql += " and " + searchProperty.getKey() + " <= ?";
					break;
				}
				case NEQ:{//不等于
					searchSql += " and " + searchProperty.getKey() + " <> ?";
					break;
				}
				case LIKE:{//模糊匹配
					searchSql += " and " + searchProperty.getKey() + " like ?";
					break;
				}
				case IN:{//在范围内
					searchSql += " and " + searchProperty.getKey() + " in(" + searchProperty.getValue() + ")";
					break;
				}
			}
		}
		if(!"".equals(searchSql)){
			searchSql = searchSql.replaceFirst("and", "where");
		}
		System.out.println(searchSql);
		return searchSql;
	}


	/**
	 * 给从数据库获取的数据赋值并实例化
	 * @param newInstance
	 * @param executeQuery
	 * @return
	 */
	private T setParams(T newInstance, ResultSet executeQuery) {
		// TODO Auto-generated method stub
		//首先获取自身定义的字段
		Field[] declaredFields = newInstance.getClass().getDeclaredFields();
		try {
			for(Field field :declaredFields){
				field.setAccessible(true);
				if(field.isAnnotationPresent(Column.class)){
					Column annotation = field.getAnnotation(Column.class);
					if(!annotation.isForeignEntity()){
						//如果不是自定义对象
						field.set(newInstance, executeQuery.getObject(annotation.name()));
					}else{
						Blob blob = executeQuery.getBlob(annotation.name());
						ObjectInputStream objectInputStream = new ObjectInputStream(blob.getBinaryStream());
						field.set(newInstance, objectInputStream.readObject());
					}
				}else{
					field.set(newInstance, executeQuery.getObject(StringUtil.convertToUnderline(field.getName())));
				}
			}
			Field[] parentFields = newInstance.getClass().getFields();
			for(Field field :parentFields){
				field.setAccessible(true);
				if(field.isAnnotationPresent(Column.class)){
					Column annotation = field.getAnnotation(Column.class);
					field.set(newInstance, executeQuery.getObject(annotation.name()));
				}else{
					field.set(newInstance, executeQuery.getObject(StringUtil.convertToUnderline(field.getName())));
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return newInstance;
	}

	/**
	 * 设置数据库链接信息相关值即复制给变量
	 * @param prepareStatement
	 * @return
	 */
	private PreparedStatement setPreparedStatement(T t,
			PreparedStatement prepareStatement,int curdType) {
		// TODO Auto-generated method stub
		Field[] declaredFields = t.getClass().getDeclaredFields();
		try {
			switch (curdType) {
				case CURD_ADD:{
					int index = 1;
					for(int i = 0;i<declaredFields.length;i++){
						declaredFields[i].setAccessible(true);
						//如果设置了注解字段，则检查注解字段是否是主键且自增
						if(declaredFields[i].isAnnotationPresent(Column.class)){
							Column annotation = declaredFields[i].getAnnotation(Column.class);
							if(!annotation.autoIncrease()){
								//创建时间赋值默认值
								if(annotation.autoCreateTime()){
									declaredFields[i].set(t, new Timestamp(System.currentTimeMillis()));
								}
								prepareStatement.setObject(index++, declaredFields[i].get(t));
								continue;
							}
						}else if(!"id".equals(declaredFields[i].getName())){
							prepareStatement.setObject(index++, declaredFields[i].get(t));
						}
					}
					if(BaseEntity.class.isAssignableFrom(t.getClass())){
						//若继承了基类
						Field[] fields = t.getClass().getFields();
						for(int i = 0;i<fields.length;i++){
							fields[i].setAccessible(true);
							//如果设置了注解字段，则检查注解字段是否是主键且自增
							if(fields[i].isAnnotationPresent(Column.class)){
								Column annotation = fields[i].getAnnotation(Column.class);
								if(!annotation.autoIncrease()){
									//创建时间赋值默认值
									if(annotation.autoCreateTime()){
										fields[i].set(t, new Timestamp(System.currentTimeMillis()));
									}
									prepareStatement.setObject(index++, fields[i].get(t));
									continue;
								}
							}else if(!"id".equals(fields[i].getName())){
								prepareStatement.setObject(index++, fields[i].get(t));
							}
						}
					}
					break;
				}
				case CURD_UPDATE:{
					int index = 1;
					for(Field field : declaredFields){
						field.setAccessible(true);
						//如果设置了注解字段，则检查注解字段是否是主键且自增
						if(field.isAnnotationPresent(Column.class)){
							Column annotation = field.getAnnotation(Column.class);
							//若不是主键且可更新
							if(!annotation.primaryKey() && annotation.updateEnable()){
								prepareStatement.setObject(index++, field.get(t));
							}
							
						}else{
							if(!"id".equals(field.getName())){
								prepareStatement.setObject(index++, field.get(t));
							}
						}
					}
					
					if(BaseEntity.class.isAssignableFrom(t.getClass())){
						//若继承了基类
						Field[] parentFields = t.getClass().getFields();
						for(int i = 0;i<parentFields.length;i++){
							parentFields[i].setAccessible(true);
							//如果设置了注解字段，则检查注解字段是否是主键且自增
							if(parentFields[i].isAnnotationPresent(Column.class)){
								Column annotation = parentFields[i].getAnnotation(Column.class);
								//若不是主键且可更新
								if(!annotation.primaryKey() && annotation.updateEnable()){
									prepareStatement.setObject(index++, parentFields[i].get(t));
								}
								
							}else{
								if(!"id".equals(parentFields[i].getName())){
									prepareStatement.setObject(index++, parentFields[i].get(t));
								}
							}
						}
					}
					
					//设置主键
					for(Field field : declaredFields){
						field.setAccessible(true);
						//如果设置了注解字段，则检查注解字段是否是主键且自增
						if(field.isAnnotationPresent(Column.class)){
							Column annotation = field.getAnnotation(Column.class);
							//若是主键
							if(annotation.primaryKey()){
								prepareStatement.setObject(index++, field.get(t));
							}
							
						}else{
							if("id".equals(field.getName())){
								prepareStatement.setObject(index++, field.get(t));
							}
						}
					}
					
					if(BaseEntity.class.isAssignableFrom(t.getClass())){
						//若继承了基类
						Field[] parentFields = t.getClass().getFields();
						for(int i = 0;i<parentFields.length;i++){
							parentFields[i].setAccessible(true);
							//如果设置了注解字段，则检查注解字段是否是主键且自增
							if(parentFields[i].isAnnotationPresent(Column.class)){
								Column annotation = parentFields[i].getAnnotation(Column.class);
								//若是主键且可更新
								if(annotation.primaryKey()){
									prepareStatement.setObject(index++, parentFields[i].get(t));
								}
								
							}else{
								if("id".equals(parentFields[i].getName())){
									prepareStatement.setObject(index++, parentFields[i].get(t));
								}
							}
						}
					}
					break;
				}
				case CURD_FIND:{
					
					break;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return prepareStatement;
	}

	/**
	 * 构建sql语句
	 * @param t
	 * @param curdType
	 * @return
	 */
	private String buildSql(int curdType){
		String sql = "";
		switch (curdType) {
			case CURD_ADD:{
				sql = "insert into " + getTableName()  +"("+getAddTableFields()+") values("+getAddTableValues()+")";
				break;
			}
			case CURD_UPDATE:{
				sql = "update " + getTableName()  +" set "+getUpdateTableParams();
				break;
			}
			case CURD_FIND:{
				sql = "select * from " + getTableName()  +" where "+getFindSingleParams();
				break;
			}
			case CURD_SELECT:{
				sql = "select * from " + getTableName();
				break;
			}
			case CURD_COUNT:{
				sql = "select count(*) as total from " + getTableName();
				break;
			}
			case CURD_DELETE:{
				sql = "delete from " + getTableName() + " where id in(?)";
				break;
			}
		}
		System.out.println(sql);
		return sql;
	}
	
	/**
	 * 获取单个实体的字段预填充
	 * @return
	 */
	private String getFindSingleParams() {
		// TODO Auto-generated method stub
		//首先从自己的字段中获取主键
		Field[] declaredFields = t.getDeclaredFields();
		String where = "";
		for(Field field:declaredFields){
			//若字段被注解
			if(field.isAnnotationPresent(Column.class)){
				Column annotation = field.getAnnotation(Column.class);
				if(annotation.primaryKey()){
					where = annotation.name() + " = ?";
					break;
				}
			}else{
				if("id".equals(field.getName())){
					where = " id = ?";
				}
			}
		}
		
		//若继承了父类
		if(BaseEntity.class.isAssignableFrom(t)){
			Field[] fields = t.getFields();
			for(Field field:fields){
				//若字段被注解
				if(field.isAnnotationPresent(Column.class)){
					Column annotation = field.getAnnotation(Column.class);
					if(annotation.primaryKey()){
						where = annotation.name() + " = ?";
						break;
					}
				}else{
					if("id".equals(field.getName())){
						where = " id = ?";
						break;
					}
				}
			}
		}
		return where;
	}

	/**
	 * 获取更新操作时的字段预填充值
	 * @return
	 */
	private String getUpdateTableParams() {
		// TODO Auto-generated method stub
		//获取自己定义的所有字段
		Field[] declaredFields = t.getDeclaredFields();
		String params = "";
		String where = " ";
		for(Field field : declaredFields){
			field.setAccessible(true);
			//如果设置了注解字段，则检查注解字段是否是主键且自增
			if(field.isAnnotationPresent(Column.class)){
				Column annotation = field.getAnnotation(Column.class);
				//若不是主键且可更新
				if(!annotation.primaryKey() && annotation.updateEnable()){
					params += annotation.name() + "=?,";
				}
				if(annotation.primaryKey()){
					where += "and " + annotation.name() + " = ? ";
				}
			}else{
				if(!"id".equals(field.getName())){
					params += StringUtil.convertToUnderline(field.getName()) + "=?,";
				}else{
					where += "and " + StringUtil.convertToUnderline(field.getName()) + " = ? ";
				}
			}
		}
		
		if(BaseEntity.class.isAssignableFrom(t)){
			//若继承了基类
			Field[] parentFields = t.getFields();
			for(int i = 0;i<parentFields.length;i++){
				parentFields[i].setAccessible(true);
				//如果设置了注解字段，则检查注解字段是否是主键且自增
				if(parentFields[i].isAnnotationPresent(Column.class)){
					Column annotation = parentFields[i].getAnnotation(Column.class);
					//若不是主键且可更新
					if(!annotation.primaryKey() && annotation.updateEnable()){
						params += annotation.name() + "=?,";
					}
					if(annotation.primaryKey()){
						where += "and " + annotation.name() + " = ? ";
					}
				}else{
					if(!"id".equals(parentFields[i].getName())){
						params += StringUtil.convertToUnderline(parentFields[i].getName()) + "=?,";
					}else{
						where += "and " + StringUtil.convertToUnderline(parentFields[i].getName()) + " = ? ";
					}
				}
			}
		}
		where = where.substring(0,where.length()-1).replaceFirst("and", "where");
		return params.substring(0,params.length()-1) + where;
	}

	/**
	 * 获取添加操作时的字段预填充值
	 * @return
	 */
	private String getAddTableValues() {
		// TODO Auto-generated method stub
		List<String> fields = getTableFields();
		String[] values = new String[fields.size()];
		Arrays.fill(values, "?");
		for(int i=0;i <values.length;i++){
			
		}
		Field[] declaredFields = t.getDeclaredFields();
		for(int i = 0;i<declaredFields.length;i++){
			declaredFields[i].setAccessible(true);
			//如果设置了注解字段，则检查注解字段是否是主键且自增
			if(declaredFields[i].isAnnotationPresent(Column.class)){
				Column annotation = declaredFields[i].getAnnotation(Column.class);
				if(annotation.autoIncrease()){
					values[i] = "null";
				}
			}else{
				if("id".equals(declaredFields[i].getName())){
					values[i] = "null";
				}
			}
			
		}
		if(BaseEntity.class.isAssignableFrom(t)){
			//若继承了基类
			Field[] parentFields = t.getFields();
			for(int i = 0;i<parentFields.length;i++){
				parentFields[i].setAccessible(true);
				//如果设置了注解字段，则检查注解字段是否是主键且自增
				if(parentFields[i].isAnnotationPresent(Column.class)){
					Column annotation = parentFields[i].getAnnotation(Column.class);
					if(annotation.autoIncrease()){
						values[declaredFields.length + i] = "null";
					}
				}else{
					if("id".equals(parentFields[i].getName())){
						values[i] = "null";
					}
				}
			}
		}
		
		
		return StringUtil.join(Arrays.asList(values), ",");
	}

	/**
	 * 获取添加操作的实体字段
	 * @return
	 */
	private String getAddTableFields() {
		// TODO Auto-generated method stub
		List<String> fields = getTableFields();
		return StringUtil.join(fields, ",");
	}


	/**
	 * 获取数据库表的所有字段
	 * @param t
	 * @return
	 */
	private List<String> getTableFields() {
		// TODO Auto-generated method stub
		List<String> ret = new ArrayList<String>();
		Field[] declaredFields = t.getDeclaredFields();
		for(Field field : declaredFields){
			//如果字段被注解，则以注解的为准
			if(field.isAnnotationPresent(Column.class)){
				ret.add(field.getAnnotation(Column.class).name());
				continue;
			}
			ret.add(StringUtil.convertToUnderline(field.getName()));
		}
		if(BaseEntity.class.isAssignableFrom(t)){
			//说明继承自BaseEntity
			Field[] fields = t.getFields();
			for(Field field : fields){
				//如果字段被注解，则以注解的为准
				if(field.isAnnotationPresent(Column.class)){
					ret.add(field.getAnnotation(Column.class).name());
					continue;
				}
				ret.add(StringUtil.convertToUnderline(field.getName()));
			}
		}
		return ret;
	}

	/**
	 * 获取实体对应的数据库表名
	 * @return
	 */
	private String getTableName() {
		// TODO Auto-generated method stub
		String tableName = StringUtil.convertToUnderline(t.getSimpleName());
		//如果注解了表名，则以注解的名称为准
		if(t.isAnnotationPresent(Table.class)){
			String prefix = t.getAnnotation(Table.class).prefix();
			String sufix = t.getAnnotation(Table.class).sufix();
			tableName = StringUtil.isEmpty(prefix) ? "" : prefix + "_";
			tableName += t.getAnnotation(Table.class).tableName();
			tableName += StringUtil.isEmpty(sufix) ? "" : "_" + sufix;
		}
		return tableName;
	}


	/**
	 * 关闭数据库链接
	 */
	public void closeConnection(){
		dbUtil.releaseConnection();
	}


	public DbUtil getDbUtil() {
		return dbUtil;
	}


	public void setDbUtil(DbUtil dbUtil) {
		this.dbUtil = dbUtil;
	}
	
	
}
