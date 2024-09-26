/*
 *
 *  * Copyright © 2024 Lever And Fulcrum Solutions (hereinafter referred to as "LNF").
 *  * All rights reserved.
 *  *
 *  * This source code is the proprietary property of LNF
 *  *
 *  * Unauthorized copying, redistribution, or modification of this code,
 *  * via any medium, is strictly prohibited unless expressly authorized
 *  * in writing by LNF.
 *  *
 *  * This code is confidential and intended solely for the use of LNF
 *  * and its authorized personnel.
 *
 */

package com.lnf.company;

import com.lnf.company.service.*;
import com.lnf.company.service.*;
import com.lnf.service.common.page.PaginationAndSortingHandler;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ContextConfiguration
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseTestClass {

    @MockBean
    public CompanyAddressService companyAddressService;

    @MockBean
    public CompanyService companyService;

    @MockBean
    public CompanyEventService companyEventService;

    @MockBean
    public CompanyEventSchedulerService companyEventSchedulerService;

    @MockBean
    public CompanyGstService companyGstService;

    @MockBean
    public CompanyHolidayService companyHolidayService;

    @MockBean
    public CompanyNotesService companyNotesService;

    @MockBean
    public TaskService taskService;

    @MockBean
    public ThemeService themeService;

    @InjectMocks
    public PaginationAndSortingHandler paginationAndSortingHandler;

    @MockBean
    private CompanyFileService service;

}
