package com.andrew.Service;

import com.andrew.Model.DBConnModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DBConnService {


    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String SELECT_DBCONN_ALL="SELECT DBCONN_INT,DB.DATABASE_NAME,DB.INSTANCE_NAME,DB.HOST,PRO.VERSION,PRO.PORT,PRO.INSTANCE_PATH,PRO.OS_TYPE " +
            "FROM IBMPDQ.ADMIN_INSTANCE_DATABASE DB LEFT JOIN IBMPDQ.ADMIN_INSTANCE_PROPERTY PRO ON (DB.HOST,DB.INSTANCE_NAME)=(PRO.HOST,PRO.INSTANCE_NAME) " +
            "LEFT JOIN IBM_RTMON_METADATA.RTMON_MAP_DBCONN ON (DB.DATABASE_NAME)=(DBCONN_ID) ORDER BY DBCONN_INT";

    private String SELECT_DBCONN_ANY="SELECT DBCONN_INT,DB.DATABASE_NAME,DB.INSTANCE_NAME,DB.HOST,PRO.VERSION,PRO.PORT,PRO.INSTANCE_PATH,PRO.OS_TYPE " +
            "FROM IBMPDQ.ADMIN_INSTANCE_DATABASE DB LEFT JOIN IBMPDQ.ADMIN_INSTANCE_PROPERTY PRO ON (DB.HOST,DB.INSTANCE_NAME)=(PRO.HOST,PRO.INSTANCE_NAME) " +
            "LEFT JOIN IBM_RTMON_METADATA.RTMON_MAP_DBCONN ON (DB.DATABASE_NAME)=(DBCONN_ID) WHERE DB.DATABASE_NAME=? ORDER BY DBCONN_INT FETCH FIRST 1 ROWS ONLY";

    /**
     * Get ALL DBConn From DSM Repo Database
     * @return list of DBConns
     */
    public List<Map<String, Object>> getDBConnALL(){
        return jdbcTemplate.queryForList(SELECT_DBCONN_ALL);
    }

    /**
     * Get Any DBConn From DSM Repo Database
     * @param DBName
     * @return
     */
    public Map<String,Object> getDBConnAny(String DBName){
        try {
            return jdbcTemplate.queryForMap(this.SELECT_DBCONN_ANY,DBName);
        }
        catch (EmptyResultDataAccessException exception){
            return null;
        }
    }
}
