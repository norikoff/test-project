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

    public Operation() {
    }

    public Operation(LocalDate date, LocalTime time, Long pointId, Long operationId, BigDecimal amount) {
        this.date = date;
        this.time = time;
        this.pointId = pointId;
        this.operationId = operationId;
        this.amount = amount;
    }

    public Operation(LocalDate date, BigDecimal amount) {
        this.date = date;
        this.amount = amount;
    }

    public Operation(Long pointId, BigDecimal amount) {
        this.pointId = pointId;
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

    public String getDateStatus() {
        return "date=" + date +
                ", amount=" + amount;
    }

    public String getPointsStatus() {
        return "pointId=" + pointId +
                ", amount=" + amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public Long getPointId() {
        return pointId;
    }

    public void setPointId(Long pointId) {
        this.pointId = pointId;
    }

    public Long getOperationId() {
        return operationId;
    }

    public void setOperationId(Long operationId) {
        this.operationId = operationId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
