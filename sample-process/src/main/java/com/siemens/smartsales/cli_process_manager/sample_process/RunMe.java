package com.siemens.smartsales.cli_process_manager.sample_process;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RunMe {
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static C3P0DataSource db = C3P0DataSource.getInstance("org.postgresql.Driver",
            "jdbc:postgresql://localhost:5432/postgres", "postgres", "postgres");

    private static void insertProcessDBEntry(long pid) throws SQLException {

        String currentTimestamp = sdf.format(new Date());
        String query = String.format(
                "insert into cli_process_manager.cli_processes (pid, create_date, update_date, status) values(%s,'%s','%s', '%s') on conflict (pid) do update set create_date=excluded.create_date, update_date=excluded.update_date, status=excluded.status",
                Long.toString(pid), currentTimestamp, currentTimestamp, Statuses.STARTED);

        try (Connection con = db.getConnection()) {

            Statement stmt = con.createStatement();
            stmt.execute(query);
            stmt.close();
        } catch (Exception e) {
            throw e;
        }

    }

    private static void updateStatus(long pid, Statuses status) throws SQLException {

        String currentTimestamp = sdf.format(new Date());
        String query = String.format(
                "update cli_process_manager.cli_processes set update_date='%s', status='%s' where pid = %s",
                currentTimestamp, status.toString(), Long.toString(pid));

        try (Connection con = db.getConnection()) {

            Statement stmt = con.createStatement();
            stmt.execute(query);
            stmt.close();
        } catch (Exception e) {
            throw e;
        }

    }

    private static void updateProgress(long pid, double progress) throws SQLException {

        String currentTimestamp = sdf.format(new Date());
        String query = String.format(
                "update cli_process_manager.cli_processes set update_date='%s', status='%s', progress=%s where pid = %s",
                currentTimestamp, Statuses.RUNNING.toString(), Double.toString(progress), Long.toString(pid));

        try (Connection con = db.getConnection()) {

            Statement stmt = con.createStatement();
            stmt.execute(query);
            stmt.close();
        } catch (Exception e) {
            throw e;
        }

    }

    private static boolean shouldTerminate(long pid) throws SQLException {

        try (Connection con = db.getConnection()) {
            String query = String.format("select status from cli_process_manager.cli_processes where pid = %s", pid);

            Statement stmt = con.createStatement();

            ResultSet rs = stmt.executeQuery(query);
            rs.next();

            String status = rs.getString(1);

            stmt.close();

            return status.equals(Statuses.SENT_TERMINATE.toString());
        }

    }

    public static void main(String[] args) throws InterruptedException, SQLException {

        System.out.println("SAMPLE CLI PROCESS STARTED");

        long pid = ProcessHandle.current().pid();
        System.out.println("PID: " + Long.toString(pid));

        insertProcessDBEntry(pid);

        try {

            int steps = 104;
            for (int count = 1; count <= steps; count++) {
                System.out.println(Math.random());
                double progress = 1.0 * count / steps * 100;
                updateProgress(pid, progress);

                Thread.sleep(1000);

                if (shouldTerminate(pid)) {
                    updateStatus(pid, Statuses.TERMINATED);
                    System.exit(1);
                }
            }

            updateStatus(pid, Statuses.FINISHED);

        } catch (Exception e) {
            updateStatus(pid, Statuses.FAILED);

            throw e;
        } finally {
            System.out.println("SAMPLE CLI PROCESS STOPPED");
        }

    }

}
