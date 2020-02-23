package com.norikoff.project;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class Operation implements Serializable {
    LocalDate date;
    LocalTime time;
    Long pointId;
    Long operationId;
    BigDecimal amount;

    public Operation(LocalDate date, LocalTime time, Long pointId, Long operationId, BigDecimal amount) {
        this.date = date;
        this.time = time;
        this.pointId = pointId;
        this.operationId = operationId;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "date=" + date +
                ", time=" + time +
                ", pointId=" + pointId +
                ", operationId=" + operationId +
                ", amount=" + amount;
    }
}
