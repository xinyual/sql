/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.sql.calcite.standalone;

import static org.opensearch.sql.legacy.TestsConstants.TEST_INDEX_DATE_FORMATS;
import static org.opensearch.sql.legacy.TestsConstants.TEST_INDEX_STATE_COUNTRY;
import static org.opensearch.sql.util.MatcherUtils.*;
import static org.opensearch.sql.util.MatcherUtils.rows;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.json.JSONObject;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.opensearch.client.Request;

public class CalcitePPLDateTimeBuiltinFunctionIT extends CalcitePPLIntegTestCase {
  @Override
  public void init() throws IOException {
    super.init();
    loadIndex(Index.STATE_COUNTRY);
    loadIndex(Index.STATE_COUNTRY_WITH_NULL);
    loadIndex(Index.DATE_FORMATS);
    initRelativeDocs();
  }

  @Test
  public void testDate() {
    JSONObject actual =
        executeQuery(
            String.format(
                "source=%s | eval `DATE('2020-08-26')` = DATE('2020-08-26') | eval"
                    + " `DATE(TIMESTAMP('2020-08-26 13:49:00'))` = DATE(TIMESTAMP('2020-08-26"
                    + " 13:49:00')) | eval `DATE('2020-08-26 13:49')` = DATE('2020-08-26 13:49') |"
                    + " fields `DATE('2020-08-26')`, `DATE(TIMESTAMP('2020-08-26 13:49:00'))`,"
                    + " `DATE('2020-08-26 13:49')` | head 1",
                TEST_INDEX_STATE_COUNTRY));

    verifySchema(
        actual,
        schema("DATE('2020-08-26')", "date"),
        schema("DATE(TIMESTAMP('2020-08-26 13:49:00'))", "date"),
        schema("DATE('2020-08-26 13:49')", "date"));

    verifyDataRows(
        actual,
        rows(Date.valueOf("2020-08-26"), Date.valueOf("2020-08-26"), Date.valueOf("2020-08-26")));
  }

  @Test
  public void testTimestamp() {
    JSONObject actual =
        executeQuery(
            String.format(
                "source=%s | eval `TIMESTAMP('2020-08-26 13:49:00')` = TIMESTAMP('2020-08-26"
                    + " 13:49:00')| eval `TIMESTAMP(DATE('2020-08-26 13:49:00'))` ="
                    + " TIMESTAMP(date)| eval `TIMESTAMP(TIMESTAMP('2020-08-26 13:49:00'))` ="
                    + " TIMESTAMP(TIMESTAMP('2020-08-26 13:49:00'))| eval"
                    + " `TIMESTAMP(TIME('2020-08-26 13:49:00'))` = TIMESTAMP(TIME('2020-08-26"
                    + " 13:49:00'))| eval `TIMESTAMP('2020-08-26 13:49:00', 2020-08-26 00:10:10)` ="
                    + " TIMESTAMP('2020-08-26 13:49:00', '2020-08-26 00:10:10')| eval"
                    + " `TIMESTAMP('2020-08-26 13:49:00', TIMESTAMP(2020-08-26 00:10:10))` ="
                    + " TIMESTAMP('2020-08-26 13:49:00', TIMESTAMP('2020-08-26 00:10:10'))| eval"
                    + " `TIMESTAMP('2020-08-26 13:49:00', DATE(2020-08-26 00:10:10))` ="
                    + " TIMESTAMP('2020-08-26 13:49:00', DATE('2020-08-26 00:10:10'))| eval"
                    + " `TIMESTAMP('2020-08-26 13:49:00', TIME(00:10:10))` = TIMESTAMP('2020-08-26"
                    + " 13:49:00', TIME('00:10:10'))| fields `TIMESTAMP('2020-08-26 13:49:00')`,"
                    + " `TIMESTAMP(DATE('2020-08-26 13:49:00'))`, `TIMESTAMP(TIMESTAMP('2020-08-26"
                    + " 13:49:00'))`,  `TIMESTAMP(TIME('2020-08-26 13:49:00'))`,"
                    + " `TIMESTAMP('2020-08-26 13:49:00', 2020-08-26 00:10:10)`,"
                    + " `TIMESTAMP('2020-08-26 13:49:00', TIMESTAMP(2020-08-26 00:10:10))`,"
                    + " `TIMESTAMP('2020-08-26 13:49:00', DATE(2020-08-26 00:10:10))`,"
                    + " `TIMESTAMP('2020-08-26 13:49:00', TIME(00:10:10))`| head 1",
                TEST_INDEX_DATE_FORMATS));

    verifySchema(
        actual,
        schema("TIMESTAMP('2020-08-26 13:49:00')", "timestamp"),
        schema("TIMESTAMP(DATE('2020-08-26 13:49:00'))", "timestamp"),
        schema("TIMESTAMP(TIMESTAMP('2020-08-26 13:49:00'))", "timestamp"),
        schema("TIMESTAMP(TIME('2020-08-26 13:49:00'))", "timestamp"),
        schema("TIMESTAMP('2020-08-26 13:49:00', 2020-08-26 00:10:10)", "timestamp"),
        schema("TIMESTAMP('2020-08-26 13:49:00', TIMESTAMP(2020-08-26 00:10:10))", "timestamp"),
        schema("TIMESTAMP('2020-08-26 13:49:00', DATE(2020-08-26 00:10:10))", "timestamp"),
        schema("TIMESTAMP('2020-08-26 13:49:00', TIME(00:10:10))", "timestamp"));

    verifyDataRows(
        actual,
        rows(
            "2020-08-26 13:49:00",
            "1984-04-12 00:00:00",
            "2020-08-26 13:49:00",
            getUtcDate() + " 13:49:00",
            "2020-08-26 13:59:10",
            "2020-08-26 13:59:10",
            "2020-08-26 13:49:00",
            "2020-08-26 13:59:10"));
  }

  @Test
  public void testTime() {
    JSONObject actual =
        executeQuery(
            String.format(
                "source=%s | eval `TIME('2020-08-26 13:49:00')` = TIME('2020-08-26 13:49:00')| eval"
                    + " `TIME('2020-08-26 13:49')` = TIME('2020-08-26 13:49')| eval `TIME('13:49')`"
                    + " = TIME('13:49')| eval `TIME('13:49:00')` = TIME('13:49:00')| eval"
                    + " `TIME(TIME('13:49:00'))` = TIME(TIME('13:49:00'))| eval"
                    + " `TIME(TIMESTAMP('2024-08-06 13:49:00'))` = TIME(TIMESTAMP('2024-08-06"
                    + " 13:49:00'))| eval `TIME(DATE('2024-08-06 13:49:00'))` ="
                    + " TIME(DATE('2024-08-06 13:49:00'))| fields `TIME('2020-08-26 13:49:00')`,"
                    + " `TIME('2020-08-26 13:49')`, `TIME('13:49')`,  `TIME('13:49:00')`,"
                    + " `TIME(TIME('13:49:00'))`, `TIME(TIMESTAMP('2024-08-06 13:49:00'))`,"
                    + " `TIME(DATE('2024-08-06 13:49:00'))`| head 1",
                TEST_INDEX_STATE_COUNTRY));

    verifySchema(
        actual,
        schema("TIME('2020-08-26 13:49:00')", "time"),
        schema("TIME('2020-08-26 13:49')", "time"),
        schema("TIME('13:49')", "time"),
        schema("TIME('13:49:00')", "time"),
        schema("TIME(TIME('13:49:00'))", "time"),
        schema("TIME(TIMESTAMP('2024-08-06 13:49:00'))", "time"),
        schema("TIME(DATE('2024-08-06 13:49:00'))", "time"));

    verifyDataRows(
        actual,
        rows("13:49:00", "13:49:00", "13:49:00", "13:49:00", "13:49:00", "13:49:00", "00:00:00"));
  }

  @Test
  public void testDateSubAndCount() {
    JSONObject actual =
        executeQuery(
            String.format(
                "source=%s "
                    + "| where strict_date_optional_time > DATE_SUB(NOW(), INTERVAL 1 DAY) "
                    + "| stats COUNT() AS CNT ",
                TEST_INDEX_DATE_FORMATS));
    verifySchema(actual, schema("CNT", "long"));

    // tmr, +month, now
    verifyDataRows(actual, rows(3));
  }

  @Test
  public void testWeekAndWeekOfYear() {
    JSONObject actual =
        executeQuery(
            String.format(
                "source=%s | fields  strict_date_optional_time| where"
                    + " YEAR(strict_date_optional_time) < 2000| eval"
                    + " `WEEK(DATE(strict_date_optional_time))` ="
                    + " WEEK(DATE(strict_date_optional_time))| eval"
                    + " `WEEK_OF_YEAR(DATE(strict_date_optional_time))` ="
                    + " WEEK_OF_YEAR(DATE(strict_date_optional_time))| eval"
                    + " `WEEK(DATE(strict_date_optional_time), 1)` ="
                    + " WEEK(DATE(strict_date_optional_time), 1)| eval"
                    + " `WEEK_OF_YEAR(DATE(strict_date_optional_time), 1)` ="
                    + " WEEK_OF_YEAR(DATE(strict_date_optional_time), 1)| eval"
                    + " `WEEK(DATE('2008-02-20'))` = WEEK(DATE('2008-02-20')),"
                    + " `WEEK(DATE('2008-02-20'), 1)` = WEEK(DATE('2008-02-20'), 1) | fields"
                    + " `WEEK(DATE('2008-02-20'))`, `WEEK(DATE('2008-02-20'), 1)`| fields"
                    + " `WEEK(DATE(strict_date_optional_time))`,"
                    + " `WEEK_OF_YEAR(DATE(strict_date_optional_time))`,"
                    + " `WEEK(DATE(strict_date_optional_time), 1)`,"
                    + " `WEEK_OF_YEAR(DATE(strict_date_optional_time), 1)`| head 1 ",
                TEST_INDEX_DATE_FORMATS));

    verifySchema(
        actual,
        schema("WEEK(DATE(strict_date_optional_time))", "long"),
        schema("WEEK_OF_YEAR(DATE(strict_date_optional_time))", "long"),
        schema("WEEK(DATE(strict_date_optional_time), 1)", "long"),
        schema("WEEK_OF_YEAR(DATE(strict_date_optional_time), 1)", "long"),
        schema("WEEK(DATE('2008-02-20'))", "long"),
        schema("WEEK(DATE('2008-02-20'))", "long"));

    verifyDataRows(actual, rows("15", "15", "8", "8", 7, 8));
  }

  private void initRelativeDocs() throws IOException {
    List<String> relativeList = List.of("NOW", "TMR", "+month", "-2wk", "-1d@d");
    int index = 0;
    for (String time : relativeList) {
      Request request =
          new Request(
              "PUT",
              "/opensearch-sql_test_index_date_formats/_doc/%s?refresh=true".formatted(index));
      request.setJsonEntity(
          "{\"strict_date_optional_time\":\"%s\"}".formatted(convertTimeExpression(time)));

      index++;
      client().performRequest(request);
    }
  }

  private String convertTimeExpression(String expression) {
    ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
    ZonedDateTime result = now;

    switch (expression) {
      case "NOW":
        break;
      case "TMR": // Tomorrow
        result = now.plusDays(1).truncatedTo(ChronoUnit.DAYS);
        break;
      case "+month": // In one month
        result = now.plusMonths(1);
        break;
      case "-2wk": // Two weeks ago
        result = now.minusWeeks(2);
        break;
      case "-1d@d": // Yesterday
        result = now.minusDays(1).truncatedTo(ChronoUnit.DAYS);
        break;
      default:
        throw new IllegalArgumentException("Unknown time expression: " + expression);
    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    return result.format(formatter);
  }

  public void testAddAndSubTime() {
    JSONObject actual =
        executeQuery(
            String.format(
                "source=%s | eval t1 = ADDTIME(date, date) | eval t2 = SUB(time, date) | eval t3 ="
                    + " ADDTIME(DATE('2004-01-01'), TIME('23:59:59'))| eval t4 ="
                    + " ADDTIME(TIME('10:20:30'), TIME('00:05:42'))| eval t5 ="
                    + " ADDTIME(TIMESTAMP('2007-02-28 10:20:30'), DATETIME('2002-03-04 20:40:50'))|"
                    + " fields t1, t2, t3, t4, t5 | head 1",
                TEST_INDEX_STATE_COUNTRY));

    verifySchema(
        actual,
        schema("t1", "timestamp"),
        schema("t2", "time"),
        schema("t3", "timestamp"),
        schema("t4", "time"),
        schema("t5", "timestamp"));
    verifyDataRows(
        actual,
        rows(
            "2008-12-12 00:00:00",
            "23:59:59",
            "2004-01-01 23:59:59",
            "10:26:12",
            "2007-03-01 07:01:20"));
  }

  @Test
  public void testSubTime() {
    JSONObject actual =
        executeQuery(
            String.format(
                "source=%s | eval t1 = SUBTIME(DATE('2008-12-12'), DATE('2008-11-15'))| eval t2 ="
                    + " SUBTIME(TIME('23:59:59'), DATE('2004-01-01'))| eval t3 ="
                    + " SUBTIME(DATE('2004-01-01'), TIME('23:59:59'))| eval t4 ="
                    + " SUBTIME(TIME('10:20:30'), TIME('00:05:42'))| eval t5 ="
                    + " SUBTIME(TIMESTAMP('2007-03-01 10:20:30'), TIMESTAMP('2002-03-04"
                    + " 20:40:50'))| fields t1, t2, t3, t4, t5 | head 1",
                TEST_INDEX_STATE_COUNTRY));

    verifySchema(
        actual,
        schema("t1", "timestamp"),
        schema("t2", "time"),
        schema("t3", "timestamp"),
        schema("t4", "time"),
        schema("t5", "timestamp"));
    verifyDataRows(
        actual,
        rows(
            "2008-12-12 00:00:00",
            "23:59:59",
            "2003-12-31 00:00:01",
            "10:14:48",
            "2007-02-28 13:39:40"));
  }

  private static String getUtcDate() {
    return LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
  }

  @Test
  public void testAddDateAndSubDateWithConditionsAndRename() {
    JSONObject actual =
        executeQuery(
            String.format(
                "source=%s | eval lower = SUBDATE(date, 3), upper = ADDDATE(date, 1), ts ="
                    + " ADDDATE(date, INTERVAL 1 DAY) | where strict_date < upper AND strict_date >"
                    + " lower | rename strict_date as d | head 1 | fields lower, upper, d, ts",
                TEST_INDEX_DATE_FORMATS));

    verifySchema(
        actual,
        schema("lower", "date"),
        schema("upper", "date"),
        schema("d", "date"),
        schema("ts", "timestamp"));
    verifyDataRows(actual, rows("1984-04-09", "1984-04-13", "1984-04-12", "1984-04-13 00:00:00"));
  }

  @Test
  public void testDateAddAndSub() {
    String expectedDate = getUtcDate();

    JSONObject actual =
        executeQuery(
            String.format(
                "source=%s "
                    + "| eval t1 = DATE_ADD(strict_date_optional_time, INTERVAL 1 HOUR) "
                    + "| eval t2 = DATE_ADD(strict_date_optional_time, INTERVAL 1 DAY) "
                    + "| eval t3 = DATE_ADD(strict_date, INTERVAL 1 HOUR) "
                    + "| eval t4 = DATE_ADD('2020-08-26 01:01:01', INTERVAL 1 DAY) "
                    + "| eval t5 = DATE_ADD(time, INTERVAL 1 HOUR) "
                    + "| eval t6 = DATE_ADD(time, INTERVAL 5 HOUR) "
                    + "| eval t7 = DATE_ADD(strict_date, INTERVAL 2 YEAR)"
                    + "| eval t8 = DATE_ADD(DATE('2020-01-30'), INTERVAL 1 MONTH)" // edge case
                    + "| eval t9 = DATE_ADD(DATE('2020-11-30'), INTERVAL 1 QUARTER)" // rare case
                    + "| eval t10 = DATE_SUB(date, INTERVAL 31 DAY)"
                    + "| eval t11 = DATE_SUB(basic_date_time, INTERVAL 1 HOUR)"
                    + "| fields t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11 "
                    + "| head 1",
                TEST_INDEX_DATE_FORMATS));

    verifySchema(
        actual,
        schema("t1", "timestamp"),
        schema("t2", "timestamp"),
        schema("t3", "timestamp"),
        schema("t4", "timestamp"),
        schema("t5", "timestamp"),
        schema("t6", "timestamp"),
        schema("t7", "timestamp"),
        schema("t8", "timestamp"),
        schema("t9", "timestamp"),
        schema("t10", "timestamp"),
        schema("t11", "timestamp"));

    verifyDataRows(
        actual,
        rows(
            "1984-04-12 10:07:42",
            "1984-04-13 09:07:42",
            "1984-04-12 01:00:00",
            "2020-08-27 01:01:01",
            expectedDate + " 10:07:42",
            expectedDate + " 14:07:42",
            "1986-04-12 00:00:00",
            "2020-02-29 00:00:00",
            "2021-02-28 00:00:00",
            "1984-03-12 00:00:00",
            "1984-04-12 08:07:42"));
  }

  @Test
  public void testDateAddWithComparisonAndConditions() {
    JSONObject actual =
        executeQuery(
            String.format(
                "source=%s | where date > DATE('1984-04-11') | eval tomorrow = DATE_ADD(date,"
                    + " INTERVAL 1 DAY) | fields date, tomorrow",
                TEST_INDEX_DATE_FORMATS));

    verifySchema(actual, schema("date", "date"), schema("tomorrow", "timestamp"));
    verifyDataRows(
        actual,
        rows("1984-04-12", "1984-04-13 00:00:00"),
        rows("1984-04-12", "1984-04-13 00:00:00"));
  }

  @Ignore
  @Test
  public void testComparisonBetweenDateAndTimestamp() {
    // TODO: Fix this
    JSONObject actual =
        executeQuery(
            String.format(
                "source=%s | where date > TIMESTAMP('1984-04-11 00:00:00') | stats COUNT() AS cnt",
                TEST_INDEX_DATE_FORMATS));
    verifySchema(actual, schema("cnt", "long"));
    verifyDataRows(actual, rows(2));
  }
}
