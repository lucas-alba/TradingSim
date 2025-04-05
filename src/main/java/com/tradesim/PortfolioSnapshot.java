package com.tradesim;

import java.time.LocalDateTime;

public class PortfolioSnapshot {
    private final LocalDateTime timestamp;
    private final double portfolioValue;

    public PortfolioSnapshot(LocalDateTime timestamp, double portfolioValue) {
        this.timestamp = timestamp;
        this.portfolioValue = portfolioValue;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public double getPortfolioValue() {
        return portfolioValue;
    }
}