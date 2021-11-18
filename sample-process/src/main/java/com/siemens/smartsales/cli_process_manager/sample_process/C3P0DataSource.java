package com.siemens.smartsales.cli_process_manager.sample_process;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class C3P0DataSource {

    static Map<String, C3P0DataSource> instances = new HashMap<String, C3P0DataSource>();

    ComboPooledDataSource comboPooledDataSource;

    private C3P0DataSource(String driver, String connectionString, String username, String password) {
        try {
            comboPooledDataSource = new ComboPooledDataSource();
            comboPooledDataSource.setMaxPoolSize(4);
            comboPooledDataSource.setDriverClass(driver);
            comboPooledDataSource.setJdbcUrl(connectionString);
            comboPooledDataSource.setUser(username);
            comboPooledDataSource.setPassword(password);
        } catch (PropertyVetoException ex1) {
            ex1.printStackTrace();
        }
    }

    public static C3P0DataSource getInstance(String driver, String connectionString, String username, String password) {
        if (instances.get(connectionString) == null)
            instances.put(connectionString, new C3P0DataSource(driver, connectionString, username, password));

        return instances.get(connectionString);
    }

    public Connection getConnection() {
        Connection con = null;
        try {
            con = comboPooledDataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return con;
    }

}
