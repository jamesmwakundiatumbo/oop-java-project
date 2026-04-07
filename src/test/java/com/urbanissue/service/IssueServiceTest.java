package com.urbanissue.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class IssueServiceTest {

    @Test
    void getAllCategories_returnsList() {
        IssueService service = new IssueService();
        var list = service.getAllCategories();
        assertNotNull(list);
    }
}
