package com.andrew.Service;

import com.andrew.Common.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DBPerfService {

    private String SELECT_DSM_RTSP_ALL="SELECT DBCONN_ID AS DBNAME, " +
            "       COLLECTED + 8 HOURS AS SNAPTIME, " +
            "       CAST ( " +
            "            TOTAL_ACT_TIME_DELTA " +
            "          * 1.0 " +
            "          / DECODE (TOTAL_APP_COMMITS_DELTA + TOTAL_APP_ROLLBACKS_DELTA, " +
            "                    0, " +
            "                    1, " +
            "                    TOTAL_APP_COMMITS_DELTA + TOTAL_APP_ROLLBACKS_DELTA) AS DECIMAL (8, 3)) " +
            "          AS VAL " +
            "  FROM IBM_DSM_VIEWS.THROUGHPUT_ALL " +
            " WHERE     COLLECTED + 8 HOURS BETWEEN ? AND ?  " +
            "       AND DBCONN_ID IN ('DCOMDB', " +
            "                         'RTSPDB', " +
            "                         'NSBPMDB', " +
            "                         'NSEPSPDB') " +
            "ORDER BY DBCONN_ID, COLLECTED ASC";

    private String SELECT_DSM_TPS_ALL="SELECT DBCONN_ID AS DBNAME, " +
            "       COLLECTED + 8 HOURS AS SNAPTIME, " +
            "         (TOTAL_APP_COMMITS_DELTA + TOTAL_APP_ROLLBACKS_DELTA) " +
            "       / (DELTA_MSEC / 1000) " +
            "          AS VAL  " +
            "  FROM IBM_DSM_VIEWS.THROUGHPUT_ALL " +
            " WHERE     COLLECTED + 8 HOURS BETWEEN ? AND ? " +
            "       AND DBCONN_ID IN ('DCOMDB', " +
            "                         'RTSPDB', " +
            "                         'NSBPMDB', " +
            "                         'NSEPSPDB') " +
            "ORDER BY DBCONN_ID, COLLECTED";

    private String SELECT_DSM_TBSP_ALL="SELECT IBMIOCM_DATABASE AS DBNAME, " +
            "       IBMIOCM_TIMESTAMP + 8 HOURS AS SNAPTIME, " +
            "       cast ( " +
            "            SUM (TBSP_USED_PAGES * TBSP_PAGE_SIZE) " +
            "          * 1.0 " +
            "          / SUM (TBSP_USABLE_PAGES * TBSP_PAGE_SIZE) AS DECIMAL (7, 4)) " +
            "          AS VAL  " +
            "  FROM IBMIOCM.DB2LUW_MONTABLESPACE_HIS " +
            " WHERE     IBMIOCM_TIMESTAMP + 8 HOURS BETWEEN ? AND ? " +
            "       AND IBMIOCM_DATABASE IN ('DCOMDB', " +
            "                                'RTSPDB', " +
            "                                'NSBPMDB', " +
            "                                'NSEPSPDB') " +
            "GROUP BY IBMIOCM_DATABASE, IBMIOCM_TIMESTAMP " +
            "ORDER BY IBMIOCM_DATABASE, IBMIOCM_TIMESTAMP ASC";

    private String SELECT_DSM_MEM_USED_ALL="SELECT DBCONN_ID AS DBNAME, "+
            "       COLLECTED + 8 HOURS AS SNAPTIME, "+
            "       CAST (MEMORY_POOL_USED_GB AS DECIMAL (8, 2)) AS VAL  "+
            "  FROM IBM_DSM_VIEWS.MEM_DB_TOTAL_USED "+
            " WHERE     COLLECTED + 8 HOURS BETWEEN ? AND ? "+
            "       AND DBCONN_ID IN ('DCOMDB', "+
            "                         'RTSPDB', "+
            "                         'NSBPMDB', "+
            "                         'NSEPSPDB') "+
            "ORDER BY DBCONN_ID, COLLECTED";

    private String SELECT_DSM_CONCURRENT_ALL="SELECT DBCONN_ID AS DBNAME, COLLECTED + 8 HOURS AS SNAPTIME, NUM_ACTIVE_SESSIONS AS VAL  " +
            "  FROM IBM_DSM_VIEWS.DB_SUMMARY_SESSIONS " +
            " WHERE     COLLECTED + 8 HOURS BETWEEN ? AND ? " +
            "       AND DBCONN_ID IN ('DCOMDB', " +
            "                         'RTSPDB', " +
            "                         'NSBPMDB', " +
            "                         'NSEPSPDB') " +
            "ORDER BY DBCONN_ID, COLLECTED";

    private String SELECT_DSM_BP_HIT_RATIO_ALL="SELECT DBCONN_ID AS DBNAME, " +
            "       COLLECTED + 8 HOURS AS SNAPTIME, " +
            "       CAST (  1 " +
            "             -   PHYSICAL_READS " +
            "               * 1.0 " +
            "               / decode (LOGICAL_READS, " +
            "                         0, " +
            "                         1, " +
            "                         LOGICAL_READS) AS DECIMAL (5, 2)) " +
            "          AS VAL " +
            "  FROM IBM_DSM_VIEWS.THROUGHPUT_ALL " +
            " WHERE     COLLECTED + 8 HOURS BETWEEN ? AND ? " +
            "       AND DBCONN_ID IN ('DCOMDB', " +
            "                         'RTSPDB', " +
            "                         'NSBPMDB', " +
            "                         'NSEPSPDB') " +
            "ORDER BY DBCONN_ID, COLLECTED";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Map<String,Object> getRtspAll(String startTime, String endTime){
        return ArrayUtils.listToMapForAll(this.jdbcTemplate.queryForList(this.SELECT_DSM_RTSP_ALL,startTime,endTime));
    }

    public Map<String,Object> getTPSAll(String startTime, String endTime){
        return ArrayUtils.listToMapForAll(this.jdbcTemplate.queryForList(this.SELECT_DSM_TPS_ALL,startTime,endTime));
    }

    public Map<String,Object> getTbspAll(String startTime, String endTime){
        return ArrayUtils.listToMapForAll(this.jdbcTemplate.queryForList(this.SELECT_DSM_TBSP_ALL,startTime,endTime));
    }

    public Map<String,Object> getMemUsedAll(String startTime, String endTime){
        return ArrayUtils.listToMapForAll(this.jdbcTemplate.queryForList(this.SELECT_DSM_MEM_USED_ALL,startTime,endTime));
    }

    public Map<String,Object> getConCurrentAll(String startTime, String endTime){
        return ArrayUtils.listToMapForAll(this.jdbcTemplate.queryForList(this.SELECT_DSM_CONCURRENT_ALL,startTime,endTime));
    }

    public Map<String,Object> getBpfHitRatioAll(String startTime, String endTime){
        return ArrayUtils.listToMapForAll(this.jdbcTemplate.queryForList(this.SELECT_DSM_BP_HIT_RATIO_ALL,startTime,endTime));
    }
}
