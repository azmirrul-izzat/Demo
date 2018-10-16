package com.apddemo.core.daos;

import com.apddemo.core.models.QueryDataResult;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface BaseDao {
    QueryDataResult queryData(String queryString, Object... data);
    void closeQueryDataResult(QueryDataResult queryDataResult);
    boolean insertData(String statement, Object... data);
    List<Map<String, Object>> retrieveData(String queryString, Object... data);
    int insertDataReturnId(String statement, Object... data);
    List<Map<String, Object>> resultSetToList(ResultSet rs) throws SQLException;
    boolean removeUpdateData(String statement, Object... data);
    void closeResultSet(ResultSet resultSet);
}
