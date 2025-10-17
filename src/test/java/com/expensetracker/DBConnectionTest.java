package com.expensetracker;

import com.expensetracker.util.DBConnectionManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

class DBConnectionTest {

    @Test
    @DisplayName("DB ping: can connect and SELECT 1")
    void dbPing() throws Exception {
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             Statement st = conn.createStatement()) {
            try (ResultSet rs = st.executeQuery("SELECT 1")) {
                Assertions.assertTrue(rs.next(), "ResultSet should have one row");
                Assertions.assertEquals(1, rs.getInt(1), "SELECT 1 should return 1");
            }
        }
    }
}
