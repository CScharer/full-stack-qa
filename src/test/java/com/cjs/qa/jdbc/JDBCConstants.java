package com.cjs.qa.jdbc;

public final class JDBCConstants {

  public static final String AS = "as ";
  public static final String ALTER = "alter ";
  public static final String IF_EXISTS = "if exists ";
  public static final String DELETE = "delete ";
  public static final String CREATE = "create ";
  public static final String DROP = "drop ";
  public static final String TABLE = "table ";
  public static final String VIEW = "view ";
  public static final String ALTER_TABLE = ALTER + TABLE;
  public static final String CREATE_TABLE = CREATE + TABLE;
  public static final String DROP_TABLE = DROP + TABLE;
  public static final String CREATE_VIEW = CREATE + VIEW;
  public static final String DROP_VIEW = DROP + VIEW;
  public static final String FROM = "from ";
  public static final String DROP_FROM = DROP + FROM;
  public static final String DELETE_FROM = DELETE + FROM;
  public static final String COUNT = "count";
  public static final String DISTINCT = "distinct ";
  public static final String SELECT = "select ";
  public static final String UPDATE = "update ";
  public static final String SET = "set ";
  public static final String SELECT_DISTINCT = SELECT + DISTINCT;
  public static final String SELECT_ALL = SELECT + "* ";
  public static final String SELECT_ALL_FROM = SELECT_ALL + FROM;
  public static final String INSERT_INTO = "insert into ";
  public static final String VALUES = " values ";
  public static final String INNER = "inner ";
  public static final String OUTER = "outer ";
  public static final String LEFT = "left ";
  public static final String RIGHT = "right ";
  public static final String JOIN = "join ";
  public static final String LEFT_JOIN = LEFT + JOIN;
  public static final String RIGHT_JOIN = RIGHT + JOIN;
  public static final String INNER_JOIN = INNER + JOIN;
  public static final String OUTER_JOIN = OUTER + JOIN;
  public static final String UNION = "union ";
  public static final String ALL = "all ";
  public static final String UNION_ALL = UNION + ALL;
  public static final String INNER_ON = "on ";
  public static final String WHERE = "where ";
  public static final String AND = "and ";
  public static final String ON = "on ";
  public static final String OR = "on ";
  public static final String NOT = "not ";
  public static final String NULL = "null ";
  public static final String NOT_NULL = NOT + NULL;
  public static final String ORDER_BY = "order by ";
  public static final String GROUP_BY = "group by ";
  public static final String LIMIT = "limit ";
  public static final String SELECT_COUNT = SELECT + COUNT + "(*) ";
  public static final String SELECT_COUNT_FROM = SELECT_COUNT + FROM;

  private JDBCConstants() {
    // Empty
  }
}
