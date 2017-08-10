package com.andrew.Service;

import org.springframework.stereotype.Service;

@Service
public class ConfigService {

    public ConfigService(){

    }

    public String getShellPath() {
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
