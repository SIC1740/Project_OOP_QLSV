// File: src/main/java/com/myuniv/sm/dao/impl/ScoreDaoJdbc.java
package com.myuniv.sm.dao.impl;

import com.myuniv.sm.dao.ScoreDao;
import com.myuniv.sm.dao.util.DBConnection;
import com.myuniv.sm.model.Score;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScoreDaoJdbc implements ScoreDao {
    private static final String SELECT_ALL =
            "SELECT ID,MSV,Diem_TB,Ngay_tao,Ngay_sua FROM DiemTB";
    private static final String SELECT_BY_ID = SELECT_ALL+" WHERE ID=?";
    private static final String INSERT_SQL =
            "INSERT INTO DiemTB(MSV,Diem_TB,Ngay_tao,Ngay_sua) VALUES(?,?,?,?)";
    private static final String UPDATE_SQL =
            "UPDATE DiemTB SET MSV=?,Diem_TB=?,Ngay_tao=?,Ngay_sua=? WHERE ID=?";
    private static final String DELETE_SQL = "DELETE FROM DiemTB WHERE ID=?";

    @Override public List<Score> findAll() {
        List<Score> list=new ArrayList<>();
        try(Connection c=DBConnection.getConnection();
            Statement s=c.createStatement(); ResultSet rs=s.executeQuery(SELECT_ALL)){
            while(rs.next()){ Score sc=new Score();
                sc.setId(rs.getInt("ID")); sc.setMsv(rs.getString("MSV"));
                sc.setAvgScore(rs.getDouble("Diem_TB"));
                sc.setDateCreated(rs.getDate("Ngay_tao").toLocalDate());
                sc.setDateModified(rs.getDate("Ngay_sua").toLocalDate());
                list.add(sc);
            }
        }catch(SQLException e){e.printStackTrace();}
        return list;
    }
    @Override public Score findById(int id) {
        try(Connection c=DBConnection.getConnection();
            PreparedStatement ps=c.prepareStatement(SELECT_BY_ID)){
            ps.setInt(1,id);
            try(ResultSet rs=ps.executeQuery()){ if(rs.next()){ Score sc=new Score();
                sc.setId(rs.getInt("ID")); sc.setMsv(rs.getString("MSV"));
                sc.setAvgScore(rs.getDouble("Diem_TB"));
                sc.setDateCreated(rs.getDate("Ngay_tao").toLocalDate());
                sc.setDateModified(rs.getDate("Ngay_sua").toLocalDate());
                return sc;} }
        }catch(SQLException e){e.printStackTrace();}
        return null;
    }
    @Override public boolean create(Score s) {
        try(Connection c=DBConnection.getConnection();
            PreparedStatement ps=c.prepareStatement(INSERT_SQL,Statement.RETURN_GENERATED_KEYS)){
            ps.setString(1,s.getMsv()); ps.setDouble(2,s.getAvgScore());
            ps.setDate(3,Date.valueOf(s.getDateCreated()));
            ps.setDate(4,Date.valueOf(s.getDateModified()));
            int aff=ps.executeUpdate();
            if(aff>0){ try(ResultSet k=ps.getGeneratedKeys()){ if(k.next()) s.setId(k.getInt(1)); } return true; }
        }catch(SQLException e){e.printStackTrace();}
        return false;
    }
    @Override public boolean update(Score s) {
        try(Connection c=DBConnection.getConnection();
            PreparedStatement ps=c.prepareStatement(UPDATE_SQL)){
            ps.setString(1,s.getMsv()); ps.setDouble(2,s.getAvgScore());
            ps.setDate(3,Date.valueOf(s.getDateCreated()));
            ps.setDate(4,Date.valueOf(s.getDateModified())); ps.setInt(5,s.getId());
            return ps.executeUpdate()>0;
        }catch(SQLException e){e.printStackTrace();}
        return false;
    }
    @Override public boolean delete(int id) {
        try(Connection c=DBConnection.getConnection();
            PreparedStatement ps=c.prepareStatement(DELETE_SQL)){
            ps.setInt(1,id); return ps.executeUpdate()>0;
        }catch(SQLException e){e.printStackTrace();}
        return false;
    }
}