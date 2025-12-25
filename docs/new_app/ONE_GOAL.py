import json
import os
import pandas as pd
import sqlite3
from pydantic import BaseModel

# Get the directory where this script is located
SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
PATH_JSON = os.path.join(SCRIPT_DIR, "ONE_GOAL.json")
PATH_SQL = os.path.join(SCRIPT_DIR, "ONE_GOAL.sql")


def read_file(filepath: str) -> str:
    with open(filepath, "r") as f:
        return f.read()

def read_file_sql() -> str:
    return read_file(PATH_SQL)
def read_file_json() -> str:
    with open(PATH_JSON, "r") as file: return json.load(file)


class ForeignKey(BaseModel):
    table_name: str
    column_name: str


class Field(BaseModel):
    name: str
    type: str
    pk: bool | None
    nn: bool | None
    ai: bool | None
    u: bool | None
    default: str | None
    check: str | None
    collation: str | None
    foreign_key: dict | None


def export_schema_to_excel(schema_data: dict, output_filename: str = "DatabaseSchema.xlsx"):
    """
    Converts a schema dictionary into a pandas DataFrame and exports it to an Excel file.
    
    Each row in the final sheet represents one column across all tables.
    """
    # We will flatten the nested dictionary into a list of dictionaries, 
    # where each item is a single row in the final spreadsheet.
    rows_list = []
    for table_name, columns in schema_data.items():
        for column_name, details in columns.items():
            row = {
                "Table Name": table_name,
                "Column Name": column_name,
                "Data Type": details.get("type"),
                "Not Null": details.get("notnull"),
                "Default Value": details.get("default"),
                "Primary Key": details.get("primary_key")
            }
            rows_list.append(row)
    # Convert the list of rows into a pandas DataFrame
    df = pd.DataFrame(rows_list)
    # Write the DataFrame to an Excel file
    try:
        df.to_excel(output_filename, index=False, sheet_name="Schema Details")
        print(f"Successfully created Excel file: '{output_filename}'")
    except Exception as e:
        print(f"Failed to write Excel file: {e}")


def parse_schema_from_sql_definition(sql_script: str) -> dict:
    """
    Parses CREATE TABLE schema information using an in-memory SQLite database.
    This is robust to various SQL formatting issues.
    """
    conn = sqlite3.connect(":memory:")
    cursor = conn.cursor()
    schema_info = {}
    try:
        # 1. Execute the provided SQL script to create tables in memory
        cursor.executescript(sql_script)
        # 2. Query the master table for table names
        cursor.execute("SELECT name FROM sqlite_master WHERE type='table';")
        tables = cursor.fetchall()
        for table_row in tables:
            table_name = table_row[0]
            # 3. Use PRAGMA table_info to get column details for each table
            cursor.execute(f"PRAGMA table_info('{table_name}');")
            columns = cursor.fetchall()
            # The PRAGMA result format is: (cid, name, type, notnull, dflt_value, pk)
            column_details = {}
            for col in columns:
                cid, name, dtype, notnull, dflt_value, pk = col
                column_details[name] = {
                    "type": dtype,
                    "notnull": bool(notnull),
                    "default": dflt_value,
                    "primary_key": bool(pk)
                }
            schema_info[table_name] = column_details
    except sqlite3.Error as e:
        print(f"An SQLite error occurred: {e}")
    finally:
        conn.close()
    return schema_info


def transform_data(records: list[dict], target_class: type[BaseModel]) -> list[BaseModel]:
    """
    Transforms a list of raw data dictionaries into a list of Record objects,
    mapping the keys to the desired Pydantic model fields.
    """
    transformed_records: list[BaseModel] = []
    if len(records) == 0:
        return transformed_records

    # Map JSON keys to Pydantic field names, handling typos and spaces
    key_mapping = {
        "name": "Name",
        "type": "Type",
        "pk": "PK",
        "nn": "NN",
        "ai": "AI",
        "u": "U",
        "default": "Default",
        "check": "Check",
        "collation": "Collation",
        "foreign_key": "Foreign Key"  # Handle typo in JSON key
    }

    for record in records:
        # Build mapped_item using the key mapping
        mapped_item = {pydantic_key: record.get(json_key, None)
                      for pydantic_key, json_key in key_mapping.items()}
        # Use the Pydantic model to validate and instantiate the object
        try:
            record_instance = target_class(**mapped_item)
        except Exception as e:
            raise Exception(f"{e}\nIssue trying to map:\n{json.dumps(mapped_item, indent=2)}")
        transformed_records.append(record_instance)
    return transformed_records


if __name__ == "__main__":
    os.system("clear")
    # fields = read_file_json()["DATA"]["TABLES"][0]["FIELDS"]
    # records: list[Field] = transform_data(fields, Field)
    # for record in records: print(record)
    # df = pd.DataFrame(fields)
    # print(df)
    # print(DATA)
    data = read_file_sql().split("CREATE TABLE ")
    # for record in data:
    #     if record:            
    #         table = record.split("(\n")
    #         table_name = table[0].replace("\"", "").strip()
    #         table_data = table[1].split(");")[0]
    #         columns = table_data.split("\n")
    #         print(len(columns))
    #         print(columns[len(columns)-2])
    #         # print(columns[len(columns)-3])
    #         if table_name != "job_search":
    #             # print(table_name)
    #             # print(table_data)
    #             # for column in columns:
    #             #     print(column)
    #             fields = columns
    #         # if table[0]:
    #         #     print(len(table))
    #         #     print(table)

    # The SQL data provided in the prompt
    # Run the parser
    schema_info_pragmatic = parse_schema_from_sql_definition(read_file_sql())
    export_schema_to_excel(schema_data=schema_info_pragmatic)
    # Print the results nicely
    # print(json.dumps(schema_info_pragmatic, indent=4))
    # for tables in schema_info_pragmatic:
    #     print(tables)
    #     table = schema_info_pragmatic[tables]
    #     print(table)
    #     for columns in tables:
    #         print(columns)
    #     # if table == "job_search":
    #     #     fields = schema_info_pragmatic[table]
    #     #     for field in fields:
    #     #         print(field)
    #     #         print(fields[field])
    #     #         # for key in field:
    #     #         #     print(key)

