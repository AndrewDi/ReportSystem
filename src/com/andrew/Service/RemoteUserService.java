package com.andrew.Service;

import com.andrew.Common.Base64Utils;
import com.andrew.Common.SymmetricEncoder;
import com.andrew.Model.RemoteUserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class RemoteUserService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ConfigService configService;

    private final Logger logger= LoggerFactory.getLogger(this.getClass());
    private final String SELECT_REMOTEUSER_BY_HOST="SELECT ID,HOST,USERNAME,PASSWD FROM CMDB.REMOTEUSER WHERE HOST=?";
    private final String UPDATE_REMOTEUSER_PASSWD="UPDATE CMDB.REMOTEUSER SET PASSWD=? WHERE HOST=?";
    private final String SELECT_REMOTEUSER_BY_ALL="SELECT ID,HOST,USERNAME,PASSWD FROM CMDB.REMOTEUSER ORDER BY ID";
    private final String UPDATE_REMOTEUSER_BY_ID="UPDATE CMDB.REMOTEUSER SET HOST=?,USERNAME=?,PASSWD=? WHERE ID=?";
    private final String INSERT_REMOTEUSER="INSERT INTO CMDB.REMOTEUSER(HOST,USERNAME,PASSWD) VALUES(?,?,?)";
    private final String DELTEE_REMOTEUSER_BY_ID="DELETE FROM CMDB.REMOTEUSER WHERE ID=?";

    private RowMapper rowMapper= (resultSet, i) -> {
        RemoteUserModel remoteUserModel=new RemoteUserModel();
        remoteUserModel.setId(resultSet.getInt("ID"));
        remoteUserModel.setHost(resultSet.getString("HOST"));
        remoteUserModel.setUserName(resultSet.getString("USERNAME"));
        remoteUserModel.setPasswd(resultSet.getString("PASSWD"));
        return remoteUserModel;
    };

    /**
     * Use Host to get Remote User,Passwd
     * If Passwd is not AES Enpycrt,then Enpycrt it
     * @param Host
     * @return
     */
    public RemoteUserModel getRemoteUserByHost(String Host){
        RemoteUserModel remoteUserModel=(RemoteUserModel)jdbcTemplate.query(this.SELECT_REMOTEUSER_BY_HOST,rowMapper,Host).get(0);
        if(remoteUserModel==null){
            logger.error("Can Not Find RemoteUser info For Host:"+Host);
            return null;
        }
        if(remoteUserModel.getPasswd().startsWith(configService.getKeyPre())){
            String tmpPasswd=remoteUserModel.getPasswd().substring(configService.getKeyPre().length());
            remoteUserModel.setPasswd(Base64Utils.Decode(tmpPasswd));
        }
        else {
            String tmpPasswd=configService.getKeyPre().concat(Base64Utils.Encode(remoteUserModel.getPasswd()));
            jdbcTemplate.update(this.UPDATE_REMOTEUSER_PASSWD,tmpPasswd,Host);
        }
        return remoteUserModel;
    }

    /**
     * Get All data from table cmdb.remoteuser
     * @return
     */
    public List<RemoteUserModel> getRemoteUserAll(){
        return this.jdbcTemplate.query(this.SELECT_REMOTEUSER_BY_ALL,rowMapper);
    }

    public int editRemoteUserByID(RemoteUserModel model){
        int rows =  this.jdbcTemplate.update(this.UPDATE_REMOTEUSER_BY_ID,model.host,model.userName,model.passwd,model.getId());
        return rows;
    }

    public int insertRemoteUser(RemoteUserModel model){
        int rows = this.jdbcTemplate.update(this.INSERT_REMOTEUSER,model.host,model.userName,model.passwd);
        return rows;
    }

    public int deleteRemoteUser(int id){
        return this.jdbcTemplate.update(this.DELTEE_REMOTEUSER_BY_ID,id);
    }
}