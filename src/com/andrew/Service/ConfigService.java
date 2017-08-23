package com.andrew.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.util.WebUtils;

import java.io.FileNotFoundException;

@Service
public class ConfigService {

    public ConfigService(){

    }

    public String getShellPath() {
        if(this.shellPath.equals("")||this.shellPath==null){
            try {
                this.shellPath= WebUtils.getRealPath(ContextLoader.getCurrentWebApplicationContext().getServletContext(), "/Shell");
            } catch (FileNotFoundException e) {
                this.shellPath="/";
            }
        }
        if(!this.shellPath.endsWith("/")&&!this.shellPath.endsWith("\\")){
            this.shellPath=this.shellPath+"/";
        }
        return shellPath;
    }

    public void setShellPath(String shellPath) {
        this.shellPath = shellPath;
    }

    public String getKeyPre() {
        return keyPre;
    }

    public void setKeyPre(String keyPre) {
        this.keyPre = keyPre;
    }

    private String shellPath;

    private String keyPre;


}
