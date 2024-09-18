package com.technofacts.lnf.company;

import com.technofacts.lnf.company.service.*;
import com.technofacts.lnf.service.common.page.PaginationAndSortingHandler;
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
