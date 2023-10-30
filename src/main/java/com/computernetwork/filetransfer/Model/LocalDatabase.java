package com.computernetwork.filetransfer.Model;

import java.sql.*;
import java.util.ArrayList;
import java.io.File;

public class LocalDatabase {
    private Connection connection;
    public LocalDatabase() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:LocalFileData.db");
        System.out.println("Connected to the database.");
        Statement statement = connection.createStatement();
        String createFileTable = "CREATE TABLE IF NOT EXISTS file_data (" +
                "name TEXT PRIMARY KEY," +
                "file_size INTEGER NOT NULL," +
                "description TEXT NOT NULL," +
                "date INTEGER NOT NULL," +
                "file_location TEXT NOT NULL" +
                ")";
        String createUserTable = "CREATE TABLE IF NOT EXISTS user_data (name TEXT PRIMARY KEY)";
        String createServerIPTable = "CREATE TABLE IF NOT EXISTS server_IP_data (name TEXT PRIMARY KEY)";
        statement.execute(createFileTable);
        statement.execute(createUserTable);
        statement.execute(createServerIPTable);
    }
    public void close() throws SQLException {
        connection.close();
        System.out.println("Connection closed");
    }
    /**
     * return the name of the user in user_data, return null if there is none
     */
    public String getUser() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet row = statement.executeQuery("SELECT name FROM user_data");
        String userName = row.getString("name");
        if(!row.wasNull()) return userName;
        return null;
    }
    /**
     * insert user into user_data table, replace the old user if they exist, return operation result
     */
    public boolean setUser(String user) throws SQLException {
        Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
        ResultSet row = statement.executeQuery("SELECT name FROM user_data");
        String userName = row.getString("name");
        if(!row.wasNull()) {
            row.updateString("name", user);
            row.updateRow();
        } else {
            row.moveToInsertRow();
            row.updateString("name", user);
            row.insertRow();
        }
        return true;
    }
    /**
     * return the serverIP address in server_IP_data, return null if there is none
     */
    public String getServerIP() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet row = statement.executeQuery("SELECT name FROM server_IP_data");
        String serverIpName = row.getString("name");
        if(!row.wasNull()) return serverIpName;
        return null;
    }
    /**
     * insert serverIP address into server_IP_data table, replace the old serverIP address if it exists, return operation result
     */
    public boolean setServerIP(String serverIP) throws SQLException {
        Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
        ResultSet row = statement.executeQuery("SELECT name FROM server_IP_data");
        String serverIpName = row.getString("name");
        if(!row.wasNull()) {
            row.updateString("name", serverIP);
            row.updateRow();
        } else {
            row.moveToInsertRow();
            row.updateString("name", serverIP);
            row.insertRow();
        }
        return true;
    }
    /**
     * return a list of file data saved in the file_data table
     */
    public ArrayList<FileData> getFileData() throws SQLException {
        ArrayList<FileData> fileList = new ArrayList<FileData>();
        Statement statement = connection.createStatement();
        ResultSet row = statement.executeQuery("SELECT * FROM file_data");
        while(row.next()) {
            String name = row.getString("name");
            Long file_size = row.getLong("file_size");
            String description = row.getString("description");
            Date date = row.getDate("date");
            String file_location = row.getString("file_location");

            FileData fileData = new FileData(name, file_size, description, date, file_location);

            fileList.add(fileData);
        }
        return fileList;
    }
    /**
     * insert fileData into the file_data table, return insert result
     */
    public boolean insertFileData(FileData fileData) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO file_data VALUES (?,?,?,?,?)");
        ps.setString(1, fileData.getName());
        ps.setLong(2, fileData.getSize());
        ps.setString(3, fileData.getDescription());
        ps.setDate(4, fileData.getUploadedDate());
        ps.setString(5, fileData.getFileLocation());
        
        ps.executeUpdate();
        return true;
    }
    /**
     * go through the list of file and check their file location, delete them from the file_data table if they're no longer exist
     */
    public ArrayList<FileData> checkFile() throws SQLException {
        ArrayList<FileData> fileList = new ArrayList<FileData>();
        Statement statement = connection.createStatement();
        ResultSet row = statement.executeQuery("SELECT * FROM file_data");
        while(row.next()) {
            String file_location = row.getString("file_location");
            File fileLocation = new File(file_location);
            if(fileLocation.exists()) {
                String name = row.getString("name");
                Long file_size = row.getLong("file_size");
                String description = row.getString("description");
                Date date = row.getDate("date");
                FileData fileData = new FileData(name, file_size, description, date, file_location);
                fileList.add(fileData);
            } else {
                PreparedStatement ps = connection.prepareStatement("DELETE FROM file_data WHERE file_location = ?");
                ps.setString(1, file_location);
                ps.executeUpdate();
            }
        }
        return fileList;
    }
}
