package com.saurav.lld.inmemorytimeseriesmetricsstore;

/**
 * In-Memory Time Series Metrics Store Design and implement an in-memory
 * component for storing and querying time-series metric data for a monitoring /
 * observability system. The component will be used by clients that continuously
 * emit metric datapoints, and by other clients that query those metrics over
 * time. This is not meant to be a full database or distributed system. Focus on
 * a clean low-level design for an in-memory implementation. Problem Statement
 * Build a lightweight metrics store that supports: registering metrics A metric
 * may have a name, type, and optional metadata such as a description. Possible
 * metric types include: COUNTER, GAUGE writing metric datapoints querying
 * datapoints over time querying recent/latest data for a metric. computing
 * simple aggregates Clients should also be able to ask for aggregate values
 * over a time range, such as: SUM, AVG, MIN, MAX, COUNT handling concurrent
 * access safelyx
 *
 *
 * Entry point for in-memory-time-series-metrics-store. Add domain types in this
 * package (or subpackages), not in the default package.
 *
 *
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("in-memory-time-series-metrics-store ready.");
    }
}
