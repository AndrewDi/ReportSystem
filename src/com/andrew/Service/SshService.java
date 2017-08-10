package com.andrew.Service;

import com.trilead.ssh2.Connection;
import com.trilead.ssh2.SCPClient;
import com.trilead.ssh2.SFTPv3Client;
import com.trilead.ssh2.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.*;

@Service
public class SshService {

    private final Logger logger= LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ConfigService configService;

    /**
     * Scp File From Local Machine to Remote Machine
     * @param source Full Source File Path
     * @param target Full Target File Path
     * @param host Target Host IP/Hostname
     * @param username Target Host SSH Username
     * @param passwd Target Host SSH Passwd
     * @return Scp Operation is complete(True/False)
     */
    public Boolean scpFile(String source,String target,String host,String username,String passwd){
        String fullPath=configService.getShellPath()+source;
        File file=new File(fullPath);
        if(!file.exists())
        {
            logger.error("File Not Exists:"+fullPath);
            return false;
        }
        Connection sshConnection=new Connection(host);
        try {
            sshConnection.connect();
            sshConnection.authenticateWithPassword(username,passwd);
            SCPClient scpClient=sshConnection.createSCPClient();
            logger.info("Begin to Transfer File to Remote:"+fullPath);
            scpClient.put(fullPath,target);
            logger.info("Complete Transfer File to Remote:"+fullPath);
        } catch (Exception e1) {
            logger.error(e1.getMessage());
            return false;
        }
        finally {
            sshConnection.close();
        }
        return true;
    }

    /**
     * SSh to Remote Machine And Exec One Command
     * @param command The Command that will execute
     * @param sb return StringBuffer
     * @param host Target Host IP/Hostname
     * @param username Target Host SSH Username
     * @param passwd Target Host SSH Passwd
     * @return SSH And Exec Operation is complete(True/False)
     */
    public Boolean sshCommand(String command, StringBuffer sb,String host, String username, String passwd){
        Connection sshConnection = new Connection(host);
        try {
            sshConnection.connect();
            sshConnection.authenticateWithPassword(username,passwd);
            Session session=sshConnection.openSession();
            session.execCommand(command);
            InputStream inputStream = session.getStdout();
            InputStreamReader inputStreamReader=new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(inputStreamReader);
            String line = null;
            while((line = br.readLine())!=null){
                sb.append(line);
                sb.append("\n");
            }
            br.close();
            inputStreamReader.close();
            inputStream.close();
            session.close();
        }catch (Exception ex){
            logger.error(ex.getMessage());
            return false;
        }
        finally {
            sshConnection.close();
        }
        return true;
    }

    /**
     * Scp Shell to Remote Machine And Execute Once
     * @param shellName Full Shell File Path
     * @param host Target Host IP/Hostname
     * @param username Target Host SSH Username
     * @param passwd Target Host SSH Passwd
     * @return Result
     */
    public String ShScpAndExecOnce(String shellName,String host,String username,String passwd){
        StringBuffer stringBuffer=new StringBuffer();
        if(this.sshCommand("[ ! -d /tmp/script ]&&mkdir -p /tmp/script",stringBuffer,host,username,passwd)) {
            if (this.scpFile(shellName, "/tmp/script/", host, username, passwd)) {
                if(this.sshCommand("sh /tmp/script/"+shellName,stringBuffer,host,username,passwd)){
                    this.sshCommand("rm -f /tmp/script/"+shellName,stringBuffer,host,username,passwd);
                    return stringBuffer.toString();
                }
                stringBuffer.append("execute command failed");
            }
            stringBuffer.append("scp file failed");
        }
        stringBuffer.append("can not make temporary script directory");
        return stringBuffer.toString();
    }

    /**
     * Scp Shell to Remote Machine And Execute Once
     * @param shellName Full Shell File Path
     * @param args Shell Args pass to Shell Script
     * @param host Target Host IP/Hostname
     * @param username Target Host SSH Username
     * @param passwd Target Host SSH Passwd
     * @return Result
     */
    public String ShScpAndExecWithArgOnce(String shellName,String args,String host,String username,String passwd){
        StringBuffer stringBuffer=new StringBuffer();
        if(this.sshCommand("[ ! -d /tmp/script ]&&mkdir -p /tmp/script",stringBuffer,host,username,passwd)) {
            if (this.scpFile(shellName, "/tmp/script/", host, username, passwd)) {
                if(this.sshCommand("sh /tmp/script/"+shellName+" "+args,stringBuffer,host,username,passwd)){
                    this.sshCommand("rm -f /tmp/script/"+shellName,stringBuffer,host,username,passwd);
                    return stringBuffer.toString();
                }
                stringBuffer.append("execute command failed");
            }
            stringBuffer.append("scp file failed");
        }
        stringBuffer.append("can not make temporary script directory");
        return stringBuffer.toString();
    }
}
