package com.technofacts.lnf.company;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.springframework.test.context.ContextConfiguration;

@Suite
@SelectPackages("com.technofacts.lnf.company.controller")
@ContextConfiguration(classes = Application.class)
public class TestSuite {

}

