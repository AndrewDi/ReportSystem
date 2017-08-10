package com.andrew.Service;

import com.andrew.Common.SymmetricEncoder;
import com.andrew.Model.RemoteUserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;

@Service
public class RemoteUserService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ConfigService configService;

    private final Logger logger= LoggerFactory.getLogger(this.getClass());
    private final String SELECT_REMOTEUSER_BY_HOST="SELECT HOST,USERNAME,PASSWD FROM CMDB.REMOTEUSER WHERE HOST=?";
    private final String UPDATE_REMOTEUSER_PASSWD="UPDATE CMDB.REMOTEUSER SET PASSWD=? WHERE HOST=?";

    /**
     * Use Host to get Remote User,Passwd
     * If Passwd is not AES Enpycrt,then Enpycrt it
     * @param Host
     * @return
     */
    public RemoteUserModel getRemoteUserByHost(String Host){
        RowMapper rowMapper= (resultSet, i) -> {
            RemoteUserModel remoteUserModel=new RemoteUserModel();
            remoteUserModel.Host=resultSet.getString("HOST");
            remoteUserModel.UserName=resultSet.getString("USERNAME");
            remoteUserModel.Passwd=resultSet.getString("PASSWD");
            return remoteUserModel;
        };

        RemoteUserModel remoteUserModel=(RemoteUserModel)jdbcTemplate.query(this.SELECT_REMOTEUSER_BY_HOST,rowMapper,Host).get(0);
        if(remoteUserModel==null){
            logger.error("Can Not Find RemoteUser info For Host:"+Host);
            return null;
        }
        if(remoteUserModel.Passwd.startsWith(configService.getKeyPre())){
            String tmpPasswd=remoteUserModel.Passwd.substring(configService.getKeyPre().length());
            remoteUserModel.Passwd= SymmetricEncoder.AESDncode(tmpPasswd);
        }
        else {
            String tmpPasswd=configService.getKeyPre().concat(SymmetricEncoder.AESEncode(remoteUserModel.Passwd));
            jdbcTemplate.update(this.UPDATE_REMOTEUSER_PASSWD,tmpPasswd,Host);
        }
        return remoteUserModel;
    }
}