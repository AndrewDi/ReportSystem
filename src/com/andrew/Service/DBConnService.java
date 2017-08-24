package com.andrew.Service;

import com.andrew.Common.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
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


    private String SELECT_TBSPACE_UTILIZATION="SELECT TBSP_NAME,TBSP_TYPE,TBSP_STATE,TBSP_TOTAL_SIZE_KB,TBSP_USED_SIZE_KB,TBSP_FREE_SIZE_KB,TBSP_UTILIZATION_PERCENT,TBSP_AUTO_RESIZE_ENABLED,TBSP_MAX_SIZE" +
            " FROM (SELECT HEALTHMETRICS.TBSPACE_UTILIZATION.*,ROW_NUMBER() OVER (PARTITION BY DATABASE_NAME,TBSP_ID ORDER BY SNAPSHOT_TIME DESC) RN FROM HEALTHMETRICS.TBSPACE_UTILIZATION) " +
            " WHERE RN=1 AND DATABASE_NAME=? ORDER BY TBSP_NAME DESC,TBSP_TYPE";


    private String SELECT_BPF_HIT_RATION="SELECT BP_NAME,POOL_DATA_L_READS,POOL_DATA_P_READS,POOL_INDEX_L_READS,POOL_INDEX_P_READS,POOL_TEMP_DATA_L_READS,POOL_TEMP_DATA_P_READS," +
            "  POOL_TEMP_INDEX_L_READS,POOL_TEMP_INDEX_P_READS,CAST(1-(Pool_Data_P_Reads+Pool_Index_P_Reads+Pool_Temp_Data_P_Reads+Pool_Temp_Index_P_Reads)/(Pool_Data_L_Reads+Pool_Index_L_Reads+Pool_Temp_Data_L_Reads+Pool_Temp_Index_L_Reads+1) AS DECIMAL(5,2)) AS BP_HIT_RATIO,BP_CUR_BUFFSZ,PAGESIZE,SELF_TUNING_ENABLED" +
            " FROM (" +
            "SELECT IBM_DSM_VIEWS.MON_GET_BUFFERPOOL.*,ROW_NUMBER() OVER (PARTITION BY DBCONN_ID,BP_NAME ORDER BY COLLECTED DESC ) RN FROM IBM_DSM_VIEWS.MON_GET_BUFFERPOOL" +
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
            "ORDER BY COLLECTED ASC";


    private String SELECT_DSM_TPS="SELECT COLLECTED + 8 HOURS AS SNAPTIME," +
            "         (TOTAL_APP_COMMITS_DELTA + TOTAL_APP_ROLLBACKS_DELTA)/(DELTA_MSEC/1000) AS TPS" +
            "  FROM IBM_DSM_VIEWS.THROUGHPUT_ALL" +
            "  WHERE   DBCONN_ID =? AND COLLECTED + 8 HOURS BETWEEN ? AND ? " +
            "ORDER BY COLLECTED ASC";

    private String SELECT_DSM_CONCURRENT_USED="SELECT COLLECTED + 8 HOURS AS SNAPTIME,NUM_ACTIVE_SESSIONS" +
            "  FROM IBM_DSM_VIEWS.DB_SUMMARY_SESSIONS" +
            "  WHERE  DBCONN_ID =? AND COLLECTED + 8 HOURS BETWEEN ? AND ? " +
            " ORDER BY COLLECTED ASC";

    private String SELECT_DSM_AVG_LOGICAL_READS="SELECT COLLECTED+8 HOURS as SNAPTIME,LOGICAL_READS_DELTA/DELTA_MSEC AS AVG_READ FROM IBM_DSM_VIEWS.THROUGHPUT_ALL WHERE " +
            "  DBCONN_ID =? AND COLLECTED + 8 HOURS BETWEEN ? AND ? ORDER BY COLLECTED ASC";

    private String SELECT_DSM_AVG_LOCK_WAIT_TIME="SELECT COLLECTED+8 HOURS as SNAPTIME,LOCK_WAIT_TIME_DELTA/(TOTAL_APP_COMMITS_DELTA + TOTAL_APP_ROLLBACKS_DELTA+1) AS AVG_LOCK_WAIT_TIME FROM IBM_DSM_VIEWS.THROUGHPUT_ALL WHERE " +
            "  DBCONN_ID =? AND COLLECTED + 8 HOURS BETWEEN ? AND ? ORDER BY COLLECTED ASC";

    private String SELECT_DSM_LOCK_ESCALS="SELECT COLLECTED+8 HOURS as SNAPTIME,LOCK_ESCALS_DELTA FROM IBM_DSM_VIEWS.THROUGHPUT_ALL WHERE " +
            "  DBCONN_ID =? AND COLLECTED + 8 HOURS BETWEEN ? AND ? ORDER BY COLLECTED ASC";


    private String SELECT_DSM_TOP_SLOW_SQL="SELECT SNAPTIME,NUM_EXEC_WITH_METRICS,AVG_EXEC_TIME,AVG_ROW_READ,AVG_ROW_RETURNED,AVG_LOCK_WAIT_TIME,SQL_TEXT FROM ( " +
            "  SELECT " +
            "    sqltab.*, " +
            "    ROW_NUMBER() " +
            "    OVER ( " +
            "      PARTITION BY SQL_HASH_ID " +
            "      ORDER BY AVG_EXEC_TIME DESC ) AS RN " +
            "  FROM ( " +
            "    SELECT " +
            "      COLLECTED + 8 HOURS                                AS SNAPTIME, " +
            "      NUM_EXEC_WITH_METRICS, " +
            "      CAST(STMT_EXEC_TIME * 1.0 /DECODE(NUM_EXEC_WITH_METRICS,0,1,NUM_EXEC_WITH_METRICS) AS DECIMAL(15,2)) AS AVG_EXEC_TIME, " +
            "      ROWS_READ/DECODE(NUM_EXEC_WITH_METRICS,0,1,NUM_EXEC_WITH_METRICS) AS AVG_ROW_READ, " +
            "      ROWS_RETURNED/DECODE(NUM_EXEC_WITH_METRICS,0,1,NUM_EXEC_WITH_METRICS) AS AVG_ROW_RETURNED, " +
            "      LOGICAL_READS/DECODE(NUM_EXEC_WITH_METRICS,0,1,NUM_EXEC_WITH_METRICS) AS AVG_LOGICAL_READS, " +
            "      PHYSICAL_READS/DECODE(NUM_EXEC_WITH_METRICS,0,1,NUM_EXEC_WITH_METRICS) AS AVG_PHYSICAL_READS, " +
            "      LOCK_WAIT_TIME/DECODE(NUM_EXEC_WITH_METRICS,0,1,NUM_EXEC_WITH_METRICS) AS AVG_LOCK_WAIT_TIME, " +
            "      SQL_TEXT, " +
            "      IBMOTS.SQL_FACT.SQL_HASH_ID " +
            "    FROM IBMOTS.SQL_FACT " +
            "      LEFT JOIN IBMOTS.SQL_DIM ON (IBMOTS.SQL_DIM.SQL_HASH_ID = IBMOTS.SQL_FACT.SQL_HASH_ID) " +
            "    WHERE SYSTEM_QUERY=0 AND DBCONN_INT =? AND COLLECTED + 8 HOURS BETWEEN ? AND ? " +
            "  ) sqltab " +
            ") WHERE RN=1 AND NUM_EXEC_WITH_METRICS>20 ORDER BY AVG_EXEC_TIME DESC,NUM_EXEC_WITH_METRICS DESC " +
            "FETCH FIRST 20 ROWS ONLY";

    private String SELECT_DSM_TOP_ROWS_READ_SQL="SELECT SNAPTIME,NUM_EXEC_WITH_METRICS,AVG_EXEC_TIME,AVG_ROW_READ,AVG_ROW_RETURNED,AVG_LOCK_WAIT_TIME,SQL_TEXT FROM ( " +
            "  SELECT " +
            "    sqltab.*, " +
            "    ROW_NUMBER() " +
            "    OVER ( " +
            "      PARTITION BY SQL_HASH_ID " +
            "      ORDER BY AVG_ROW_READ DESC ) AS RN " +
            "  FROM ( " +
            "    SELECT " +
            "      COLLECTED + 8 HOURS                                AS SNAPTIME, " +
            "      NUM_EXEC_WITH_METRICS, " +
            "      CAST(STMT_EXEC_TIME * 1.0 /DECODE(NUM_EXEC_WITH_METRICS,0,1,NUM_EXEC_WITH_METRICS) AS DECIMAL(15,2)) AS AVG_EXEC_TIME, " +
            "      ROWS_READ/DECODE(NUM_EXEC_WITH_METRICS,0,1,NUM_EXEC_WITH_METRICS) AS AVG_ROW_READ, " +
            "      ROWS_RETURNED/DECODE(NUM_EXEC_WITH_METRICS,0,1,NUM_EXEC_WITH_METRICS) AS AVG_ROW_RETURNED, " +
            "      LOGICAL_READS/DECODE(NUM_EXEC_WITH_METRICS,0,1,NUM_EXEC_WITH_METRICS) AS AVG_LOGICAL_READS, " +
            "      PHYSICAL_READS/DECODE(NUM_EXEC_WITH_METRICS,0,1,NUM_EXEC_WITH_METRICS) AS AVG_PHYSICAL_READS, " +
            "      LOCK_WAIT_TIME/DECODE(NUM_EXEC_WITH_METRICS,0,1,NUM_EXEC_WITH_METRICS) AS AVG_LOCK_WAIT_TIME, " +
            "      SQL_TEXT, " +
            "      IBMOTS.SQL_FACT.SQL_HASH_ID " +
            "    FROM IBMOTS.SQL_FACT " +
            "      LEFT JOIN IBMOTS.SQL_DIM ON (IBMOTS.SQL_DIM.SQL_HASH_ID = IBMOTS.SQL_FACT.SQL_HASH_ID) " +
            "    WHERE SYSTEM_QUERY=0 AND DBCONN_INT =? AND COLLECTED + 8 HOURS BETWEEN ? AND ? " +
            "  ) sqltab  " +
            ") WHERE RN=1 AND NUM_EXEC_WITH_METRICS>20 ORDER BY AVG_ROW_READ DESC,NUM_EXEC_WITH_METRICS DESC " +
            "FETCH FIRST 20 ROWS ONLY";

    private String SELECT_DSM_TOP_LOCK_WAIT_SQL="SELECT SNAPTIME,NUM_EXEC_WITH_METRICS,AVG_EXEC_TIME,AVG_ROW_READ,AVG_ROW_RETURNED,AVG_LOCK_WAIT_TIME,SQL_TEXT FROM ( " +
            "  SELECT " +
            "    sqltab.*, " +
            "    ROW_NUMBER() " +
            "    OVER ( " +
            "      PARTITION BY SQL_HASH_ID " +
            "      ORDER BY AVG_LOCK_WAIT_TIME DESC ) AS RN " +
            "  FROM ( " +
            "    SELECT " +
            "      COLLECTED + 8 HOURS                                AS SNAPTIME, " +
            "      NUM_EXEC_WITH_METRICS, " +
            "      CAST(STMT_EXEC_TIME * 1.0 /DECODE(NUM_EXEC_WITH_METRICS,0,1,NUM_EXEC_WITH_METRICS) AS DECIMAL(15,2)) AS AVG_EXEC_TIME, " +
            "      ROWS_READ/DECODE(NUM_EXEC_WITH_METRICS,0,1,NUM_EXEC_WITH_METRICS) AS AVG_ROW_READ, " +
            "      ROWS_RETURNED/DECODE(NUM_EXEC_WITH_METRICS,0,1,NUM_EXEC_WITH_METRICS) AS AVG_ROW_RETURNED, " +
            "      LOGICAL_READS/DECODE(NUM_EXEC_WITH_METRICS,0,1,NUM_EXEC_WITH_METRICS) AS AVG_LOGICAL_READS, " +
            "      PHYSICAL_READS/DECODE(NUM_EXEC_WITH_METRICS,0,1,NUM_EXEC_WITH_METRICS) AS AVG_PHYSICAL_READS, " +
            "      LOCK_WAIT_TIME*1.0/DECODE(NUM_EXEC_WITH_METRICS,0,1,NUM_EXEC_WITH_METRICS) AS AVG_LOCK_WAIT_TIME, " +
            "      SQL_TEXT, " +
            "      IBMOTS.SQL_FACT.SQL_HASH_ID " +
            "    FROM IBMOTS.SQL_FACT " +
            "      LEFT JOIN IBMOTS.SQL_DIM ON (IBMOTS.SQL_DIM.SQL_HASH_ID = IBMOTS.SQL_FACT.SQL_HASH_ID) " +
            "    WHERE SYSTEM_QUERY=0 AND DBCONN_INT =? AND COLLECTED + 8 HOURS BETWEEN ? AND ? " +
            "  ) sqltab  " +
            ") WHERE RN=1 AND NUM_EXEC_WITH_METRICS>20 ORDER BY AVG_LOCK_WAIT_TIME DESC,NUM_EXEC_WITH_METRICS DESC " +
            "FETCH FIRST 20 ROWS ONLY";


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

    public Map<String, Object[]> getAvgLogReads(String dbname, String startTime, String endTime){
        List<Map<String,Object>> datas = jdbcTemplate.queryForList(this.SELECT_DSM_AVG_LOGICAL_READS,dbname,startTime,endTime);
        return ArrayUtils.listToMap(datas);
    }

    public Map<String, Object[]> getAvgLockWaitTime(String dbname, String startTime, String endTime){
        List<Map<String,Object>> datas = jdbcTemplate.queryForList(this.SELECT_DSM_AVG_LOCK_WAIT_TIME,dbname,startTime,endTime);
        return ArrayUtils.listToMap(datas);
    }

    public Map<String, Object[]> getLockEscals(String dbname, String startTime, String endTime){
        List<Map<String,Object>> datas = jdbcTemplate.queryForList(this.SELECT_DSM_LOCK_ESCALS,dbname,startTime,endTime);
        return ArrayUtils.listToMap(datas);
    }

    public List<Map<String, Object>> getTopSlowSql(int dbconnid, String startTime, String endTime){
        return jdbcTemplate.queryForList(this.SELECT_DSM_TOP_SLOW_SQL,dbconnid,startTime,endTime);
    }

    public List<Map<String, Object>> getTopRowsReadSql(int dbconnid, String startTime, String endTime){
        return jdbcTemplate.queryForList(this.SELECT_DSM_TOP_ROWS_READ_SQL,dbconnid,startTime,endTime);
    }

    public List<Map<String, Object>> getTopLockWaitSql(int dbconnid, String startTime, String endTime){
        return jdbcTemplate.queryForList(this.SELECT_DSM_TOP_LOCK_WAIT_SQL,dbconnid,startTime,endTime);
    }
}