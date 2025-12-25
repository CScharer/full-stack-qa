# ONE GOAL (Isolating the application and services to prevent external issues when testing)

```sql
CREATE TABLE "" (
	"FieldName"	INTEGER NOT NULL DEFAULT 'Default' CHECK(>0) UNIQUE COLLATE BINARY,
	CONSTRAINT "IndexConstraintName" PRIMARY KEY("FieldName" AUTOINCREMENT),
	CONSTRAINT "ForeignKeyName" Foreign Key("FieldName") REFERENCES "IWD_Y2025_m09_d20_W37_w6_aSat"("job_search_id"),
	CONSTRAINT "CheckConstraintsName" CHECK(CheckConstraintsCheck)
);
CREATE TABLE "" (
	"FieldName"	INTEGER NOT NULL DEFAULT 'Default' CHECK(>0) UNIQUE COLLATE BINARY,
	CONSTRAINT "ConstraintName" PRIMARY KEY("FieldName" AUTOINCREMENT),
	Foreign Key("FieldName") REFERENCES "IWD_Y2025_m09_d20_W37_w6_aSat"("job_search_id")
);
```

## The Purpose
- The purpose of this document is to detail the steps to implement so we can avoid failure issues when testing applications that are external.
- Currently when we run our tests, they test external sites such as Google, Wikipedia and other sites that are developed and maintained by other organizations.
- Because those sites have changes made without our knowledge our tests fail and we spend time debugging and we want to avoid that.
- To avoid that I would like to develop an application and with services endpoints in this repo that are maintained by us.
- I have an application that's already developed that I would like to duplicate (not copy) but create something similar in the current repo.

## The Goal
- The goal of this is to develop a front end application and back end services as well as databases in the current repository that so we can run them and use them for testing purposes.

## Reference Tables
### Acronyms
| Acronym | Description |
|---| ---| 
| APP | Application |
| API | Application Programming Interface |
| DB | Database |
| DEV | Development |
| CORS | Cross-Origin Resource Sharing |
| GUI | Graphical User Interface |
| N/A | Not Applicable |
| PROD | Production |
| TBD | To Be Determined |
| TEST | Test |
| UI | User Interface |

### Item Locations
<a id="my-target-location"></a>
| Item | Location | Link |
|---| ---| ---| 
| [This Repo](#this-repo) | /Users/christopherscharer/dev/full-stack-qa | [../../full-stack-qa/](../../) |
| [The Current APP](#the-current-app) | /Users/christopherscharer/dev/apis/next-add-job | [../../../apis/next-add-job/](../../../apis/next-add-job) |
| [The Current Service Endpoints](##the-current-services--endpoints) | /Users/christopherscharer/dev/apis/job_search_api_fast_api.py | [../../../apis/job_search_api_fast_api.py](../../../apis/job_search_api_fast_api.py) |
| [The Current Database](#the-current-database) | /Users/christopherscharer/dev/CScharer.db | [../../../CScharer.db](../../../CScharer.db) |
| [The New APP](#the-new-app) | TBD | TBD |
| [The New Service Endpoints](##the-new-services--endpoints) | TBD | TBD |
| [The New Database](#the-new-database) | TBD | TBD |

# Hosts and Ports (Standard)
- NOTE: The hostname you use depends on your network configuration

### Hosts
| Type | Description |
|---| ---| 
| localhost | This is a universal standard hostname that always refers to "this computer" (the local machine itself) via the loopback IP address 127.0.0.1 (for IPv4) or ::1 (for IPv6). It's primarily used during local development or for a service running on the same physical server. |
| Network Hostnames/Ips | In production environments, you use specific server names (database-server-01.internal, api.mycompany.com) or IP addresses (10.0.0.5) that are assigned by network administrators. |

## Ports
- NOTE: Ports 0 through 1023 are known as "well-known ports" and are reserved system processes and widely used services.

### Ports APPs & APIs (Web Traffic)
- NOTE: These services primarily use standard HTTP/HTTPS ports, though alternative ports are common in development or for internal applications

| Port Number | Service/Protocol | Description |
|---| ---| ---| 
| 80 | HTTP | Default for unencrypted web traffic and web applications/APIs. |
| 443 | HTTPS | Default for secure, encrypted web traffic (SSL/TLS) for applications/APIs. |
| 8080 | HTTP (Alternate) | A common alternative port used for development servers, proxies, or application servers like Tomcat to avoid conflicts with port 80. |
| 3000 | Development | Common default ports for local application development (e.g., Node.js, React, Django, Flask). |
| 8000 | Development | Common default ports for local application development (e.g., Node.js, React, Django, Flask). |

### Ports Databases
- NOTE: Database systems have specific, registered default ports for client-to-server communication.  While these are the standard ports, network administrators often change them for security reasons or to run multiple instances of the same service on one server.

| Port Number | Database System | Protocol |
|---| ---| ---| 
| 1433 | Microsoft SQL Server | TCP |
| 1521 | Oracle Database | TCP (TNS Listener port) |
| 3306 | MySQL / MariaDB | TCP |
| 5432 | PostgreSQL | TCP |
| 27017 | MongoDB | TCP (Default access port) |
| 6379 | Redis | TCP |
| 9200 | Elasticsearch | TCP (REST API port) |


## Databases (Recommendations for Mapping)
### Recommended Table Names
| Name Option | Example Name | Explanation |
|---| ---| ---| 
| Cross-Reference (XRef) | related_tables_xref | Very common in enterprise systems; clearly indicates linking records across systems. |
| Mapping Table | related_tables_map | Explicitly describes that this table maps IDs between the two databases. |
| Linkage Table | related_tables_link | A clear, descriptive name for linking related records. |
| Synchronization Table | related_tables_sync | Implies the purpose of keeping records in sync or linked together. |

### Standard Column Naming
- NOTE: The columns within this new table should clearly identify which database/system each ID belongs to:
- If you choose the name application_xref, the table structure might look like this:

| Column Name | Data Type | Description |
|---| ---| ---| 
| mongodb_id | String/ObjectID | Stores the primary key from the MongoDB application table. |
| sqlite_id | Integer | Stores the primary key from the SQLite application table. |

## Hosts and Ports (Default)
- NOTE: http://127.0.0.1 is the same as http://localhost when running on my local machine.
### Defaults
| Item | Environment | Host | Port |
|---| ---| ---| ---| 
| APP | DEV | http://localhost | 3003 |
| APP | TEST |  http://localhost | 3004 |
| APP | PROD |  http://localhost | 3005 |
| API | DEV |  http://localhost | 8003 |
| API | TEST |  http://localhost | 8004 |
| API | PROD |  http://localhost | 8005 |
| DB | DEV |  http://localhost | N/A |
| DB | TEST |  http://localhost | N/A |
| DB | PROD |  http://localhost | N/A |

## Repo
* [see Reference Tables - Item-Locations](#item-locations)
<!-- * [see Reference Tables - Item-Locations](#my-target-location) -->
- This repo will hold all of the source code, files, databases, tests that we will maintain and run in GitHub.

## Current APP
* [see Reference Tables - Item-Locations](#item-locations)
- I currently have an APP that I developed  and I would like to duplicate (not copy) that APP, and make enhancements at the same time so we have a better APP in this repo.
- The APP should be developed in Next.js with TypeScript.
- It should use a standard folder and file structure.  Such as:
  1. A lib folder that contains files like contants.ts, model.ts and other files that have global scope.
  2. A hooks folder that contains all the hooks.  Files like useClientSide.ts, useJobAPI.ts, etc.
  3. A component folder that contains all the components in component files like AddJob.tsx, BootstrapProvider.tsx, LeftNav.tsx, ViewTable.tsx, etc.
  4. A state folder that contains all the APP states
  5. Any other common folders that would be used in a Next.js APP
 - This APP also uses endpoints and Fast API with Pydantic to be able to perform transactions against a specified SQLite database.
 - It should be configurable so the host and port can be maintained.

## Current Service Endpoints
* [see Reference Tables - Item-Locations](#item-locations)
- I currently have services endpoints that I developed  and I would like to duplicate (not copy) those, and make enhancements at the same time so we have a better services endpoints in this repo.
- The endpoints should be developed in Python and use Fast API with Pydantic
- It should be configurable so the host and port can be maintained.
- It should impliment CORS
- It should perform transactions against specified SQLite database.


## Current Database
* [see Reference Tables - Item-Locations](#item-locations)
- I currently have a database that I developed  and I would like to duplicate (not copy) that, and make enhancements at the same time so we have a better database in this repo.
- A local SQLite database.
- The current the database contains only 3 tables that we will need to implement in our new database, but we need to restructure them because they have a lot of duplication currently.
- t_JobSearch
  - This table contains data that is for job applications.  I currently contains information that has duplicated data due to the structure.
```sql
CREATE TABLE "t_JobSearch" (
	"job_search_id"	INTEGER, -- Unique Id/Primary key for this table
	"Mongodb_ID"	TEXT, -- Unique Foreign Key that would be used in a MongoDB to store data for the job application
	"created_at"	TIMESTAMP NOT NULL DEFAULT (datetime('now', 'localtime')), -- Standard created datetime stamp
	"modified_at"	TIMESTAMP NOT NULL DEFAULT (datetime('now', 'localtime')), -- Standard modified datetime stamp
	"Status"	TEXT NOT NULL DEFAULT 'Pending', -- The status of the application
	"Firm"	TEXT NOT NULL, -- The Firm who the application is being submitted to
	"Account_Managers"	TEXT, -- The Account Manager of the Firm
	"Recruiter"	TEXT NOT NULL, -- The Recruiter of the Firm
	"Title"	TEXT NOT NULL DEFAULT 'Recruiter', -- The Title of the Recruiter of the Firm
	"Client"	TEXT, -- The Client who the Firm is working with and where the subsequent job would be at
	"Work_Setting"	TEXT NOT NULL DEFAULT 'Remote', -- Where you would be working, On-Site, Hybrid or Remote
	"Compensation"	TEXT, -- The Compensation you would get paid on an Hourly or Annual basis
	"Position"	TEXT, -- The Job Title of the job at the Client
	"Job_Description"	TEXT, -- The Job Descrtion of the Position at the Client
	"EMail"	TEXT, -- The EMail of the Recruiter
	"Phone"	TEXT, -- The Phone of the Recruiter
	"LinkedIn"	TEXT, -- The LinkedIn of the Recruiter
	"Requirement"	TEXT, -- The Requirement of the job the Application is for at the Client
	"Job_Link"	TEXT, -- The Link to the website where the job is posted
	"Notes"	TEXT, -- Any notes taken while communicating with the Recruiter
	"Manager"	TEXT, -- The Manager at the Client
	"Leads"	TEXT, -- The The Team Leads at the Client
	"Location"	TEXT, -- The Location where the work will be done (typically correlates to the Work Setting, but could be an Address)
	"Date_Close"	TEXT, -- The application Close Date
	"Address"	TEXT, -- The Address of the Firm
	"City"	TEXT, -- The City of the Firm
	"State"	TEXT, -- The State of the Firm
	"Zip"	TEXT, -- The Zip of the Firm
	"Country"	TEXT NOT NULL DEFAULT 'United States', -- The Country of the Firm
	"Job_Type"	TEXT NOT NULL DEFAULT 'Technology', -- The Technology of the Client (Currently I only work in the Technology field so this only includes Technology, but could be others.  This probably also be named Industry)
	"Resume"	TEXT, -- The location of the Resume that was submitted for the application
	"Cover_Letter"	TEXT, -- The location of the Cover Letter that was submitted for the application
	"Entered_IWD"	INTEGER DEFAULT 0, -- A boolean value that specifies whether the application has been added to the Iowa Workforce Development web-site
	PRIMARY KEY("job_search_id")
)
```
- t_JobSearchNotes
  - This table contains the notes that have been taken for different job applications.
```sql
CREATE TABLE "t_JobSearchNotes" (
	"job_search_note_id"	INTEGER NOT NULL, -- Unique Id/Primary key for this table.
	"job_search_id"	TIMESTAMP NOT NULL, -- Foreign Key from t_Jobs table.
	"created_at"	TEXT NOT NULL DEFAULT (datetime('now', 'localtime')), -- Standard created datetime stamp
	"Note"	TEXT,
	PRIMARY KEY("job_search_note_id")
)
```
- t_JobSearchSites
  - This table contains a list of job search web-sites
```sql
CREATE TABLE "t_JobSearchSites" (
	"job_search_site_id"	INTEGER, -- Unique Id/Primary key for this table.
	"Site_Name"	TEXT NOT NULL, -- Site Name
	PRIMARY KEY("job_search_site_id")
)
```  

## New APP
* [see Reference Tables - Item-Locations](#item-locations)
- The new APP should mimic what the current APP does, but make the appropriate mdifications based on the new database structure.

## New Service Endpoints
* [see Reference Tables - Item-Locations](#item-locations)
- The new APP should mimic what the current APP does, but make the appropriate mdifications based on the new database structure.

## New Database
* [see Reference Tables - Item-Locations](#item-locations)
-  Create a new improved database that is optimized with the correct tables that include the appropriate primary keys (Primary, Foreign, etc.)
  1. Set up an itinital script to create database and tables so if someone clones the repo they can run the script to have the database created if it does not exist (this should exist in it's own folder and should be prefixed with a datetime stamp that includ).
  2. Set up a 
- t_application_sync
  - This table contains the ids of the t_application in the SQLite and MognoDB.
```sql
CREATE TABLE "t_application_sync" (
	"id"	INTEGER, -- Unique Id/Primary key for this table
	"sqlite_id"	INTEGER, -- Foreign Key (Primary key from the SQLite table)
	"mongodb_id"	TEXT, -- Foreign Key (Primary key from the MogoDB table) String/ObjectID
	"created_on"	TIMESTAMP NOT NULL DEFAULT (datetime('now', 'localtime')),
	"modified_on"	TIMESTAMP NOT NULL DEFAULT (datetime('now', 'localtime')),
	"created_by"	TEXT NOT NULL,
	"modified_by"	TEXT NOT NULL,
	PRIMARY KEY("id")
```
- t_application
  - This table contains the application data.
```sql
CREATE TABLE "t_application" (
	"id"	INTEGER, -- Unique Id/Primary key for this table
	"mongodb_id"	TEXT, -- Foreign Key (Primary key from the MogoDB table) String/ObjectID
	"status"	TEXT NOT NULL DEFAULT 'Pending', -- The status of the application
	"requirement"	TEXT, -- The Requirement of the job the Application is for at the Client
	"work_setting"	TEXT NOT NULL DEFAULT 'Remote', -- Where you would be working, On-Site, Hybrid or Remote
	"compensation"	TEXT, -- The Compensation you would get paid on an Hourly or Annual basis
	"position"	TEXT, -- The Job Title of the job at the Client
	"job_description"	TEXT, -- The Job Descrtion of the Position at the Client
	"resume"	TEXT, -- The location of the Resume that was submitted for the application
	"cover_letter"	TEXT, -- The location of the Cover Letter that was submitted for the application
	"entered_iwd"	INTEGER DEFAULT 0, -- A boolean value that specifies whether the application has been added to the Iowa Workforce Development web-site
	"date_close"	TEXT, -- The application Close Date
	"created_on"	TIMESTAMP NOT NULL DEFAULT (datetime('now', 'localtime')),
	"modified_on"	TIMESTAMP NOT NULL DEFAULT (datetime('now', 'localtime')),
	"created_by"	TEXT NOT NULL,
	"modified_by"	TEXT NOT NULL,
	PRIMARY KEY("id")
```
- t_notes
  - This table contains the notes that have been taken for different applications.
```sql
CREATE TABLE "t_JobSearch" (
	"job_search_id"	INTEGER, -- Unique Id Primary key for this table
	"Firm"	TEXT NOT NULL, -- The Firm who the application is being submitted to
	"Account_Managers"	TEXT, -- The Account Manager of the Firm
	"Recruiter"	TEXT NOT NULL, -- The Recruiter of the Firm
	"Title"	TEXT NOT NULL DEFAULT 'Recruiter', -- The Title of the Recruiter of the Firm
	"Client"	TEXT, -- The Client who the Firm is working with and where the subsequent job would be at
	"EMail"	TEXT, -- The EMail of the Recruiter
	"Phone"	TEXT, -- The Phone of the Recruiter
	"LinkedIn"	TEXT, -- The LinkedIn of the Recruiter
	"Job_Link"	TEXT, -- The Link to the website where the job is posted
	"Notes"	TEXT, -- Any notes taken while communicating with the Recruiter
	"Manager"	TEXT, -- The Manager at the Client
	"Leads"	TEXT, -- The The Team Leads at the Client
	"Location"	TEXT, -- The Location where the work will be done (typically correlates to the Work Setting, but could be an Address)
	"Address"	TEXT, -- The Address of the Firm
	"City"	TEXT, -- The City of the Firm
	"State"	TEXT, -- The State of the Firm
	"Zip"	TEXT, -- The Zip of the Firm
	"Country"	TEXT NOT NULL DEFAULT 'United States', -- The Country of the Firm
	"Job_Type"	TEXT NOT NULL DEFAULT 'Technology', -- The Technology of the Client (Currently I only work in the Technology field so this only includes Technology, but could be others.  This probably also be named Industry)
	PRIMARY KEY("job_search_id")
)
```
- t_contacts
  - This table contains the notes that have been taken for different applications.
```sql
CREATE TABLE "t_contacts" (
	"id"	INTEGER NOT NULL, -- Unique Id/Primary key for this table.
	"note"	TEXT,
	"created_on"	TIMESTAMP NOT NULL DEFAULT (datetime('now', 'localtime')),
	"modified_on"	TIMESTAMP NOT NULL DEFAULT (datetime('now', 'localtime')),
	"created_by"	TEXT NOT NULL,
	"modified_by"	TEXT NOT NULL,
	PRIMARY KEY("job_search_note_id")
)
```
- t_company
  - This table contains the notes that have been taken for different applications.
```sql
CREATE TABLE "t_company" (
	"id"	INTEGER NOT NULL, -- Unique Id/Primary key for this table.
	"application_id"	TIMESTAMP NOT NULL, -- Foreign Key from t_application table.
	"address"	TEXT, -- The Address of the Firm
	"city"	TEXT, -- The City of the Firm
	"state"	TEXT, -- The State of the Firm
	"zip"	TEXT, -- The Zip of the Firm
	"country"	TEXT NOT NULL DEFAULT 'United States', -- The Country of the Firm
	"created_on"	TIMESTAMP NOT NULL DEFAULT (datetime('now', 'localtime')),
	"modified_on"	TIMESTAMP NOT NULL DEFAULT (datetime('now', 'localtime')),
	"created_by"	TEXT NOT NULL,
	"modified_by"	TEXT NOT NULL,
	PRIMARY KEY("job_search_note_id")
)
```
- t_company
  - This table contains the notes that have been taken for different applications.
```sql
CREATE TABLE "t_company" (
	"id"	INTEGER NOT NULL, -- Unique Id/Primary key for this table.
	"application_id"	TIMESTAMP NOT NULL, -- Foreign Key from t_application table.
	"address"	TEXT, -- The Address of the Firm
	"city"	TEXT, -- The City of the Firm
	"state"	TEXT, -- The State of the Firm
	"zip"	TEXT, -- The Zip of the Firm
	"country"	TEXT NOT NULL DEFAULT 'United States', -- The Country of the Firm
	"created_on"	TEXT NOT NULL DEFAULT (datetime('now', 'localtime')),
	"note"	TEXT,
	PRIMARY KEY("job_search_note_id")
)
```
- t_company
  - This table contains the notes that have been taken for different applications.
```sql
CREATE TABLE "t_company" (
	"id"	INTEGER NOT NULL, -- Unique Id/Primary key for this table.
	"application_id"	TIMESTAMP NOT NULL, -- Foreign Key from t_application table.
	"address"	TEXT, -- The Address of the Firm
	"city"	TEXT, -- The City of the Firm
	"state"	TEXT, -- The State of the Firm
	"zip"	TEXT, -- The Zip of the Firm
	"country"	TEXT NOT NULL DEFAULT 'United States', -- The Country of the Firm
	"created_on"	TEXT NOT NULL DEFAULT (datetime('now', 'localtime')),
	"note"	TEXT,
	PRIMARY KEY("job_search_note_id")
)
```
- t_note
  - This table contains the notes that have been taken for different applications.
```sql
CREATE TABLE "t_note" (
	"id"	INTEGER NOT NULL, -- Unique Id/Primary key for this table.
	"application_id"	TIMESTAMP NOT NULL, -- Foreign Key from t_application table.
	"note"	TEXT,
	"created_on"	TIMESTAMP NOT NULL DEFAULT (datetime('now', 'localtime')),
	"modified_on"	TIMESTAMP NOT NULL DEFAULT (datetime('now', 'localtime')),
	"created_by"	TEXT NOT NULL,
	"modified_by"	TEXT NOT NULL,
	PRIMARY KEY("job_search_note_id")
)
```
- t_JobSearchSites
  - This table contains a list of job search web-sites
```sql
CREATE TABLE "t_JobSearchSites" (
	"job_search_site_id"	INTEGER,
	"Site_Name"	TEXT NOT NULL,
	"created_on"	TIMESTAMP NOT NULL DEFAULT (datetime('now', 'localtime')),
	"modified_on"	TIMESTAMP NOT NULL DEFAULT (datetime('now', 'localtime')),
	"created_by"	TEXT NOT NULL,
	"modified_by"	TEXT NOT NULL,
	PRIMARY KEY("job_search_site_id")
)
```  

## The Order of Events or Process for development
1. Analyze the current database to determine how we can create a more robust database.

    1.1 This includes determining what tables we should create in our new database.


## Other Significant Items
- The APP, Service Endpoints and Database should all exist in the same folder that is separate from the other folders in this repo to isolate them as they will be the development part of the repo while the rest of the repo is primarily for automated testing.