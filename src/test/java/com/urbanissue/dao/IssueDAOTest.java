package com.urbanissue.dao;

import com.urbanissue.model.Issue;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Requires MySQL running with civictrack schema. Skip if DB not available.
 */
class IssueDAOTest {

    @Test
    void findAll_returnsList() throws Exception {
        IssueDAO dao = new IssueDAO();
        List<Issue> list = dao.findAll();
        assertNotNull(list);
    }
}
