package com.andrew.Controller;

import com.andrew.Common.DocxUtils;
import com.andrew.Model.DocxPerfModel;
import com.andrew.Service.DBPerfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;
import org.springframework.web.servlet.HttpServletBean;
import org.springframework.web.util.WebUtils;

import java.io.FileNotFoundException;
import java.util.Map;

@Controller
@RequestMapping("/DBPerf")
public class DBPerfController{

    @Autowired
    private DBPerfService dbPerfService;

    @RequestMapping("getrspt")
    public @ResponseBody
    Map<String, Object> getRsptAll(@RequestParam(value = "StartTime",required = true) String StartTime,
                                 @RequestParam(value = "EndTime",required = true) String EndTime){

        return this.dbPerfService.getRtspAll(StartTime,EndTime);
    }

    @RequestMapping("gettps")
    public @ResponseBody
    Map<String, Object> getTPSAll(@RequestParam(value = "StartTime",required = true) String StartTime,
                                  @RequestParam(value = "EndTime",required = true) String EndTime){

        return this.dbPerfService.getTPSAll(StartTime,EndTime);
    }

    @RequestMapping("gettbsp")
    public @ResponseBody
    Map<String, Object> getTbspAll(@RequestParam(value = "StartTime",required = true) String StartTime,
                                  @RequestParam(value = "EndTime",required = true) String EndTime){

        return this.dbPerfService.getTbspAll(StartTime,EndTime);
    }
    @RequestMapping("getmemused")
    public @ResponseBody
    Map<String, Object> getMemUsedAll(@RequestParam(value = "StartTime",required = true) String StartTime,
                                  @RequestParam(value = "EndTime",required = true) String EndTime){

        return this.dbPerfService.getMemUsedAll(StartTime,EndTime);
    }

    @RequestMapping("getconcurrent")
    public @ResponseBody
    Map<String, Object> getConCurrentAll(@RequestParam(value = "StartTime",required = true) String StartTime,
                                  @RequestParam(value = "EndTime",required = true) String EndTime){

        return this.dbPerfService.getConCurrentAll(StartTime,EndTime);
    }

    @RequestMapping("getbpfhitratio")
    public @ResponseBody
    Map<String, Object> getBpfHitRatioAll(@RequestParam(value = "StartTime",required = true) String StartTime,
                                  @RequestParam(value = "EndTime",required = true) String EndTime){

        return this.dbPerfService.getBpfHitRatioAll(StartTime,EndTime);
    }

    @RequestMapping("downloadReport")
    public @ResponseBody
    String downloadReport(@RequestBody DocxPerfModel docxPerfModel){
        String path="";
        try {
            path=WebUtils.getRealPath(ContextLoader.getCurrentWebApplicationContext().getServletContext(), "/Download");
        }catch (FileNotFoundException e){
            path="/";
        }
        return DocxUtils.buildReport(path,docxPerfModel.getTitles(),docxPerfModel.getCaptions(),docxPerfModel.getCharts());
    }
}
