package com.andrew.Service;

import com.andrew.Common.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
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


    private String SELECT_TBSPACE_UTILIZATION="SELECT TBSP_NAME,TBSP_TYPE,TBSP_STATE,TBSP_TOTAL_SIZE_KB,TBSP_USED_SIZE_KB,TBSP_FREE_SIZE_KB,TBSP_UTILIZATION_PERCENT,TBSP_AUTO_RESIZE_ENABLED,TBSP_MAX_SIZE" +
            " FROM (SELECT *,ROW_NUMBER() OVER (PARTITION BY DATABASE_NAME,TBSP_ID ORDER BY SNAPSHOT_TIME DESC) RN FROM HEALTHMETRICS.TBSPACE_UTILIZATION) " +
            " WHERE RN=1 AND DATABASE_NAME=? ORDER BY TBSP_NAME DESC,TBSP_TYPE";


    private String SELECT_BPF_HIT_RATION="SELECT BP_NAME,POOL_DATA_L_READS,POOL_DATA_P_READS,POOL_INDEX_L_READS,POOL_INDEX_P_READS,POOL_TEMP_DATA_L_READS,POOL_TEMP_DATA_P_READS," +
            "  POOL_TEMP_INDEX_L_READS,POOL_TEMP_INDEX_P_READS,CAST(1-(Pool_Data_P_Reads+Pool_Index_P_Reads+Pool_Temp_Data_P_Reads+Pool_Temp_Index_P_Reads)/(Pool_Data_L_Reads+Pool_Index_L_Reads+Pool_Temp_Data_L_Reads+Pool_Temp_Index_L_Reads+1) AS DECIMAL(5,2)) AS BP_HIT_RATIO,BP_CUR_BUFFSZ,PAGESIZE,SELF_TUNING_ENABLED" +
            " FROM (" +
            "SELECT *,ROW_NUMBER() OVER (PARTITION BY DBCONN_ID,BP_NAME ORDER BY COLLECTED DESC ) RN FROM IBM_DSM_VIEWS.MON_GET_BUFFERPOOL" +
            " WHERE DBCONN_ID=? AND COLLECTED + 8 HOURS BETWEEN ? AND ? " +
            ")  WHERE RN=1";


    private String SELECT_DSM_RSPT="SELECT COLLECTED + 8 HOURS AS SNAPTIME," +
            "       CAST (" +
            "            TOTAL_ACT_TIME_DELTA" +
            "          * 1.0" +
            "          / DECODE (TOTAL_APP_COMMITS_DELTA + TOTAL_APP_ROLLBACKS_DELTA," +
            "                    0," +
            "                    1," +
            "                    TOTAL_APP_COMMITS_DELTA + TOTAL_APP_ROLLBACKS_DELTA) AS DECIMAL (8, 3))" +
            "          AS yaxis" +
            "  FROM IBM_DSM_VIEWS.THROUGHPUT_ALL" +
            " WHERE    DBCONN_ID =? AND COLLECTED + 8 HOURS BETWEEN ? AND ? " +
            "ORDER BY DBCONN_ID, COLLECTED ASC";


    private String SELECT_DSM_TPS="SELECT COLLECTED + 8 HOURS AS SNAPTIME," +
            "         (TOTAL_APP_COMMITS_DELTA + TOTAL_APP_ROLLBACKS_DELTA)/(DELTA_MSEC/1000) AS TPS" +
            "  FROM IBM_DSM_VIEWS.THROUGHPUT_ALL" +
            "  WHERE   DBCONN_ID =? AND COLLECTED + 8 HOURS BETWEEN ? AND ? " +
            "ORDER BY DBCONN_ID, COLLECTED ASC";

    private String SELECT_DSM_CONCURRENT_USED="SELECT COLLECTED + 8 HOURS AS SNAPTIME,NUM_ACTIVE_SESSIONS" +
            "  FROM IBM_DSM_VIEWS.DB_SUMMARY_SESSIONS" +
            "  WHERE    DBCONN_ID =? AND COLLECTED + 8 HOURS BETWEEN ? AND ? " +
            " ORDER BY DBCONN_ID, COLLECTED ASC";
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

    /**
     * 通过数据库名获取数据库表空间使用量
     * @param dbname 数据库名
     * @return
     */
    public List<Map<String, Object>> getTBSpaceUtilization(String dbname){
        return jdbcTemplate.queryForList(this.SELECT_TBSPACE_UTILIZATION,dbname);
    }


    /**
     * 通过数据库名获取数据库缓存池信息
     * @param dbname 数据库名
     * @return
     */
    public List<Map<String,Object>> getBpfHitRation(String dbname,String startTime, String endTime){
        return jdbcTemplate.queryForList(this.SELECT_BPF_HIT_RATION,dbname,startTime,endTime);
    }

    public Map<String, Object[]> getRSPT(String dbname, String startTime, String endTime){
        List<Map<String,Object>> datas = jdbcTemplate.queryForList(this.SELECT_DSM_RSPT,dbname,startTime,endTime);
        return ArrayUtils.listToMap(datas);
    }

    public Map<String, Object[]> getTPS(String dbname, String startTime, String endTime){
        List<Map<String,Object>> datas = jdbcTemplate.queryForList(this.SELECT_DSM_TPS,dbname,startTime,endTime);
        return ArrayUtils.listToMap(datas);
    }

    public Map<String, Object[]> getConCurrent(String dbname, String startTime, String endTime){
        List<Map<String,Object>> datas = jdbcTemplate.queryForList(this.SELECT_DSM_CONCURRENT_USED,dbname,startTime,endTime);
        return ArrayUtils.listToMap(datas);
    }
}