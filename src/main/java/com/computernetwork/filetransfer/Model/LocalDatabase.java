package com.computernetwork.filetransfer.Model;

import java.sql.*;
import java.util.ArrayList;
import java.io.File;

public class LocalDatabase {
    private final Connection connection;
    public LocalDatabase() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:LocalFileData.db");
        System.out.println("Connected to the database.");
        Statement statement = connection.createStatement();
        String createFileTable = "CREATE TABLE IF NOT EXISTS file_data (" +
                "name TEXT PRIMARY KEY," +
                "file_size INTEGER NOT NULL," +
                "description TEXT NOT NULL," +
                "date TEXT NOT NULL," +
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

        if (row.next()) {
            return row.getString("name");
        }
        return null;
    }
    /**
     * insert user into user_data table, replace the old user if they exist, return operation result
     */
    public void setUser(String user) throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate("DELETE from user_data");
        PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO user_data (name) VALUES (?)");
        insertStatement.setString(1, user);
        insertStatement.executeUpdate();
    }
    /**
     * return the serverIP address in server_IP_data, return null if there is none
     */
    public String getServerIP() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet row = statement.executeQuery("SELECT name FROM server_IP_data");

        if (row.next()) {
            return row.getString("name");
        }
        return null;
    }
    /**
     * insert serverIP address into server_IP_data table, replace the old serverIP address if it exists, return operation result
     */
    public void setServerIP(String serverIP) throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate("DELETE from server_IP_data");
        PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO server_IP_data (name) VALUES (?)");
        insertStatement.setString(1, serverIP);
        insertStatement.executeUpdate();

    }
    /**
     * return a list of file data saved in the file_data table
     */
    public ArrayList<ClientFileData> getFileData() throws SQLException {
        ArrayList<ClientFileData> fileList = new ArrayList<>();
        Statement statement = connection.createStatement();
        ResultSet row = statement.executeQuery("SELECT * FROM file_data");
        while(row.next()) {
            String name = row.getString("name");
            Long file_size = row.getLong("file_size");
            String description = row.getString("description");
            Date date = Date.valueOf(row.getString("date"));
            String file_location = row.getString("file_location");

            ClientFileData fileData = new ClientFileData(name, file_size, description, date, file_location);

            fileList.add(fileData);
        }
        return fileList;
    }
    /**
     * check if fileName already exist in the file_data table, return null if not, return the fileData otherwise
     */
    public ClientFileData existFile(String fileName) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM file_data WHERE name = ?");
        ps.setString(1, fileName);
        ResultSet row = ps.executeQuery();

        if (!row.next()) return null;

        String name = row.getString("name");
        Long file_size = row.getLong("file_size");
        String description = row.getString("description");
        Date date = Date.valueOf(row.getString("date"));
        String file_location = row.getString("file_location");

        return new ClientFileData(name, file_size, description, date, file_location);
    }
    /**
     * insert fileData into the file_data table
     */
    public void insertFileData(ClientFileData fileData) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO file_data VALUES (?,?,?,?,?)");
        ps.setString(1, fileData.getName());
        ps.setLong(2, fileData.getSize());
        ps.setString(3, fileData.getDescription());
        ps.setString(4, fileData.getUploadedDate().toString());
        ps.setString(5, fileData.getFileLocation());
        
        ps.executeUpdate();
    }
    /**
     * delete fileData from the file_data table
     */
    public void deleteFileData(String fileName) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("DELETE FROM file_data WHERE name = ?");
        ps.setString(1, fileName);
        ps.executeUpdate();
    }
    /**
     * go through the list of file and check their file location, delete them from the file_data table if they're no longer exist
     */
    public ArrayList<ClientFileData> checkFile() throws SQLException {
        ArrayList<ClientFileData> fileList = new ArrayList<>();
        Statement statement = connection.createStatement();
        ResultSet row = statement.executeQuery("SELECT * FROM file_data");
        while(row.next()) {
            String file_location = row.getString("file_location");
            File fileLocation = new File(file_location);
            if(fileLocation.exists()) {
                String name = row.getString("name");
                Long file_size = row.getLong("file_size");
                String description = row.getString("description");
                Date date = Date.valueOf(row.getString("date"));
                ClientFileData fileData = new ClientFileData(name, file_size, description, date, file_location);
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
