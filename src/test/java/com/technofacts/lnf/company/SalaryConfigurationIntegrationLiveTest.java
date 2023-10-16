package com.technofacts.lnf.company;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.given;


public class SalaryConfigurationIntegrationLiveTest {

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "http://localhost:8081/api/v1";
    }

    @Test
    public void testGetSalaryConfigurations() {
        Response response = given()
                .when()
                .get("/lnf/company/salary-configurations")
                .then()
                .statusCode(200)
                .extract()
                .response();

        String responseBody = response.getBody().asString();

    }

    @Test
    public void whenUseQueryParam_thenOK(){
        given().when().get("/lnf/company/salary-configurations").then().statusCode(200);

    }



}
