package com.example.demo.mygeneraltest;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = {
        "pretty" }, strict = true, junit = "--step-notifications")

public class MyClass {

}
