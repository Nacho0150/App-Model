package com.laula.demo.errors;

public class ErrorService extends Exception {

    public ErrorService(String msn){
        super(msn);
    }
}