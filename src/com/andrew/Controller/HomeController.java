package com.andrew.Controller;

import com.andrew.Common.ArrayUtils;
import com.andrew.Model.RemoteUserModel;
import com.andrew.Service.ConfigService;
import com.andrew.Service.DBConnService;
import com.andrew.Service.RemoteUserService;
import com.andrew.Service.SshService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/Home")
public class HomeController {

    private final Logger logger= LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DBConnService dbConnService;

    @Autowired
    private RemoteUserService remoteUserService;

    @Autowired
    private SshService sshService;

    @Autowired
    private ConfigService configService;

    @RequestMapping("index")
    public String Index(ModelMap modelMap){

        return "Home/Index";
    }

    @RequestMapping("DBList")
    public String DBList(ModelMap modelMap){
        modelMap.addAttribute("data",dbConnService.getDBConnALL());
        return "Home/DBList";
    }

    @RequestMapping("DBReport")
    public String GetDBReport(ModelMap modelMap,
                              @RequestParam(value = "DBName",required = true) String DBName,
                              @RequestParam(value = "StartTime",required = true) String StartTime,
                              @RequestParam(value = "EndTime",required = true) String EndTime){
        logger.info("Found DBName:"+DBName+" StartTime:"+StartTime+" EndTime:"+EndTime);
        Map<String,Object> BaseInfo=dbConnService.getDBConnAny(DBName);
        if(BaseInfo!=null){
            String host=BaseInfo.get("HOST").toString();
            BaseInfo.put("startTime",StartTime);
            BaseInfo.put("endTime",EndTime);

            //Sync to get Remote SSH Username and Passwd for Host
            RemoteUserModel remoteUserModel=remoteUserService.getRemoteUserByHost(host);

            //Sync to get FileSystem Information
            String result = sshService.ShScpAndExecOnce("getFileSystem.sh",host,remoteUserModel.UserName,remoteUserModel.Passwd);
            modelMap.addAttribute("fss",ArrayUtils.strToList(result));

            //Sync to get BaseInfo of System
            String shBaseInfo = sshService.ShScpAndExecOnce("getBaseInfo.sh",host,remoteUserModel.UserName,remoteUserModel.Passwd);
            for (String[] info:ArrayUtils.strToList(shBaseInfo)) {
                BaseInfo.put(info[0],info[1]);
            }

            //Sync to get Tablespace Usage and State
            modelMap.addAttribute("tbspace",dbConnService.getTBSpaceUtilization(DBName));

            //Sync to get Bufferpool Usae and Info
            modelMap.addAttribute("bpfs",dbConnService.getBpfHitRation(DBName,StartTime,EndTime));

            //Sync to get Ulimit Values
            String ulimits = sshService.ShScpAndExecOnce("getUlimit.sh",host,remoteUserModel.UserName,remoteUserModel.Passwd);
            modelMap.addAttribute("ulimits",ArrayUtils.strToList(ulimits));
        }
        modelMap.addAttribute("BaseInfo",BaseInfo);
        return "Home/DBReport";
    }


    @RequestMapping("getrspt")
    public @ResponseBody
    Map<String, Object[]> getRSPT(@RequestParam(value = "DBName",required = true) String DBName,
                                            @RequestParam(value = "StartTime",required = true) String StartTime,
                                            @RequestParam(value = "EndTime",required = true) String EndTime){

        return this.dbConnService.getRSPT(DBName,StartTime,EndTime);
    }

    @RequestMapping("gettps")
    public @ResponseBody
    Map<String, Object[]> getTPS(@RequestParam(value = "DBName",required = true) String DBName,
                                  @RequestParam(value = "StartTime",required = true) String StartTime,
                                  @RequestParam(value = "EndTime",required = true) String EndTime){

        return this.dbConnService.getTPS(DBName,StartTime,EndTime);
    }

    @RequestMapping("getconcurrent")
    public @ResponseBody
    Map<String, Object[]> getConCurrent(@RequestParam(value = "DBName",required = true) String DBName,
                                 @RequestParam(value = "StartTime",required = true) String StartTime,
                                 @RequestParam(value = "EndTime",required = true) String EndTime){

        return this.dbConnService.getConCurrent(DBName,StartTime,EndTime);
    }

    @RequestMapping("getavglogreads")
    public @ResponseBody
    Map<String, Object[]> getAvgLogReads(@RequestParam(value = "DBName",required = true) String DBName,
                                        @RequestParam(value = "StartTime",required = true) String StartTime,
                                        @RequestParam(value = "EndTime",required = true) String EndTime){

        return this.dbConnService.getAvgLogReads(DBName,StartTime,EndTime);
    }

    @RequestMapping("getavglockwaittime")
    public @ResponseBody
    Map<String, Object[]> getAvgLockWaitTime(@RequestParam(value = "DBName",required = true) String DBName,
                                         @RequestParam(value = "StartTime",required = true) String StartTime,
                                         @RequestParam(value = "EndTime",required = true) String EndTime){

        return this.dbConnService.getAvgLockWaitTime(DBName,StartTime,EndTime);
    }

    @RequestMapping("getlockescals")
    public @ResponseBody
    Map<String, Object[]> getLockEscals(@RequestParam(value = "DBName",required = true) String DBName,
                                             @RequestParam(value = "StartTime",required = true) String StartTime,
                                             @RequestParam(value = "EndTime",required = true) String EndTime){

        return this.dbConnService.getLockEscals(DBName,StartTime,EndTime);
    }

    @RequestMapping("gettopslowsql")
    public @ResponseBody
    List<Map<String, Object>> getTopSlowSql(@RequestParam(value = "DBConnID",required = true) int DBConnINT,
                                        @RequestParam(value = "StartTime",required = true) String StartTime,
                                        @RequestParam(value = "EndTime",required = true) String EndTime){

        return this.dbConnService.getTopSlowSql(DBConnINT,StartTime,EndTime);
    }

    @RequestMapping("gettoprowsreadsql")
    public @ResponseBody
    List<Map<String, Object>> getTopRowsReadSql(@RequestParam(value = "DBConnID",required = true) int DBConnINT,
                                            @RequestParam(value = "StartTime",required = true) String StartTime,
                                            @RequestParam(value = "EndTime",required = true) String EndTime){

        return this.dbConnService.getTopRowsReadSql(DBConnINT,StartTime,EndTime);
    }

    @RequestMapping("gettoplockwaitsql")
    public @ResponseBody
    List<Map<String, Object>> getTopLockWaitSql(@RequestParam(value = "DBConnID",required = true) int DBConnINT,
                                                @RequestParam(value = "StartTime",required = true) String StartTime,
                                                @RequestParam(value = "EndTime",required = true) String EndTime){

        return this.dbConnService.getTopLockWaitSql(DBConnINT,StartTime,EndTime);
    }
}