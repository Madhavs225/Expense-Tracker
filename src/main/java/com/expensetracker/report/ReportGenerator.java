package com.expensetracker.report;

/**
 * Generic contract for generating a report of type R from a request Q.
 */
public interface ReportGenerator<R extends AbstractReport, Q> {

    R generate(Q request);
}
