# SQL Injection Hardening Worklog

Date: 2026-04-20
Branch: security/sql-injection-hardening-20260420
Status: In progress - initial hardening implemented

## Scope

This worklog captures the security analysis completed for:

- Dependabot alert #93 (`org.hibernate:hibernate-core`, CVE-2026-0603 / GHSA-2p5w-cvg5-gc5c)
- Additional targeted review for raw JDBC SQL injection risk across test/support code

## Findings Completed

### 1) Dependabot #93 review (Hibernate)

- Confirmed vulnerable dependency in `pom.xml`:
  - `hibernate.version` is `5.6.15.Final`
  - advisory vulnerable range includes this version
- Confirmed no obvious application DAO/repository Hibernate query paths in current repository scan
- Confirmed Hibernate/JPA usage is limited to test setup/bootstrap areas (`EntityManagerFactory` / `EntityManager`) and persistence test config
- Advisory metadata currently reports no published `first_patched_version`

Conclusion: alert remains open; practical risk appears low in current usage, but version must be upgraded when a fixed release is published.

**Status update (2026-07-19)**: Still on `hibernate.version` **5.6.15.Final**. GitHub advisory GHSA-2p5w-cvg5-gc5c still reports no `first_patched_version` for the 5.x line. Intentionally left as-is after Dependabot cleanup PRs #282–#283.

**Status update (2026-07-19, deferred majors)**: Migrated to Hibernate ORM **6.6.54.Final** (`org.hibernate.orm:hibernate-core`) with Jakarta Persistence **3.1**. Advisory #93 applies only to `org.hibernate:hibernate-core` **5.2.8–5.6.15**, so this coordinate/version change is the remediation path. Test JPA bootstrap and `persistence.xml` updated accordingly.

### 2) Raw JDBC SQL injection review

High-risk query construction patterns identified in:

- `src/test/java/com/cjs/qa/jdbc/SQL.java`
  - direct concatenation of parameters such as `eMail`, `company`, `environment`, `status`, and `fileName` into SQL
- `src/test/java/com/cjs/qa/jdbc/JDBC.java`
  - generic execution methods use `Statement.executeQuery/executeUpdate` on caller-supplied SQL strings

Medium-risk dynamic identifier interpolation identified in:

- `src/test/java/com/cjs/qa/jdbc/JDBC.java`
  - table/field names interpolated in DDL helpers (e.g., add/drop/rename field/table methods) without strict allowlisting

## Implemented Hardening Changes (This Session)

### 1) Added prepared-statement execution APIs in `JDBC.java`

- Added `executeUpdatePrepared(String sql, List<Object> parameters, boolean autoCommit)`
- Added `queryResultsStringPrepared(String sql, List<Object> parameters, boolean includeColumnNames)`
- Added internal helpers:
  - `bindParameters(PreparedStatement preparedStatement, List<Object> parameters)`
  - `closeConnectionQuietly()`
- Added `PreparedStatement` import

### 2) Refactored high-risk call sites in `SQL.java` to parameter binding

Updated methods to use `?` placeholders and `JDBC` prepared helpers:

- `exUpdateDbUserToAdmin`
- `exUpdateDbUserToNonAdmin`
- `exSqlGetPartyId`
- `getCompanyNumber`
- `getPDFCompare`
- `getPSTARInfo`
- `getURL`
- `getUserName`
- `updateSubmissionStatus`
- `validEmailActive`

Additional detail:

- Replaced direct `ResultSet` reads in these methods with row maps from
  `queryResultsStringPrepared(...)` where practical.
- Preserved existing method signatures and return types to minimize API impact.
- Corrected `updateSubmissionStatus` SQL to use parameterized assignment and predicate.

## Remaining Hardening Backlog

- Introduce strict identifier allowlisting for dynamic table/column interpolation in
  `JDBC.java` DDL helpers (e.g., add/drop/rename field/table methods).
- Incrementally migrate other raw string-built DML helpers where inputs can be external.
- Re-run static checks and targeted tests to verify behavior parity.

## Planned Hardening Changes

1. Add identifier validation/allowlisting utilities for table and column names used in dynamic DDL.
2. Keep existing quote-escaping only as defense-in-depth (not primary mitigation).
3. Re-scan query construction patterns after refactor to verify risk reduction.
4. Run focused test/lint checks after each migration step.

## Current Repository State

- Branch created: `security/sql-injection-hardening-20260420`
- Documentation file added: `docs/work/20260420_SQL_INJECTION_HARDENING_WORKLOG.md`
- No commits made
- No staging performed

