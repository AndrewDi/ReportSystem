package com.andrew.Service;

import com.andrew.Model.DBConnModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DBConnService {

    private String SELECT_DBCONN="SELECT DBCONN_INT,DBCONN_ID AS DBCONN_NAME FROM IBM_RTMON_METADATA.RTMON_MAP_DBCONN";

    @Autowired
    private JdbcTemplate jdbcTemplate;


    /**
     * Get ALL DBConn From DSM Repo Database
     * @return list of DBConns
     */
    public List<Map<String, Object>> getDBConnALL(){
        /**
        RowMapper<DBConnModel> rowMapper = (resultSet, i) -> {
            DBConnModel dbConnModel=new DBConnModel();
            dbConnModel.DBCONN_INT=resultSet.getInt("DBCONN_INT");
            dbConnModel.DBCONN_NAME=resultSet.getString("DBCONN_NAME");
            return dbConnModel;
        };
        //return jdbcTemplate.query(SELECT_DBCONN,rowMapper);
         **/
        return jdbcTemplate.queryForList(SELECT_DBCONN);
    }
}
