CREATE TABLE "application_sync" (
	"application_sync_id"	INTEGER, -- Unique Id/Primary key for this table
	"sqlite_id"	INTEGER, -- Foreign Key (Primary key from the SQLite table)
	"mongodb_id"	TEXT, -- Foreign Key (Primary key from the MogoDB table) String/ObjectID
	"created_on"	TIMESTAMP NOT NULL DEFAULT (datetime('now', 'localtime')),
	"modified_on"	TIMESTAMP NOT NULL DEFAULT (datetime('now', 'localtime')),
	"created_by"	TEXT NOT NULL,
	"modified_by"	TEXT NOT NULL,
	PRIMARY KEY("application_sync_id")
);
CREATE TABLE "application" (
	"application_id"	INTEGER, -- Unique Id/Primary key for this table
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
	PRIMARY KEY("application_id")
);
CREATE TABLE "contact" (
	"contact_id"	INTEGER NOT NULL, -- Unique Id/Primary key for this table.
	"note"	TEXT,
	"created_on"	TIMESTAMP NOT NULL DEFAULT (datetime('now', 'localtime')),
	"modified_on"	TIMESTAMP NOT NULL DEFAULT (datetime('now', 'localtime')),
	"created_by"	TEXT NOT NULL,
	"modified_by"	TEXT NOT NULL,
	PRIMARY KEY("contact_id")
);
CREATE TABLE "company" (
	"company_id"	INTEGER NOT NULL, -- Unique Id/Primary key for this table.
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
	PRIMARY KEY("company_id")
);
CREATE TABLE "note" (
	"note_id"	INTEGER NOT NULL, -- Unique Id/Primary key for this table.
	"application_id"	TIMESTAMP NOT NULL, -- Foreign Key from t_application table.
	"note"	TEXT,
	"created_on"	TIMESTAMP NOT NULL DEFAULT (datetime('now', 'localtime')),
	"modified_on"	TIMESTAMP NOT NULL DEFAULT (datetime('now', 'localtime')),
	"created_by"	TEXT NOT NULL,
	"modified_by"	TEXT NOT NULL,
	PRIMARY KEY("note_id")
);
CREATE TABLE "job_search" (
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
);
CREATE TABLE "job_search_site" (
	"job_search_site_id"	INTEGER,
	"Site_Name"	TEXT NOT NULL,
	"created_on"	TIMESTAMP NOT NULL DEFAULT (datetime('now', 'localtime')),
	"modified_on"	TIMESTAMP NOT NULL DEFAULT (datetime('now', 'localtime')),
	"created_by"	TEXT NOT NULL,
	"modified_by"	TEXT NOT NULL,
	PRIMARY KEY("job_search_site_id")
);