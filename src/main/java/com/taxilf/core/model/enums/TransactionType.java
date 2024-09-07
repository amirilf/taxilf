package com.taxilf.core.model.enums;

public enum TransactionType {

    // external
    PASSENGER_DEPOSIT,
    DRIVER_DEPOSIT,
    PASSENGER_WITHDRAWAL,
    DRIVER_WITHDRAWAL,

    // enternal
    PASSENGER_TRIP_DEDUCTION,
    DRIVER_TRIP_ADDITION,

    // enternal cashe
    PASSENGER_TRIP_DEDUCTION_CASH,
    DRIVER_TRIP_ADDITION_CASH

}