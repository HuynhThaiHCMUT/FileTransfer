package com.computernetwork.filetransfer.Model;

import javafx.collections.ObservableList;

import java.sql.*;

public class LocalDatabase {
    private Connection connection;
    public LocalDatabase() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:LocalFileData.db");
        System.out.println("Connected to the database.");
        Statement statement = connection.createStatement();
        String createFileTable = "CREATE TABLE IF NOT EXISTS file_data (" +
                "id INTEGER PRIMARY KEY," +
                "name TEXT NOT NULL," +
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
    public String getUser() {
        //TODO
        return null;
    }
    /**
     * insert user into user_data table, replace the old user if they exist, return operation result
     */
    public boolean setUser(String user) {
        //TODO
        return false;
    }
    /**
     * return the serverIP address in server_IP_data, return null if there is none
     */
    public String getServerIP() {
        //TODO
        return null;
    }
    /**
     * insert serverIP address into server_IP_data table, replace the old serverIP address if it exist, return operation result
     */
    public boolean setServerIP(String serverIP) {
        //TODO
        return false;
    }
    /**
     * return a list of file data saved in the file_data table
     */
    public ObservableList<FileData> getFileData() {
        //TODO
        return null;
    }
    /**
     * insert fileData into the file_data table, return insert result
     */
    public boolean insertFileData(FileData fileData) {
        //TODO
        return false;
    }
    /**
     * go through the list of file and check their file location, delete them from the file_data table if they're no longer exist, return the new list
     */
    public ObservableList<FileData> checkFile() {
        //TODO
        return null;
    }
}
